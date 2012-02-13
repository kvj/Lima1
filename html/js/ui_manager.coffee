class Protocol
	constructor: () ->

	convert: (text, value) ->
		null

	accept: (config, value) ->
		no
	
	prepare: (config, value) ->

class ItemProtocol extends Protocol

	convert: (text, value) ->
		return text

class DateProtocol extends Protocol

	fromDateByType: (type) ->
		method = null
		switch type
			when 'e' then method = 'Day'
			when 'w' then method = 'Week'
			when 'd' then method = 'Date'
			when 'm' then method = 'Month'
			when 'y' then method = 'FullYear'
		method

	prepare: (config, value) ->
		if not config.dt
			dt = new Date()
			dt.fromString value.substr(value.indexOf(':')+1)
			config.dt = dt.format('yyyyMMdd')

	accept: (config, value) ->
		# log 'Check accept', value
		dt = new Date()
		dt.fromString value
		for type, value of config
			method = @fromDateByType type
			if not method then continue
			val = dt['get'+method]()
			if val in value or val is value
				continue
			else
				return no
		return {dt: dt.format('yyyyMMdd')}
	
	convert: (text, value) ->
		dt = new Date().fromString(value.dt)
		exp = ///^						#start
			(\(							#(
			(([ewmdy][+-]?[0-9]+)+)	#e1d+2m0
			\))?						#)
			([EwMdy/\:\.]*)				#ddmmyyyy
		$///
		m = text.match exp
		if not m then return text
		modifiers = m[2]
		format = if m[4] and m[4].length>0 then m[4] else 'yyyyMMdd'
		if modifiers
			mexp = /([ewmdy][+-]?[0-9]+)/g
			mm = modifiers.match mexp
			for item in mm
				mmm = item.match /([ewmdy])([+-]?)([0-9]+)/
				if mmm
					method = @fromDateByType mmm[1]
					val = parseInt mmm[3], 10
					if mmm[2]
						dt['set'+method](dt['get'+method]()+(if mmm[2] is '+' then val else -val))
					else
						dt['set'+method](val)
		# log 'Format', dt, format
		dt.format format

