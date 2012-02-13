(function() {
  var start_app;

  start_app = function() {
    var db, jqnet, manager, oauth, storage, ui;
    Date.prototype.firstDayOfWeek = 1;
    db = new HTML5Provider('test.db', '1.1');
    storage = new StorageProvider(null, db);
    manager = new DataManager(storage);
    jqnet = new jQueryTransport('http://lima1sync.appspot.com');
    oauth = new OAuthProvider({
      clientID: 'lima1web',
      token: manager.get('token')
    }, jqnet);
    ui = null;
    if (!env.mobile) {
      ui = new UIManager(manager, oauth);
    } else {
      ui = new MobileUIManager(manager, oauth);
    }
    return ui.start(null);
  };

  $(document).ready(function() {
    if (env.mobile) {
      return $('#main_page').live('pageinit', function() {
        return start_app();
      });
    } else {
      return start_app();
    }
  });

}).call(this);
