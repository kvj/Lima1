(function() {
  var MobileUIManager,
    __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; };

  env.mobile = true;

  env.prefix = 'mobile_';

  MobileUIManager = (function() {

    MobileUIManager.prototype.title = 'Lima1';

    MobileUIManager.prototype.pages = 1;

    MobileUIManager.prototype.currentPage = 0;

    MobileUIManager.prototype.close_delay = 500;

    function MobileUIManager(manager, oauth) {
      var _this = this;
      this.manager = manager;
      this.oauth = oauth;
      this.date_dialog = __bind(this.date_dialog, this);
      this.time_dialog = __bind(this.time_dialog, this);
      this.protocols = new ProtocolManager();
      this.oauth.on_new_token = function(token) {
        return _this.manager.set('token', token);
      };
      this.oauth.on_token_error = function() {
        return _this.login(null);
      };
      this.manager.on_scheduled_sync = function() {
        return _this.sync(null);
      };
      $('#do_login').bind('click', function() {
        _this.do_login(null);
        return false;
      });
      $('#main_page_content').bind('swipeleft', function() {
        return _this.scroll_sheets(1);
      }).bind('swiperight', function() {
        return _this.scroll_sheets(-1);
      });
      $('#sheets_left').bind('click', function() {
        return _this.scroll_sheets(-1);
      });
      $('#sheets_right').bind('click', function() {
        return _this.scroll_sheets(1);
      });
      $('#sheets_sync').bind('click', function() {
        return _this.sync(false);
      });
      $('#bmarks_toggle').bind('click', function() {
        return _this.navigator.root.toggle();
      });
      this.navigator = new PageNavigator(this.manager, this);
      this.navigator.left = true;
      this.navigator.auto_hide = true;
      this._title(this.title);
    }

    MobileUIManager.prototype._title = function(title) {
      return $('#main_title').text(title);
    };

    MobileUIManager.prototype.do_login = function() {
      var password, username,
        _this = this;
      username = $('#username').val();
      password = $('#password').val();
      return this.oauth.tokenByUsernamePassword(username, password, function(err) {
        var _ref;
        log('Auth result:', err);
        if (err) {
          return _this.show_error((_ref = err.error_description) != null ? _ref : err);
        }
        $('#login_dialog').dialog('close');
        return _this.sync(true);
      });
    };

    MobileUIManager.prototype.login = function() {
      log('Need login');
      $.mobile.changePage($('#login_dialog'));
      $('#password').text('');
      return $('#username').focus().text('');
    };

    MobileUIManager.prototype.sync = function(load_sheets) {
      var _this = this;
      log('Need show sync');
      this._title('Sync...');
      return this.manager.sync(this.oauth, function(err, sync_data) {
        var in_items, out_items, sync_at;
        _this._title(_this.title);
        if (err) return _this.show_error(err);
        log('Sync done', load_sheets);
        sync_at = new Date().format('M/d h:mma');
        out_items = sync_data.out;
        in_items = sync_data["in"];
        _this.show_error("Sync done: " + sync_at + " sent: " + out_items + " received: " + in_items);
        return _this.load_sheets(load_sheets);
      });
    };

    MobileUIManager.prototype.start = function() {
      var _this = this;
      return this.manager.open(function(err) {
        if (err) return _this.show_error(err);
        _this.navigator.root.height(_this._page_height() + 10);
        $('.page_mobile').css('min-height', _this._page_height() + 10);
        return _this.load_sheets(true);
      });
    };

    MobileUIManager.prototype.show_error = function(err) {
      return $('#info_dialog').show().text(err != null ? err : '???').fadeOut(3000);
    };

    MobileUIManager.prototype.load_sheets = function(show_sheets) {
      var _this = this;
      return this.manager.getPageNavigator(function(err, data, bmarks) {
        if (err) return _this.show_error(err);
        _this.sheets = data;
        _this.navigator.show_sheets(data, bmarks);
        if (data.length > 0 && show_sheets) return _this.show_sheets(0);
      });
    };

    MobileUIManager.prototype.find_scroll_sheets = function(id) {
      var i, item, _ref;
      _ref = this.sheets;
      for (i in _ref) {
        item = _ref[i];
        i = parseInt(i);
        if (item.id === id) {
          this.currentPage = i;
          return this.scroll_sheets(0);
        }
      }
      return this.show_error('Page not found');
    };

    MobileUIManager.prototype.scroll_sheets = function(inc) {
      var index;
      index = this.currentPage + inc * this.pages;
      if (index >= this.sheets.length - this.pages) {
        index = this.sheets.length - this.pages;
      }
      if (index < 0) index = 0;
      return this.show_sheets(index);
    };

    MobileUIManager.prototype.show_sheets = function(index) {
      var sheet;
      if (index < 0 || index >= this.sheets.length) {
        log('Index too few');
        return;
      }
      this.currentPage = index;
      sheet = this.sheets[index];
      return this.show_sheet(sheet.template_id, sheet);
    };

    MobileUIManager.prototype._page_height = function() {
      return window.innerHeight - $('#main_page_header').outerHeight() - $('#main_page_footer').outerHeight() - 40;
    };

    MobileUIManager.prototype.show_sheet = function(template_id, sheet, place) {
      var _this = this;
      if (place == null) place = '#page0';
      log('Show page', template_id, sheet, place);
      return this.manager.findTemplate(template_id, function(err, template) {
        var conf, config, height, name, p, renderer, _ref;
        if (err) return _this.show_error(err);
        if (sheet.template_id && sheet.template_id !== template_id) {
          sheet.template_id = template_id;
          _this.manager.saveSheet(sheet, function(err, object) {
            if (err) return _this.show_error(err);
          });
        }
        sheet.template_id = template_id;
        config = {};
        if (template.protocol && sheet.code) {
          _ref = template.protocol;
          for (name in _ref) {
            conf = _ref[name];
            p = _this.protocols.get(name);
            if (!p) continue;
            p.prepare(sheet, sheet.code);
          }
        }
        $(place).data('sheet_id', sheet.id);
        log('Height:', screen.height, window.outerHeight, window.innerHeight, screen.availHeight);
        height = _this._page_height();
        renderer = new Renderer(_this.manager, _this, $(place), template, sheet, height);
        renderer.on_sheet_change = function() {
          return _this.load_sheets(false);
        };
        return renderer.render(null);
      });
    };

    MobileUIManager.prototype.replace = function(text, item) {
      return this.protocols.replace(text, item);
    };

    MobileUIManager.prototype.inject = function(txt, item) {
      return this.protocols.inject(txt, item);
    };

    MobileUIManager.prototype.open_link = function(link, place) {
      var _this = this;
      return this.manager.getTemplates(function(err, data) {
        var code, id, index, obj, templates, _ref;
        if (err) return _this.show_error(err);
        templates = {};
        for (index in data) {
          obj = data[index];
          templates[obj.id] = JSON.parse(obj.body);
        }
        _ref = _this.protocols.open_link(link, place, templates), err = _ref[0], code = _ref[1], id = _ref[2];
        if (err) {
          return _this.show_error(err);
        } else {
          return _this.open_sheet_by_code(code, id);
        }
      });
    };

    MobileUIManager.prototype.open_sheet_by_code = function(code, template_id) {
      var _this = this;
      log('open_sheet_by_code', code, template_id);
      return this.manager.findSheet(['code', code], function(err, data) {
        if (err) return _this.show_error(err);
        if (data.length > 0) {
          return _this.show_sheet(data[0].template_id, data[0]);
        } else {
          return _this.show_sheet(template_id, {
            code: code
          });
        }
      });
    };

    MobileUIManager.prototype.time_dialog = function(hour, min, handler) {
      var _this = this;
      $.mobile.changePage($('#time_dialog'));
      $('#hour_slide').val(hour).slider('refresh');
      $('#min_slide').val(min).slider('refresh');
      $('#time_save').unbind('click').bind('click', function() {
        $('#time_dialog').dialog('close');
        return setTimeout(function() {
          return handler(true, $('#hour_slide').val(), $('#min_slide').val());
        }, _this.close_delay);
      });
      return $('#time_remove').unbind('click').bind('click', function() {
        $('#time_dialog').dialog('close');
        return setTimeout(function() {
          return handler(false);
        }, _this.close_delay);
      });
    };

    MobileUIManager.prototype.date_dialog = function(dt, handler) {
      var _this = this;
      log('show date dialog', dt);
      $.mobile.changePage($('#date_dialog'));
      $('#date_day').val(dt.getDate()).selectmenu('refresh');
      $('#date_month').val(dt.getMonth()).selectmenu('refresh');
      $('#date_year').val(dt.getFullYear()).selectmenu('refresh');
      $('#date_save').unbind('click').bind('click', function() {
        $('#date_dialog').dialog('close');
        return setTimeout(function() {
          var result;
          result = new Date();
          result.setFullYear($('#date_year').val());
          result.setMonth($('#date_month').val());
          result.setDate($('#date_day').val());
          return handler(true, result);
        }, _this.close_delay);
      });
      return $('#date_remove').unbind('click').bind('click', function() {
        $('#date_dialog').dialog('close');
        return setTimeout(function() {
          return handler(false);
        }, _this.close_delay);
      });
    };

    return MobileUIManager;

  })();

  window.MobileUIManager = MobileUIManager;

}).call(this);
