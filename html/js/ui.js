(function() {
  var CheckElement, ColsElement, DateElement, HRElement, ListElement, MarkElement, Renderer, SimpleElement, Textlement, Title1Element, TitleElement, UIElement, w1, w2, w47, wnotes;
  var __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  }, __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }, __slice = Array.prototype.slice;
  w2 = {
    "defaults": {
      "title": "Actions"
    },
    "direct": true,
    "flow": [
      {
        "type": "title",
        "name": "Actions",
        "edit": "@:title"
      }, {
        "type": "hr"
      }, {
        "type": "list",
        "grid": 1,
        "area": "main",
        "config": {
          "grid": 1,
          "delimiter": 1
        },
        "flow": [
          {
            "type": "cols",
            "size": [20, 1, 35],
            "flow": [
              {
                "type": "check",
                "edit": "@:done"
              }, {
                "type": "text",
                "edit": "@:text"
              }, {
                "type": "date",
                "edit": "@:due"
              }
            ]
          }, {
            "type": "cols",
            "size": [15, 1],
            "flow": [
              {
                "type": "mark",
                "edit": "@:mark"
              }, {
                "type": "text",
                "edit": "@:notes"
              }
            ]
          }
        ]
      }
    ]
  };
  wnotes = {
    "defaults": {
      "title": "Notes"
    },
    "direct": true,
    "flow": [
      {
        "type": "title",
        "name": "Notes",
        "edit": "@:title"
      }, {
        "type": "hr"
      }, {
        "type": "list",
        "grid": 2,
        "delimiter": 2,
        "area": "main",
        "flow": [
          {
            "type": "text",
            "edit": "@:text"
          }
        ]
      }
    ]
  };
  w1 = {
    "code": "w13:${dt:(e1)}",
    "protocol": {
      "dt": {
        "e": [1, 2, 3]
      }
    },
    "defaults": {
      "title": "${dt:(e1)yyyy}: ${dt:(e1)MM/dd} - ${dt:(e3)MM/dd}",
      "dt": "${dt:(e1)}"
    },
    "flow": [
      {
        "type": "title",
        "name": "Week ${dt:ww}/${dt:yyyy}",
        "edit": "@:title"
      }, {
        "type": "hr"
      }, {
        "type": "title1",
        "name": "${dt:(e1)dd} Monday"
      }, {
        "type": "cols",
        "size": [0.5, 0.5],
        "space": 10,
        "flow": [
          {
            "type": "list",
            "area": "d1",
            "drag": "right",
            "grid": 1,
            "delimiter": 1,
            "defaults": {
              "due": "${dt:(e1)}"
            },
            "flow": [
              {
                "type": "text",
                "edit": "@:text"
              }
            ]
          }, {
            "flow": [
              {
                "type": "list",
                "area": "t1",
                "grid": 1,
                "delimiter": 1,
                "defaults": {
                  "due": "${dt:(e1)}"
                },
                "flow": [
                  {
                    "type": "cols",
                    "size": [15, 1],
                    "flow": [
                      {
                        "type": "check",
                        "edit": "@:done"
                      }, {
                        "type": "text",
                        "edit": "@:text"
                      }
                    ]
                  }
                ]
              }, {
                "type": "list",
                "area": "n1",
                "grid": 2,
                "delimiter": 2,
                "flow": [
                  {
                    "type": "text",
                    "edit": "@:text"
                  }
                ]
              }
            ]
          }
        ]
      }, {
        "type": "title1",
        "name": "${dt:(e2)dd} Tuesday"
      }, {
        "type": "cols",
        "size": [0.5, 0.5],
        "space": 10,
        "flow": [
          {
            "type": "list",
            "area": "d2",
            "drag": "right",
            "grid": 1,
            "delimiter": 1,
            "defaults": {
              "due": "${dt:(e2)}"
            },
            "flow": [
              {
                "type": "text",
                "edit": "@:text"
              }
            ]
          }, {
            "flow": [
              {
                "type": "list",
                "area": "t2",
                "grid": 1,
                "delimiter": 1,
                "defaults": {
                  "due": "${dt:(e2)}"
                },
                "flow": [
                  {
                    "type": "cols",
                    "size": [15, 1],
                    "flow": [
                      {
                        "type": "check",
                        "edit": "@:done"
                      }, {
                        "type": "text",
                        "edit": "@:text"
                      }
                    ]
                  }
                ]
              }, {
                "type": "list",
                "area": "n2",
                "grid": 2,
                "delimiter": 2,
                "flow": [
                  {
                    "type": "text",
                    "edit": "@:text"
                  }
                ]
              }
            ]
          }
        ]
      }, {
        "type": "title1",
        "name": "${dt:(e3)dd} Wednesday"
      }, {
        "type": "cols",
        "size": [0.5, 0.5],
        "space": 10,
        "flow": [
          {
            "type": "list",
            "area": "d3",
            "drag": "right",
            "grid": 1,
            "delimiter": 1,
            "defaults": {
              "due": "${dt:(e3)}"
            },
            "flow": [
              {
                "type": "text",
                "edit": "@:text"
              }
            ]
          }, {
            "flow": [
              {
                "type": "list",
                "area": "t3",
                "grid": 1,
                "delimiter": 1,
                "defaults": {
                  "due": "${dt:(e3)}"
                },
                "flow": [
                  {
                    "type": "cols",
                    "size": [15, 1],
                    "flow": [
                      {
                        "type": "check",
                        "edit": "@:done"
                      }, {
                        "type": "text",
                        "edit": "@:text"
                      }
                    ]
                  }
                ]
              }, {
                "type": "list",
                "area": "n3",
                "grid": 2,
                "delimiter": 2,
                "flow": [
                  {
                    "type": "text",
                    "edit": "@:text"
                  }
                ]
              }
            ]
          }
        ]
      }
    ]
  };
  w47 = {
    "code": "w47:${dt:(e1)}",
    "protocol": {
      "dt": {
        "e": [4, 5, 6, 0]
      }
    },
    "defaults": {
      "title": "${dt:(e4)yyyy}: ${dt:(e4)MM/dd} - ${dt:(e7)MM/dd}",
      "dt": "${dt:(e4)}"
    },
    "flow": [
      {
        "type": "title",
        "name": "Week ${dt:ww}/${dt:yyyy}",
        "edit": "@:title"
      }, {
        "type": "hr"
      }, {
        "type": "title1",
        "name": "${dt:(e4)dd} Thursday"
      }, {
        "type": "cols",
        "size": [0.5, 0.5],
        "space": 10,
        "flow": [
          {
            "type": "list",
            "area": "d1",
            "drag": "right",
            "grid": 1,
            "delimiter": 1,
            "defaults": {
              "due": "${dt:(e4)}"
            },
            "flow": [
              {
                "type": "text",
                "edit": "@:text"
              }
            ]
          }, {
            "flow": [
              {
                "type": "list",
                "area": "t1",
                "grid": 1,
                "delimiter": 1,
                "defaults": {
                  "due": "${dt:(e4)}"
                },
                "flow": [
                  {
                    "type": "cols",
                    "size": [15, 1],
                    "flow": [
                      {
                        "type": "check",
                        "edit": "@:done"
                      }, {
                        "type": "text",
                        "edit": "@:text"
                      }
                    ]
                  }
                ]
              }, {
                "type": "list",
                "area": "n1",
                "grid": 2,
                "delimiter": 2,
                "flow": [
                  {
                    "type": "text",
                    "edit": "@:text"
                  }
                ]
              }
            ]
          }
        ]
      }, {
        "type": "title1",
        "name": "${dt:(e5)dd} Friday"
      }, {
        "type": "cols",
        "size": [0.5, 0.5],
        "space": 10,
        "flow": [
          {
            "type": "list",
            "area": "d2",
            "drag": "right",
            "grid": 1,
            "delimiter": 1,
            "defaults": {
              "due": "${dt:(e5)}"
            },
            "flow": [
              {
                "type": "text",
                "edit": "@:text"
              }
            ]
          }, {
            "flow": [
              {
                "type": "list",
                "area": "t2",
                "grid": 1,
                "delimiter": 1,
                "defaults": {
                  "due": "${dt:(e5)}"
                },
                "flow": [
                  {
                    "type": "cols",
                    "size": [15, 1],
                    "flow": [
                      {
                        "type": "check",
                        "edit": "@:done"
                      }, {
                        "type": "text",
                        "edit": "@:text"
                      }
                    ]
                  }
                ]
              }, {
                "type": "list",
                "area": "n2",
                "grid": 2,
                "delimiter": 2,
                "flow": [
                  {
                    "type": "text",
                    "edit": "@:text"
                  }
                ]
              }
            ]
          }
        ]
      }, {
        "type": "title1",
        "name": "${dt:(e6)dd}, ${dt:(e7)dd} Weekend"
      }, {
        "type": "cols",
        "size": [0.5, 0.5],
        "space": 10,
        "flow": [
          {
            "type": "list",
            "area": "d3",
            "drag": "right",
            "grid": 1,
            "delimiter": 1,
            "flow": [
              {
                "type": "text",
                "edit": "@:text"
              }
            ]
          }, {
            "flow": [
              {
                "type": "list",
                "area": "t3",
                "grid": 1,
                "delimiter": 1,
                "flow": [
                  {
                    "type": "cols",
                    "size": [15, 1],
                    "flow": [
                      {
                        "type": "check",
                        "edit": "@:done"
                      }, {
                        "type": "text",
                        "edit": "@:text"
                      }
                    ]
                  }
                ]
              }, {
                "type": "list",
                "area": "n3",
                "grid": 2,
                "delimiter": 2,
                "flow": [
                  {
                    "type": "text",
                    "edit": "@:text"
                  }
                ]
              }
            ]
          }
        ]
      }
    ]
  };
  UIElement = (function() {
    function UIElement(renderer) {
      this.renderer = renderer;
    }
    UIElement.prototype.child = function(element, cl, index) {
      var child;
      child = element.children(cl).eq(index);
      if (child.size() === 1) {
        return child;
      } else {
        return null;
      }
    };
    UIElement.prototype.render = function(item, config, element, options, handler) {
      return handler(null);
    };
    return UIElement;
  })();
  SimpleElement = (function() {
    __extends(SimpleElement, UIElement);
    function SimpleElement() {
      SimpleElement.__super__.constructor.apply(this, arguments);
    }
    SimpleElement.prototype.name = 'simple';
    SimpleElement.prototype.render = function(item, config, element, options, handler) {
      var fl, flow, i, _ref, _results;
      if (config.defaults && !options.empty) {
        this.renderer.applyDefaults(config.defaults, item);
      }
      flow = (_ref = config.flow) != null ? _ref : [];
      _results = [];
      for (i in flow) {
        fl = flow[i];
        i = parseInt(i);
        _results.push(__bind(function(i) {
          var el, _ref2;
          el = (_ref2 = this.child(element, '.simple', i)) != null ? _ref2 : $('<div/>').addClass('simple').appendTo(element);
          return this.renderer.get(fl.type).render(item, fl, el, options, __bind(function() {
            if (i === flow.length - 1) {
              this.renderer.fix_grid(element, config.type === 'list' ? config.config : config);
              return handler(null);
            }
          }, this));
        }, this)(i));
      }
      return _results;
    };
    return SimpleElement;
  })();
  TitleElement = (function() {
    __extends(TitleElement, UIElement);
    function TitleElement() {
      TitleElement.__super__.constructor.apply(this, arguments);
    }
    TitleElement.prototype.name = 'title';
    TitleElement.prototype.render = function(item, config, element, options, handler) {
      var el, _ref;
      if (options.empty) {
        return handler;
      }
      $('<span/>').addClass('title0_text').appendTo(element).text(this.renderer.inject((_ref = config.name) != null ? _ref : ' '));
      if (config.edit) {
        el = $('<span/>').addClass('text_editor title0_editor').appendTo(element);
        this.renderer.text_editor(el, item, this.renderer.replace(config.edit));
      }
      return handler(null);
    };
    return TitleElement;
  })();
  Textlement = (function() {
    __extends(Textlement, UIElement);
    function Textlement() {
      Textlement.__super__.constructor.apply(this, arguments);
    }
    Textlement.prototype.name = 'text';
    Textlement.prototype.render = function(item, config, element, options, handler) {
      var el;
      if (options.empty) {
        return handler;
      }
      el = $('<div/>').addClass('text_editor').appendTo(element);
      if (config.edit && !options.readonly) {
        this.renderer.text_editor(el, item, this.renderer.replace(config.edit));
      }
      return handler(null);
    };
    return Textlement;
  })();
  CheckElement = (function() {
    __extends(CheckElement, UIElement);
    function CheckElement() {
      CheckElement.__super__.constructor.apply(this, arguments);
    }
    CheckElement.prototype.name = 'check';
    CheckElement.prototype.render = function(item, config, element, options, handler) {
      var checked, el, property;
      if (options.empty) {
        return handler;
      }
      el = $('<div/>').addClass('check_editor').appendTo(element);
      property = this.renderer.replace(config.edit);
      checked = false;
      if (property && item[property] === 1) {
        checked = true;
        el.addClass('checked');
      }
      if (!options.readonly) {
        el.bind('click', __bind(function(event) {
          this.renderer.on_edited(item, property, checked ? null : 1);
          return false;
        }, this));
      }
      return handler(null);
    };
    return CheckElement;
  })();
  DateElement = (function() {
    __extends(DateElement, UIElement);
    function DateElement() {
      DateElement.__super__.constructor.apply(this, arguments);
    }
    DateElement.prototype.name = 'date';
    DateElement.prototype.print_date = function(date, el) {
      var dt;
      dt = new Date();
      if (date && dt.fromString(date)) {
        el.text(dt.format('MM/dd'));
        return true;
      } else {
        el.html('&nbsp;');
        return false;
      }
    };
    DateElement.prototype.render = function(item, config, element, options, handler) {
      var el, property;
      if (options.empty) {
        return handler;
      }
      el = $('<div/>').addClass('date_editor').appendTo(element);
      property = this.renderer.replace(config.edit);
      this.print_date(item[property], el);
      el.bind('click', __bind(function(e) {
        if (e.shiftKey) {
          return this.renderer.on_edited(item, property, null);
        } else {
          return el.datepicker('dialog', null, __bind(function(date) {
            if (this.print_date(date, el)) {
              return this.renderer.on_edited(item, property, date);
            }
          }, this), {
            dateFormat: 'yymmdd'
          }, e);
        }
      }, this));
      $('<div style="clear: both;"/>').appendTo(element);
      return handler(null);
    };
    return DateElement;
  })();
  MarkElement = (function() {
    __extends(MarkElement, UIElement);
    function MarkElement() {
      MarkElement.__super__.constructor.apply(this, arguments);
    }
    MarkElement.prototype.name = 'mark';
    MarkElement.prototype.render = function(item, config, element, options, handler) {
      if (options.empty) {
        return handler;
      }
      $('<div/>').addClass('mark_editor').appendTo(element);
      return handler(null);
    };
    return MarkElement;
  })();
  HRElement = (function() {
    __extends(HRElement, UIElement);
    function HRElement() {
      HRElement.__super__.constructor.apply(this, arguments);
    }
    HRElement.prototype.name = 'hr';
    HRElement.prototype.render = function(item, config, element, options, handler) {
      if (options.empty) {
        return handler;
      }
      $('<div/>').addClass('hr').appendTo(element);
      return handler(null);
    };
    return HRElement;
  })();
  Title1Element = (function() {
    __extends(Title1Element, UIElement);
    function Title1Element() {
      Title1Element.__super__.constructor.apply(this, arguments);
    }
    Title1Element.prototype.name = 'title1';
    Title1Element.prototype.render = function(item, config, element, options, handler) {
      var bg;
      if (options.empty) {
        return handler;
      }
      bg = $('<div/>').addClass('title1_bg').appendTo(element);
      $('<div/>').addClass('title1').appendTo(bg).text(this.renderer.inject(config.name, item));
      $('<div style="clear: both;"/>').appendTo(element);
      return handler(null);
    };
    return Title1Element;
  })();
  ColsElement = (function() {
    __extends(ColsElement, UIElement);
    function ColsElement() {
      ColsElement.__super__.constructor.apply(this, arguments);
    }
    ColsElement.prototype.name = 'cols';
    ColsElement.prototype.render = function(item, config, element, options, handler) {
      var el, fixed_size, fl, float_size, flow, i, index, lsizes, margin, sizes, sz, w, width, _i, _len, _ref, _ref2, _ref3, _results;
      flow = (_ref = config.flow) != null ? _ref : [];
      sizes = (_ref2 = config.size) != null ? _ref2 : [];
      if (flow.length !== sizes.length) {
        return handler;
      }
      w = element.innerWidth() - 4;
      fixed_size = 0;
      if (config.space) {
        fixed_size += config.space * (flow.length - 1);
      }
      float_size = 0;
      lsizes = [];
      for (_i = 0, _len = sizes.length; _i < _len; _i++) {
        sz = sizes[_i];
        lsizes.push(sz);
        if (sz > 1) {
          fixed_size += sz;
        } else {
          float_size += sz;
        }
      }
      if (float_size > 0) {
        for (i in lsizes) {
          sz = lsizes[i];
          if (sz <= 1) {
            lsizes[i] = Math.floor((w - fixed_size) * sz / float_size);
          }
        }
      }
      margin = 0;
      if (!options.empty) {
        element.addClass('group');
      }
      index = 0;
      _results = [];
      for (i in flow) {
        fl = flow[i];
        i = parseInt(i);
        if (i > 0 && config.space) {
          index++;
          if (!options.empty) {
            $('<div/>').appendTo(element).addClass('col').width(config.space).html('&nbsp;');
          }
        }
        width = lsizes[i];
        el = (_ref3 = this.child(element, '.col', index)) != null ? _ref3 : $('<div/>').addClass('col').appendTo(element).width(width);
        if (i === flow.length - 1) {
          if (!options.empty) {
            $('<div style="clear: both;"/>').appendTo(element);
          }
        }
        index++;
        margin += width;
        _results.push(__bind(function(i) {
          return this.renderer.get(fl.type).render(item, fl, el, options, __bind(function() {
            if (i === flow.length - 1) {
              return handler(null);
            }
          }, this));
        }, this)(i));
      }
      return _results;
    };
    return ColsElement;
  })();
  ListElement = (function() {
    __extends(ListElement, UIElement);
    function ListElement() {
      ListElement.__super__.constructor.apply(this, arguments);
    }
    ListElement.prototype.name = 'list';
    ListElement.prototype._render = function(item, config, element, options, handler) {
      var el, handle;
      el = $('<div/>').addClass('list_item').appendTo(element);
      if (options.disable) {
        el.addClass('disabled');
      }
      if (options.draggable) {
        handle = $('<div/>').addClass('list_item_handle').appendTo(el);
        if (config.drag === 'right') {
          handle.addClass('list_item_handle_right');
        } else {
          handle.addClass('list_item_handle_left');
        }
        el.bind('mousemove', __bind(function() {
          return handle.show();
        }, this));
        el.bind('mouseout', __bind(function() {
          return handle.hide();
        }, this));
        el.data('type', 'note');
        el.data('renderer', this.renderer);
        el.data('item', item);
        el.draggable({
          handle: handle,
          zIndex: 3,
          containment: 'document',
          helper: 'clone',
          appendTo: 'body'
        });
      }
      el.droppable({
        accept: '.list_item',
        hoverClass: 'list_item_drop',
        tolerance: 'pointer',
        drop: __bind(function(event, ui) {
          var drop, renderer;
          drop = ui.draggable.data('item');
          renderer = ui.draggable.data('renderer');
          return this.renderer.move_note(drop, item.area, (item.id ? item : null), __bind(function() {
            if (renderer !== this.renderer) {
              renderer.render(null);
            }
            return this.renderer.render(null);
          }, this));
        }, this)
      });
      return this.renderer.get('simple').render(item, config, el, {
        empty: false,
        readonly: options.disable
      }, __bind(function() {
        return handler(el);
      }, this));
    };
    ListElement.prototype._fill_empty = function(config, element, parent, handler) {
      var parent_height;
      parent_height = parent.outerHeight();
      return this._render({
        area: config.area
      }, config, element, {
        disable: true
      }, __bind(function(el) {
        var need_grid;
        need_grid = true;
        if (parent.outerHeight() <= parent_height) {
          this.renderer.have_space = true;
        } else {
          if (this.renderer.size_too_big()) {
            need_grid = false;
            el.remove();
          } else {
            this.renderer.have_space = true;
          }
        }
        if (need_grid) {
          this.renderer.fix_grid(element, config);
        }
        return handler(null);
      }, this));
    };
    ListElement.prototype.render = function(item, config, element, options, handler) {
      var flow, parent, _ref;
      flow = (_ref = config.flow) != null ? _ref : [];
      if (options.empty) {
        parent = element.parents('.group').last();
        return this._fill_empty(config, element, parent, handler);
      }
      return this.renderer.items(config.area, __bind(function(items) {
        var i, itm;
        for (i in items) {
          itm = items[i];
          this._render(itm, config, element, {
            disable: false,
            draggable: true
          }, __bind(function() {}, this));
        }
        return this._render({
          area: config.area
        }, config, element, {
          disable: false
        }, __bind(function() {
          this.renderer.fix_grid(element, config);
          return handler(null);
        }, this));
      }, this));
    };
    return ListElement;
  })();
  Renderer = (function() {
    function Renderer(manager, ui, root, template, data, env) {
      this.manager = manager;
      this.ui = ui;
      this.root = root;
      this.template = template;
      this.data = data;
      this.env = env;
      this.elements = [new SimpleElement(this), new TitleElement(this), new HRElement(this), new Title1Element(this), new ColsElement(this), new ListElement(this), new Textlement(this), new CheckElement(this), new MarkElement(this), new DateElement(this)];
      this.root.data('sheet', this.data);
    }
    Renderer.prototype.fix_grid = function(element, config) {
      var gr;
      if (!config || !config.grid) {
        return;
      }
      gr = config.grid;
      element.children(':not(.list_item_handle)').removeClass('grid_top' + gr + ' grid_bottom' + gr).addClass('grid' + gr);
      if (config.delimiter) {
        element.children(':not(.list_item_handle)').addClass('grid_delimiter' + config.delimiter);
      }
      element.children(':not(.list_item_handle)').first().addClass('grid_top' + gr);
      return element.children(':not(.list_item_handle)').last().addClass('grid_bottom' + gr);
    };
    Renderer.prototype.get = function(name) {
      var el, _i, _len, _ref;
      _ref = this.elements;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        el = _ref[_i];
        if (el.name === name) {
          return el;
        }
      }
      return this.elements[0];
    };
    Renderer.prototype.applyDefaults = function(config, item) {
      var key, value, _ref, _results;
      _results = [];
      for (key in config) {
        if (!__hasProp.call(config, key)) continue;
        value = config[key];
        _results.push((_ref = item[key]) != null ? _ref : item[key] = this.inject(value, this.env));
      }
      return _results;
    };
    Renderer.prototype.inject = function(txt, item) {
      return this.ui.inject(txt, item, this.env);
    };
    Renderer.prototype.replace = function(text, item) {
      return this.ui.replace(text, item, this.env);
    };
    Renderer.prototype._save_sheet = function(handler) {
      if (this.template.code) {
        this.data.code = this.inject(this.template.code, this.data);
      }
      return this.manager.saveSheet(this.data, __bind(function(err, object) {
        if (err) {
          return this.ui.show_error(err);
        }
        this.on_sheet_change(this.data);
        return handler(object);
      }, this));
    };
    Renderer.prototype.move_note = function(item, area, before, handler) {
      var to_save;
      to_save = this.manager.sortArray(this.items(area), item, before);
      item.area = area;
      return this._save_note(item, __bind(function() {
        var el, i, last, _results;
        if (to_save.length === 0) {
          return handler(item);
        } else {
          _results = [];
          for (i in to_save) {
            el = to_save[i];
            last = parseInt(i) === to_save.length - 1;
            _results.push(__bind(function(last) {
              return this.manager.saveNote(el, __bind(function() {
                if (last) {
                  return handler(item);
                }
              }, this));
            }, this)(last));
          }
          return _results;
        }
      }, this));
    };
    Renderer.prototype._save_note = function(item, handler) {
      var others;
      if (!item.place) {
        others = this.items(item.area);
        this.manager.sortArray(others, item);
      }
      if (!this.data.id) {
        return this._save_sheet(__bind(function(object) {
          item.sheet_id = this.data.id;
          return this.manager.saveNote(item, __bind(function(err, object) {
            if (err) {
              return this.ui.show_error(err);
            }
            return handler(object);
          }, this));
        }, this));
      } else {
        item.sheet_id = this.data.id;
        return this.manager.saveNote(item, __bind(function(err, object) {
          if (err) {
            return this.ui.show_error(err);
          }
          return handler(object);
        }, this));
      }
    };
    Renderer.prototype.on_edited = function(item, property, value) {
      if (!item || !property) {
        return false;
      }
      item[property] = value;
      if (item === this.data) {
        return this._save_sheet(__bind(function(object) {
          return this.render(null);
        }, this));
      } else {
        return this._save_note(item, __bind(function() {
          return this.render(null);
        }, this));
      }
    };
    Renderer.prototype.remove_note = function(item) {
      return this.manager.removeNote(item, __bind(function(err) {
        if (err) {
          return this.ui.show_error(err);
        }
        return this.render(null);
      }, this));
    };
    Renderer.prototype.text_editor = function(element, item, property, handler) {
      var old_value, _ref;
      if (!property) {
        return;
      }
      element.attr('contentEditable', true);
      old_value = (_ref = item[property]) != null ? _ref : '';
      element.text(old_value);
      element.bind('keypress', __bind(function(event) {
        if (event.keyCode === 13) {
          element.blur();
          event.preventDefault();
          return false;
        }
      }, this));
      return element.bind('blur', __bind(function(event) {
        var value, _ref2;
        value = (_ref2 = element.text()) != null ? _ref2 : '';
        element.text(value);
        if (value === old_value) {
          return;
        }
        if (handler) {
          return handler(item, property, value);
        } else {
          return this.on_edited(item, property, value);
        }
      }, this));
    };
    Renderer.prototype.render = function() {
      this.root.addClass('page_render');
      this.prev_content = this.root.children();
      this.content = $('<div/>').addClass('page_content group').prependTo(this.root);
      return this._load_items(__bind(function(data) {
        this.notes = data;
        return this.get(this.template.name).render(this.data, this.template, this.content, {
          empty: false
        }, __bind(function() {
          return this.fix_height(null);
        }, this));
      }, this));
    };
    Renderer.prototype.size_too_big = function() {
      return this.root.innerHeight() < this.content.outerHeight(true);
    };
    Renderer.prototype.fix_height = function() {
      this.have_space = false;
      return this.get(this.template.name).render(this.data, this.template, this.content, {
        empty: true
      }, __bind(function() {
        if (this.have_space) {
          return this.fix_height(null);
        } else {
          this.prev_content.remove();
          return this.root.removeClass('page_render');
        }
      }, this));
    };
    Renderer.prototype._load_items = function(handler) {
      return this.manager.getNotes(this.data.id, null, __bind(function(err, data) {
        if (err) {
          log('Error getting items', err);
          return handler([]);
        } else {
          return handler(data);
        }
      }, this));
    };
    Renderer.prototype.items = function(area, handler) {
      var item, result, _i, _len, _ref;
      result = [];
      _ref = this.notes;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        item = _ref[_i];
        if (item.area === area) {
          result.push(item);
        }
      }
      if (handler) {
        handler(result);
      }
      return result;
    };
    Renderer.prototype.on_sheet_change = function() {};
    return Renderer;
  })();
  window.log = function() {
    var params;
    params = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
    return console.log.apply(console, params);
  };
  window.Renderer = Renderer;
  $(document).ready(function() {
    var db, manager, storage, ui;
    db = new HTML5Provider('test.db', '1.1');
    storage = new StorageProvider(null, db);
    manager = new DataManager(storage);
    ui = new UIManager(manager);
    return ui.start(null);
  });
}).call(this);
