class NetTransport

	constructor: (@uri) ->
	
	request: (config, handler) -> 
		handler 'Not implemented'

class jQueryTransport extends NetTransport

	request: (config, handler) -> 
		log 'Doing request', @uri, config?.uri, config?.type
		$.ajax({
			url: @uri+config?.uri
			type: config?.type ? 'GET'
			data: config?.data ? null
			error: (err, status, text) =>
				log 'jQuery error:', err, status, text
				data = null
				if err and err.responseText
					try
						data = JSON.parse err.responseText
					catch e
				handler (text ? 'HTTP error'), data
			success: (data) =>
				if not data then return handler 'No data'
				handler null, data
		})

class OAuthProvider

	constructor: (@config, @transport) ->
		@tokenURL = @config?.tokenURL ? '/token'
		@clientID = @config?.clientID ? 'no_client_id'

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

window.jQueryTransport = jQueryTransport
window.OAuthProvider = OAuthProvider
