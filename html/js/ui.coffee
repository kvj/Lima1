w2 = {
	"defaults": {
		"title": "Actions"
	},
	"direct": true,
	"flow": [
		{"type": "title", "name": "Actions", "edit":"@:title"},
		{"type": "hr"},
		{
			"type": "list",
			"grid": 1,
			"area": "main",
			"config": {
				"grid": 1,
				"delimiter": 1
			},
			"flow": [
				{"type": "cols", "size": [20, 1, 35], "flow": [
					{"type": "check", "edit": "@:done"},
					{"type": "text", "edit": "@:text"},
					{"type": "date", "edit": "@:due"}
				]},
				{"type": "cols", "size": [15, 1], "flow": [
					{"type": "mark", "edit": "@:mark"},
					{"type": "text", "edit": "@:notes"}
				]}
			]
		}
	]	
}

wnotes = {
	"defaults": {
		"title": "Notes"
	},
	"direct": true,
	"flow": [
		{"type": "title", "name": "Notes", "edit":"@:title"},
		{"type": "hr"},
		{
			"type": "list",
			"grid": 2,
			"delimiter": 2,
			"area": "main",
			"flow": [
				{"type": "text", "edit": "@:text"}
			]
		}
	]	
}

w1 = {
	"code": "w13:${dt:(e1)}",
	"protocol": {
		"dt": {
			"e": [1, 2, 3]
		}
	},
	"defaults": {
		"title": "${dt:(e1)yyyy}: ${dt:(e1)MM/dd} - ${dt:(e3)MM/dd}",
		"dt": "${dt:(e1)}"
	},
	"flow": [
		{"type": "title", "name": "Week ${dt:ww}/${dt:yyyy}", "edit":"@:title"},
		{"type": "hr"},
		{"type": "title1", "name": "${dt:(e1)dd} Monday"},
		{"type": "cols", "size": [0.5, 0.5], "space": 10, "flow": [
			{
				"type": "list", 
				"area": "d1",
				"drag": "right",
				"grid": 1,
				"delimiter": 1,
				"defaults": {"due": "${dt:(e1)}"},
				"flow": [
					{"type": "text", "edit": "@:text"}
				]
			}, {
				"flow": [
					{
						"type": "list", 
						"area": "t1",
						"grid": 1,
						"delimiter": 1,
						"defaults": {"due": "${dt:(e1)}"},
						"flow": [
							{"type": "cols", "size": [15, 1], "flow": [
								{"type": "check", "edit": "@:done"},
								{"type": "text", "edit": "@:text"}
							]}
						]
					}, {
						"type": "list",
						"area": "n1",
						"grid": 2,
						"delimiter": 2,
						"flow": [
							{"type": "text", "edit": "@:text"}
						]
					}
				]
			}
		]},
		{"type": "title1", "name": "${dt:(e2)dd} Tuesday"},
		{"type": "cols", "size": [0.5, 0.5], "space": 10, "flow": [
			{
				"type": "list", 
				"area": "d2",
				"drag": "right",
				"grid": 1,
				"delimiter": 1,
				"defaults": {"due": "${dt:(e2)}"},
				"flow": [
					{"type": "text", "edit": "@:text"}
				]
			}, {
				"flow": [
					{
						"type": "list", 
						"area": "t2",
						"grid": 1,
						"delimiter": 1,
						"defaults": {"due": "${dt:(e2)}"},
						"flow": [
							{"type": "cols", "size": [15, 1], "flow": [
								{"type": "check", "edit": "@:done"},
								{"type": "text", "edit": "@:text"}
							]}
						]
					}, {
						"type": "list",
						"area": "n2",
						"grid": 2,
						"delimiter": 2,
						"flow": [
							{"type": "text", "edit": "@:text"}
						]
					}
				]
			}
		]},
		{"type": "title1", "name": "${dt:(e3)dd} Wednesday"},
		{"type": "cols", "size": [0.5, 0.5], "space": 10, "flow": [
			{
				"type": "list", 
				"area": "d3",
				"drag": "right",
				"grid": 1,
				"delimiter": 1,
				"defaults": {"due": "${dt:(e3)}"},
				"flow": [
					{"type": "text", "edit": "@:text"}
				]
			}, {
				"flow": [
					{
						"type": "list", 
						"area": "t3",
						"grid": 1,
						"delimiter": 1,
						"defaults": {"due": "${dt:(e3)}"},
						"flow": [
							{"type": "cols", "size": [15, 1], "flow": [
								{"type": "check", "edit": "@:done"},
								{"type": "text", "edit": "@:text"}
							]}
						]
					}, {
						"type": "list",
						"area": "n3",
						"grid": 2,
						"delimiter": 2,
						"flow": [
							{"type": "text", "edit": "@:text"}
						]
					}
				]
			}
		]}
	]
}

