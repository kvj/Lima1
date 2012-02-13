env.mobile = yes
env.prefix = 'mobile_'

class MobileUIManager

	title: 'Lima1'
	pages: 1
	currentPage: 0

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
			if load_sheets then @load_sheets yes

	start: ->
		@manager.open (err) =>
			if err
				return @show_error err
			# @sync no
			@load_sheets yes

	show_error: (err) ->
		$('#info_dialog').show().text(err ? '???').fadeOut(3000)

	load_sheets: (show_sheets) ->
		@manager.getPageNavigator (err, data) =>
			if err then return @show_error err
			@sheets = data
			log 'load_sheets', show_sheets, @sheets
			if data.length>0 and show_sheets
				@show_sheets 0

	scroll_sheets: (inc) ->
		log 'scroll_sheets', inc, @pages, @currentPage
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
			height = $('#main_page').outerHeight() - $('#main_page_header').outerHeight() - $('#main_page_footer').outerHeight() - 40
			renderer = new Renderer @manager, this, $(place), template, sheet, height
			renderer.on_sheet_change = () =>
				@load_sheets no
			renderer.render null

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
				@show_sheet data[0].template_id, data[0]
			else
				@show_sheet template_id, {code: code}

window.MobileUIManager = MobileUIManager