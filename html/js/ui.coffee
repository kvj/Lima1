class UIElement
	constructor: (@renderer) ->

	child: (element, cl, index) ->
		child = null
		if index<0
			child = element.children(cl).eq(element.children(cl).size()+index)
		else
			child = element.children(cl).eq(index)
		if child.size() is 1 then child else null
	
	render: (item, config, element, options, handler) ->
		handler null

	canGrow: (config) ->
		no

	grow: (height, config, element, options) ->
		height

	fullHeight: (element, config, canHaveDelimiter = no) ->
		w = element.outerHeight element, true
		if canHaveDelimiter && config.delimiter
			w++
		w

__id = 0

class SimpleElement extends UIElement

	name: 'simple'

	canGrow: (config) ->
		if config and config.grow is 'no' then no else yes

	grow: (height, config, element, options) ->
		id = ++__id
		nowHeight = element.innerHeight()
		floats = 0
		floatHeight = 0
		fixedHeight = 0
		flow = config.flow ? []
		for i, fl of flow
			i = parseInt(i)
			el = @child element, '.simple', i
			type = @renderer.get fl.type
			if type.canGrow fl
				floatHeight += @fullHeight el, config, i>0
				floats++
			else
				fixedHeight += @fullHeight el, config, i>0
		if floats>0
			for i, fl of flow
				i = parseInt(i)
				type = @renderer.get fl.type
				el = @child element, '.simple', i
				# log 'simple grow:', id, type.canGrow(fl), floats, type, @fullHeight(el, config, i>0)
				if type.canGrow(fl) and floats>0
					freeHeight = height - element.innerHeight()
					floatPlus = Math.floor(freeHeight / floats)
					thisHeight = @fullHeight el, config, i>0
					type.grow(thisHeight + floatPlus, fl, el, options)
					newHeight = @fullHeight el, config, i>0
					fixedHeight += newHeight
					floatHeight -= thisHeight
					# log 'simple grow after:', id, thisHeight+floatPlus, newHeight, i, height, freeHeight, floats
					floats--
		return fixedHeight

	render: (item, config, element, options, handler) ->
		if config.defaults and not options.empty
			@renderer.applyDefaults config.defaults, item
		if config.border
			element.addClass('border_'+config.border)
		flow = config.flow ? []
		for i, fl of flow
			i = parseInt(i)
			do (i) =>
				# log i, fl
				el = @child(element, '.simple', i) ? $('<div/>').addClass('simple').appendTo(element)
				if config.delimiter and i>0
					el.addClass('delimiter_'+config.delimiter)
				@renderer.get(fl.type).render item, fl, el, options, () =>
					if i is flow.length-1
						handler null

class TitleElement extends UIElement

	name: 'title'

	render: (item, config, element, options, handler) ->
		if options.empty then return handler null
		$('<span/>').addClass('title0_text').appendTo(element).text(@renderer.inject config.name ? ' ')
		if config.edit
			el = $('<span/>').addClass('text_editor title0_editor').appendTo(element)
			@renderer.text_editor el, item, (@renderer.replace config.edit)
		handler null

class CalendarElement extends UIElement

	name: 'calendar'

	render: (item, config, element, options, handler) ->
		if not env.mobile
			calendar = $('<div/>').addClass('calendar_element').appendTo(element)
			calendar.datepicker({
				dateFormat: 'yymmdd',
				firstDay: 1,
				onSelect: (dt) =>
					@renderer.ui.open_link 'dt:'+dt
					return false
			});
		handler null

class Textlement extends UIElement

	name: 'text'

	render: (item, config, element, options, handler) ->
		if options.empty then return handler null
		el = $('<div/>').addClass('text_editor').appendTo(element)
		if config.edit and not options.readonly
			property = @renderer.replace config.edit, item
			ed = @renderer.text_editor el, item, property
			ed.attr('item_id', item.id)
			ed.attr('property', property)
			ed.attr('option', options.text_option)
			if config.title
				title = $('<div/>').addClass('text_editor_title').appendTo(element).text(config.title)
		handler null

