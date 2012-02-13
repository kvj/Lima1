class NetTransport

	constructor: (@uri) ->
	
	request: (config, handler) -> 
		handler 'Not implemented'

class jQueryTransport extends NetTransport

	request: (config, handler) -> 
		# log 'Doing request', @uri, config?.uri, config?.type
		$.ajax({
			url: @uri+config?.uri
			type: config?.type ? 'GET'
			data: config?.data ? null
			contentType: config?.contentType ? undefined
			error: (err, status, text) =>
				log 'jQuery error:', err, status, text
				message = text or 'HTTP error'
				statusNo = 500
				if err and err.status
					statusNo = err.status
				data = null
				if err and err.responseText
					try
						data = JSON.parse err.responseText
					catch e
				handler {status: statusNo, message: message}, data
			success: (data) =>
				if not data then return handler 'No data'
				try
					data = JSON.parse data
				catch e
				handler null, data
		})

class OAuthProvider

	constructor: (@config, @transport) ->
		@tokenURL = @config?.tokenURL ? '/token'
		@clientID = @config?.clientID ? 'no_client_id'
		@token = @config?.token
	
	rest: (app, path, body, handler, options) ->
		@transport.request {
			uri: "#{path}app=#{app}&oauth_token=#{@token}"
			type: if body then 'POST' else 'GET'
			data: body
			contentType: if body then 'text/plain' else null
		}, (error, data) =>
			# log 'Rest response:', error, data
			if error
				if error.status is 401
					@on_token_error null 
				return handler error.message
			handler null, data


	tokenByUsernamePassword: (username, password, handler) ->
		url = @tokenURL
		@transport.request {
			uri: url
			type: 'POST'
			data: {
				username: username
				password: password
				client_id: @clientID
				grant_type: 'password'
			}
		}, (error, data) =>
			log 'Response:', error, data
			if error then return handler data ? error
			@token = data.access_token
			@on_new_token @token
			handler null, data

	on_token_error: () ->

	on_new_token: () ->


window.jQueryTransport = jQueryTransport
window.OAuthProvider = OAuthProvider
