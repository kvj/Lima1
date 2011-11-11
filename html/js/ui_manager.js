(function() {
  var DateProtocol, ItemProtocol, Protocol, UIManager;
  var __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  }, __indexOf = Array.prototype.indexOf || function(item) {
    for (var i = 0, l = this.length; i < l; i++) {
      if (this[i] === item) return i;
    }
    return -1;
  }, __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; };
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
  ItemProtocol = (function() {
    __extends(ItemProtocol, Protocol);
    function ItemProtocol() {
      ItemProtocol.__super__.constructor.apply(this, arguments);
    }
    ItemProtocol.prototype.convert = function(text, value) {
      return text;
    };
    return ItemProtocol;
  })();
  DateProtocol = (function() {
    __extends(DateProtocol, Protocol);
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
        if (!method) {
          continue;
        }
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
      if (!m) {
        return text;
      }
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
  })();
  UIManager = (function() {
    UIManager.prototype.archive_state = null;
    function UIManager(manager, oauth) {
      var i, page, _fn;
      this.manager = manager;
      this.oauth = oauth;
      this.scroll_sheets = __bind(this.scroll_sheets, this);
      this.protocols = {
        "dt": new DateProtocol(null),
        "@": new ItemProtocol(null)
      };
      $('button').button();
      $('#templates_button').bind('click', __bind(function() {
        return this.edit_templates(null);
      }, this));
      $('#new_sheet_button').bind('click', __bind(function() {
        return this.new_sheet(null);
      }, this));
      $('#templates').bind('selectablestop', __bind(function() {
        return this.template_selected(null);
      }, this));
      $('#new_template_button').bind('click', __bind(function() {
        return this.new_template(null);
      }, this));
      $('#reload_sheets_button').bind('click', __bind(function() {
        this.load_templates(null);
        return this.show_sheets(null);
      }, this));
      $('#remove_template').bind('click', __bind(function() {
        return this.remove_template(null);
      }, this));
      $('#save_template').bind('click', __bind(function() {
        return this.save_template(null);
      }, this));
      $('#trash').droppable({
        accept: '.list_item, .sheet',
        hoverClass: 'toolbar_drop',
        tolerance: 'pointer',
        drop: __bind(function(event, ui) {
          this.remove_drop(ui.draggable);
          return event.preventDefault();
        }, this)
      });
      $('#archive').droppable({
        accept: '.sheet',
        hoverClass: 'toolbar_drop',
        tolerance: 'pointer',
        drop: __bind(function(event, ui) {
          this.archive_drop(ui.draggable);
          return event.preventDefault();
        }, this)
      }).bind('click', __bind(function(event) {
        return this.invert_archive(null);
      }, this));
      $('.page').droppable({
        accept: '.sheet',
        hoverClass: 'page_drop',
        tolerance: 'pointer',
        drop: __bind(function(event, ui) {
          var item;
          item = ui.draggable.data('item');
          log('Drop', event.target);
          this.show_page(item.template_id, item, '#' + $(event.target).attr('id'));
          return event.preventDefault();
        }, this)
      });
      $('#calendar').datepicker({
        dateFormat: 'yymmdd',
        firstDay: 1,
        onSelect: __bind(function(dt) {
          this.open_link('dt:' + dt);
          return false;
        }, this)
      });
      this.oauth.on_new_token = __bind(function(token) {
        return this.manager.set('token', token);
      }, this);
      this.oauth.on_token_error = __bind(function() {
        return this.login(null);
      }, this);
      $('#sync_button').bind('click', __bind(function() {
        return this.sync(null);
      }, this));
      $('#login_button').bind('click', __bind(function() {
        return this.do_login(null);
      }, this));
      this.manager.on_scheduled_sync = __bind(function() {
        return this.sync(null);
      }, this);
      this.pages = parseInt(this.manager.get('pages', 2));
      $('#page_slider').slider({
        min: 1,
        max: 4,
        step: 1,
        value: this.pages,
        slide: __bind(function(event, ui) {
          return this.show_pages(ui.value);
        }, this)
      });
      _fn = __bind(function(page) {
        page.bind('mouseover', __bind(function(event) {
          return page.children('.page_scroll').show();
        }, this));
        return page.bind('mouseout', __bind(function(event) {
          return page.children('.page_scroll').hide();
        }, this));
      }, this);
      for (i = 0; i < 4; i++) {
        page = $("#page" + i);
        _fn(page);
      }
      this.show_pages(this.pages);
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
      var password, username;
      username = $('#username').val();
      password = $('#password').val();
      return this.oauth.tokenByUsernamePassword(username, password, __bind(function(err) {
        log('Auth result:', err);
        if (err) {
          return this.show_error(err);
        }
        $('#login_dialog').dialog('close');
        return this.sync(null);
      }, this));
    };
    UIManager.prototype.sync = function() {
      $('#sync_button').find('.ui-button-text').text('Sync in progress...');
      return this.manager.sync(this.oauth, __bind(function(err, sync_data) {
        var in_items, out_items, sync_at;
        $('#sync_button').find('.ui-button-text').text('Sync');
        if (err) {
          return this.show_error(err);
        }
        sync_at = new Date().format('M/d h:mma');
        out_items = sync_data.out;
        in_items = sync_data["in"];
        return $('#sync_message').text("Sync done: " + sync_at + " sent: " + out_items + " received: " + in_items);
      }, this));
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
    UIManager.prototype.inject = function(txt, item) {
      var exp, m, name, p, text, value, _ref;
      text = txt;
      exp = /\$\{([a-z\@]+\:)([a-zA-Z0-9\s\(\)\+\-\_\/\:\.]*)\}/;
      while (m = text.match(exp)) {
        if (!m) {
          return text;
        }
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
    UIManager.prototype.open_link = function(link, place) {
      var code, config, exp, id, m, name, p, templates_found, tmpl, _ref;
      log('Open link', link);
      exp = /^([a-z\@]+)\:([a-zA-Z0-9\s\(\)\+\-\_\/\:\.]*)$/;
      templates_found = [];
      if (m = link.match(exp)) {
        name = m[1];
        p = this.protocols[name];
        if (!p) {
          return;
        }
        _ref = this.templates;
        for (id in _ref) {
          tmpl = _ref[id];
          if (tmpl.protocol && tmpl.protocol[name] && tmpl.code) {
            config = p.accept(tmpl.protocol[name], m[2]);
            if (!config) {
              continue;
            }
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
        this.show_error('No templates matching link');
      }
      if (templates_found.length === 1) {
        this.open_sheet_by_code(templates_found[0].code, templates_found[0].template_id);
      }
      if (templates_found.length > 1) {
        return this.show_error('Too many templates');
      }
    };
    UIManager.prototype.open_sheet_by_code = function(code, template_id) {
      return this.manager.findSheet(['code', code], __bind(function(err, data) {
        if (err) {
          return this.show_error(err);
        }
        if (data.length > 0) {
          return this.show_page(data[0].template_id, data[0]);
        } else {
          return this.show_page(template_id, {
            code: code
          });
        }
      }, this));
    };
    UIManager.prototype.archive_drop = function(drag) {
      var sheet;
      sheet = drag.data('item');
      sheet.archived = this.archive_state === 1 ? null : 1;
      return this.manager.saveSheet(sheet, __bind(function(err) {
        if (err) {
          return this.show_error(err);
        }
        return this.show_sheets(null);
      }, this));
    };
    UIManager.prototype.remove_drop = function(drag) {
      if (drag.data('type') === 'note') {
        drag.data('renderer').remove_note(drag.data('item'), drag);
      }
      if (drag.data('type') === 'sheet') {
        return this.remove_sheet(drag.data('item'), drag);
      }
    };
    UIManager.prototype.remove_sheet = function(item, element) {
      return this.manager.removeSheet(item, __bind(function(err) {
        if (err) {
          return this.show_error(err);
        }
        $('.page').each(__bind(function(i, el) {
          var _ref;
          if (((_ref = $(el).data('sheet')) != null ? _ref.id : void 0) === item.id) {
            return $(el).empty();
          }
        }, this));
        return this.show_sheets(null);
      }, this));
    };
    UIManager.prototype.remove_template = function() {
      var selected;
      selected = $('#templates').children('.ui-selected');
      return selected.each(__bind(function(index, item) {
        var last;
        last = selected.size() - 1 === index;
        return __bind(function(last) {
          return this.manager.removeTemplate($(item).data('template'), __bind(function() {
            if (last) {
              this.show_templates(null);
              this.new_template(null);
              return this.load_templates(null);
            }
          }, this));
        }, this)(last);
      }, this));
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
      return this.manager.open(__bind(function(err) {
        if (err) {
          $('#main').hide();
          return this.show_error(err);
        }
        this.load_templates(null);
        return this.show_sheets(null);
      }, this));
    };
    UIManager.prototype.save_template = function() {
      this.edit_template.name = $('#template_name').val();
      this.edit_template.body = $('#template_body').val();
      if (!this.edit_template.name) {
        return this.show_error('Name is required');
      }
      try {
        JSON.parse(this.edit_template.body);
      } catch (e) {
        return this.show_error('Body isnt JSON');
      }
      return this.manager.saveTemplate(this.edit_template, __bind(function(err, object) {
        if (err) {
          return this.show_error(err);
        }
        this.show_templates(object.id);
        return this.load_templates(null);
      }, this));
    };
    UIManager.prototype.show_error = function(message) {
      var div;
      log('Error', message);
      div = $('<div/>').addClass('message error_message').text(message != null ? message : 'Error!').appendTo($('#messages')).delay(5000).fadeOut();
      return __bind(function(div) {
        return setTimeout(__bind(function() {
          return div.remove();
        }, this), 7000);
      }, this)(div);
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
      var ul;
      ul = $('#templates').empty();
      this.manager.getTemplates(__bind(function(err, data) {
        var item, li, _i, _len;
        if (err) {
          return this.show_error(err);
        }
        for (_i = 0, _len = data.length; _i < _len; _i++) {
          item = data[_i];
          li = $('<li/>').appendTo(ul);
          li.text(item.name);
          li.data('template', item);
          if (select && item.id === select) {
            li.addClass('ui-selected');
          }
        }
        return ul.selectable();
      }, this));
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
      return this.manager.getTemplates(__bind(function(err, data) {
        var item, _i, _len, _results;
        if (err) {
          return;
        }
        this.templates = {};
        _results = [];
        for (_i = 0, _len = data.length; _i < _len; _i++) {
          item = data[_i];
          _results.push((function() {
            try {
              return this.templates[item.id] = JSON.parse(item.body);
            } catch (e) {
              return log('JSON error', e);
            }
          }).call(this));
        }
        return _results;
      }, this));
    };
    UIManager.prototype.scroll_sheets = function(from, step) {
      var i, j, _ref, _ref2;
      if (step == null) {
        step = 0;
      }
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
          if (i < 0) {
            i = 0;
          }
          for (j = i, _ref2 = Math.min(this.pages + i, this.sheets_displayed.length); i <= _ref2 ? j < _ref2 : j > _ref2; i <= _ref2 ? j++ : j--) {
            this.show_page(this.sheets_displayed[j].template_id, this.sheets_displayed[j], "#page" + (j - i));
          }
          return;
        }
      }
    };
    UIManager.prototype.show_page = function(template_id, sheet, place) {
      var conf, config, name, p, renderer, template, _ref;
      if (place == null) {
        place = '#page0';
      }
      log('Show page', template_id, sheet, place);
      if (!this.templates || !this.templates[template_id]) {
        return this.show_error('No template found');
      }
      template = this.templates[template_id];
      sheet.template_id = template_id;
      config = {};
      if (template.protocol && sheet.code) {
        _ref = template.protocol;
        for (name in _ref) {
          conf = _ref[name];
          p = this.protocols[name];
          if (!p) {
            continue;
          }
          p.prepare(sheet, sheet.code);
        }
      }
      renderer = new Renderer(this.manager, this, $(place), template, sheet);
      renderer.on_sheet_change = __bind(function() {
        return this.show_sheets(null);
      }, this);
      return renderer.render(null);
    };
    UIManager.prototype.move_sheet = function(item, before) {
      var i, itm, last, to_save, _results;
      to_save = this.manager.sortArray(this.sheets, item, before);
      to_save.push(item);
      _results = [];
      for (i in to_save) {
        itm = to_save[i];
        last = parseInt(i) === to_save.length - 1;
        _results.push(__bind(function(last) {
          return this.manager.saveSheet(itm, __bind(function() {
            if (last) {
              return this.show_sheets(null);
            }
          }, this));
        }, this)(last));
      }
      return _results;
    };
    UIManager.prototype.show_sheets = function() {
      var ul;
      ul = $('#sheets').empty();
      return this.manager.getSheets(__bind(function(err, data) {
        var index, item, li, _fn, _fn2, _i, _len, _ref;
        if (err) {
          return this.show_error(err);
        }
        this.sheets = data;
        this.sheets_displayed = [];
        index = 0;
        _fn = __bind(function(item) {
          return li.droppable({
            accept: '.sheet',
            hoverClass: 'trash_drop',
            tolerance: 'pointer',
            drop: __bind(function(event, ui) {
              this.move_sheet(ui.draggable.data('item'), item);
              return event.preventDefault();
            }, this)
          });
        }, this);
        _fn2 = __bind(function(item) {
          return li.bind('dblclick', __bind(function() {
            return this.scroll_sheets(item.id);
          }, this));
        }, this);
        for (_i = 0, _len = data.length; _i < _len; _i++) {
          item = data[_i];
          if (((_ref = item.archived) != null ? _ref : null) !== this.archive_state) {
            continue;
          }
          this.sheets_displayed.push(item);
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
          hoverClass: 'trash_drop',
          tolerance: 'pointer',
          drop: __bind(function(event, ui) {
            this.move_sheet(ui.draggable.data('item'));
            return event.preventDefault();
          }, this)
        });
      }, this));
    };
    UIManager.prototype.new_sheet = function() {
      var ul;
      $('#new_sheet_dialog').dialog({
        width: 400,
        height: 200
      });
      ul = $('#new_sheet_templates').empty();
      return this.manager.getTemplates(__bind(function(err, data) {
        var item, li, tmpl, _fn, _i, _len;
        if (err) {
          return this.show_error(err);
        }
        _fn = __bind(function(item) {
          li.bind('mousedown', __bind(function(e) {
            return false;
          }, this));
          return li.bind('dblclick', __bind(function(e) {
            $('#new_sheet_dialog').dialog('close');
            this.show_page(item.id, {});
            return false;
          }, this));
        }, this);
        for (_i = 0, _len = data.length; _i < _len; _i++) {
          item = data[_i];
          tmpl = this.templates[item.id];
          if (!tmpl) {
            continue;
          }
          if (!tmpl.direct) {
            continue;
          }
          li = $('<div/>').addClass('new_sheet_template').appendTo(ul);
          li.text(item.name);
          _fn(item);
        }
        return $('<div/>').addClass('clear').appendTo(ul);
      }, this));
    };
    return UIManager;
  })();
  window.UIManager = UIManager;
}).call(this);