class CheckElement extends UIElement

	name: 'check'

	render: (item, config, element, options, handler) ->
		if options.empty then return handler null
		el = $('<div/>').addClass('check_editor').appendTo(element)
		if config.inset then el.addClass 'check_inset'
		property = @renderer.replace config.edit, item
		checked = no
		if property and item[property] is 1
			checked = yes
			el.addClass('checked').text('×')
		if not options.readonly
			el.bind 'click', (event) =>
				@renderer.on_edited item, property, if checked then null else 1
				return false
		handler null

class DateElement extends UIElement

	name: 'date'

	print_date: (date, el) ->
		dt = new Date()
		if date and dt.fromString(date)
			el.text(dt.format('MM/dd'))
			return true
		else
			el.html('&nbsp;')
			return false

	render: (item, config, element, options, handler) ->
		if options.empty then return handler null
		el = $('<div/>').addClass('date_editor').appendTo(element)
		property = @renderer.replace config.edit, item
		@print_date item[property], el
		el.bind 'click', (e) =>
			if e.shiftKey
				@renderer.on_edited item, property, null
			else
				el.datepicker 'dialog' , null, (date) =>
					if @print_date date, el
						@renderer.on_edited item, property, date
				, {dateFormat: 'yymmdd'}, e
		$('<div style="clear: both;"/>').appendTo(element)
		handler null

class TimeElement extends UIElement

	name: 'time'

	_split_value: (value) ->
		[parseInt(value?.substr(0, 2) ? 0, 10), parseInt(value?.substr(2) ? 0, 10)]

	value_to_string: (txt) ->
		if not txt or txt.length isnt 4
			return ''
		[hr, min] = @_split_value txt
		ap = 'a'
		if hr is 0
			hr = 12
		else
			if hr>11
				ap = 'p'
				if hr>12 then hr -= 12
		return ''+hr+(if min>0 then ':'+min else '')+ap

	show_editor: (value, element, handler) ->
		connect_to = element.offset()
		el = $('<div/>').addClass('ui-timepicker ui-corner-all ui-widget-content').appendTo(document.body)
		x = Math.floor(connect_to.left-el.width()/2)
		x = 0 if x<0
		y = connect_to.top+element.height()
		caption_div = $('<div/>').addClass('ui-timepicker-caption').appendTo(el)
		hour_div = $('<div/>').addClass('ui-timepicker-slider').appendTo(el)
		minute_div = $('<div/>').addClass('ui-timepicker-slider').appendTo(el)
		val = value
		_on_change = (hour, minute) =>
			hr = parseInt(hour ? hour_div.slider 'value')
			min = parseInt(minute ? minute_div.slider 'value')
			val = ''+(if hr<10 then '0' else '')+hr+(if min<10 then '0' else '')+min
			caption_div.text @value_to_string val
		[hr_val, min_val] = @_split_value value
		hour_div.slider({
			min: 0
			max: 23
			step: 1
			value: hr_val
			slide: (event, ui) =>
				_on_change ui.value, null
		})
		minute_div.slider({
			min: 0
			max: 45
			step: 15
			value: min_val
			slide: (event, ui) =>
				_on_change null, ui.value
		})

		_on_close = () =>
			el.remove()
		buttons = $('<div/>').addClass('ui-timepicker-buttons').appendTo(el)
		$('<button/>').addClass('ui-timepicker-button').appendTo(buttons).text('OK').bind 'click', () =>
			_on_close null
			handler val
		$('<button/>').addClass('ui-timepicker-button').appendTo(buttons).text('Cancel').bind 'click', () =>
			_on_close null
		_on_change null
		buttons.find('button').button()
		el.css('left', x).css('top', y)


	render: (item, config, element, options, handler) ->
		if options.empty then return handler
		el = $('<div/>').addClass('time_editor').appendTo(element)
		property = @renderer.replace config.edit, item
		parts = (@value_to_string item[property]).split(':')
		if parts.length is 2
			el.html parts[0]+'<br/>'+parts[1]
		else
			el.text parts[0]
		el.bind 'click', (e) =>
			if e.shiftKey
		 		@renderer.on_edited item, property, null
		 	else
		 		@show_editor item[property], el, (time) =>
		 			@renderer.on_edited item, property, time
		# 		el.datepicker 'dialog' , null, (date) =>
		# 			if @print_date date, el
		# 				@renderer.on_edited item, property, date
		# 		, {dateFormat: 'yymmdd'}, e
		$('<div class="clear"/>').appendTo(element)
		handler null

