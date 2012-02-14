(function() {
  var DateProtocol, ItemProtocol, PageNavigator, Protocol, ProtocolManager, UIManager,
    __hasProp = Object.prototype.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor; child.__super__ = parent.prototype; return child; },
    __indexOf = Array.prototype.indexOf || function(item) { for (var i = 0, l = this.length; i < l; i++) { if (i in this && this[i] === item) return i; } return -1; },
    __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; };

  Protocol = (function() {

    function Protocol() {}

    Protocol.prototype.convert = function(text, value) {
      return null;
    };

    Protocol.prototype.accept = function(config, value) {
      return false;
    };

    Protocol.prototype.prepare = function(config, value) {};

    return Protocol;

  })();

  ItemProtocol = (function(_super) {

    __extends(ItemProtocol, _super);

    function ItemProtocol() {
      ItemProtocol.__super__.constructor.apply(this, arguments);
    }

    ItemProtocol.prototype.convert = function(text, value) {
      return text;
    };

    return ItemProtocol;

  })(Protocol);

  DateProtocol = (function(_super) {

    __extends(DateProtocol, _super);

    function DateProtocol() {
      DateProtocol.__super__.constructor.apply(this, arguments);
    }

    DateProtocol.prototype.fromDateByType = function(type) {
      var method;
      method = null;
      switch (type) {
        case 'e':
          method = 'Day';
          break;
        case 'w':
          method = 'Week';
          break;
        case 'd':
          method = 'Date';
          break;
        case 'm':
          method = 'Month';
          break;
        case 'y':
          method = 'FullYear';
      }
      return method;
    };

    DateProtocol.prototype.prepare = function(config, value) {
      var dt;
      if (!config.dt) {
        dt = new Date();
        dt.fromString(value.substr(value.indexOf(':') + 1));
        return config.dt = dt.format('yyyyMMdd');
      }
    };

    DateProtocol.prototype.accept = function(config, value) {
      var dt, method, type, val;
      dt = new Date();
      dt.fromString(value);
      for (type in config) {
        value = config[type];
        method = this.fromDateByType(type);
        if (!method) continue;
        val = dt['get' + method]();
        if (__indexOf.call(value, val) >= 0 || val === value) {
          continue;
        } else {
          return false;
        }
      }
      return {
        dt: dt.format('yyyyMMdd')
      };
    };

    DateProtocol.prototype.convert = function(text, value) {
      var dt, exp, format, item, m, method, mexp, mm, mmm, modifiers, val, _i, _len;
      dt = new Date().fromString(value.dt);
      exp = /^(\((([ewmdy][+-]?[0-9]+)+)\))?([EwMdy\/\:\.]*)$/;
      m = text.match(exp);
      if (!m) return text;
      modifiers = m[2];
      format = m[4] && m[4].length > 0 ? m[4] : 'yyyyMMdd';
      if (modifiers) {
        mexp = /([ewmdy][+-]?[0-9]+)/g;
        mm = modifiers.match(mexp);
        for (_i = 0, _len = mm.length; _i < _len; _i++) {
          item = mm[_i];
          mmm = item.match(/([ewmdy])([+-]?)([0-9]+)/);
          if (mmm) {
            method = this.fromDateByType(mmm[1]);
            val = parseInt(mmm[3], 10);
            if (mmm[2]) {
              dt['set' + method](dt['get' + method]() + (mmm[2] === '+' ? val : -val));
            } else {
              dt['set' + method](val);
            }
          }
        }
      }
      return dt.format(format);
    };

    return DateProtocol;

  })(Protocol);

  PageNavigator = (function() {

    PageNavigator.prototype.colors = [
      [
        {
          color: '#556270',
          dark: true
        }, {
          color: '#4ECDC4',
          dark: true
        }, {
          color: '#C7F464',
          dark: false
        }, {
          color: '#FF6B6B',
          dark: true
        }, {
          color: '#C44D58',
          dark: true
        }
      ], [
        {
          color: '#D1F2A5',
          dark: false
        }, {
          color: '#EFFAB4',
          dark: false
        }, {
          color: '#FFC48C',
          dark: false
        }, {
          color: '#FF9F80',
          dark: false
        }, {
          color: '#F56991',
          dark: true
        }
      ], [
        {
          color: '#8C2318',
          dark: true
        }, {
          color: '#5E8C6A',
          dark: true
        }, {
          color: '#88A65E',
          dark: true
        }, {
          color: '#BFB35A',
          dark: false
        }, {
          color: '#F2C45A',
          dark: false
        }
      ], [
        {
          color: '#2A044A',
          dark: true
        }, {
          color: '#0B2E59',
          dark: true
        }, {
          color: '#0D6759',
          dark: true
        }, {
          color: '#7AB317',
          dark: false
        }, {
          color: '#A0C55F',
          dark: false
        }
      ], [
        {
          color: '#3E4147',
          dark: true
        }, {
          color: '#FFFEDF',
          dark: false
        }, {
          color: '#DFBA69',
          dark: false
        }, {
          color: '#5A2E2E',
          dark: true
        }, {
          color: '#2A2C31',
          dark: true
        }
      ]
    ];

    PageNavigator.prototype.left = false;

    PageNavigator.prototype.auto_hide = false;

    function PageNavigator(manager, ui) {
      var _this = this;
      this.manager = manager;
      this.ui = ui;
      this.root = $('#page_navigator');
      this.bmarkDialog = $('#bmark_dialog');
      $('#new_bmark_button').bind('click', function() {
        return _this.edit_bookmark(null);
      });
      $('#bmark_save_button').bind('click', function() {
        return _this.save_bookmark(null);
      });
    }

    PageNavigator.prototype.show_sheets = function(data, bmarks) {
      var bmark, bmarkHeight, bmarkMap, div, i, id, missing, pageHeight, pages, renderBookmark, sheet, _i, _j, _len, _len2, _len3, _ref, _ref2,
        _this = this;
      this.sheets = data;
      this.bookmarks = bmarks;
      log('PageNavigator sheets', data, bmarks, this.root.height());
      this.root.empty();
      missing = [];
      bmarkMap = {};
      _ref = this.bookmarks;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        bmark = _ref[_i];
        if (bmark.sheet_id) {
          bmarkMap[bmark.sheet_id] = bmark;
        } else {
          missing.push(bmark);
        }
      }
      bmarkHeight = 20;
      renderBookmark = function(bmark) {
        var div;
        div = $('<div/>').addClass('nav_bmark').appendTo(_this.root);
        if (_this.left) div.addClass('nav_bmark_left');
        if (bmark.sheet_id) div.attr('id', 'pn' + bmark.sheet_id);
        div.height(bmarkHeight - 2);
        if (!env.mobile) {
          div.bind('dblclick', function(e) {
            _this.edit_bookmark(bmark);
            return false;
          });
        }
        div.text(bmark.name);
        div.css('backgroundColor', bmark.color).css('color', bmark.dark ? '#ffffff' : '#000000');
        _this.root.append('<div class="clear"/>');
        if (!env.mobile) {
          div.data('type', 'bmark');
          div.data('item', bmark);
          div.draggable({
            zIndex: 3,
            containment: 'document',
            helper: 'clone',
            appendTo: 'body'
          });
        }
        return div;
      };
      pages = 0;
      if (this.sheets.length > 0) {
        for (i = 0, _ref2 = this.sheets.length; 0 <= _ref2 ? i < _ref2 : i > _ref2; 0 <= _ref2 ? i++ : i--) {
          sheet = this.sheets[i];
          div = null;
          if (bmarkMap[sheet.id]) {
            div = renderBookmark(bmarkMap[sheet.id]);
            (function(sheet) {
              return div.bind('click', function() {
                _this.ui.find_scroll_sheets(sheet.id);
                if (_this.auto_hide) _this.root.hide();
                return false;
              });
            })(sheet);
          } else {
            pages++;
            div = $('<div/>').addClass('nav_sheet').appendTo(this.root).attr('id', 'pn' + sheet.id);
            if (this.left) div.addClass('nav_sheet_left');
            if (i === 0) div.addClass('nav_sheet_top');
            this.root.append('<div class="clear"/>');
          }
        }
      }
      for (bmark = 0, _len2 = bmarkMap.length; bmark < _len2; bmark++) {
        id = bmarkMap[bmark];
        missing.push(bmark);
      }
      for (_j = 0, _len3 = missing.length; _j < _len3; _j++) {
        bmark = missing[_j];
        renderBookmark(bmark);
      }
      if (pages > 0) {
        pageHeight = Math.floor((this.root.innerHeight() + 3 - bmarkHeight * this.bookmarks.length) / pages);
        return this.root.children('.nav_sheet').height(pageHeight - 1);
      }
    };

    PageNavigator.prototype.load_sheets = function() {
      var _this = this;
      return this.manager.getPageNavigator(function(err, data, bmarks) {
        if (err) return _this.ui.show_error(err);
        return _this.show_sheets(data, bmarks);
      });
    };

    PageNavigator.prototype.attach_bookmark = function(bmark, sheet_id) {
      var _this = this;
      log('Attach', bmark, sheet_id);
      bmark.sheet_id = sheet_id;
      return this.manager.saveBookmark(bmark, function(err) {
        if (err) return _this.ui.show_error(err);
        return _this.load_sheets(null);
      });
    };

    PageNavigator.prototype.page_selected = function(sheet_id) {
      this.root.children().removeClass('nav_page_selected').find('#pn' + sheet_id).addClass('nav_page_selected');
      return this.root.children('#pn' + sheet_id).addClass('nav_page_selected');
    };

    PageNavigator.prototype.edit_bookmark = function(bmark) {
      var applyColor, col, item, row, rowItem, _fn, _i, _j, _len, _len2, _ref, _results,
        _this = this;
      this.bmark = bmark;
      if (!this.bmark) {
        this.bmark = {
          name: 'New bookmark',
          dark: false,
          color: '#eeeeee'
        };
      }
      this.bmarkDialog.dialog({
        width: 400,
        height: 300
      });
      applyColor = function(color) {
        log('applyColor', color);
        $('#color_select_example').css('backgroundColor', color.color).css('color', color.dark ? '#ffffff' : '#000000');
        _this.bmark.color = color.color;
        return _this.bmark.dark = color.dark;
      };
      applyColor(this.bmark);
      $('#bmark_name').val(this.bmark.name);
      $('#color_select').empty();
      _ref = this.colors;
      _results = [];
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        row = _ref[_i];
        rowItem = $('<div/>').appendTo($('#color_select')).addClass('clear');
        _fn = function(col) {
          return item.bind('click', function() {
            applyColor(col);
            return false;
          });
        };
        for (_j = 0, _len2 = row.length; _j < _len2; _j++) {
          col = row[_j];
          item = $('<div/>').appendTo(rowItem).addClass('color_select_item').css('backgroundColor', col.color);
          item.html('&nbsp;');
          _fn(col);
        }
        _results.push($('<div class="clear"/>').appendTo(rowItem));
      }
      return _results;
    };

    PageNavigator.prototype.save_bookmark = function() {
      var _this = this;
      this.bmark.name = $('#bmark_name').val();
      return this.manager.saveBookmark(this.bmark, function(err) {
        if (err) return _this.ui.show_error(err);
        _this.bmarkDialog.dialog('close');
        return _this.load_sheets(null);
      });
    };

    PageNavigator.prototype.remove_bookmark = function(bmark) {
      var _this = this;
      return this.manager.removeBookmark(bmark, function(err) {
        if (err) return _this.ui.show_error(err);
        return _this.load_sheets(null);
      });
    };

    return PageNavigator;

  })();

  ProtocolManager = (function() {

    function ProtocolManager() {
      this.protocols = {
        "dt": new DateProtocol(null),
        "@": new ItemProtocol(null)
      };
    }

    ProtocolManager.prototype.get = function(name) {
      return this.protocols[name];
    };

    ProtocolManager.prototype.replace = function(text, item) {
      var exp, m, name, p, value, _ref;
      exp = /^([a-z\@]+\:)([a-zA-Z0-9\s\(\)\+\-\_\/\:\.]*)$/;
      if (m = text.match(exp)) {
        value = '';
        _ref = this.protocols;
        for (name in _ref) {
          p = _ref[name];
          if (name + ':' === m[1]) {
            value = p.convert(m[2], item);
            break;
          }
        }
        return value;
      }
      return text;
    };

    ProtocolManager.prototype.inject = function(text, item) {
      var exp, m, name, p, value, _ref;
      exp = /\$\{([a-z\@]+\:)([a-zA-Z0-9\s\(\)\+\-\_\/\:\.]*)\}/;
      while (m = text.match(exp)) {
        if (!m) return text;
        value = '';
        _ref = this.protocols;
        for (name in _ref) {
          p = _ref[name];
          if (name + ':' === m[1]) {
            value = p.convert(m[2], item);
            break;
          }
        }
        text = text.replace(m[0], value != null ? value : '');
      }
      return text;
    };

    ProtocolManager.prototype.open_link = function(link, place, templates) {
      var code, config, exp, id, m, name, p, templates_found, tmpl;
      log('Open link', link);
      exp = /^([a-z\@]+)\:([a-zA-Z0-9\s\(\)\+\-\_\/\:\.]*)$/;
      templates_found = [];
      if (m = link.match(exp)) {
        name = m[1];
        p = this.protocols[name];
        if (!p) return;
        for (id in templates) {
          tmpl = templates[id];
          if (tmpl.protocol && tmpl.protocol[name] && tmpl.code) {
            config = p.accept(tmpl.protocol[name], m[2]);
            if (!config) continue;
            code = this.inject(tmpl.code, config);
            if (code) {
              templates_found.push({
                code: code,
                template_id: id
              });
            }
          }
        }
      }
      log('Templates found:', templates_found);
      if (templates_found.length === 0) {
        return ['No templates matching link', null, null];
      }
      if (templates_found.length === 1) {
        return [null, templates_found[0].code, templates_found[0].template_id];
      }
      if (templates_found.length > 1) return ['Too many templates', null, null];
    };

    return ProtocolManager;

  })();

  UIManager = (function() {

    UIManager.prototype.archive_state = null;

    function UIManager(manager, oauth) {
      var i, page,
        _this = this;
      this.manager = manager;
      this.oauth = oauth;
      this.scroll_sheets = __bind(this.scroll_sheets, this);
      this.protocols = new ProtocolManager();
      $('button').button();
      $('#templates_button').bind('click', function() {
        return _this.edit_templates(null);
      });
      $('#new_sheet_button').bind('click', function() {
        return _this.new_sheet(null);
      });
      $('#new_sheet_button').droppable({
        accept: '.sheet',
        hoverClass: 'toolbar_drop',
        tolerance: 'pointer',
        drop: function(event, ui) {
          _this.new_sheet(ui.draggable.data('item'));
          return event.preventDefault();
        }
      });
      $('#templates').bind('selectablestop', function() {
        return _this.template_selected(null);
      });
      $('#new_template_button').bind('click', function() {
        return _this.new_template(null);
      });
      $('#reload_sheets_button').bind('click', function() {
        _this.load_templates(null);
        return _this.show_sheets(null);
      });
      $('#remove_template').bind('click', function() {
        return _this.remove_template(null);
      });
      $('#save_template').bind('click', function() {
        return _this.save_template(null);
      });
      $('#trash').droppable({
        accept: '.list_item, .sheet, .nav_bmark',
        hoverClass: 'toolbar_drop',
        tolerance: 'pointer',
        drop: function(event, ui) {
          _this.remove_drop(ui.draggable);
          return event.preventDefault();
        }
      });
      $('#archive').droppable({
        accept: '.sheet',
        hoverClass: 'toolbar_drop',
        tolerance: 'pointer',
        drop: function(event, ui) {
          _this.archive_drop(ui.draggable);
          return event.preventDefault();
        }
      }).bind('click', function(event) {
        return _this.invert_archive(null);
      });
      $('.page').droppable({
        accept: '.sheet, .nav_bmark',
        hoverClass: 'page_drop',
        tolerance: 'pointer',
        drop: function(event, ui) {
          var item;
          item = ui.draggable.data('item');
          log('Drop', event.target);
          if (ui.draggable.data('type') === 'bmark') {
            _this.navigator.attach_bookmark(item, $(event.target).data('sheet_id'));
          } else {
            _this.show_page(item.template_id, item, '#' + $(event.target).attr('id'));
          }
          return event.preventDefault();
        }
      });
      $('#calendar').datepicker({
        dateFormat: 'yymmdd',
        firstDay: 1,
        onSelect: function(dt) {
          _this.open_link('dt:' + dt);
          return false;
        }
      });
      this.oauth.on_new_token = function(token) {
        return _this.manager.set('token', token);
      };
      this.oauth.on_token_error = function() {
        return _this.login(null);
      };
      $('#sync_button').bind('click', function() {
        return _this.sync(null);
      });
      $('#login_button').bind('click', function() {
        return _this.do_login(null);
      });
      this.manager.on_scheduled_sync = function() {
        return _this.sync(null);
      };
      this.pages = parseInt(this.manager.get('pages', 2));
      $('#page_slider').slider({
        min: 1,
        max: 4,
        step: 1,
        value: this.pages,
        slide: function(event, ui) {
          return _this.show_pages(ui.value);
        }
      });
      for (i = 0; i < 4; i++) {
        page = $("#page" + i);
      }
      this.show_pages(this.pages);
      this.navigator = new PageNavigator(this.manager, this);
    }

    UIManager.prototype.invert_archive = function() {
      if (this.archive_state === null) {
        this.archive_state = 1;
        $('#archive').text('Unarchive');
      } else {
        this.archive_state = null;
        $('#archive').text('Archive');
      }
      return this.show_sheets(null);
    };

    UIManager.prototype.show_pages = function(count) {
      var i;
      for (i = 0; 0 <= count ? i < count : i > count; 0 <= count ? i++ : i--) {
        $("#page" + i).show();
      }
      for (i = count; count <= 4 ? i < 4 : i > 4; count <= 4 ? i++ : i--) {
        $("#page" + i).hide();
      }
      this.pages = count;
      return this.manager.set('pages', count);
    };

    UIManager.prototype.do_login = function() {
      var password, username,
        _this = this;
      username = $('#username').val();
      password = $('#password').val();
      return this.oauth.tokenByUsernamePassword(username, password, function(err) {
        log('Auth result:', err);
        if (err) return _this.show_error(err);
        $('#login_dialog').dialog('close');
        return _this.sync(null);
      });
    };

    UIManager.prototype.sync = function() {
      var _this = this;
      $('#sync_button').text('Sync in progress...');
      return this.manager.sync(this.oauth, function(err, sync_data) {
        var in_items, out_items, sync_at;
        $('#sync_button').text('Sync');
        if (err) return _this.show_error(err);
        sync_at = new Date().format('M/d h:mma');
        out_items = sync_data.out;
        in_items = sync_data["in"];
        return $('#sync_message').text("Sync done: " + sync_at + " sent: " + out_items + " received: " + in_items);
      });
    };

    UIManager.prototype.login = function() {
      $('#login_dialog').dialog({
        width: 300,
        height: 200,
        modal: true
      });
      $('#username').val('').focus();
      return $('#password').val('');
    };

    UIManager.prototype.replace = function(text, item) {
      return this.protocols.replace(text, item);
    };

    UIManager.prototype.inject = function(txt, item) {
      return this.protocols.inject(txt, item);
    };

    UIManager.prototype.open_link = function(link, place) {
      var code, err, id, _ref;
      _ref = this.protocols.open_link(link, place, this.templates), err = _ref[0], code = _ref[1], id = _ref[2];
      if (err) {
        return this.show_error(err);
      } else {
        return this.open_sheet_by_code(code, id);
      }
    };

    UIManager.prototype.open_sheet_by_code = function(code, template_id) {
      var _this = this;
      return this.manager.findSheet(['code', code], function(err, data) {
        if (err) return _this.show_error(err);
        if (data.length > 0) {
          return _this.show_page(data[0].template_id, data[0]);
        } else {
          return _this.show_page(template_id, {
            code: code
          });
        }
      });
    };

    UIManager.prototype.archive_drop = function(drag) {
      var sheet,
        _this = this;
      sheet = drag.data('item');
      sheet.archived = this.archive_state === 1 ? null : 1;
      return this.manager.saveSheet(sheet, function(err) {
        if (err) return _this.show_error(err);
        return _this.show_sheets(null);
      });
    };

    UIManager.prototype.remove_drop = function(drag) {
      if (drag.data('type') === 'note') {
        drag.data('renderer').remove_note(drag.data('item'), drag);
      }
      if (drag.data('type') === 'bmark') {
        this.navigator.remove_bookmark(drag.data('item'));
      }
      if (drag.data('type') === 'sheet') {
        return this.remove_sheet(drag.data('item'), drag);
      }
    };

    UIManager.prototype.remove_sheet = function(item, element) {
      var _this = this;
      return this.manager.removeSheet(item, function(err) {
        if (err) return _this.show_error(err);
        $('.page').each(function(i, el) {
          var _ref;
          if (((_ref = $(el).data('sheet')) != null ? _ref.id : void 0) === item.id) {
            return $(el).empty();
          }
        });
        return _this.show_sheets(null);
      });
    };

    UIManager.prototype.remove_template = function() {
      var selected,
        _this = this;
      selected = $('#templates').children('.ui-selected');
      return selected.each(function(index, item) {
        var last;
        last = selected.size() - 1 === index;
        return (function(last) {
          return _this.manager.removeTemplate($(item).data('template'), function() {
            if (last) {
              _this.show_templates(null);
              _this.new_template(null);
              return _this.load_templates(null);
            }
          });
        })(last);
      });
    };

    UIManager.prototype.new_template = function() {
      var selected;
      selected = $('#templates').children().removeClass('ui-selected');
      this.show_template({});
      return this.template_selected(null);
    };

    UIManager.prototype.show_template = function(tmpl) {
      this.edit_template = tmpl;
      $('#template_name').val(tmpl.name);
      return $('#template_body').val(tmpl.body);
    };

    UIManager.prototype.start = function() {
      var _this = this;
      return this.manager.open(function(err) {
        if (err) {
          $('#main').hide();
          return _this.show_error(err);
        }
        _this.load_templates(null);
        _this.show_sheets(null);
        return _this.sync(null);
      });
    };

    UIManager.prototype.save_template = function() {
      var _this = this;
      this.edit_template.name = $('#template_name').val();
      this.edit_template.body = $('#template_body').val();
      if (!this.edit_template.name) return this.show_error('Name is required');
      try {
        JSON.parse(this.edit_template.body);
      } catch (e) {
        return this.show_error('Body isnt JSON');
      }
      return this.manager.saveTemplate(this.edit_template, function(err, object) {
        if (err) return _this.show_error(err);
        _this.show_templates(object.id);
        return _this.load_templates(null);
      });
    };

    UIManager.prototype.show_error = function(message) {
      var div,
        _this = this;
      log('Error', message);
      div = $('<div/>').addClass('message error_message').text(message != null ? message : 'Error!').appendTo($('#messages')).delay(5000).fadeOut();
      return (function(div) {
        return setTimeout(function() {
          return div.remove();
        }, 7000);
      })(div);
    };

    UIManager.prototype.template_selected = function() {
      var selected;
      selected = $('#templates').children('.ui-selected');
      log('template_selected', selected.size());
      $('#remove_template').attr('disabled', selected.size() === 0 ? 'disabled' : false);
      $('#save_template').attr('disabled', selected.size() > 1 ? 'disabled' : false);
      if (selected.size() > 0) {
        return this.show_template(selected.last().data('template'));
      }
    };

    UIManager.prototype.show_templates = function(select) {
      var ul,
        _this = this;
      ul = $('#templates').empty();
      this.manager.getTemplates(function(err, data) {
        var item, li, _i, _len;
        if (err) return _this.show_error(err);
        for (_i = 0, _len = data.length; _i < _len; _i++) {
          item = data[_i];
          li = $('<li/>').appendTo(ul);
          li.text(item.name);
          li.data('template', item);
          if (select && item.id === select) li.addClass('ui-selected');
        }
        return ul.selectable();
      });
      return this.template_selected(null);
    };

    UIManager.prototype.edit_templates = function() {
      log('Show edit templates');
      $('#templates_dialog').dialog({
        width: 800,
        height: 600
      });
      this.show_templates(null);
      return this.new_template(null);
    };

    UIManager.prototype.load_templates = function() {
      var _this = this;
      return this.manager.getTemplates(function(err, data) {
        var item, _i, _len, _results;
        if (err) return;
        _this.templates = {};
        _results = [];
        for (_i = 0, _len = data.length; _i < _len; _i++) {
          item = data[_i];
          try {
            _results.push(_this.templates[item.id] = JSON.parse(item.body));
          } catch (e) {
            _results.push(log('JSON error', e));
          }
        }
        return _results;
      });
    };

    UIManager.prototype.find_scroll_sheets = function(id) {
      return this.scroll_sheets(id);
    };

    UIManager.prototype.scroll_sheets = function(from, step) {
      var i, j, _ref, _ref2;
      if (step == null) step = 0;
      if (!this.sheets) {
        this.show_error('No data loaded');
        return;
      }
      for (i = 0, _ref = this.sheets_displayed.length; 0 <= _ref ? i < _ref : i > _ref; 0 <= _ref ? i++ : i--) {
        if (this.sheets_displayed[i].id === from) {
          i += step;
          if (i + this.pages >= this.sheets_displayed.length) {
            i = this.sheets_displayed.length - this.pages;
          }
          if (i < 0) i = 0;
          this.navigator.page_selected(this.sheets_displayed[i].id);
          for (j = i, _ref2 = Math.min(this.pages + i, this.sheets_displayed.length); i <= _ref2 ? j < _ref2 : j > _ref2; i <= _ref2 ? j++ : j--) {
            this.show_page(this.sheets_displayed[j].template_id, this.sheets_displayed[j], "#page" + (j - i));
          }
          return;
        }
      }
    };

    UIManager.prototype.show_page = function(template_id, sheet, place) {
      var conf, config, name, p, renderer, template, _ref,
        _this = this;
      if (place == null) place = '#page0';
      log('Show page', template_id, sheet, place);
      if (!this.templates || !this.templates[template_id]) {
        return this.show_error('No template found');
      }
      template = this.templates[template_id];
      if (sheet.template_id && sheet.template_id !== template_id) {
        sheet.template_id = template_id;
        this.manager.saveSheet(sheet, function(err, object) {
          if (err) return _this.ui.show_error(err);
        });
      }
      sheet.template_id = template_id;
      config = {};
      if (template.protocol && sheet.code) {
        _ref = template.protocol;
        for (name in _ref) {
          conf = _ref[name];
          p = this.protocols.get(name);
          if (!p) continue;
          p.prepare(sheet, sheet.code);
        }
      }
      $(place).data('sheet_id', sheet.id);
      renderer = new Renderer(this.manager, this, $(place), template, sheet, $(place).innerHeight() - 50);
      renderer.on_sheet_change = function() {
        return _this.show_sheets(null);
      };
      return renderer.render(null);
    };

    UIManager.prototype.move_sheet = function(item, before) {
      var i, itm, last, to_save, _results,
        _this = this;
      to_save = this.manager.sortArray(this.sheets, item, before);
      to_save.push(item);
      _results = [];
      for (i in to_save) {
        itm = to_save[i];
        last = parseInt(i) === to_save.length - 1;
        _results.push((function(last) {
          return _this.manager.saveSheet(itm, function() {
            if (last) return _this.show_sheets(null);
          });
        })(last));
      }
      return _results;
    };

    UIManager.prototype.show_sheets = function() {
      var ul,
        _this = this;
      ul = $('#sheets').empty();
      this.manager.getSheets(function(err, data) {
        var index, item, li, _fn, _fn2, _i, _len, _ref;
        if (err) return _this.show_error(err);
        _this.sheets = data;
        _this.sheets_displayed = [];
        index = 0;
        _fn = function(item) {
          return li.droppable({
            accept: '.sheet',
            hoverClass: 'toolbar_drop',
            tolerance: 'pointer',
            drop: function(event, ui) {
              _this.move_sheet(ui.draggable.data('item'), item);
              return event.preventDefault();
            }
          });
        };
        _fn2 = function(item) {
          return li.bind('dblclick', function() {
            return _this.scroll_sheets(item.id);
          });
        };
        for (_i = 0, _len = data.length; _i < _len; _i++) {
          item = data[_i];
          if (((_ref = item.archived) != null ? _ref : null) !== _this.archive_state) {
            continue;
          }
          _this.sheets_displayed.push(item);
          li = $('<li/>').addClass('sheet').appendTo(ul);
          li.data('type', 'sheet');
          li.data('item', item);
          li.draggable({
            zIndex: 3,
            containment: 'document',
            helper: 'clone',
            appendTo: 'body'
          });
          _fn(item);
          li.text(item.title);
          _fn2(item);
        }
        li = $('<li/>').addClass('sheet last_sheet').appendTo(ul);
        return li.droppable({
          accept: '.sheet',
          hoverClass: 'toolbar_drop',
          tolerance: 'pointer',
          drop: function(event, ui) {
            _this.move_sheet(ui.draggable.data('item'));
            return event.preventDefault();
          }
        });
      });
      return this.navigator.load_sheets(null);
    };

    UIManager.prototype.new_sheet = function(sheet) {
      var ul,
        _this = this;
      $('#new_sheet_dialog').dialog({
        width: 400,
        height: 200
      });
      ul = $('#new_sheet_templates').empty();
      return this.manager.getTemplates(function(err, data) {
        var item, li, tmpl, _fn, _i, _len;
        if (err) return _this.show_error(err);
        _fn = function(item) {
          li.bind('mousedown', function(e) {
            return false;
          });
          return li.bind('dblclick', function(e) {
            $('#new_sheet_dialog').dialog('close');
            _this.show_page(item.id, sheet != null ? sheet : {});
            return false;
          });
        };
        for (_i = 0, _len = data.length; _i < _len; _i++) {
          item = data[_i];
          tmpl = _this.templates[item.id];
          if (!tmpl) continue;
          if (!tmpl.direct) continue;
          li = $('<div/>').addClass('new_sheet_template').appendTo(ul);
          li.text(item.name);
          _fn(item);
        }
        return $('<div/>').addClass('clear').appendTo(ul);
      });
    };

    return UIManager;

  })();

  window.UIManager = UIManager;

  window.ProtocolManager = ProtocolManager;

  window.PageNavigator = PageNavigator;

}).call(this);