class PageNavigator

	colors: [
		[{color: '#556270', dark: yes}, {color: '#4ECDC4', dark: yes}, {color: '#C7F464', dark: no}, {color: '#FF6B6B', dark: yes}, {color: '#C44D58', dark: yes} ]
		[{color: '#D1F2A5', dark: no}, {color: '#EFFAB4', dark: no}, {color: '#FFC48C', dark: no}, {color: '#FF9F80', dark: no}, {color: '#F56991', dark: yes} ]
		[{color: '#8C2318', dark: yes}, {color: '#5E8C6A', dark: yes}, {color: '#88A65E', dark: yes}, {color: '#BFB35A', dark: no}, {color: '#F2C45A', dark: no} ]
		[{color: '#2A044A', dark: yes}, {color: '#0B2E59', dark: yes}, {color: '#0D6759', dark: yes}, {color: '#7AB317', dark: no}, {color: '#A0C55F', dark: no} ]
		[{color: '#3E4147', dark: yes}, {color: '#FFFEDF', dark: no}, {color: '#DFBA69', dark: no}, {color: '#5A2E2E', dark: yes}, {color: '#2A2C31', dark: yes} ]
	]

	constructor: (@manager, @ui) ->
		@root = $('#page_navigator')
		@bmarkDialog = $('#bmark_dialog')
		$('#new_bmark_button').bind 'click', () =>
			@edit_bookmark null
		$('#bmark_save_button').bind 'click', () =>
			@save_bookmark null
		

	load_sheets: () ->
		@manager.getPageNavigator (err, data, bmarks) =>
			if err then return @ui.show_error(err)
			@sheets = data
			@bookmarks = bmarks
			log 'PageNavigator sheets', data, bmarks, @root.height()
			@root.empty()
			missing = []
			bmarkMap = {}
			for bmark in @bookmarks
				if bmark.sheet_id
					bmarkMap[bmark.sheet_id] = bmark
				else
					missing.push bmark
			bmarkHeight = 20
			renderBookmark = (bmark) =>
				div = $('<div/>').addClass('nav_bmark').appendTo(@root)
				if bmark.sheet_id
					div.attr('id', 'pn'+bmark.sheet_id)
				div.height(bmarkHeight-2)
				div.bind 'dblclick', (e) =>
					@edit_bookmark bmark
					return false
				div.text bmark.name
				div.css('backgroundColor', bmark.color).css('color', if bmark.dark then '#ffffff' else '#000000')
				@root.append('<div class="clear"/>')
				div.data('type', 'bmark')
				div.data('item', bmark)
				div.draggable({
					zIndex: 3
					containment: 'document'
					helper: 'clone' 
					appendTo: 'body'
				})
				return div
			pages = 0
			if @sheets.length>0
				for i in [0...@sheets.length]
					sheet = @sheets[i]
					div = null
					if bmarkMap[sheet.id]
						div = renderBookmark bmarkMap[sheet.id]
						do (sheet) =>
							div.bind 'click', () =>
								@ui.scroll_sheets sheet.id
								return false
					else
						pages++
						div = $('<div/>').addClass('nav_sheet').appendTo(@root).attr('id', 'pn'+sheet.id)
						if i is 0
							div.addClass('nav_sheet_top')
						@root.append('<div class="clear"/>')
			
			for id, bmark in bmarkMap
				missing.push bmark
			for bmark in missing
				renderBookmark bmark
			if pages>0
				pageHeight = Math.floor((@root.innerHeight() + 3 - bmarkHeight*@bookmarks.length)/pages)
				@root.children('.nav_sheet').height(pageHeight-1)
			
	attach_bookmark: (bmark, sheet_id) ->
		log 'Attach', bmark, sheet_id
		bmark.sheet_id = sheet_id
		@manager.saveBookmark bmark, (err) =>
			if err then return @ui.show_error(err)
			@load_sheets null
	
	page_selected: (sheet_id) ->
		@root.children().removeClass('nav_page_selected').find('#pn'+sheet_id).addClass('nav_page_selected')
		@root.children('#pn'+sheet_id).addClass('nav_page_selected')

	edit_bookmark: (@bmark) ->
		if not @bmark
			@bmark = 
				name: 'New bookmark'
				dark: no
				color: '#eeeeee'
		@bmarkDialog.dialog {
			width: 400
			height: 300
		}
		applyColor = (color) =>
			log 'applyColor', color
			$('#color_select_example').css('backgroundColor', color.color).css('color', if color.dark then '#ffffff' else '#000000')
			@bmark.color = color.color
			@bmark.dark = color.dark

		applyColor @bmark
		$('#bmark_name').val @bmark.name
		$('#color_select').empty()
		for row in @colors
			rowItem = $('<div/>').appendTo($('#color_select')).addClass('clear')
			for col in row
				item = $('<div/>').appendTo(rowItem).addClass('color_select_item').css('backgroundColor', col.color)
				item.html '&nbsp;'
				do (col) =>
					item.bind 'click', () =>
						applyColor col
						no
			$('<div class="clear"/>').appendTo(rowItem)
	
	save_bookmark: () ->
		@bmark.name = $('#bmark_name').val()
		@manager.saveBookmark @bmark, (err) =>
			if err then return @ui.show_error(err)
			@bmarkDialog.dialog('close')
			@load_sheets null
	
	remove_bookmark: (bmark) ->
		@manager.removeBookmark bmark, (err) =>
			if err then return @ui.show_error(err)
			@load_sheets null


class ProtocolManager

	constructor: ->
		@protocols = {
			"dt": new DateProtocol null
			"@": new ItemProtocol null
		}

	get: (name) ->
		return @protocols[name]

	replace: (text, item) ->
		exp = ///^
			([a-z\@]+\:)					#protocol:
			([a-zA-Z0-9\s\(\)\+\-\_/\:\.]*)	#value
			$
		///
		if m = text.match exp
			value = ''
			for name, p of @protocols
				if name+':' is m[1]
					value = p.convert m[2], item
					break
			return value
		return text
	
	inject: (text, item) ->
		# log 'Inject', text, item
		exp = ///
			\$\{ 							#${
			([a-z\@]+\:)					#protocol:
			([a-zA-Z0-9\s\(\)\+\-\_/\:\.]*)	#value
			\}								#}
		///
		while m = text.match exp
			if not m then return text
			value = ''
			for name, p of @protocols
				if name+':' is m[1]
					value = p.convert m[2], item
					break
			text = text.replace m[0], (value ? '')
		return text

	open_link: (link, place, templates) ->
		log 'Open link', link
		exp = ///^
			([a-z\@]+)\:					#protocol:
			([a-zA-Z0-9\s\(\)\+\-\_/\:\.]*)	#value
			$
		///
		templates_found = []
		if m = link.match exp
			name = m[1]
			p = @protocols[name]
			if not p then return
			for id, tmpl of templates
				if tmpl.protocol and tmpl.protocol[name] and tmpl.code
					config = p.accept tmpl.protocol[name], m[2]
					if not config then continue
					code = @inject tmpl.code, config
					if code
						templates_found.push {code: code, template_id: id}
		log 'Templates found:', templates_found
		if templates_found.length is 0
			return ['No templates matching link', null, null]
		if templates_found.length is 1
			return [null, templates_found[0].code, templates_found[0].template_id]
		if templates_found.length > 1
			return ['Too many templates', null, null]


