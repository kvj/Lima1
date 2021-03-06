(function() {
  var CalendarElement, CheckElement, ColsElement, DateElement, HRElement, HeaderElement, ListElement, MarkElement, Renderer, SimpleElement, Textlement, TimeElement, Title1Element, Title2Element, Title3Element, TitleElement, UIElement, __id,
    __hasProp = Object.prototype.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor; child.__super__ = parent.prototype; return child; },
    __indexOf = Array.prototype.indexOf || function(item) { for (var i = 0, l = this.length; i < l; i++) { if (i in this && this[i] === item) return i; } return -1; },
    __slice = Array.prototype.slice;

  UIElement = (function() {

    function UIElement(renderer) {
      this.renderer = renderer;
    }

    UIElement.prototype.child = function(element, cl, index) {
      var child;
      child = null;
      if (index < 0) {
        child = element.children(cl).eq(element.children(cl).size() + index);
      } else {
        child = element.children(cl).eq(index);
      }
      if (child.size() === 1) {
        return child;
      } else {
        return null;
      }
    };

    UIElement.prototype.render = function(item, config, element, options, handler) {
      return handler(null);
    };

    UIElement.prototype.canGrow = function(config) {
      return false;
    };

    UIElement.prototype.grow = function(height, config, element, options) {
      return height;
    };

    UIElement.prototype.fullHeight = function(element, config, canHaveDelimiter) {
      var w;
      if (canHaveDelimiter == null) canHaveDelimiter = false;
      w = element.outerHeight(element, true);
      if (canHaveDelimiter && config.delimiter) w++;
      return w;
    };

    return UIElement;

  })();

  __id = 0;

  SimpleElement = (function(_super) {

    __extends(SimpleElement, _super);

    function SimpleElement() {
      SimpleElement.__super__.constructor.apply(this, arguments);
    }

    SimpleElement.prototype.name = 'simple';

    SimpleElement.prototype.canGrow = function(config) {
      if (config && config.grow === 'no') {
        return false;
      } else {
        return true;
      }
    };

    SimpleElement.prototype.grow = function(height, config, element, options) {
      var el, fixedHeight, fl, floatHeight, floatPlus, floats, flow, freeHeight, i, id, newHeight, nowHeight, thisHeight, type, _ref;
      id = ++__id;
      nowHeight = element.innerHeight();
      floats = 0;
      floatHeight = 0;
      fixedHeight = 0;
      flow = (_ref = config.flow) != null ? _ref : [];
      for (i in flow) {
        fl = flow[i];
        i = parseInt(i);
        el = this.child(element, '.simple', i);
        type = this.renderer.get(fl.type);
        if (type.canGrow(fl)) {
          floatHeight += this.fullHeight(el, config, i > 0);
          floats++;
        } else {
          fixedHeight += this.fullHeight(el, config, i > 0);
        }
      }
      if (floats > 0) {
        for (i in flow) {
          fl = flow[i];
          i = parseInt(i);
          type = this.renderer.get(fl.type);
          el = this.child(element, '.simple', i);
          if (type.canGrow(fl) && floats > 0) {
            freeHeight = height - element.innerHeight();
            floatPlus = Math.floor(freeHeight / floats);
            thisHeight = this.fullHeight(el, config, i > 0);
            type.grow(thisHeight + floatPlus, fl, el, options);
            newHeight = this.fullHeight(el, config, i > 0);
            fixedHeight += newHeight;
            floatHeight -= thisHeight;
            floats--;
          }
        }
      }
      return fixedHeight;
    };

    SimpleElement.prototype.render = function(item, config, element, options, handler) {
      var fl, flow, i, _ref, _results,
        _this = this;
      if (config.defaults && !options.empty) {
        this.renderer.applyDefaults(config.defaults, item);
      }
      if (config.border) element.addClass('border_' + config.border);
      flow = (_ref = config.flow) != null ? _ref : [];
      _results = [];
      for (i in flow) {
        fl = flow[i];
        i = parseInt(i);
        _results.push((function(i) {
          var el, _ref2;
          el = (_ref2 = _this.child(element, '.simple', i)) != null ? _ref2 : $(document.createElement('div')).addClass('simple').appendTo(element);
          if (config.delimiter && i > 0) {
            el.addClass('delimiter_' + config.delimiter);
          }
          return _this.renderer.get(fl.type).render(item, fl, el, options, function() {
            if (i === flow.length - 1) return handler(null);
          });
        })(i));
      }
      return _results;
    };

    return SimpleElement;

  })(UIElement);

  TitleElement = (function(_super) {

    __extends(TitleElement, _super);

    function TitleElement() {
      TitleElement.__super__.constructor.apply(this, arguments);
    }

    TitleElement.prototype.name = 'title';

    TitleElement.prototype.render = function(item, config, element, options, handler) {
      var el, _ref;
      if (options.empty) return handler(null);
      $(document.createElement('span')).addClass('title0_text').appendTo(element).text(this.renderer.inject((_ref = config.name) != null ? _ref : ' '));
      if (config.edit) {
        el = $(document.createElement('span')).addClass('text_editor title0_editor').appendTo(element);
        this.renderer.text_editor(el, item, this.renderer.replace(config.edit));
      }
      return handler(null);
    };

    return TitleElement;

  })(UIElement);

  CalendarElement = (function(_super) {

    __extends(CalendarElement, _super);

    function CalendarElement() {
      CalendarElement.__super__.constructor.apply(this, arguments);
    }

    CalendarElement.prototype.name = 'calendar';

    CalendarElement.prototype.render = function(item, config, element, options, handler) {
      var calendar, dt,
        _this = this;
      if (env.mobile) {
        dt = new Date();
        calendar = $(document.createElement('div')).addClass('calendar_mobile').appendTo(element).text(dt.format('dddd, MMMM d, yyyy'));
        calendar.bind('tap', function() {
          return _this.renderer.ui.date_dialog(dt, function(set, dt) {
            if (set) {
              return _this.renderer.ui.open_link('dt:' + dt.format('yyyyMMdd'));
            }
          });
        });
      } else {
        calendar = $(document.createElement('div')).addClass('calendar_element').appendTo(element);
        calendar.datepicker({
          dateFormat: 'yymmdd',
          firstDay: 1,
          onSelect: function(dt) {
            _this.renderer.ui.open_link('dt:' + dt);
            return false;
          }
        });
      }
      return handler(null);
    };

    return CalendarElement;

  })(UIElement);

  Textlement = (function(_super) {

    __extends(Textlement, _super);

    function Textlement() {
      Textlement.__super__.constructor.apply(this, arguments);
    }

    Textlement.prototype.name = 'text';

    Textlement.prototype.render = function(item, config, element, options, handler) {
      var ed, el, property, title;
      if (options.empty) return handler(null);
      el = $(document.createElement('div')).addClass('text_editor').appendTo(element);
      if (config.edit && !options.readonly) {
        property = this.renderer.replace(config.edit, item);
        ed = this.renderer.text_editor(el, item, property);
        ed.attr('item_id', item.id);
        ed.attr('property', property);
        ed.attr('option', options.text_option);
        if (config.title && !env.mobile) {
          title = $(document.createElement('div')).addClass('text_editor_title').appendTo(element).text(config.title);
        }
      }
      return handler(null);
    };

    return Textlement;

  })(UIElement);

  CheckElement = (function(_super) {

    __extends(CheckElement, _super);

    function CheckElement() {
      CheckElement.__super__.constructor.apply(this, arguments);
    }

    CheckElement.prototype.name = 'check';

    CheckElement.prototype.render = function(item, config, element, options, handler) {
      var checked, el, property,
        _this = this;
      if (options.empty) return handler(null);
      el = $(document.createElement('div')).addClass('check_editor').appendTo(element);
      if (config.inset) el.addClass('check_inset');
      property = this.renderer.replace(config.edit, item);
      checked = false;
      if (property && item[property] === 1) {
        checked = true;
        el.addClass('checked').text('×');
      }
      if (!options.readonly) {
        el.bind('click', function(event) {
          _this.renderer.on_edited(item, property, checked ? null : 1);
          return false;
        });
      }
      return handler(null);
    };

    return CheckElement;

  })(UIElement);

  DateElement = (function(_super) {

    __extends(DateElement, _super);

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
      var el, property,
        _this = this;
      if (options.empty) return handler(null);
      el = $(document.createElement('div')).addClass('date_editor').appendTo(element);
      property = this.renderer.replace(config.edit, item);
      this.print_date(item[property], el);
      el.bind('click', function(e) {
        var dt, _ref;
        if (e.shiftKey) {
          _this.renderer.on_edited(item, property, null);
        } else {
          if (env.mobile) {
            log('prop:', property, item[property]);
            dt = new Date();
            _this.renderer.ui.date_dialog((_ref = dt.fromString(item[property])) != null ? _ref : new Date(), function(set, dt) {
              log('date', set, dt);
              if (set) {
                return _this.renderer.on_edited(item, property, dt.format('yyyyMMdd'));
              } else {
                return _this.renderer.on_edited(item, property, null);
              }
            });
          } else {
            el.datepicker('dialog', null, function(date) {
              if (_this.print_date(date, el)) {
                return _this.renderer.on_edited(item, property, date);
              }
            }, {
              dateFormat: 'yymmdd'
            }, e);
          }
        }
        return false;
      });
      $('<div style="clear: both;"/>').appendTo(element);
      return handler(null);
    };

    return DateElement;

  })(UIElement);

  TimeElement = (function(_super) {

    __extends(TimeElement, _super);

    function TimeElement() {
      TimeElement.__super__.constructor.apply(this, arguments);
    }

    TimeElement.prototype.name = 'time';

    TimeElement.prototype._split_value = function(value) {
      var _ref, _ref2;
      return [parseInt((_ref = value != null ? value.substr(0, 2) : void 0) != null ? _ref : 0, 10), parseInt((_ref2 = value != null ? value.substr(2) : void 0) != null ? _ref2 : 0, 10)];
    };

    TimeElement.prototype.value_to_string = function(txt) {
      var ap, hr, min, _ref;
      if (!txt || txt.length !== 4) return '';
      _ref = this._split_value(txt), hr = _ref[0], min = _ref[1];
      ap = 'a';
      if (hr === 0) {
        hr = 12;
      } else {
        if (hr > 11) {
          ap = 'p';
          if (hr > 12) hr -= 12;
        }
      }
      return '' + hr + (min > 0 ? ':' + min : '') + ap;
    };

    TimeElement.prototype.show_editor = function(value, element, handler) {
      var buttons, caption_div, connect_to, el, hour_div, hr_val, min_val, minute_div, val, x, y, _on_change, _on_close, _ref,
        _this = this;
      _ref = this._split_value(value), hr_val = _ref[0], min_val = _ref[1];
      if (env.mobile) {
        this.renderer.ui.time_dialog(hr_val, min_val, function(set, hr, min) {
          var val;
          if (set) {
            val = '' + (hr < 10 ? '0' : '') + hr + (min < 10 ? '0' : '') + min;
            return handler(val);
          } else {
            return handler(null);
          }
        });
        return;
      }
      connect_to = element.offset();
      el = $(document.createElement('div')).addClass('ui-timepicker ui-corner-all ui-widget-content').appendTo(document.body);
      x = Math.floor(connect_to.left - el.width() / 2);
      if (x < 0) x = 0;
      y = connect_to.top + element.height();
      caption_div = $(document.createElement('div')).addClass('ui-timepicker-caption').appendTo(el);
      hour_div = $(document.createElement('div')).addClass('ui-timepicker-slider').appendTo(el);
      minute_div = $(document.createElement('div')).addClass('ui-timepicker-slider').appendTo(el);
      val = value;
      _on_change = function(hour, minute) {
        var hr, min;
        hr = parseInt(hour != null ? hour : hour_div.slider('value'));
        min = parseInt(minute != null ? minute : minute_div.slider('value'));
        val = '' + (hr < 10 ? '0' : '') + hr + (min < 10 ? '0' : '') + min;
        return caption_div.text(_this.value_to_string(val));
      };
      hour_div.slider({
        min: 0,
        max: 23,
        step: 1,
        value: hr_val,
        slide: function(event, ui) {
          return _on_change(ui.value, null);
        }
      });
      minute_div.slider({
        min: 0,
        max: 45,
        step: 15,
        value: min_val,
        slide: function(event, ui) {
          return _on_change(null, ui.value);
        }
      });
      _on_close = function() {
        return el.remove();
      };
      buttons = $(document.createElement('div')).addClass('ui-timepicker-buttons').appendTo(el);
      $(document.createElement('button')).addClass('ui-timepicker-button').appendTo(buttons).text('OK').bind('click', function() {
        _on_close(null);
        return handler(val);
      });
      $(document.createElement('button')).addClass('ui-timepicker-button').appendTo(buttons).text('Cancel').bind('click', function() {
        return _on_close(null);
      });
      _on_change(null);
      buttons.find('button').button();
      return el.css('left', x).css('top', y);
    };

    TimeElement.prototype.render = function(item, config, element, options, handler) {
      var el, parts, property,
        _this = this;
      if (options.empty) return handler;
      el = $(document.createElement('div')).addClass('time_editor').appendTo(element);
      property = this.renderer.replace(config.edit, item);
      parts = (this.value_to_string(item[property])).split(':');
      if (parts.length === 2) {
        el.html(parts[0] + '<br/>' + parts[1]);
      } else {
        el.text(parts[0]);
      }
      el.bind('click', function(e) {
        if (e.shiftKey) {
          _this.renderer.on_edited(item, property, null);
        } else {
          _this.show_editor(item[property], el, function(time) {
            return _this.renderer.on_edited(item, property, time);
          });
        }
        return false;
      });
      $('<div class="clear"/>').appendTo(element);
      return handler(null);
    };

    return TimeElement;

  })(UIElement);

  MarkElement = (function(_super) {

    __extends(MarkElement, _super);

    function MarkElement() {
      MarkElement.__super__.constructor.apply(this, arguments);
    }

    MarkElement.prototype.name = 'mark';

    MarkElement.prototype._show_value = function(value, el) {
      if (!value) return true;
      if (value === 1 || value === 2) {
        el.addClass('mark' + value);
        return true;
      }
      return false;
    };

    MarkElement.prototype.render = function(item, config, element, options, handler) {
      var el, property, value,
        _this = this;
      if (options.empty) return handler(null);
      property = this.renderer.replace(config.edit, item);
      value = item[property];
      el = $(document.createElement('div')).addClass('mark_editor').appendTo(element);
      this._show_value(value, el);
      el.bind('click', function(event) {
        if (!value) {
          value = 1;
        } else {
          if (value === 1) {
            value = 2;
          } else {
            value = null;
          }
        }
        _this._show_value(value, el);
        return _this.renderer.on_edited(item, property, value);
      });
      return handler(null);
    };

    return MarkElement;

  })(UIElement);

  HRElement = (function(_super) {

    __extends(HRElement, _super);

    function HRElement() {
      HRElement.__super__.constructor.apply(this, arguments);
    }

    HRElement.prototype.name = 'hr';

    HRElement.prototype.render = function(item, config, element, options, handler) {
      if (options.empty) return handler(null);
      $(document.createElement('div')).addClass('hr').appendTo(element);
      return handler(null);
    };

    return HRElement;

  })(UIElement);

  Title1Element = (function(_super) {

    __extends(Title1Element, _super);

    function Title1Element() {
      Title1Element.__super__.constructor.apply(this, arguments);
    }

    Title1Element.prototype.name = 'title1';

    Title1Element.prototype.render = function(item, config, element, options, handler) {
      var bg;
      if (options.empty) return handler(null);
      bg = $(document.createElement('div')).addClass('title1_bg').appendTo(element);
      $(document.createElement('div')).addClass('title1').appendTo(bg).text(this.renderer.inject(config.name, item));
      $(document.createElement('div')).addClass('clear').appendTo(element);
      return handler(null);
    };

    return Title1Element;

  })(UIElement);

  Title2Element = (function(_super) {

    __extends(Title2Element, _super);

    function Title2Element() {
      Title2Element.__super__.constructor.apply(this, arguments);
    }

    Title2Element.prototype.name = 'title2';

    Title2Element.prototype.render = function(item, config, element, options, handler) {
      var bg, el, _ref;
      if (options.empty) return handler(null);
      bg = $(document.createElement('div')).addClass('title2').appendTo(element);
      $(document.createElement('span')).addClass('title2_text').appendTo(bg).text(this.renderer.inject((_ref = config.name) != null ? _ref : ' '));
      if (config.edit) {
        el = $(document.createElement('span')).addClass('text_editor title2_editor').appendTo(bg);
        this.renderer.text_editor(el, item, this.renderer.replace(config.edit));
      }
      return handler(null);
    };

    return Title2Element;

  })(UIElement);

  Title3Element = (function(_super) {

    __extends(Title3Element, _super);

    function Title3Element() {
      Title3Element.__super__.constructor.apply(this, arguments);
    }

    Title3Element.prototype.name = 'title3';

    Title3Element.prototype.render = function(item, config, element, options, handler) {
      var bg;
      if (options.empty) return handler(null);
      bg = $(document.createElement('span')).addClass('title3').appendTo(element);
      $(document.createElement('span')).addClass('title3_text').appendTo(bg).text(this.renderer.inject(config.name, item));
      return handler(null);
    };

    return Title3Element;

  })(UIElement);

  HeaderElement = (function(_super) {

    __extends(HeaderElement, _super);

    function HeaderElement() {
      HeaderElement.__super__.constructor.apply(this, arguments);
    }

    HeaderElement.prototype.name = 'header';

    HeaderElement.prototype.render = function(item, config, element, options, handler) {
      var bg, diff, el, fl, float_size, flow, i, last, lsizes, margin, sizes, space_size, sz, w, width, _i, _len, _ref, _ref2;
      if (options.empty) return handler(null);
      flow = (_ref = config.flow) != null ? _ref : [];
      sizes = (_ref2 = config.size) != null ? _ref2 : [];
      if (flow.length === 1 && sizes.length === 0) sizes = [1];
      if (flow.length !== sizes.length) return handler(null);
      bg = $(document.createElement('div')).addClass('header').appendTo(element);
      w = element.innerWidth() - 2;
      float_size = 0;
      lsizes = [];
      for (_i = 0, _len = sizes.length; _i < _len; _i++) {
        sz = sizes[_i];
        lsizes.push(sz);
        float_size += sz;
      }
      space_size = 0;
      if (float_size > 0) {
        for (i in lsizes) {
          sz = lsizes[i];
          if (sz <= 1) lsizes[i] = Math.floor(w * sz / float_size);
        }
      }
      margin = 0;
      for (i in flow) {
        fl = flow[i];
        i = parseInt(i);
        last = i === flow.length - 1;
        width = lsizes[i];
        if (last) width = w - margin;
        el = $(document.createElement('div')).addClass('col header_col').appendTo(bg).width(width);
        if (fl) {
          el.text(this.renderer.inject(fl));
        } else {
          el.html('&nbsp;');
        }
        diff = el.outerWidth() - el.innerWidth();
        if (diff > 0) el.width(el.innerWidth() - diff);
        margin += width;
      }
      return handler(null);
    };

    return HeaderElement;

  })(UIElement);

  ColsElement = (function(_super) {

    __extends(ColsElement, _super);

    function ColsElement() {
      ColsElement.__super__.constructor.apply(this, arguments);
    }

    ColsElement.prototype.name = 'cols';

    ColsElement.prototype.canGrow = function() {
      return true;
    };

    ColsElement.prototype.grow = function(height, config, element, options) {
      var body, el, fl, flow, h, i, maxh, type, _ref;
      flow = (_ref = config.flow) != null ? _ref : [];
      maxh = 0;
      body = element.children().first();
      for (i in flow) {
        fl = flow[i];
        i = parseInt(i);
        el = this.child(body, '.col_data', i).children().first();
        type = this.renderer.get(fl.type);
        h = 0;
        if (type.canGrow(fl)) {
          h = type.grow(height, fl, el, options);
        } else {
          h = this.fullHeight(el, config);
        }
        if (h > maxh) maxh = h;
      }
      return maxh;
    };

    ColsElement.prototype.render = function(item, config, element, options, handler) {
      var body, el, el_body, fl, float_size, flow, i, index, last, lsizes, margin, sizes, space_size, sz, w, width, _i, _len, _ref, _ref2, _results,
        _this = this;
      flow = (_ref = config.flow) != null ? _ref : [];
      sizes = (_ref2 = config.size) != null ? _ref2 : [];
      if (flow.length !== sizes.length) return handler;
      w = element.innerWidth();
      element.addClass('group');
      body = $(document.createElement('div')).addClass('col_body').appendTo(element);
      float_size = 0;
      if (config.space) float_size += config.space * (flow.length - 1);
      lsizes = [];
      for (_i = 0, _len = sizes.length; _i < _len; _i++) {
        sz = sizes[_i];
        lsizes.push(sz);
        float_size += sz;
      }
      space_size = 0;
      if (float_size > 0) {
        for (i in lsizes) {
          sz = lsizes[i];
          if (sz <= 1) lsizes[i] = Math.floor(w * sz / float_size);
        }
        if (config.space) space_size = Math.floor(w * config.space / float_size);
      }
      margin = 0;
      index = 0;
      _results = [];
      for (i in flow) {
        fl = flow[i];
        i = parseInt(i);
        last = i === flow.length - 1;
        el = null;
        el_body = null;
        if (i > 0 && config.space) {
          $(document.createElement('div')).appendTo(body).addClass('col').width(space_size).html('&nbsp;');
          margin += space_size;
        }
        width = lsizes[i];
        if (last) width = w - margin;
        el = $(document.createElement('div')).addClass('col col_data').appendTo(body);
        el_body = $(document.createElement('div')).appendTo(el).addClass('col_item');
        margin += width;
        if (fl.line > 0) {
          width -= fl.line;
          el.addClass('col_' + fl.line);
        }
        if (fl.bg) el.addClass('col_g');
        el.width(width);
        _results.push((function(last) {
          return _this.renderer.get(fl.type).render(item, fl, el_body, options, function() {
            if (last) return handler(null);
          });
        })(last));
      }
      return _results;
    };

    return ColsElement;

  })(UIElement);

  ListElement = (function(_super) {

    __extends(ListElement, _super);

    function ListElement() {
      ListElement.__super__.constructor.apply(this, arguments);
    }

    ListElement.prototype.name = 'list';

    ListElement.prototype.grow = function(height, config, element, options) {
      var added, emptyHeight, i, nowHeight,
        _this = this;
      nowHeight = this.fullHeight(element, config);
      emptyHeight = this.fullHeight(this.child(element, '.list_item', -1), config, true);
      added = Math.floor((height - nowHeight) / emptyHeight);
      for (i = 0; 0 <= added ? i < added : i > added; 0 <= added ? i++ : i--) {
        this._render({
          area: config.area
        }, config.item, element, {
          disabled: true,
          delimiter: config.delimiter
        }, function(el) {
          return null;
        });
        nowHeight += emptyHeight;
      }
      return nowHeight;
    };

    ListElement.prototype.canGrow = function(config) {
      if (config && config.grow === 'no') {
        return false;
      } else {
        return true;
      }
    };

    ListElement.prototype._render = function(item, config, element, options, handler) {
      var el, handle,
        _this = this;
      el = $(document.createElement('div')).addClass('list_item').appendTo(element);
      if (options.delimiter) el.addClass('delimiter_' + options.delimiter);
      if (options.disabled) el.addClass('disabled');
      if (!env.mobile) {
        if (options.draggable) {
          handle = $(document.createElement('div')).addClass('list_item_handle list_item_handle_left').appendTo(el);
          el.bind('mousemove', function() {
            return handle.show();
          });
          el.bind('mouseout', function() {
            return handle.hide();
          });
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
          drop: function(event, ui) {
            var drop, renderer;
            drop = ui.draggable.data('item');
            renderer = ui.draggable.data('renderer');
            return _this.renderer.move_note(drop, item.area, (item.id ? item : null), function() {
              if (renderer !== _this.renderer) renderer.render(null);
              return _this.renderer.render(null);
            });
          }
        });
      }
      return this.renderer.get('simple').render(item, config, el, {
        empty: false,
        readonly: options.disabled,
        text_option: options.empty ? item.area : ''
      }, function() {
        return handler(el);
      });
    };

    ListElement.prototype.render = function(item, config, element, options, handler) {
      var flow, _ref,
        _this = this;
      if (config.border) element.addClass('border_' + config.border);
      flow = (_ref = config.flow) != null ? _ref : [];
      return this.renderer.items(config.area, function(items) {
        var i, itm;
        for (i in items) {
          itm = items[i];
          i = parseInt(i);
          _this._render(itm, config.item, element, {
            disable: false,
            draggable: true,
            delimiter: i > 0 ? config.delimiter : null
          }, function() {});
        }
        return _this._render({
          area: config.area
        }, config.item, element, {
          disable: false,
          empty: true,
          delimiter: items.length > 0 ? config.delimiter : null
        }, function() {
          return handler(null);
        });
      });
    };

    return ListElement;

  })(UIElement);

  Renderer = (function() {

    Renderer.prototype.show_archived = false;

    function Renderer(manager, ui, root, template, data, max_height) {
      this.manager = manager;
      this.ui = ui;
      this.root = root;
      this.template = template;
      this.data = data;
      this.max_height = max_height;
      this.elements = [new SimpleElement(this), new TitleElement(this), new HRElement(this), new Title1Element(this), new Title2Element(this), new Title3Element(this), new HeaderElement(this), new ColsElement(this), new ListElement(this), new Textlement(this), new CheckElement(this), new MarkElement(this), new DateElement(this), new TimeElement(this), new CalendarElement(this)];
      this.root.data('sheet', this.data);
    }

    Renderer.prototype.get = function(name) {
      var el, _i, _len, _ref;
      _ref = this.elements;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        el = _ref[_i];
        if (el.name === name) return el;
      }
      return this.elements[0];
    };

    Renderer.prototype.applyDefaults = function(config, item) {
      var key, value, _ref, _results;
      _results = [];
      for (key in config) {
        if (!__hasProp.call(config, key)) continue;
        value = config[key];
        _results.push((_ref = item[key]) != null ? _ref : item[key] = this.inject(value));
      }
      return _results;
    };

    Renderer.prototype.inject = function(txt, item) {
      if (item == null) item = this.data;
      return this.ui.inject(txt, item);
    };

    Renderer.prototype.replace = function(text, item) {
      if (item == null) item = this.data;
      return this.ui.replace(text, item);
    };

    Renderer.prototype._save_sheet = function(handler) {
      var _this = this;
      if (this.template.code) {
        this.data.code = this.inject(this.template.code, this.data);
      }
      return this.manager.saveSheet(this.data, function(err, object) {
        if (err) return _this.ui.show_error(err);
        _this.on_sheet_change(_this.data);
        return handler(object);
      });
    };

    Renderer.prototype.move_note = function(item, area, before, handler) {
      var to_save,
        _this = this;
      to_save = this.manager.sortArray(this.items(area), item, before);
      item.area = area;
      return this._save_note(item, function() {
        var el, i, last, _results;
        if (to_save.length === 0) {
          return handler(item);
        } else {
          _results = [];
          for (i in to_save) {
            el = to_save[i];
            last = parseInt(i) === to_save.length - 1;
            _results.push((function(last) {
              return _this.manager.saveNote(el, function() {
                if (last) return handler(item);
              });
            })(last));
          }
          return _results;
        }
      });
    };

    Renderer.prototype._save_note = function(item, handler) {
      var others,
        _this = this;
      if (!item.place) {
        others = this.items(item.area);
        this.manager.sortArray(others, item);
      }
      if (!this.data.id) {
        return this._save_sheet(function(object) {
          item.sheet_id = _this.data.id;
          return _this.manager.saveNote(item, function(err, object) {
            if (err) return _this.ui.show_error(err);
            return handler(object);
          });
        });
      } else {
        item.sheet_id = this.data.id;
        return this.manager.saveNote(item, function(err, object) {
          if (err) return _this.ui.show_error(err);
          return handler(object);
        });
      }
    };

    Renderer.prototype.on_edited = function(item, property, value) {
      var _this = this;
      if (!item || !property) return false;
      item[property] = value;
      if (item === this.data) {
        return this._save_sheet(function(object) {
          return _this.render(null);
        });
      } else {
        return this._save_note(item, function() {
          return _this.render(null);
        });
      }
    };

    Renderer.prototype.remove_note = function(item) {
      var _this = this;
      return this.manager.removeNote(item, function(err) {
        if (err) return _this.ui.show_error(err);
        return _this.render(null);
      });
    };

    Renderer.prototype.text_editor = function(element, item, property, handler) {
      var old_value, _on_finish_edit, _ref,
        _this = this;
      if (!property) return null;
      old_value = (_ref = item[property]) != null ? _ref : '';
      element.text(old_value);
      _on_finish_edit = function(value) {
        element.text(value);
        if (value === old_value) return;
        if (handler) {
          return handler(item, property, value);
        } else {
          if (env.mobile) {
            return setTimeout(function() {
              return _this.on_edited(item, property, value);
            }, _this.renderer.ui.close_delay);
          } else {
            return _this.on_edited(item, property, value);
          }
        }
      };
      if (env.mobile) {
        element.bind('click', function(event) {
          $('#text_dialog_title').text((item != null ? item.id : void 0) ? 'Edit text' : 'New text');
          $.mobile.changePage($('#text_dialog'));
          $('#text_editor').focus().val(old_value);
          $('#text_editor').unbind('keydown').bind('keydown', function(e) {
            var _ref2;
            if (e.keyCode === 13) {
              return _on_finish_edit((_ref2 = $('#text_editor').val()) != null ? _ref2 : '');
            }
          });
          $('#text_save').unbind('click').bind('click', function() {
            var _ref2;
            $('#text_dialog').dialog('close');
            return _on_finish_edit((_ref2 = $('#text_editor').val()) != null ? _ref2 : '');
          });
          $('#text_remove').unbind('click').bind('click', function() {
            $('#text_dialog').dialog('close');
            if (item.id) return _this.remove_note(item);
          });
          return false;
        });
      } else {
        element.attr('contentEditable', true);
        element.bind('keypress', function(event) {
          var _ref2;
          if (event.keyCode === 13) {
            event.preventDefault();
            _on_finish_edit((_ref2 = element.text()) != null ? _ref2 : '');
            return false;
          }
        });
      }
      return element;
    };

    Renderer.prototype.render = function() {
      var focus,
        _this = this;
      this.root.addClass('page_render');
      focus = this.root.find('*:focus');
      this.prev_content = this.root.children();
      this.content = $(document.createElement('div')).addClass('page_content group').prependTo(this.root);
      if (this.data.archived) this.content.addClass('sheet_archived');
      return this._load_items(function() {
        return _this.get(_this.template.name).render(_this.data, _this.template, _this.content, {
          empty: false
        }, function() {
          return _this.fix_height(function() {
            var el, item, no_area_div, _i, _len, _ref, _results;
            if (focus.attr('option')) {
              _this.root.find('.text_editor[property=' + focus.attr('property') + '][option=' + focus.attr('option') + ']').focus();
            } else {
              _this.root.find('.text_editor[property=' + focus.attr('property') + '][item_id=' + focus.attr('item_id') + ']').focus();
            }
            if (_this.no_area.length > 0 && !env.mobile) {
              no_area_div = $(document.createElement('div')).addClass('no_area_notes').appendTo(_this.root);
              _ref = _this.no_area;
              _results = [];
              for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                item = _ref[_i];
                el = $(document.createElement('div')).addClass('list_item no_area_note').appendTo(no_area_div);
                el.data('type', 'note');
                el.data('renderer', _this);
                el.data('item', item);
                el.draggable({
                  zIndex: 4,
                  containment: 'document',
                  helper: 'clone',
                  appendTo: 'body'
                });
                _results.push(el.attr('title', item.text));
              }
              return _results;
            }
          });
        });
      });
    };

    Renderer.prototype.fix_height = function(handler) {
      var archive_toggle, page_actions, sleft, sright,
        _this = this;
      this.get(this.template.name).grow(this.max_height, this.template, this.content, {
        empty: true,
        disabled: true
      });
      this.prev_content.remove();
      this.root.removeClass('page_render');
      if (!env.mobile) {
        sleft = $(document.createElement('div')).addClass('page_scroll scroll_left').appendTo(this.root);
        sleft.bind('click', function() {
          return _this.ui.scroll_sheets(_this.data.id, -1);
        });
        sright = $(document.createElement('div')).addClass('page_scroll scroll_right').appendTo(this.root);
        sright.bind('click', function() {
          return _this.ui.scroll_sheets(_this.data.id, 1);
        });
        page_actions = $(document.createElement('div')).addClass('page_actions').appendTo(this.root);
        archive_toggle = $(document.createElement('div')).addClass('page_action archive_toggle').appendTo(page_actions);
        archive_toggle.bind('click', function() {
          _this.show_archived = !_this.show_archived;
          return _this.render(null);
        });
      }
      if (handler) return handler(null);
    };

    Renderer.prototype._load_items = function(handler) {
      var areas, find_areas,
        _this = this;
      areas = [];
      find_areas = function(item) {
        var fl, _i, _len, _ref, _ref2, _results;
        if (item.type === 'list' && item.area && (_ref = item.area, __indexOf.call(areas, _ref) < 0)) {
          areas.push(item.area);
        }
        if (item.flow) {
          _ref2 = item.flow;
          _results = [];
          for (_i = 0, _len = _ref2.length; _i < _len; _i++) {
            fl = _ref2[_i];
            _results.push(find_areas(fl));
          }
          return _results;
        }
      };
      find_areas(this.template);
      return this.manager.getNotes(this.data.id, null, function(err, data) {
        var area, item, _i, _j, _len, _len2;
        if (err) {
          log('Error getting items', err);
          return handler({});
        } else {
          _this.notes = {};
          _this.no_area = [];
          for (_i = 0, _len = areas.length; _i < _len; _i++) {
            area = areas[_i];
            _this.notes[area] = [];
          }
          for (_j = 0, _len2 = data.length; _j < _len2; _j++) {
            item = data[_j];
            if (!_this.show_archived && item.done === 1) continue;
            if (_this.notes[item.area]) {
              _this.notes[item.area].push(item);
            } else {
              _this.no_area.push(item);
            }
          }
          return handler(_this.notes);
        }
      });
    };

    Renderer.prototype.items = function(area, handler) {
      var result, _ref;
      result = (_ref = this.notes[area]) != null ? _ref : [];
      if (handler) handler(result);
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

}).call(this);
