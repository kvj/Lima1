w1 = {
	code: 'w1${dt:(e1)ddmmyyyy}',
	protocol: {
		dt: {
			'e': [1, 2, 3]
		}
	},
	defaults: {
		title: '${dt:(e1)yyyy}: ${dt:(e1)MM/dd} - ${dt:(e7)MM/dd}',
		dt: '${dt:(e1)}',
	},
	flow: [
		{type: 'title', name: 'Week ${dt:ww}/${dt:yyyy}', edit:'@:title'},
		{type: 'hr'},
		{type: 'title1', name: '${dt:(e1)dd} Monday'},
		{type: 'cols', size: [0.6, 0.4], flow: [
			{
				type: 'list', 
				area: 'd1',
				defaults: {due: '${dt:(e1)}'},
				flow: [
					{type: 'text', edit: '@:text'}
				]
			}, {
				flow: [
					{
						type: 'list', 
						area: 't1',
						defaults: {due: '${dt:(e1)}'},
						flow: [
							{type: 'cols', size: [0.1, 0.9], flow: [
								{type: 'check', edit: '@:done'},
								{type: 'text', edit: '@:text'}
							]},
						]
					}, {
						type: 'list',
						area: 'n1',
						edit: '@:text',
						flow: [
							{type: 'text', edit: '@:text'}
						]
					}
				]
			}
		]}
	]
}

class UIElement
	constructor: (@renderer) ->

	child: (element, index) ->
		return element.children().eq(index).get(0)

	render: (item, config, element, empty, handler) ->
		handler

class SimpleElement extends UIElement

	name: 'simple'

	render: (item, config, element, empty, handler) ->
		if config.defaults and not empty
			@renderer.applyDefaults config.defaults, item
			log 'After apply item', item
		flow = config.flow ? []
		for i, fl of flow
			do (i) =>
				# log i, fl
				el = @child(element, i) ? $('<div/>').appendTo(element).width(element.width())
				@renderer.get(fl.type).render item, fl, el, empty, () =>
					if i is flow.length
						handler

class TitleElement extends UIElement

	name: 'title'

	render: (item, config, element, empty, handler) ->
		if empty then return handler
		element.empty()
		$('<span/>').addClass('title0_text').appendTo(element).text(@renderer.inject config.name ? ' ')
		$('<span/>').addClass('title0_editor').attr('contentEditable', true).appendTo(element).text(@renderer.inject config.edit, item)
		handler

class HRElement extends UIElement

	name: 'hr'

	render: (item, config, element, empty, handler) ->
		if empty then return handler
		$('<hr/>').addClass('hr').appendTo(element.empty())
		handler

class Title1Element extends UIElement

	name: 'title1'

	render: (item, config, element, empty, handler) ->
		if empty then return handler
		$('<span/>').addClass('title1').appendTo(element.empty()).text(@renderer.inject config.name, item)
		handler

class ColsElement extends UIElement

	name: 'cols'

	render: (item, config, element, empty, handler) ->
		flow = config.flow ? []
		sizes = config.size ? []
		log flow, sizes
		if flow.length isnt sizes.length then return handler
		w = element.width()
		margin = 0
		for i, fl of flow
			width = Math.floor sizes[i]*w
			el = @child(element, i) ? $('<div/>').css('float', 'left').css('margin-left', margin).appendTo(element).width(width)
			margin += width
			do (i) =>
				@renderer.get(fl.type).render item, fl, el, empty, () =>
					if i is flow.length
						$('<div style="clear: both;"/>').appendTo(element)
						handler

class ListElement extends UIElement

	name: 'list'

	_render: (item, config, element, handler)->
		handler

	render: (item, config, element, empty, handler) ->
		flow = config.flow ? []
		if empty
			@_render {}, config, null
		for i, fl of flow
			el = @child(element, i) ? $('<div/>').appendTo(element)
			do (i) =>
				@renderer.get(fl.type).render item, fl, el, empty, () =>
					if i is flow.length
						handler



class Renderer
	constructor: (@root, @data, @env) ->
		@elements = [new SimpleElement this
			new TitleElement this
			new HRElement this
			new Title1Element this
			new ColsElement this
		]

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

	inject: (text, item) ->
		return inject text, @env

	render: (template) ->
		@root.empty
		content = $('<div/>').addClass('page_content').appendTo(@root)
		@get(template.name).render @data, template, content, false, () =>
			log 'Render done'
	
	items: (area, handler) ->
		handler([
			{text: 'Text 01'}
			{text: 'Text 02', done: true}
			{text: 'Text 03'}
		])


class Protocol
	constructor: (@name) ->

	convert: (text, value) ->
		null

	accept: (config, value) ->
		no

class DateProtocol extends Protocol

	convert: (text, value) ->
		dt = new Date value.dt #from int
		exp = ///^						#start
			(\(							#(
			(([ewmdy][+-]?[0-9]+)+)	#e1d+2m0
			\))?						#)
			([EwMdy/\:\.]*)				#ddmmyyyy
		$///
		m = text.match exp
		if not m then return text
		modifiers = m[2]
		format = if m[4] and m[4].length>0 then m[4] else 'ddmmyyyy'
		if modifiers
			mexp = /([ewmdy][+-]?[0-9]+)/g
			mm = modifiers.match mexp
			for item in mm
				mmm = item.match /([ewmdy])([+-]?)([0-9]+)/
				if mmm
					method = ''
					switch mmm[1]
						when 'e' then method = 'Day'
						when 'w' then method = 'Week'
						when 'd' then method = 'Date'
						when 'm' then method = 'Month'
						when 'y' then method = 'FullYear'
					val = parseInt mmm[3]
					if mmm[2]
						dt['set'+method](dt['get'+method]()+(if mmm[2] is '+' ? val else -val))
					else
						dt['set'+method](val)
		# log 'Format', dt, format
		dt.format format

inject = (text, params) ->
	protocols = [new DateProtocol 'dt']
	exp = ///
		\$\{ 			#${
		([a-z]+\:)		#protocol:
		([a-zA-Z0-9\s\(\)\+\-\_/\:\.]*)	#value
		\}				#}
	///
	while m = text.match exp
		# log 'inject', m, m.length
		if not m then return text
		value = ''
		for p in protocols
			if p.name+':' is m[1]
				value = p.convert m[2], params
				break
		# log 'Value', value
		text = text.replace m[0], value
	text



log = (params...) ->
	console.log.apply console, params

$(document).ready () ->
	log inject 'Week ${dt:ww}/${dt:yyyy} ${dt:(e1)yyyy}: ${dt:(e1)MM/dd} - ${dt:(e7)MM/dd}'
		dt: new Date().getTime()
	renderer = new Renderer $('#page'),
		{},
		dt: new Date().getTime()
	renderer.render w1, 
	$('#test').bind 'keypress', (event) ->
		if event.keyCode is 13
			$('#test').text($('#test').text());