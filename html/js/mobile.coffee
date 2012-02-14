env.mobile = yes
env.prefix = 'mobile_'

class MobileUIManager

	title: 'Lima1'
	pages: 1
	currentPage: 0
	close_delay: 500

	constructor: (@manager, @oauth) ->
		@protocols = new ProtocolManager()
		@oauth.on_new_token = (token) =>
			# log 'Save token:', token
			@manager.set 'token', token
		@oauth.on_token_error = () =>
			# log 'Time to show dialog'
			@login null
		@manager.on_scheduled_sync = () =>
			@sync null
		$('#do_login').bind 'click', () =>
			@do_login null
			return false
		$('#main_page_content').bind 'swipeleft', () =>
			@scroll_sheets 1
		.bind 'swiperight', () =>
			@scroll_sheets -1
		$('#sheets_left').bind 'click', () =>
			@scroll_sheets -1
		$('#sheets_right').bind 'click', () =>
			@scroll_sheets 1
		$('#sheets_sync').bind 'click', () =>
			@sync false
		$('#bmarks_toggle').bind 'click', () =>
			@navigator.root.toggle()
		@navigator = new PageNavigator @manager, this
		@navigator.left = yes
		@navigator.auto_hide = yes
		@_title @title

	_title: (title) ->
		$('#main_title').text(title)

	do_login: () ->
		username = $('#username').val()
		password = $('#password').val()
		@oauth.tokenByUsernamePassword username, password, (err) =>
			log 'Auth result:', err
			if err then return @show_error err.error_description ? err
			$('#login_dialog').dialog('close')
			@sync yes

	login: () ->
		log 'Need login'
		$.mobile.changePage($('#login_dialog'))
		$('#password').text('')
		$('#username').focus().text('')

	sync: (load_sheets) ->
		log 'Need show sync'
		@_title 'Sync...'
		@manager.sync @oauth, (err, sync_data) =>
			@_title @title
			if err then return @show_error err
			log 'Sync done', load_sheets
			sync_at = new Date().format('M/d h:mma')
			out_items = sync_data.out
			in_items = sync_data.in
			@show_error "Sync done: #{sync_at} sent: #{out_items} received: #{in_items}"
			@load_sheets load_sheets

	start: ->
		@manager.open (err) =>
			if err
				return @show_error err
			# @sync no
			@navigator.root.height(@_page_height()+10)
			$('.page_mobile').css('min-height', @_page_height()+10)
			@load_sheets yes

	show_error: (err) ->
		$('#info_dialog').show().text(err ? '???').fadeOut(3000)

	load_sheets: (show_sheets) ->
		@manager.getPageNavigator (err, data, bmarks) =>
			if err then return @show_error err
			@sheets = data
			@navigator.show_sheets data, bmarks
			if data.length>0 and show_sheets
				@show_sheets 0

	find_scroll_sheets: (id) ->
		for i, item of @sheets
			i = parseInt i
			if item.id is id
				@currentPage = i
				return @scroll_sheets 0
		@show_error 'Page not found'

	scroll_sheets: (inc) ->
		index = @currentPage + inc*@pages
		if index>=@sheets.length-@pages
			index = @sheets.length-@pages
		if index<0
			index = 0
		@show_sheets index

	show_sheets: (index) ->
		if index<0 or index>=@sheets.length
			log 'Index too few'
			return
		@currentPage = index
		sheet = @sheets[index]
		# log 'Show sheet', sheet, index
		@show_sheet sheet.template_id, sheet
		
	_page_height: () ->
		return window.innerHeight - $('#main_page_header').outerHeight() - $('#main_page_footer').outerHeight() - 40

	show_sheet: (template_id, sheet, place = '#page0') ->
		log 'Show page', template_id, sheet, place
		@manager.findTemplate template_id, (err, template) =>
			if err then return @show_error err
			if sheet.template_id and sheet.template_id isnt template_id
				sheet.template_id = template_id
				@manager.saveSheet sheet, (err, object) =>
					if err then return @show_error err
			sheet.template_id = template_id
			config = {}
			if template.protocol and sheet.code
				for name, conf of template.protocol
					p = @protocols.get name
					if not p then continue
					p.prepare sheet, sheet.code
			$(place).data('sheet_id', sheet.id)
			log 'Height:', screen.height, window.outerHeight, window.innerHeight, screen.availHeight
			height = @_page_height()
			renderer = new Renderer @manager, this, $(place), template, sheet, height
			renderer.on_sheet_change = () =>
				@load_sheets no
			renderer.render null

	replace: (text, item) ->
		return @protocols.replace text, item
	
	inject: (txt, item) ->
		return @protocols.inject txt, item

	open_link: (link, place) ->
		@manager.getTemplates (err, data) =>
			if err then return @show_error err
			templates = {}
			for index, obj of data
				templates[obj.id] = JSON.parse(obj.body)
			 
			[err, code, id] = @protocols.open_link link, place, templates
			if err
				@show_error err
			else
				@open_sheet_by_code code, id

	open_sheet_by_code: (code, template_id) ->
		log 'open_sheet_by_code', code, template_id
		@manager.findSheet ['code', code], (err, data) =>
			if err then return @show_error err
			if data.length>0
				@show_sheet data[0].template_id, data[0]
			else
				@show_sheet template_id, {code: code}

	time_dialog: (hour, min, handler) =>
		$.mobile.changePage($('#time_dialog'))
		$('#hour_slide').val(hour).slider('refresh')
		$('#min_slide').val(min).slider('refresh')
		$('#time_save').unbind('click').bind 'click', () =>
			$('#time_dialog').dialog('close')
			setTimeout () =>
				handler yes, $('#hour_slide').val(), $('#min_slide').val()
			, @close_delay
		$('#time_remove').unbind('click').bind 'click', () =>
			$('#time_dialog').dialog('close')
			setTimeout () =>
				handler no
			, @close_delay

	date_dialog: (dt, handler) =>
		log 'show date dialog', dt
		$.mobile.changePage($('#date_dialog'))
		$('#date_day').val(dt.getDate()).selectmenu('refresh')
		$('#date_month').val(dt.getMonth()).selectmenu('refresh')
		$('#date_year').val(dt.getFullYear()).selectmenu('refresh')
		$('#date_save').unbind('click').bind 'click', () =>
			$('#date_dialog').dialog('close')
			setTimeout () =>
				result = new Date()
				result.setFullYear $('#date_year').val()
				result.setMonth $('#date_month').val()
				result.setDate $('#date_day').val()
				handler yes, result
			, @close_delay
		$('#date_remove').unbind('click').bind 'click', () =>
			$('#date_dialog').dialog('close')
			setTimeout () =>
				handler no
			, @close_delay

window.MobileUIManager = MobileUIManager