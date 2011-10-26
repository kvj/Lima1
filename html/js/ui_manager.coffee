class UIManager
	constructor: (@manager) ->
		log 'Create UI...'
		$('button').button();
		$('#templates_button').bind 'click', () =>
			@edit_templates null
		
		$('#new_sheet_button').bind 'click', () =>
			@new_sheet null

		$('#templates').bind 'selectablestop', () =>
			@template_selected null

		$('#new_template_button').bind 'click', () =>
			@new_template null
		$('#remove_template').bind 'click', () =>
			@remove_template null
		$('#save_template').bind 'click', () =>
			@save_template null
		$('#trash').droppable({
			accept: '.list_item, .sheet',
			hoverClass: 'trash_drop',
			tolerance: 'pointer',
			drop: (event, ui) =>
				@remove_drop ui.draggable
				event.preventDefault()
		});
		$('.page').droppable({
			accept: '.sheet',
			hoverClass: 'trash_drop',
			tolerance: 'pointer',
			drop: (event, ui) =>
				item = ui.draggable.data('item')
				log 'Drop', event.target
				@show_page item.template_id, item, '#'+$(event.target).attr('id')
				event.preventDefault()
		})

	remove_drop: (drag) ->
		log 'Remove', drag.data('type')
		if drag.data('type') is 'note'
			drag.data('renderer').remove_note drag.data('item'), drag
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
		alert message

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

	show_page: (template_id, sheet, place = '#page0') ->
		if not @templates or not @templates[template_id]
			return @show_error 'No template found'
		sheet.template_id = template_id
		renderer = new Renderer @manager, this, $(place), @templates[template_id], sheet, {}
		renderer.on_sheet_change = () =>
			@show_sheets null
		renderer.render null

	show_sheets: () ->
		ul = $('#sheets').empty()
		@manager.getSheets (err, data) =>
			if err then return @show_error(err)
			for item in data
				li = $('<li/>').addClass('sheet').appendTo(ul)
				li.data('type', 'sheet')
				li.data('item', item)
				# li.sortable({revert: true, zIndex: 3, containment: 'document', helper: 'clone'})
				li.text(item.title)
				do (item) =>
					li.bind 'dblclick', () =>
						@show_page item.template_id, item
			ul.sortable({zIndex: 3, containment: 'document', helper: 'clone', appendTo: 'body'})

	new_sheet: () ->
		log 'Show new sheet dialog'
		$('#new_sheet_dialog').dialog({width: 400, height: 200})
		ul = $('#new_sheet_templates').empty()
		@manager.getTemplates (err, data) =>
			if err then return @show_error(err)
			for item in data
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
						@show_page item.id, {}
						return false
			$('<div/>').addClass('clear').appendTo(ul)

window.UIManager = UIManager