class MarkElement extends UIElement

	name: 'mark'


	_show_value: (value, el) ->
		if not value then return true
		if value in [1, 2] 
			el.addClass('mark'+value)
			return true
		return false

	render: (item, config, element, options, handler) ->
		if options.empty then return handler null
		property = @renderer.replace config.edit, item
		value = item[property]
		el = $('<div/>').addClass('mark_editor').appendTo(element)
		@_show_value value, el
		el.bind 'click', (event) =>
			if not value
				value = 1
			else
				if value is 1
					value = 2
				else
					value = null
			@_show_value value, el
			@renderer.on_edited item, property, value
		handler null

class HRElement extends UIElement

	name: 'hr'

	render: (item, config, element, options, handler) ->
		if options.empty then return handler null
		$('<div/>').addClass('hr').appendTo(element)
		handler null

class Title1Element extends UIElement

	name: 'title1'

	render: (item, config, element, options, handler) ->
		if options.empty then return handler null
		bg = $('<div/>').addClass('title1_bg').appendTo(element)
		$('<div/>').addClass('title1').appendTo(bg).text(@renderer.inject config.name, item)
		$('<div style="clear: both;"/>').appendTo(element)
		handler null

class Title2Element extends UIElement

	name: 'title2'

	render: (item, config, element, options, handler) ->
		if options.empty then return handler null
		bg = $('<div/>').addClass('title2').appendTo(element)
		$('<span/>').addClass('title2_text').appendTo(bg).text(@renderer.inject config.name ? ' ')
		if config.edit
			el = $('<span/>').addClass('text_editor title2_editor').appendTo(bg)
			@renderer.text_editor el, item, (@renderer.replace config.edit)
		handler null

class Title3Element extends UIElement

	name: 'title3'

	render: (item, config, element, options, handler) ->
		if options.empty then return handler null
		bg = $('<div/>').addClass('title3').appendTo(element)
		$('<span/>').addClass('title3_text').appendTo(bg).text(@renderer.inject config.name, item)
		handler null

class HeaderElement extends UIElement

	name: 'header'

	render: (item, config, element, options, handler) ->
		if options.empty then return handler null
		flow = config.flow ? []
		sizes = config.size ? []
		if flow.length is 1 and sizes.length is 0
			sizes = [1]
		if flow.length isnt sizes.length then return handler null
		bg = $('<div/>').addClass('header').appendTo(element)
		w = element.innerWidth()-2
		float_size = 0
		lsizes = []
		for sz in sizes
			lsizes.push sz
			float_size += sz
		space_size = 0
		if float_size>0
			for i, sz of lsizes
				if sz<=1 then lsizes[i] = Math.floor(w*sz/float_size)
		margin = 0
		for i, fl of flow
			i = parseInt(i)
			last = i is flow.length-1
			width = lsizes[i]
			if last
				# Fix last col
				width = w-margin
			el = $('<div/>').addClass('col header_col').appendTo(bg).width(width)
			if fl then el.text(@renderer.inject fl) else el.html('&nbsp;')
			diff = el.outerWidth() - el.innerWidth()
			if diff > 0
				el.width(el.innerWidth()-diff)
			margin += width
		# $('<div style="clear: both;"/>').appendTo(bg)
		return handler null


