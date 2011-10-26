(function() {
  var UIManager;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; };
  UIManager = (function() {
    function UIManager(manager) {
      this.manager = manager;
      log('Create UI...');
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
      $('#remove_template').bind('click', __bind(function() {
        return this.remove_template(null);
      }, this));
      $('#save_template').bind('click', __bind(function() {
        return this.save_template(null);
      }, this));
      $('#trash').droppable({
        accept: '.list_item, .sheet',
        hoverClass: 'trash_drop',
        tolerance: 'pointer',
        drop: __bind(function(event, ui) {
          this.remove_drop(ui.draggable);
          return event.preventDefault();
        }, this)
      });
      $('.page').droppable({
        accept: '.sheet',
        hoverClass: 'trash_drop',
        tolerance: 'pointer',
        drop: __bind(function(event, ui) {
          var item;
          item = ui.draggable.data('item');
          log('Drop', event.target);
          this.show_page(item.template_id, item, '#' + $(event.target).attr('id'));
          return event.preventDefault();
        }, this)
      });
    }
    UIManager.prototype.remove_drop = function(drag) {
      log('Remove', drag.data('type'));
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
      log('Error', message);
      return alert(message);
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
    UIManager.prototype.show_page = function(template_id, sheet, place) {
      var renderer;
      if (place == null) {
        place = '#page0';
      }
      if (!this.templates || !this.templates[template_id]) {
        return this.show_error('No template found');
      }
      sheet.template_id = template_id;
      renderer = new Renderer(this.manager, this, $(place), this.templates[template_id], sheet, {});
      renderer.on_sheet_change = __bind(function() {
        return this.show_sheets(null);
      }, this);
      return renderer.render(null);
    };
    UIManager.prototype.show_sheets = function() {
      var ul;
      ul = $('#sheets').empty();
      return this.manager.getSheets(__bind(function(err, data) {
        var item, li, _fn, _i, _len;
        if (err) {
          return this.show_error(err);
        }
        _fn = __bind(function(item) {
          return li.bind('dblclick', __bind(function() {
            return this.show_page(item.template_id, item);
          }, this));
        }, this);
        for (_i = 0, _len = data.length; _i < _len; _i++) {
          item = data[_i];
          li = $('<li/>').addClass('sheet').appendTo(ul);
          li.data('type', 'sheet');
          li.data('item', item);
          li.text(item.title);
          _fn(item);
        }
        return ul.sortable({
          zIndex: 3,
          containment: 'document',
          helper: 'clone',
          appendTo: 'body'
        });
      }, this));
    };
    UIManager.prototype.new_sheet = function() {
      var ul;
      log('Show new sheet dialog');
      $('#new_sheet_dialog').dialog({
        width: 400,
        height: 200
      });
      ul = $('#new_sheet_templates').empty();
      return this.manager.getTemplates(__bind(function(err, data) {
        var item, li, _fn, _i, _len;
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
