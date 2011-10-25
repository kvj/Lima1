(function() {
  var CheckElement, ColsElement, DateElement, DateProtocol, HRElement, ItemProtocol, ListElement, MarkElement, Protocol, Renderer, SimpleElement, Textlement, Title1Element, TitleElement, UIElement, log, w1, w2;
  var __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  }, __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }, __slice = Array.prototype.slice;
  w2 = {
    defaults: {
      title: 'Actions'
    },
    flow: [
      {
        type: 'title',
        name: 'Actions',
        edit: '@:title'
      }, {
        type: 'hr'
      }, {
        type: 'list',
        grid: 1,
        area: 'main',
        config: {
          grid: 1,
          delimiter: 1
        },
        flow: [
          {
            type: 'cols',
            size: [20, 1, 35],
            flow: [
              {
                type: 'check',
                edit: '@:done'
              }, {
                type: 'text',
                edit: '@:text'
              }, {
                type: 'date',
                edit: '@:due'
              }
            ]
          }, {
            type: 'cols',
            size: [15, 1],
            flow: [
              {
                type: 'mark',
                edit: '@:mark'
              }, {
                type: 'text',
                edit: '@:notes'
              }
            ]
          }
        ]
      }
    ]
  };
  w1 = {
    code: 'w1${dt:(e1)ddmmyyyy}',
    protocol: {
      dt: {
        'e': [1, 2, 3]
      }
    },
    defaults: {
      title: '${dt:(e1)yyyy}: ${dt:(e1)MM/dd} - ${dt:(e7)MM/dd}',
      dt: '${dt:(e1)}'
    },
    flow: [
      {
        type: 'title',
        name: 'Week ${dt:ww}/${dt:yyyy}',
        edit: '@:title'
      }, {
        type: 'hr'
      }, {
        type: 'title1',
        name: '${dt:(e1)dd} Monday'
      }, {
        type: 'cols',
        size: [0.5, 0.5],
        space: 10,
        flow: [
          {
            type: 'list',
            area: 'd1',
            drag: 'right',
            grid: 1,
            delimiter: 1,
            defaults: {
              due: '${dt:(e1)}'
            },
            flow: [
              {
                type: 'text',
                edit: '@:text'
              }
            ]
          }, {
            flow: [
              {
                type: 'list',
                area: 't1',
                grid: 1,
                delimiter: 1,
                defaults: {
                  due: '${dt:(e1)}'
                },
                flow: [
                  {
                    type: 'cols',
                    size: [15, 1],
                    flow: [
                      {
                        type: 'check',
                        edit: '@:done'
                      }, {
                        type: 'text',
                        edit: '@:text'
                      }
                    ]
                  }
                ]
              }, {
                type: 'list',
                area: 'n1',
                grid: 2,
                delimiter: 2,
                flow: [
                  {
                    type: 'text',
                    edit: '@:text'
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
      if (options.empty) {
        return handler;
      }
      $('<div/>').addClass('check_editor').appendTo(element);
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
      if (dt.fromString(date)) {
        return el.text(dt.format('MM/dd'));
      } else {
        return el.html('&nbsp;');
      }
    };
    DateElement.prototype.render = function(item, config, element, options, handler) {
      var el;
      if (options.empty) {
        return handler;
      }
      el = $('<div/>').addClass('date_editor').appendTo(element);
      el.bind('click', __bind(function(e) {
        return el.datepicker('dialog', null, __bind(function(date) {
          return this.print_date(date, el);
        }, this), {
          dateFormat: 'yymmdd'
        }, e);
      }, this));
      $('<div style="clear: both;"/>').appendTo(element);
      this.print_date('', el);
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
        el.draggable({
          revert: true,
          handle: handle,
          zIndex: 3,
          containment: 'document',
          helper: 'clone'
        });
      }
      el.droppable({
        accept: '.list_item',
        hoverClass: 'list_item_drop',
        tolerance: 'pointer'
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
      return this._render({}, config, element, {
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
        return this._render({}, config, element, {
          disable: false
        }, handler);
      }, this));
    };
    return ListElement;
  })();
  Renderer = (function() {
    function Renderer(root, template, data, env) {
      this.root = root;
      this.template = template;
      this.data = data;
      this.env = env;
      this.elements = [new SimpleElement(this), new TitleElement(this), new HRElement(this), new Title1Element(this), new ColsElement(this), new ListElement(this), new Textlement(this), new CheckElement(this), new MarkElement(this), new DateElement(this)];
      this.protocols = [new DateProtocol('dt'), new ItemProtocol('@')];
    }
    Renderer.prototype.fix_grid = function(element, config) {
      var gr;
      if (!config || !config.grid) {
        return;
      }
      gr = config.grid;
      element.children().removeClass('grid_top' + gr + ' grid_bottom' + gr).addClass('grid' + gr);
      if (config.delimiter) {
        element.children().addClass('grid_delimiter' + config.delimiter);
      }
      element.children().first().addClass('grid_top' + gr);
      return element.children().last().addClass('grid_bottom' + gr);
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
      var exp, m, p, text, value, _i, _len, _ref;
      text = txt;
      exp = /\$\{([a-z\@]+\:)([a-zA-Z0-9\s\(\)\+\-\_\/\:\.]*)\}/;
      while (m = text.match(exp)) {
        if (!m) {
          return text;
        }
        value = '';
        _ref = this.protocols;
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          p = _ref[_i];
          if (p.name + ':' === m[1]) {
            value = p.convert(m[2], this.env);
            break;
          }
        }
        text = text.replace(m[0], value != null ? value : '');
      }
      return text;
    };
    Renderer.prototype.replace = function(text, item) {
      var exp, m, p, value, _i, _len, _ref;
      exp = /^([a-z\@]+\:)([a-zA-Z0-9\s\(\)\+\-\_\/\:\.]*)$/;
      if (m = text.match(exp)) {
        value = '';
        _ref = this.protocols;
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          p = _ref[_i];
          if (p.name + ':' === m[1]) {
            value = p.convert(m[2], this.env);
            break;
          }
        }
        return value;
      }
      return text;
    };
    Renderer.prototype.text_editor = function(element, item, property, handler) {
      if (!property) {
        return;
      }
      element.attr('contentEditable', true);
      element.text(item[property]);
      element.bind('keypress', __bind(function(event) {
        if (event.keyCode === 13) {
          element.blur();
          event.preventDefault();
          return false;
        }
      }, this));
      return element.bind('blur', __bind(function(event) {
        element.text(element.text());
        if (handler) {
          return handler(item, property, element.text());
        }
      }, this));
    };
    Renderer.prototype.render = function() {
      this.root.empty();
      this.full = false;
      this.content = $('<div/>').addClass('page_content group').appendTo(this.root);
      return this.get(this.template.name).render(this.data, this.template, this.content, {
        empty: false
      }, __bind(function() {
        return this.fix_height(null);
      }, this));
    };
    Renderer.prototype.size_too_big = function() {
      return this.root.innerHeight() < this.content.outerHeight(true);
    };
    Renderer.prototype.fix_height = function() {
      this.have_space = false;
      if (!this.full) {
        return this.get(this.template.name).render(this.data, this.template, this.content, {
          empty: true
        }, __bind(function() {
          if (this.have_space) {
            return this.fix_height(null);
          }
        }, this));
      }
    };
    Renderer.prototype.items = function(area, handler) {
      return handler([
        {
          text: '01234567890012345678901234567890'
        }
      ]);
    };
    return Renderer;
  })();
  Protocol = (function() {
    function Protocol(name) {
      this.name = name;
    }
    Protocol.prototype.convert = function(text, value) {
      return null;
    };
    Protocol.prototype.accept = function(config, value) {
      return false;
    };
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
    DateProtocol.prototype.convert = function(text, value) {
      var dt, exp, format, item, m, method, mexp, mm, mmm, modifiers, val, _i, _len, _ref;
      dt = new Date(value.dt);
      exp = /^(\((([ewmdy][+-]?[0-9]+)+)\))?([EwMdy\/\:\.]*)$/;
      m = text.match(exp);
      if (!m) {
        return text;
      }
      modifiers = m[2];
      format = m[4] && m[4].length > 0 ? m[4] : 'yyyymmdd';
      if (modifiers) {
        mexp = /([ewmdy][+-]?[0-9]+)/g;
        mm = modifiers.match(mexp);
        for (_i = 0, _len = mm.length; _i < _len; _i++) {
          item = mm[_i];
          mmm = item.match(/([ewmdy])([+-]?)([0-9]+)/);
          if (mmm) {
            method = '';
            switch (mmm[1]) {
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
            val = parseInt(mmm[3]);
            if (mmm[2]) {
              dt['set' + method](dt['get' + method]() + (((_ref = mmm[2] === '+') != null ? _ref : val) ? void 0 : -val));
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
  log = function() {
    var params;
    params = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
    return console.log.apply(console, params);
  };
  $(document).ready(function() {
    var renderer;
    renderer = new Renderer($('#page1'), w1, {}, {
      dt: new Date().getTime()
    });
    renderer.render(null);
    renderer = new Renderer($('#page2'), w2, {}, {
      dt: new Date().getTime()
    });
    renderer.render(null);
    return $('button').button();
  });
}).call(this);