class ColsElement extends UIElement

	name: 'cols'

	canGrow: ->
		yes

	grow: (height, config, element, options) ->
		flow = config.flow ? []
		maxh = 0
		body = element.children().first()
		for i, fl of flow
			i = parseInt(i)
			el = @child(body, '.col_data', i).children().first()
			type = @renderer.get(fl.type)
			h = 0
			if type.canGrow fl
				h = type.grow height, fl, el, options
				# log 'cols grow', i, h, height, fl.type, fl, type
			else
				h = @fullHeight el, config
				# log 'cols fixed', i, h, height, fl, type
			if h>maxh then maxh = h
		maxh

	render: (item, config, element, options, handler) ->
		flow = config.flow ? []
		sizes = config.size ? []
		if flow.length isnt sizes.length then return handler

		w = element.innerWidth()
		element.addClass('group')
		body = $('<div/>').addClass('col_body').appendTo(element)
		float_size = 0
		if config.space
			float_size += config.space*(flow.length-1)
		lsizes = []
		for sz in sizes
			lsizes.push sz
			float_size += sz
		space_size = 0
		if float_size>0
			for i, sz of lsizes
				if sz<=1
					lsizes[i] = Math.floor(w*sz/float_size)
			if config.space
				space_size = Math.floor(w*config.space/float_size)
		margin = 0
		index = 0
		for i, fl of flow
			i = parseInt(i)
			last = i is flow.length-1
			el = null
			el_body = null
			# First run - create all divs
			if i>0 and config.space
				#Create space between cols
				$('<div/>').appendTo(body).addClass('col').width(space_size).html('&nbsp;')
				margin += space_size
			width = lsizes[i]
			if last
				# Fix last col
				width = w-margin
			el = $('<div/>').addClass('col col_data').appendTo(body)
			el_body = $('<div/>').appendTo(el).addClass 'col_item'
			margin += width
			if fl.line>0
				width -= fl.line
				el.addClass('col_'+fl.line)
			# if fl.border>0
			# 	width -= fl.border*2
			if fl.bg
				el.addClass('col_g')
			el.width width
			diff = el.outerWidth() - el.innerWidth()
			# if diff > 0
			# 	el.width(el.innerWidth()-diff)
			# if last
			# 	$('<div style="clear: both;"/>').appendTo(element)
			do (last) =>
				@renderer.get(fl.type).render item, fl, el_body, options, () =>
					if last
						handler null

class ListElement extends UIElement

	name: 'list'

	grow: (height, config, element, options) ->
		nowHeight = @fullHeight element, config
		emptyHeight = @fullHeight @child(element, '.list_item', -1), config, yes
		added = Math.floor((height - nowHeight) / emptyHeight)
		# log 'list grow:', height, nowHeight, emptyHeight, added, nowHeight+added*emptyHeight
		for i in [0...added]
			# log 'Render empty', i, added, nowHeight, emptyHeight, height, config, element
			@_render {area: config.area}, config.item, element, {disabled: true, delimiter: config.delimiter}, (el) =>
				null
			nowHeight += emptyHeight
		nowHeight

	canGrow: (config) ->
		if config and config.grow is 'no' then no else yes

	_render: (item, config, element, options, handler)->
		el = $('<div/>').addClass('list_item').appendTo(element)
		if options.delimiter
			el.addClass('delimiter_'+options.delimiter)
		if options.disabled
			el.addClass('disabled')
		if not env.mobile
			if options.draggable
				handle = $('<div/>').addClass('list_item_handle list_item_handle_left').appendTo(el)
				el.bind 'mousemove', () =>
					handle.show()
				el.bind 'mouseout', () =>
					handle.hide()
				el.data('type', 'note')
				el.data('renderer', @renderer)
				el.data('item', item)
				el.draggable({handle: handle, zIndex: 3, containment: 'document', helper: 'clone', appendTo: 'body'})
			el.droppable({
				accept: '.list_item',
				hoverClass: 'list_item_drop',
				tolerance: 'pointer',
				drop: (event, ui) =>
					drop = ui.draggable.data('item')
					renderer = ui.draggable.data('renderer')
					@renderer.move_note drop, item.area, (if item.id then item else null), () =>
						if renderer isnt @renderer
							renderer.render null
						@renderer.render null

			})
		@renderer.get('simple').render item, config, el, {empty: false, readonly: options.disabled, text_option: if options.empty then item.area else ''}, () =>
			handler el

	render: (item, config, element, options, handler) ->
		# log 'list', config.area, empty
		if config.border
			element.addClass('border_'+config.border)
		flow = config.flow ? []
		@renderer.items config.area, (items) =>
			for i, itm of items
				i = parseInt i
				@_render itm, config.item, element, {disable: false, draggable: true, delimiter: if i>0 then config.delimiter else null}, () =>
			@_render {area: config.area}, config.item, element, {disable: false, empty: true, delimiter: if items.length>0 then config.delimiter else null}, () =>
				handler null