w47 = {
	"code": "w47:${dt:(e1)}",
	"protocol": {
		"dt": {
			"e": [4, 5, 6, 0]
		}
	},
	"defaults": {
		"title": "${dt:(e4)yyyy}: ${dt:(e4)MM/dd} - ${dt:(e7)MM/dd}",
		"dt": "${dt:(e4)}"
	},
	"flow": [
		{"type": "title", "name": "Week ${dt:ww}/${dt:yyyy}", "edit":"@:title"},
		{"type": "hr"},
		{"type": "title1", "name": "${dt:(e4)dd} Thursday"},
		{"type": "cols", "size": [0.5, 0.5], "space": 10, "flow": [
			{
				"type": "list", 
				"area": "d1",
				"drag": "right",
				"grid": 1,
				"delimiter": 1,
				"defaults": {"due": "${dt:(e4)}"},
				"flow": [
					{"type": "text", "edit": "@:text"}
				]
			}, {
				"flow": [
					{
						"type": "list", 
						"area": "t1",
						"grid": 1,
						"delimiter": 1,
						"defaults": {"due": "${dt:(e4)}"},
						"flow": [
							{"type": "cols", "size": [15, 1], "flow": [
								{"type": "check", "edit": "@:done"},
								{"type": "text", "edit": "@:text"}
							]}
						]
					}, {
						"type": "list",
						"area": "n1",
						"grid": 2,
						"delimiter": 2,
						"flow": [
							{"type": "text", "edit": "@:text"}
						]
					}
				]
			}
		]},
		{"type": "title1", "name": "${dt:(e5)dd} Friday"},
		{"type": "cols", "size": [0.5, 0.5], "space": 10, "flow": [
			{
				"type": "list", 
				"area": "d2",
				"drag": "right",
				"grid": 1,
				"delimiter": 1,
				"defaults": {"due": "${dt:(e5)}"},
				"flow": [
					{"type": "text", "edit": "@:text"}
				]
			}, {
				"flow": [
					{
						"type": "list", 
						"area": "t2",
						"grid": 1,
						"delimiter": 1,
						"defaults": {"due": "${dt:(e5)}"},
						"flow": [
							{"type": "cols", "size": [15, 1], "flow": [
								{"type": "check", "edit": "@:done"},
								{"type": "text", "edit": "@:text"}
							]}
						]
					}, {
						"type": "list",
						"area": "n2",
						"grid": 2,
						"delimiter": 2,
						"flow": [
							{"type": "text", "edit": "@:text"}
						]
					}
				]
			}
		]},
		{"type": "title1", "name": "${dt:(e6)dd}, ${dt:(e7)dd} Weekend"},
		{"type": "cols", "size": [0.5, 0.5], "space": 10, "flow": [
			{
				"type": "list", 
				"area": "d3",
				"drag": "right",
				"grid": 1,
				"delimiter": 1,
				"flow": [
					{"type": "text", "edit": "@:text"}
				]
			}, {
				"flow": [
					{
						"type": "list", 
						"area": "t3",
						"grid": 1,
						"delimiter": 1,
						"flow": [
							{"type": "cols", "size": [15, 1], "flow": [
								{"type": "check", "edit": "@:done"},
								{"type": "text", "edit": "@:text"}
							]}
						]
					}, {
						"type": "list",
						"area": "n3",
						"grid": 2,
						"delimiter": 2,
						"flow": [
							{"type": "text", "edit": "@:text"}
						]
					}
				]
			}
		]}
	]
}

class UIElement
	constructor: (@renderer) ->

	child: (element, cl, index) ->
		child = element.children(cl).eq(index)
		if child.size() is 1 then child else null

	render: (item, config, element, options, handler) ->
		handler null

