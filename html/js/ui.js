(function() {
  var ColsElement, DateProtocol, HRElement, Protocol, Renderer, SimpleElement, Title1Element, TitleElement, UIElement, inject, log, w1;
  var __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  }, __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }, __slice = Array.prototype.slice;
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
        size: [0.6, 0.4],
        flow: [
          {
            type: 'list',
            area: 'd1',
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
                defaults: {
                  due: '${dt:(e1)}'
                },
                flow: [
                  {
                    type: 'cols',
                    size: [0.1, 0.9],
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
                edit: '@:text',
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
    UIElement.prototype.child = function(element, index) {
      return element.children().eq(index).get(0);
    };
    UIElement.prototype.render = function(item, config, element, empty, handler) {
      return handler;
    };
    return UIElement;
  })();
  SimpleElement = (function() {
    __extends(SimpleElement, UIElement);
    function SimpleElement() {
      SimpleElement.__super__.constructor.apply(this, arguments);
    }
    SimpleElement.prototype.name = 'simple';
    SimpleElement.prototype.render = function(item, config, element, empty, handler) {
      var fl, flow, i, _ref, _results;
      if (config.defaults && !empty) {
        this.renderer.applyDefaults(config.defaults, item);
        log('After apply item', item);
      }
      flow = (_ref = config.flow) != null ? _ref : [];
      _results = [];
      for (i in flow) {
        fl = flow[i];
        _results.push(__bind(function(i) {
          var el, _ref2;
          el = (_ref2 = this.child(element, i)) != null ? _ref2 : $('<div/>').appendTo(element).width(element.width());
          return this.renderer.get(fl.type).render(item, fl, el, empty, __bind(function() {
            if (i === flow.length) {
              return handler;
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
    TitleElement.prototype.render = function(item, config, element, empty, handler) {
      var _ref;
      if (empty) {
        return handler;
      }
      element.empty();
      $('<span/>').addClass('title0_text').appendTo(element).text(this.renderer.inject((_ref = config.name) != null ? _ref : ' '));
      $('<span/>').addClass('title0_editor').attr('contentEditable', true).appendTo(element).text(this.renderer.inject(config.edit, item));
      return handler;
    };
    return TitleElement;
  })();
  HRElement = (function() {
    __extends(HRElement, UIElement);
    function HRElement() {
      HRElement.__super__.constructor.apply(this, arguments);
    }
    HRElement.prototype.name = 'hr';
    HRElement.prototype.render = function(item, config, element, empty, handler) {
      if (empty) {
        return handler;
      }
      $('<hr/>').addClass('hr').appendTo(element.empty());
      return handler;
    };
    return HRElement;
  })();
  Title1Element = (function() {
    __extends(Title1Element, UIElement);
    function Title1Element() {
      Title1Element.__super__.constructor.apply(this, arguments);
    }
    Title1Element.prototype.name = 'title1';
    Title1Element.prototype.render = function(item, config, element, empty, handler) {
      if (empty) {
        return handler;
      }
      $('<span/>').addClass('title1').appendTo(element.empty()).text(this.renderer.inject(config.name, item));
      return handler;
    };
    return Title1Element;
  })();
  ColsElement = (function() {
    __extends(ColsElement, UIElement);
    function ColsElement() {
      ColsElement.__super__.constructor.apply(this, arguments);
    }
    ColsElement.prototype.name = 'cols';
    ColsElement.prototype.render = function(item, config, element, empty, handler) {
      var el, fl, flow, i, margin, sizes, w, width, _fn, _ref, _ref2, _ref3;
      flow = (_ref = config.flow) != null ? _ref : [];
      sizes = (_ref2 = config.size) != null ? _ref2 : [];
      log(flow, sizes);
      if (flow.length !== sizes.length) {
        return handler;
      }
      w = element.width();
      margin = 0;
      _fn = __bind(function(i) {
        return this.renderer.get(fl.type).render(item, fl, el, empty, __bind(function() {
          if (i === flow.length) {
            $('<div style="clear: both;"/>').appendTo(element);
            return handler;
          }
        }, this));
      }, this);
      for (i in flow) {
        fl = flow[i];
        width = Math.floor(sizes[i] * w);
        el = (_ref3 = this.child(element, i)) != null ? _ref3 : $('<div/>').css('float', 'left').css('margin-left', margin).appendTo(element).width(width);
        margin += width;
        _fn(i);
      }
      return handler;
    };
    return ColsElement;
  })();
  Renderer = (function() {
    function Renderer(root, data, env) {
      this.root = root;
      this.data = data;
      this.env = env;
      this.elements = [new SimpleElement(this), new TitleElement(this), new HRElement(this), new Title1Element(this), new ColsElement(this)];
    }
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
    Renderer.prototype.inject = function(text, item) {
      return inject(text, this.env);
    };
    Renderer.prototype.render = function(template) {
      var content;
      this.root.empty;
      content = $('<div/>').addClass('page_content').appendTo(this.root);
      return this.get(template.name).render(this.data, template, content, false, __bind(function() {
        return log('Render done');
      }, this));
    };
    Renderer.prototype.items = function(area, handler) {
      return handler([
        {
          text: 'Text 01'
        }, {
          text: 'Text 02',
          done: true
        }, {
          text: 'Text 03'
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
      format = m[4] && m[4].length > 0 ? m[4] : 'ddmmyyyy';
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
  inject = function(text, params) {
    var exp, m, p, protocols, value, _i, _len;
    protocols = [new DateProtocol('dt')];
    exp = /\$\{([a-z]+\:)([a-zA-Z0-9\s\(\)\+\-\_\/\:\.]*)\}/;
    while (m = text.match(exp)) {
      if (!m) {
        return text;
      }
      value = '';
      for (_i = 0, _len = protocols.length; _i < _len; _i++) {
        p = protocols[_i];
        if (p.name + ':' === m[1]) {
          value = p.convert(m[2], params);
          break;
        }
      }
      text = text.replace(m[0], value);
    }
    return text;
  };
  log = function() {
    var params;
    params = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
    return console.log.apply(console, params);
  };
  $(document).ready(function() {
    var renderer;
    log(inject('Week ${dt:ww}/${dt:yyyy} ${dt:(e1)yyyy}: ${dt:(e1)MM/dd} - ${dt:(e7)MM/dd}', {
      dt: new Date().getTime()
    }));
    renderer = new Renderer($('#page'), {}, {
      dt: new Date().getTime()
    });
    return renderer.render(w1, $('#test').bind('keypress', function(event) {
      if (event.keyCode === 13) {
        return $('#test').text($('#test').text());
      }
    }));
  });
}).call(this);