class Renderer

	show_archived: no

	constructor: (@manager, @ui, @root, @template, @data, @max_height) ->
		@elements = [
			new SimpleElement this
			new TitleElement this
			new HRElement this
			new Title1Element this
			new Title2Element this
			new Title3Element this
			new HeaderElement this
			new ColsElement this
			new ListElement this
			new Textlement this
			new CheckElement this
			new MarkElement this
			new DateElement this
			new TimeElement this
			new CalendarElement this
		]
		@root.data('sheet', @data)

	get: (name) ->
		# log 'get', name, @elements.length
		for el in @elements
			# log 'compare', name, el.name
			if el.name is name
				return el
		return @elements[0]

	applyDefaults: (config, item) ->
		for own key, value of config
			# log 'Inject', key, value
			item[key] ?= @inject value
				# log 'After inject', item[key]

	inject: (txt, item = @data) ->
		return @ui.inject txt, item

	replace: (text, item = @data) ->
		return @ui.replace text, item

	_save_sheet: (handler) ->
		if @template.code
			@data.code = @inject @template.code, @data
		@manager.saveSheet @data, (err, object) =>
			if err then return @ui.show_error err
			@on_sheet_change @data
			handler object

	move_note: (item, area, before, handler) ->
		# log 'Moving', item.place, area, before
		to_save = @manager.sortArray @items(area), item, before
		item.area = area
		@_save_note item, () =>
			# log 'Saving others', to_save
			if to_save.length is 0
				return handler item
			else
				for i, el of to_save
					last = parseInt(i) is to_save.length-1
					do (last) =>
						@manager.saveNote el, () =>
							if last
								handler item

	_save_note: (item, handler) ->
		# log '_save_note', item.place
		if not item.place
			others = @items item.area
			@manager.sortArray others, item
		
		if not @data.id
			@_save_sheet (object) =>
				item.sheet_id = @data.id
				@manager.saveNote item, (err, object) =>
					if err then return @ui.show_error err
					handler object
		else
				item.sheet_id = @data.id
				@manager.saveNote item, (err, object) =>
					if err then return @ui.show_error err
					handler object

	on_edited: (item, property, value) ->
		if not item or not property
			return false
		item[property] = value
		if item is @data
			@_save_sheet (object) =>
				@render null
		else
			@_save_note item, () =>
				@render null

	remove_note: (item) ->
		@manager.removeNote item, (err) =>
			if err then return @ui.show_error err
			@render null


	text_editor: (element, item, property, handler) ->
		if not property
			return null
		old_value = item[property] ? ''
		element.text(old_value)
		_on_finish_edit = (value) =>
			element.text value
			if value is old_value
				return
			if handler
				handler item, property, value
			else
				@on_edited item, property, value
		if env.mobile
			# dialog
			element.bind 'tap', (event) =>
				log 'Show dialog'
				$.mobile.changePage($('#text_dialog'))
				$('#text_editor').focus().val(old_value)
				$('#text_save').unbind('click').bind 'click', () =>
					$('#text_dialog').dialog('close')
					_on_finish_edit($('#text_editor').val() ? '')
				$('#text_remove').unbind('click').bind 'click', () =>
					$('#text_dialog').dialog('close')
					if item.id
						@remove_note item
					
		else
			element.attr('contentEditable', true)
			element.bind 'keypress', (event) =>
				if event.keyCode is 13
					event.preventDefault()
					_on_finish_edit(element.text() ? '')
					return false
		return element

	render: () ->
		# @root.empty()
		@root.addClass 'page_render'
		focus = @root.find('*:focus')
		@prev_content = @root.children()
		@content = $('<div/>').addClass('page_content group').prependTo(@root)
		if @data.archived then @content.addClass 'sheet_archived'
		@_load_items () =>
			log 'Items loaded'
			@get(@template.name).render @data, @template, @content, {empty: false}, () =>
				log 'Fixing height'
				@fix_height () =>
					if focus.attr('option')
						@root.find('.text_editor[property='+focus.attr('property')+'][option='+focus.attr('option')+']').focus()
					else
						@root.find('.text_editor[property='+focus.attr('property')+'][item_id='+focus.attr('item_id')+']').focus()
					if @no_area.length>0 and not env.mobile
						no_area_div = $('<div/>').addClass('no_area_notes').appendTo(@root)
						for item in @no_area
							el = $('<div/>').addClass('list_item no_area_note').appendTo(no_area_div)
							el.data('type', 'note')
							el.data('renderer', @)
							el.data('item', item)
							el.draggable({zIndex: 4, containment: 'document', helper: 'clone', appendTo: 'body'})
							el.attr('title', item.text)
	
	fix_height: (handler) ->
		log 'Grow to', @max_height
		@get(@template.name).grow @max_height, @template, @content, {empty: true, disabled: true}
		@prev_content.remove()
		@root.removeClass 'page_render'
		if not env.mobile
			sleft = $('<div/>').addClass('page_scroll scroll_left').appendTo(@root)
			sleft.bind 'click', () =>
				@ui.scroll_sheets @data.id, -1
			sright = $('<div/>').addClass('page_scroll scroll_right').appendTo(@root)
			sright.bind 'click', () =>
				@ui.scroll_sheets @data.id, 1
			page_actions = $('<div/>').addClass('page_actions').appendTo(@root)
			archive_toggle = $('<div/>').addClass('page_action archive_toggle').appendTo(page_actions)
			archive_toggle.bind 'click', () =>
				@show_archived = not @show_archived
				@render null
		# $('<div/>').addClass('page_frame').appendTo(@root)
		if handler then handler null
	
	_load_items: (handler) ->
		areas = []
		find_areas = (item) ->
			if item.type is 'list' and item.area and item.area not in areas
				areas.push item.area
			if item.flow
				for fl in item.flow
					find_areas fl
		find_areas @template
		@manager.getNotes @data.id, null, (err, data) =>
			if err
				log 'Error getting items', err
				handler {}
			else
				@notes = {}
				@no_area = []
				for area in areas
					@notes[area] = []
				for item in data
					if !@show_archived and item.done is 1
						continue
					if @notes[item.area]
						@notes[item.area].push item
					else
						@no_area.push item
				handler @notes
	
	items: (area, handler) ->
		result = @notes[area] ? []
		if handler then handler result
		return result
	
	on_sheet_change: () ->


window.log = (params...) ->
	console.log.apply console, params

window.Renderer = Renderer

	
	# renderer = new Renderer manager, ui, $('#page0'), wobjective, {}
	# renderer.render null

	# renderer = new Renderer $('#page1'), w1, {},
	# 	dt: new Date().getTime()
	# renderer.render null
	# renderer = new Renderer $('#page2'), w2, {},
	# 	dt: new Date().getTime()
	# renderer.render null
	# $('button').button();
