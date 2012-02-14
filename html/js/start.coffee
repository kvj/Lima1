start_app = () ->
	Date::firstDayOfWeek = 1
	db = new HTML5Provider 'test.db', '1.1'
	storage = new StorageProvider null, db
	manager = new DataManager storage
	# 'http://localhost:8888'
	jqnet = new jQueryTransport 'http://lima1sync.appspot.com'
	oauth = new OAuthProvider {
		clientID: 'lima1web'
		token: manager.get('token')
	}, jqnet
	ui = null
	if not env.mobile
		ui = new UIManager manager, oauth
	else
		ui = new MobileUIManager manager, oauth
	ui.start null

$(document).bind 'mobileinit', () ->
	$.mobile.defaultPageTransition = 'none'
	$.mobile.defaultDialogTransition = 'none'

$(document).ready () ->
	if env.mobile
		$('#main_page').live 'pageinit', () ->
			start_app()
	else 
		start_app()