class SimpleElement extends UIElement

	name: 'simple'

	render: (item, config, element, options, handler) ->
		if config.defaults and not options.empty
			@renderer.applyDefaults config.defaults, item
		flow = config.flow ? []
		for i, fl of flow
			i = parseInt(i)
			do (i) =>
				# log i, fl
				el = @child(element, '.simple', i) ? $('<div/>').addClass('simple').appendTo(element)
				@renderer.get(fl.type).render item, fl, el, options, () =>
					if i is flow.length-1
						@renderer.fix_grid element, if config.type is 'list' then config.config else config
						handler null

class TitleElement extends UIElement

	name: 'title'

	render: (item, config, element, options, handler) ->
		if options.empty then return handler
		$('<span/>').addClass('title0_text').appendTo(element).text(@renderer.inject config.name ? ' ')
		if config.edit
			el = $('<span/>').addClass('text_editor title0_editor').appendTo(element)
			@renderer.text_editor el, item, (@renderer.replace config.edit)
		handler null

class Textlement extends UIElement

	name: 'text'

	render: (item, config, element, options, handler) ->
		if options.empty then return handler
		el = $('<div/>').addClass('text_editor').appendTo(element)
		if config.edit and not options.readonly
			@renderer.text_editor el, item, (@renderer.replace config.edit)
		handler null

class CheckElement extends UIElement

	name: 'check'

	render: (item, config, element, options, handler) ->
		if options.empty then return handler
		el = $('<div/>').addClass('check_editor').appendTo(element)
		property = (@renderer.replace config.edit)
		checked = no
		if property and item[property] is 1
			checked = yes
			el.addClass('checked')
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
		if options.empty then return handler
		el = $('<div/>').addClass('date_editor').appendTo(element)
		property = (@renderer.replace config.edit)
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

class MarkElement extends UIElement

	name: 'mark'

	render: (item, config, element, options, handler) ->
		if options.empty then return handler
		$('<div/>').addClass('mark_editor').appendTo(element)
		handler null

class HRElement extends UIElement

	name: 'hr'

	render: (item, config, element, options, handler) ->
		if options.empty then return handler
		$('<div/>').addClass('hr').appendTo(element)
		handler null

class Title1Element extends UIElement

	name: 'title1'

	render: (item, config, element, options, handler) ->
		if options.empty then return handler
		bg = $('<div/>').addClass('title1_bg').appendTo(element)
		$('<div/>').addClass('title1').appendTo(bg).text(@renderer.inject config.name, item)
		$('<div style="clear: both;"/>').appendTo(element)
		handler null

class ColsElement extends UIElement

	name: 'cols'

	render: (item, config, element, options, handler) ->
		flow = config.flow ? []
		sizes = config.size ? []
		if flow.length isnt sizes.length then return handler

		w = element.innerWidth()-4
		fixed_size = 0
		if config.space
			fixed_size += config.space*(flow.length-1)
		float_size = 0
		lsizes = []
		for sz in sizes
			lsizes.push sz
			if sz>1
				fixed_size += sz
			else
				float_size += sz
		if float_size>0
			for i, sz of lsizes
				if sz<=1
					lsizes[i] = Math.floor((w-fixed_size)*sz/float_size)
		margin = 0
		if not options.empty
			element.addClass('group')
		index = 0
		for i, fl of flow
			i = parseInt(i)
			if i>0 and config.space
				index++
				if not options.empty
					$('<div/>').appendTo(element).addClass('col').width(config.space).html('&nbsp;')
			width = lsizes[i]
			el = @child(element, '.col', index) ? $('<div/>').addClass('col').appendTo(element).width(width)
			if i is flow.length-1
				if not options.empty
					$('<div style="clear: both;"/>').appendTo(element)
			index++
			margin += width
			do (i) =>
				@renderer.get(fl.type).render item, fl, el, options, () =>
					if i is flow.length-1
						handler null

