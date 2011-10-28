(function() {
  var NetTransport, OAuthProvider, jQueryTransport;
  var __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  }, __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; };
  NetTransport = (function() {
    function NetTransport(uri) {
      this.uri = uri;
    }
    NetTransport.prototype.request = function(config, handler) {
      return handler('Not implemented');
    };
    return NetTransport;
  })();
  jQueryTransport = (function() {
    __extends(jQueryTransport, NetTransport);
    function jQueryTransport() {
      jQueryTransport.__super__.constructor.apply(this, arguments);
    }
    jQueryTransport.prototype.request = function(config, handler) {
      var _ref, _ref2;
      log('Doing request', this.uri, config != null ? config.uri : void 0, config != null ? config.type : void 0);
      return $.ajax({
        url: this.uri + (config != null ? config.uri : void 0),
        type: (_ref = config != null ? config.type : void 0) != null ? _ref : 'GET',
        data: (_ref2 = config != null ? config.data : void 0) != null ? _ref2 : null,
        error: __bind(function(err, status, text) {
          var data;
          log('jQuery error:', err, status, text);
          data = null;
          if (err && err.responseText) {
            try {
              data = JSON.parse(err.responseText);
            } catch (e) {

            }
          }
          return handler(text != null ? text : 'HTTP error', data);
        }, this),
        success: __bind(function(data) {
          if (!data) {
            return handler('No data');
          }
          return handler(null, data);
        }, this)
      });
    };
    return jQueryTransport;
  })();
  OAuthProvider = (function() {
    function OAuthProvider(config, transport) {
      var _ref, _ref2, _ref3, _ref4;
      this.config = config;
      this.transport = transport;
      this.tokenURL = (_ref = (_ref2 = this.config) != null ? _ref2.tokenURL : void 0) != null ? _ref : '/token';
      this.clientID = (_ref3 = (_ref4 = this.config) != null ? _ref4.clientID : void 0) != null ? _ref3 : 'no_client_id';
    }
    OAuthProvider.prototype.tokenByUsernamePassword = function(username, password, handler) {
      var url;
      url = this.tokenURL;
      return this.transport.request({
        uri: url,
        type: 'POST',
        data: {
          username: username,
          password: password,
          client_id: this.clientID,
          grant_type: 'password'
        }
      }, __bind(function(error, data) {
        return log('Response:', error, data);
      }, this));
    };
    return OAuthProvider;
  })();
  window.jQueryTransport = jQueryTransport;
  window.OAuthProvider = OAuthProvider;
}).call(this);