class UIManager

	archive_state: null

	constructor: (@manager, @oauth) ->
		@protocols = new ProtocolManager()
		# log 'Create UI...'
		$('button').button();
		$('#templates_button').bind 'click', () =>
			@edit_templates null
		
		$('#new_sheet_button').bind 'click', () =>
			@new_sheet null
		$('#new_sheet_button').droppable({
			accept: '.sheet',
			hoverClass: 'toolbar_drop',
			tolerance: 'pointer',
			drop: (event, ui) =>
				@new_sheet ui.draggable.data('item')
				event.preventDefault()
		})

		$('#templates').bind 'selectablestop', () =>
			@template_selected null

		$('#new_template_button').bind 'click', () =>
			@new_template null
		$('#reload_sheets_button').bind 'click', () =>
			@load_templates null
			@show_sheets null
		$('#remove_template').bind 'click', () =>
			@remove_template null
		$('#save_template').bind 'click', () =>
			@save_template null
		$('#trash').droppable({
			accept: '.list_item, .sheet, .nav_bmark',
			hoverClass: 'toolbar_drop',
			tolerance: 'pointer',
			drop: (event, ui) =>
				@remove_drop ui.draggable
				event.preventDefault()
		});
		$('#archive').droppable({
			accept: '.sheet',
			hoverClass: 'toolbar_drop',
			tolerance: 'pointer',
			drop: (event, ui) =>
				@archive_drop ui.draggable
				event.preventDefault()
		}).bind 'click', (event) =>
			@invert_archive null
		$('.page').droppable({
			accept: '.sheet, .nav_bmark',
			hoverClass: 'page_drop',
			tolerance: 'pointer',
			drop: (event, ui) =>
				item = ui.draggable.data('item')
				log 'Drop', event.target
				if ui.draggable.data('type') is 'bmark'
					@navigator.attach_bookmark item, $(event.target).data('sheet_id')
				else
					@show_page item.template_id, item, '#'+$(event.target).attr('id')
				event.preventDefault()
		});
		$('#calendar').datepicker({
			dateFormat: 'yymmdd',
			firstDay: 1,
			onSelect: (dt) =>
				@open_link 'dt:'+dt
				return false
		});
		@oauth.on_new_token = (token) =>
			# log 'Save token:', token
			@manager.set 'token', token
		@oauth.on_token_error = () =>
			# log 'Time to show dialog'
			@login null
		$('#sync_button').bind 'click', () =>
			@sync null
		$('#login_button').bind 'click', () =>
			@do_login null
		@manager.on_scheduled_sync = () =>
			@sync null
		# @oauth.tokenByUsernamePassword 'kostya', 'wellcome', (err) =>
		# 	log 'Auth result:', err
		@pages = parseInt(@manager.get 'pages', 2)
		$('#page_slider').slider {
			min: 1
			max: 4
			step: 1
			value: @pages
			slide: (event, ui) =>
				@show_pages ui.value
		}
		for i in [0...4]
			page = $("#page#{i}")
			# do (page) =>
			# 	page.bind 'mouseover', (event) =>
			# 		page.children('.page_scroll').show()
			# 	page.bind 'mouseout', (event) =>
			# 		page.children('.page_scroll').hide()
		@show_pages @pages
		@navigator = new PageNavigator @manager, @

	invert_archive: () ->
		if @archive_state is null
			@archive_state = 1
			$('#archive').text 'Unarchive'
		else
			@archive_state = null
			$('#archive').text 'Archive'
		@show_sheets null

	show_pages: (count) ->
		for i in [0...count]
			$("#page#{i}").show()
		for i in [count...4]
			$("#page#{i}").hide()
		@pages = count
		@manager.set 'pages', count

	do_login: () ->
		username = $('#username').val()
		password = $('#password').val()
		@oauth.tokenByUsernamePassword username, password, (err) =>
			log 'Auth result:', err
			if err then return @show_error err
			$('#login_dialog').dialog('close')
			@sync null

	sync: () ->
		$('#sync_button').text('Sync in progress...')
		@manager.sync @oauth, (err, sync_data) =>
			# log 'After sync', err, sync_data
			$('#sync_button').text('Sync')
			if err then return @show_error err
			sync_at = new Date().format('M/d h:mma')
			out_items = sync_data.out
			in_items = sync_data.in
			$('#sync_message').text "Sync done: #{sync_at} sent: #{out_items} received: #{in_items}"

	login: () ->
		$('#login_dialog').dialog({
			width: 300
			height: 200
			modal: true
		})
		$('#username').val('').focus()
		$('#password').val('')

	replace: (text, item) ->
		return @protocols.replace text, item
	
	inject: (txt, item) ->
		return @protocols.inject txt, item

	open_link: (link, place) ->
		[err, code, id] = @protocols.open_link link, place, @templates
		if err
			@show_error err
		else
			@open_sheet_by_code code, id

	open_sheet_by_code: (code, template_id) ->
		@manager.findSheet ['code', code], (err, data) =>
			if err then return @show_error err
			if data.length>0
				@show_page data[0].template_id, data[0]
			else
				@show_page template_id, {code: code}

	archive_drop: (drag) ->
		sheet = drag.data('item')
		sheet.archived = if @archive_state is 1 then null else 1
		@manager.saveSheet sheet, (err) =>
			if err then return @show_error err
			@show_sheets null

	remove_drop: (drag) ->
		# log 'Remove', drag.data('type')
		if drag.data('type') is 'note'
			drag.data('renderer').remove_note drag.data('item'), drag
		if drag.data('type') is 'bmark'
			@navigator.remove_bookmark drag.data('item')
		if drag.data('type') is 'sheet'
			@remove_sheet drag.data('item'), drag

	remove_sheet: (item, element) ->
		@manager.removeSheet item, (err) =>
			if err then return @show_error err
			$('.page').each (i, el) =>
				if $(el).data('sheet')?.id is item.id
					$(el).empty()
			@show_sheets null

	remove_template: () ->
		selected = $('#templates').children('.ui-selected')
		selected.each (index, item) =>
			last = selected.size()-1 is index
			do (last) =>
				@manager.removeTemplate $(item).data('template'), () =>
					if last
						@show_templates null
						@new_template null
						@load_templates null


	new_template: () ->
		selected = $('#templates').children().removeClass('ui-selected')
		@show_template {}
		@template_selected null

	show_template: (tmpl) ->
		@edit_template = tmpl
		$('#template_name').val(tmpl.name)
		$('#template_body').val(tmpl.body)

	start: ->
		@manager.open (err) =>
			if err
				$('#main').hide() 
				return @show_error err
			@load_templates null
			@show_sheets null
			@sync null

	save_template: () ->
		@edit_template.name = $('#template_name').val()
		@edit_template.body = $('#template_body').val()
		if not @edit_template.name
			return @show_error 'Name is required'
		try
			JSON.parse @edit_template.body
		catch e
			return @show_error 'Body isnt JSON'
		@manager.saveTemplate @edit_template, (err, object) =>
			if err then return @show_error err
			@show_templates object.id
			@load_templates null

	show_error: (message) ->
		log 'Error', message
		div = $('<div/>').addClass('message error_message').text(message ? 'Error!').appendTo($('#messages')).delay(5000).fadeOut()
		do (div) =>
			setTimeout () =>
				div.remove()
			, 7000

	template_selected: () ->
		selected = $('#templates').children('.ui-selected');
		log 'template_selected', selected.size()
		$('#remove_template').attr('disabled', if selected.size() is 0 then 'disabled' else no)
		$('#save_template').attr('disabled', if selected.size() > 1 then 'disabled' else no)
		if selected.size() > 0
			@show_template selected.last().data('template')

	show_templates: (select) ->
		ul = $('#templates').empty()
		@manager.getTemplates (err, data) =>
			if err then return @show_error(err)
			for item in data
				li = $('<li/>').appendTo(ul)
				li.text(item.name)
				li.data('template', item)
				if select and item.id is select
					li.addClass('ui-selected')
			ul.selectable()
		@template_selected null

	edit_templates: () ->
		log 'Show edit templates'
		$('#templates_dialog').dialog({width: 800, height: 600})
		@show_templates null
		@new_template null

	load_templates: () ->
		@manager.getTemplates (err, data) =>
			if err then return
			@templates = {}
			for item in data
				try
					@templates[item.id] = JSON.parse item.body
				catch e
					log 'JSON error', e

	scroll_sheets: (from, step = 0) =>
		# log 'scroll', from, step
		if not @sheets
			@show_error 'No data loaded'
			return
		for i in [0...@sheets_displayed.length]
			if @sheets_displayed[i].id is from
				i += step
				if i+@pages >= @sheets_displayed.length
					i = @sheets_displayed.length-@pages
				if i<0 then i = 0
				@navigator.page_selected @sheets_displayed[i].id
				for j in [i...Math.min(@pages+i, @sheets_displayed.length)]
					@show_page @sheets_displayed[j].template_id, @sheets_displayed[j], "#page#{j-i}"
				return

	show_page: (template_id, sheet, place = '#page0') ->
		log 'Show page', template_id, sheet, place
		if not @templates or not @templates[template_id]
			return @show_error 'No template found'
		template = @templates[template_id]
		if sheet.template_id and sheet.template_id isnt template_id
			sheet.template_id = template_id
			@manager.saveSheet sheet, (err, object) =>
				if err then return @ui.show_error err
		sheet.template_id = template_id
		config = {}
		if template.protocol and sheet.code
			for name, conf of template.protocol
				p = @protocols.get name
				if not p then continue
				p.prepare sheet, sheet.code
		$(place).data('sheet_id', sheet.id)
		renderer = new Renderer @manager, this, $(place), template, sheet, $(place).innerHeight()-50
		renderer.on_sheet_change = () =>
			@show_sheets null
		renderer.render null

	move_sheet: (item, before) ->
		# log 'Move sheet', item.title, 'before', before?.title
		to_save = @manager.sortArray @sheets, item, before
		# log 'After sort', item.place, to_save.length
		to_save.push item
		for i, itm of to_save
			last = parseInt(i) is to_save.length-1
			do (last) =>
				@manager.saveSheet itm, () =>
					if last
						@show_sheets null

	show_sheets: () ->
		ul = $('#sheets').empty()
		@manager.getSheets (err, data) =>
			if err then return @show_error(err)
			@sheets = data
			@sheets_displayed = []
			index = 0
			for item in data
				if (item.archived ? null) isnt @archive_state then continue
				@sheets_displayed.push item
				# log 'Show sheet', item.title, item.place
				li = $('<li/>').addClass('sheet').appendTo(ul)
				li.data('type', 'sheet')
				li.data('item', item)
				li.draggable({
					zIndex: 3
					containment: 'document'
					helper: 'clone' 
					appendTo: 'body'
				})
				do (item) =>
					li.droppable({
						accept: '.sheet',
						hoverClass: 'toolbar_drop',
						tolerance: 'pointer',
						drop: (event, ui) =>
							@move_sheet ui.draggable.data('item'), item
							event.preventDefault()
					})
				li.text(item.title)
				do (item) =>
					li.bind 'dblclick', () =>
						@scroll_sheets item.id
			li = $('<li/>').addClass('sheet last_sheet').appendTo(ul)
			li.droppable({
				accept: '.sheet',
				hoverClass: 'toolbar_drop',
				tolerance: 'pointer',
				drop: (event, ui) =>
					@move_sheet ui.draggable.data('item')
					event.preventDefault()
			})
		@navigator.load_sheets null

	new_sheet: (sheet) ->
		$('#new_sheet_dialog').dialog({width: 400, height: 200})
		ul = $('#new_sheet_templates').empty()
		@manager.getTemplates (err, data) =>
			if err then return @show_error(err)
			for item in data
				tmpl = @templates[item.id]
				if not tmpl
					continue
				if not tmpl.direct # not sheet and 
					continue
				li = $('<div/>').addClass('new_sheet_template').appendTo(ul)
				li.text(item.name)
				do (item) =>
					li.bind 'mousedown', (e) =>
						# e.stopPropagation()
						# e.stopImmediatePropagation()
						# e.preventDefault()
						return false
					li.bind 'dblclick', (e) =>
						$('#new_sheet_dialog').dialog('close')
						@show_page item.id, (sheet ? {})
						return false
			$('<div/>').addClass('clear').appendTo(ul)

window.UIManager = UIManager
window.ProtocolManager = ProtocolManager
window.PageNavigator = PageNavigator