class ListElement extends UIElement

	name: 'list'

	_render: (item, config, element, options, handler)->
		el = $('<div/>').addClass('list_item').appendTo(element)
		if options.disable
			el.addClass('disabled')
		if options.draggable
			handle = $('<div/>').addClass('list_item_handle').appendTo(el)
			if config.drag is 'right'
				handle.addClass('list_item_handle_right')
			else
				handle.addClass('list_item_handle_left')
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
		# if not empty
			# el.bind 'click', (ev) =>
			# 	log 'Click!'
			# 	# el.draggable('destroy')
			# 	# el.click(ev)
			# el.draggable({revert: true})
		@renderer.get('simple').render item, config, el, {empty: false, readonly: options.disable}, () =>
			handler el

	_fill_empty: (config, element, parent, handler) ->
		parent_height = parent.outerHeight()
		# log 'Before fill empty', parent_height, parent.outerHeight(), parent
		@_render {area: config.area}, config, element, {disable: true}, (el) =>
			need_grid = yes
			# log 'After fill empty', parent.outerHeight(), parent_height
			if parent.outerHeight()<=parent_height 
				@renderer.have_space = yes
			else
				if @renderer.size_too_big()
					need_grid = no
					el.remove()
				else
					@renderer.have_space = yes
			if need_grid
				@renderer.fix_grid element, config
			handler null

	render: (item, config, element, options, handler) ->
		# log 'list', config.area, empty
		flow = config.flow ? []
		if options.empty
			parent = element.parents('.group').last()
			return @_fill_empty config, element, parent, handler
		@renderer.items config.area, (items) =>
			for i, itm of items
				# log 'Render', i, itm.text, itm.place
				@_render itm, config, element, {disable: false, draggable: true}, () =>
			@_render {area: config.area}, config, element, {disable: false}, () =>
				@renderer.fix_grid element, config
				handler null



class Renderer
	constructor: (@manager, @ui, @root, @template, @data, @env) ->
		@elements = [
			new SimpleElement this
			new TitleElement this
			new HRElement this
			new Title1Element this
			new ColsElement this
			new ListElement this
			new Textlement this
			new CheckElement this
			new MarkElement this
			new DateElement this
		]
		@root.data('sheet', @data)

	fix_grid: (element, config) ->
		if not config or not config.grid
			return
		gr = config.grid
		element.children(':not(.list_item_handle)').removeClass('grid_top'+gr+' grid_bottom'+gr).addClass('grid'+gr)
		if config.delimiter
			element.children(':not(.list_item_handle)').addClass('grid_delimiter'+config.delimiter)
		element.children(':not(.list_item_handle)').first().addClass('grid_top'+gr)
		element.children(':not(.list_item_handle)').last().addClass('grid_bottom'+gr)

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
			item[key] ?= @inject value, @env
				# log 'After inject', item[key]

	inject: (txt, item) ->
		return @ui.inject txt, item, @env

	replace: (text, item) ->
		return @ui.replace text, item, @env

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
			return
		element.attr('contentEditable', true)
		old_value = item[property] ? ''
		element.text(old_value)
		element.bind 'keypress', (event) =>
			if event.keyCode is 13
				element.blur()
				event.preventDefault()
				return false
		element.bind 'blur', (event) =>
			value = element.text() ? ''
			element.text value
			if value is old_value
				return
			if handler
				handler item, property, value
			else
				@on_edited item, property, value

	render: () ->
		# @root.empty()
		@root.addClass 'page_render'
		@prev_content = @root.children()
		@content = $('<div/>').addClass('page_content group').prependTo(@root)
		@_load_items (data) =>
			@notes = data
			@get(@template.name).render @data, @template, @content, {empty: false}, () =>
				@fix_height null
	
	size_too_big: () ->
		return @root.innerHeight()<@content.outerHeight(true)

	fix_height: () ->
		@have_space = no
		@get(@template.name).render @data, @template, @content, {empty: true}, () =>
			# log 'Fix done', @have_space
			if @have_space 
				@fix_height null
			else
				@prev_content.remove()
				@root.removeClass 'page_render'
	
	_load_items: (handler) ->
		@manager.getNotes @data.id, null, (err, data) =>
			if err
				log 'Error getting items', err
				handler []
			else
				handler data
	
	items: (area, handler) ->
		result = []
		for item in @notes
			if item.area is area
				result.push item
		if handler then handler result
		return result
		# @manager.getNotes @data.id, area, (err, data) =>
		# 	if err
		# 		log 'Error getting items', err
		# 		handler []
		# 	else
		# 		handler data
	
	on_sheet_change: () ->


window.log = (params...) ->
	console.log.apply console, params

window.Renderer = Renderer

$(document).ready () ->
	db = new HTML5Provider 'test.db', '1.1'
	storage = new StorageProvider null, db
	manager = new DataManager storage
	ui = new UIManager manager
	ui.start null
	# renderer = new Renderer $('#page1'), w1, {},
	# 	dt: new Date().getTime()
	# renderer.render null
	# renderer = new Renderer $('#page2'), w2, {},
	# 	dt: new Date().getTime()
	# renderer.render null
	# $('button').button();
