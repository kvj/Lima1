(function() {
  var DBProvider, DataManager, HTML5Provider, StorageProvider;
  var __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  }, __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; };
  DBProvider = (function() {
    function DBProvider(name, version) {
      this.name = name;
      this.version = version != null ? version : '1';
    }
    DBProvider.prototype.open = function(clean, handler) {
      if (clean == null) {
        clean = true;
      }
      return handler('open: Not implemented');
    };
    DBProvider.prototype.verify = function(schema, handler) {
      return handler('Not implemented');
    };
    DBProvider.prototype.query = function(line, params, handler) {
      return handler('Not implemented');
    };
    DBProvider.prototype.get = function(name, def) {
      return null;
    };
    DBProvider.prototype.set = function(name, value) {
      return null;
    };
    return DBProvider;
  })();
  HTML5Provider = (function() {
    __extends(HTML5Provider, DBProvider);
    function HTML5Provider() {
      HTML5Provider.__super__.constructor.apply(this, arguments);
    }
    HTML5Provider.prototype.open = function(clean, handler) {
      if (!(window && window.openDatabase)) {
        return handler('HTML5 DB not supported');
      }
      log('Ready to open');
      try {
        this.db = window.openDatabase(this.name, '', this.name, 1024 * 1024 * 10);
        log('Opened', this.db.version, this.version);
        this.version_match = this.db.version === this.version;
        this.clean = clean;
        return handler(null);
      } catch (error) {
        return handler(error.message);
      }
    };
    HTML5Provider.prototype._query = function(query, params, transaction, handler) {
      log('SQL:', query, params);
      return transaction.executeSql(query, params, __bind(function(transaction, result) {
        var data, i, key, obj, value, _ref, _ref2;
        data = [];
        for (i = 0, _ref = result.rows.length; 0 <= _ref ? i < _ref : i > _ref; 0 <= _ref ? i++ : i--) {
          obj = {};
          _ref2 = result.rows.item(i);
          for (key in _ref2) {
            if (!__hasProp.call(_ref2, key)) continue;
            value = _ref2[key];
            if (value) {
              obj[key] = value;
            }
          }
          data.push(obj);
        }
        return handler(null, data, transaction);
      }, this), __bind(function(transaction, error) {
        log('Error SQL', error);
        return handler(error.message);
      }, this));
    };
    HTML5Provider.prototype.query = function(query, params, handler, transaction) {
      if (!this.db) {
        return handler("DB isn't opened");
      }
      if (transaction) {
        return this._query(query, params, transaction, handler);
      } else {
        return this.db.transaction(__bind(function(transaction) {
          return this._query(query, params, transaction, handler);
        }, this), __bind(function(error) {
          log('Error transaction', error);
          return handler(error.message);
        }, this));
      }
    };
    HTML5Provider.prototype.verify = function(schema, handler) {
      return this.query('select name, type from sqlite_master where type=? or type=?', ['table', 'index'], __bind(function(err, res, tr) {
        var create_at, drop_at;
        log('SQL result', err, res, tr);
        if (err) {
          return handler(err);
        }
        if (!this.version_match || this.clean) {
          create_at = __bind(function(index) {
            if (index < schema.length) {
              return this.query(schema[index], [], __bind(function(err) {
                if (err) {
                  return handler(err);
                }
                return create_at(index + 1);
              }, this), tr);
            } else {
              log('Changing version', this.db.version, '=>', this.version);
              if (!this.version_match) {
                return this.db.changeVersion(this.db.version, this.version, __bind(function(tr) {
                  return handler(null, true);
                }, this), __bind(function(err) {
                  log('Version change error', err);
                  return handler(err);
                }, this));
              } else {
                return handler(null, false);
              }
            }
          }, this);
          drop_at = __bind(function(index) {
            if (index < res.length) {
              if (res[index].name.substr(0, 2) === '__' || res[index].name.substr(0, 7) === 'sqlite_') {
                return drop_at(index + 1);
              }
              return this.query('drop ' + res[index].type + ' ' + res[index].name, [], __bind(function(err) {
                if (err) {
                  return handler(err);
                }
                return drop_at(index + 1);
              }, this), tr);
            } else {
              return create_at(0);
            }
          }, this);
          return drop_at(0);
        } else {
          return handler(null, false);
        }
      }, this));
    };
    HTML5Provider.prototype.get = function(name, def) {
      var _ref;
      return (_ref = typeof window !== "undefined" && window !== null ? window.localStorage[name] : void 0) != null ? _ref : def;
    };
    HTML5Provider.prototype.set = function(name, value) {
      return typeof window !== "undefined" && window !== null ? window.localStorage[name] = value : void 0;
    };
    return HTML5Provider;
  })();
  StorageProvider = (function() {
    StorageProvider.prototype.last_id = 0;
    StorageProvider.prototype.db_schema = ['create table if not exists updates (id integer primary key, version_in integer, version_out integer, version text)', 'create table if not exists data (id integer primary key, status integer default 0, updated integer default 0, own integer default 1, stream text, data text, i0 integer, i1 integer, i2 integer, i3 integer, i4 integer, i5 integer, i6 integer, i7 integer, i8 integer, i9 integer, t0 text, t1 text, t2 text, t3 text, t4 text, t5 text, t6 text, t7 text, t8 text, t9 text)'];
    function StorageProvider(connection, db) {
      this.connection = connection;
      this.db = db;
    }
    StorageProvider.prototype.open = function(handler) {
      return this.db.open(false, __bind(function(err) {
        log('Open result:', err);
        if (!err) {
          return this.db.verify(this.db_schema, __bind(function(err, reset) {
            log('Verify result', err, reset);
            return handler(err);
          }, this));
        }
      }, this));
    };
    StorageProvider.prototype._precheck = function(stream, handler) {
      if (!this.schema) {
        handler('Not synchronized');
        return false;
      }
      if (!this.schema[stream]) {
        handler('Unsupported stream');
        return false;
      }
      return true;
    };
    StorageProvider.prototype.sync = function(app, oauth, handler) {
      var clean_sync, do_reset_schema, finish_sync, get_last_sync, in_from, in_items, out_from, out_items, receive_out, reset_schema, send_in;
      log('Starting sync...', app);
      reset_schema = false;
      clean_sync = false;
      in_from = 0;
      out_from = 0;
      out_items = 0;
      in_items = 0;
      finish_sync = __bind(function(err) {
        if (err) {
          return handler(err);
        }
        return this.db.query('insert into updates (id, version_in, version_out) values (?, ?, ?)', [this._id(), in_from, out_from], __bind(function() {
          return this.db.query('delete from data where status=?', [3], __bind(function() {
            return handler(err, {
              "in": in_items,
              out: out_items
            });
          }, this));
        }, this));
      }, this);
      receive_out = __bind(function() {
        var url;
        url = "/rest/out?from=" + out_from + "&";
        if (!clean_sync) {
          url += "inc=yes&";
        }
        return oauth.rest(app, url, null, __bind(function(err, res) {
          var arr, i, item, last, object, _results;
          if (err) {
            return finish_sync(err);
          }
          arr = res.a;
          if (arr.length === 0) {
            out_from = res.u;
            return finish_sync(null);
          } else {
            _results = [];
            for (i in arr) {
              item = arr[i];
              last = parseInt(i) === arr.length - 1;
              object = null;
              out_from = item.u;
              in_items++;
              try {
                object = JSON.parse(item.o);
              } catch (e) {
                log('Error parsing object', e);
              }
              _results.push(__bind(function(last) {
                return this.create(item.s, object, __bind(function(err) {
                  if (last) {
                    return receive_out(null);
                  }
                }, this), {
                  status: item.st,
                  updated: item.u,
                  own: 0,
                  internal: true
                });
              }, this)(last));
            }
            return _results;
          }
        }, this));
      }, this);
      send_in = __bind(function() {
        var slots, _ref;
        slots = (_ref = this.schema._slots) != null ? _ref : 10;
        return this.db.query('select id, stream, data, updated, status from data where own=? and updated>? order by updated limit ' + slots, [1, in_from], __bind(function(err, data) {
          var i, item, result, slots_needed, slots_used, _ref2, _ref3;
          if (err) {
            return finish_sync(err);
          }
          result = [];
          slots_used = 0;
          for (i in data) {
            item = data[i];
            slots_needed = (_ref2 = (_ref3 = this.schema[item.stream]) != null ? _ref3["in"] : void 0) != null ? _ref2 : 1;
            if (slots_needed + slots_used > slots) {
              break;
            }
            slots_used += slots_needed;
            result.push({
              s: item.stream,
              st: item.status,
              u: item.updated,
              o: item.data,
              i: item.id
            });
            in_from = item.updated;
            out_items++;
          }
          if (result.length === 0) {
            if (reset_schema) {
              do_reset_schema(null);
            } else {
              receive_out(null);
            }
            return;
          }
          return oauth.rest(app, '/rest/in?', JSON.stringify({
            a: result
          }), __bind(function(err, res) {
            if (err) {
              return finish_sync(err);
            }
            return send_in(null);
          }, this));
        }, this));
      }, this);
      do_reset_schema = __bind(function() {
        this.db.clean = true;
        return this.db.verify(this.db_schema, __bind(function(err, reset) {
          if (err) {
            return finish_sync(err);
          }
          out_from = 0;
          return receive_out(null);
        }, this));
      }, this);
      get_last_sync = __bind(function() {
        return this.db.query('select * from updates order by id desc', [], __bind(function(err, data) {
          if (err) {
            return finish_sync(err);
          }
          if (data.length > 0) {
            in_from = data[0].version_in || 0;
            out_from = data[0].version_out || 0;
            if (!clean_sync && out_from > 0) {
              clean_sync = false;
            }
          }
          log('Start sync with', in_from, out_from);
          return send_in(null);
        }, this));
      }, this);
      return oauth.rest(app, '/rest/schema?', null, __bind(function(err, schema) {
        if (err) {
          return finish_sync(err);
        }
        if (!this.schema || this.schema._rev !== schema._rev) {
          this.db.set('schema', JSON.stringify(schema));
          this.schema = schema;
          reset_schema = true;
          clean_sync = true;
        }
        return get_last_sync(null);
      }, this), {
        check: true
      });
    };
    StorageProvider.prototype._id = function(id) {
      if (!id) {
        id = new Date().getTime();
      }
      while (id <= this.last_id) {
        id++;
      }
      this.last_id = id;
      return id;
    };
    StorageProvider.prototype.on_change = function(type, stream, id) {};
    StorageProvider.prototype.create = function(stream, object, handler, options) {
      var fields, i, numbers, questions, texts, values, _ref, _ref2, _ref3, _ref4, _ref5, _ref6, _ref7, _ref8, _ref9;
      if (!this._precheck(stream, handler)) {
        return;
      }
      if (!object.id) {
        object.id = this._id();
      }
      questions = '?, ?, ?, ?, ?, ?';
      fields = 'id, status, updated, own, stream, data';
      values = [object.id, (_ref = options != null ? options.status : void 0) != null ? _ref : 1, (_ref2 = options != null ? options.updated : void 0) != null ? _ref2 : object.id, (_ref3 = options != null ? options.own : void 0) != null ? _ref3 : 1, stream, JSON.stringify(object)];
      numbers = (_ref4 = this.schema[stream].numbers) != null ? _ref4 : [];
      texts = (_ref5 = this.schema[stream].texts) != null ? _ref5 : [];
      for (i = 0, _ref6 = numbers.length; 0 <= _ref6 ? i < _ref6 : i > _ref6; 0 <= _ref6 ? i++ : i--) {
        questions += ', ?';
        fields += ', i' + i;
        values.push((_ref7 = object[numbers[i]]) != null ? _ref7 : null);
      }
      for (i = 0, _ref8 = texts.length; 0 <= _ref8 ? i < _ref8 : i > _ref8; 0 <= _ref8 ? i++ : i--) {
        questions += ', ?';
        fields += ', t' + i;
        values.push((_ref9 = object[texts[i]]) != null ? _ref9 : null);
      }
      return this.db.query('insert or replace into data (' + fields + ') values (' + questions + ')', values, __bind(function(err) {
        if (err) {
          return handler(err);
        } else {
          if (!(options != null ? options.internal : void 0)) {
            this.on_change('create', stream, object.id);
          }
          return handler(null, object);
        }
      }, this));
    };
    StorageProvider.prototype.update = function(stream, object, handler) {
      var fields, i, numbers, texts, values, _ref, _ref2, _ref3, _ref4, _ref5, _ref6;
      if (!this._precheck(stream, handler)) {
        return;
      }
      if (!object || !object.id) {
        return handler('Invalid object ID');
      }
      fields = 'status=?, updated=?, own=?, data=?';
      values = [2, this._id(), 1, JSON.stringify(object)];
      numbers = (_ref = this.schema[stream].numbers) != null ? _ref : [];
      texts = (_ref2 = this.schema[stream].texts) != null ? _ref2 : [];
      for (i = 0, _ref3 = numbers.length; 0 <= _ref3 ? i < _ref3 : i > _ref3; 0 <= _ref3 ? i++ : i--) {
        fields += ', i' + i + '=?';
        values.push((_ref4 = object[numbers[i]]) != null ? _ref4 : null);
      }
      for (i = 0, _ref5 = texts.length; 0 <= _ref5 ? i < _ref5 : i > _ref5; 0 <= _ref5 ? i++ : i--) {
        fields += ', t' + i + '=?';
        values.push((_ref6 = object[texts[i]]) != null ? _ref6 : null);
      }
      values.push(object.id);
      values.push(stream);
      return this.db.query('update data set ' + fields + ' where id=? and stream=?', values, __bind(function(err) {
        if (!err) {
          this.on_change('update', stream, object.id);
        }
        return handler(err);
      }, this));
    };
    StorageProvider.prototype.remove = function(stream, object, handler) {
      if (!this._precheck(stream, handler)) {
        return;
      }
      if (!object || !object.id) {
        return handler('Invalid object ID');
      }
      return this.db.query('update data set status=?, updated=?, own=? where  id=? and stream=?', [3, this._id(new Date().getTime()), 1, object.id, stream], __bind(function(err) {
        if (!err) {
          this.on_change('remove', stream, object.id);
        }
        return handler(err);
      }, this));
    };
    StorageProvider.prototype.select = function(stream, query, handler, options) {
      var ar, arr, array_to_query, fields, i, name, numbers, order, values, where, _i, _len, _ref, _ref2, _ref3, _ref4, _ref5;
      if (!this._precheck(stream, handler)) {
        return;
      }
      numbers = (_ref = this.schema[stream].numbers) != null ? _ref : [];
      fields = {
        id: 'id'
      };
      _ref3 = (_ref2 = this.schema[stream].texts) != null ? _ref2 : [];
      for (i in _ref3) {
        if (!__hasProp.call(_ref3, i)) continue;
        name = _ref3[i];
        fields[name] = 't' + i;
      }
      _ref5 = (_ref4 = this.schema[stream].numbers) != null ? _ref4 : [];
      for (i in _ref5) {
        if (!__hasProp.call(_ref5, i)) continue;
        name = _ref5[i];
        fields[name] = 'i' + i;
      }
      values = [stream, 3];
      array_to_query = __bind(function(arr, op) {
        var i, res, result, value, _ref6, _ref7, _ref8;
        if (arr == null) {
          arr = [];
        }
        if (op == null) {
          op = 'and';
        }
        result = [];
        for (i = 0, _ref6 = arr.length; 0 <= _ref6 ? i < _ref6 : i > _ref6; 0 <= _ref6 ? i++ : i--) {
          name = arr[i];
          if (name != null ? name.op : void 0) {
            res = this.array_to_query((_ref7 = name["var"]) != null ? _ref7 : [], name.op);
            if (res) {
              result.push(res);
            }
          } else {
            if (fields[name]) {
              value = arr[i + 1];
              if (value != null ? value.op : void 0) {
                result.push(fields[name] + value.op + '?');
                values.push((_ref8 = value["var"]) != null ? _ref8 : null);
              } else {
                result.push(fields[name] + '=?');
                values.push(value != null ? value : null);
              }
            }
            i++;
          }
        }
        return result.join(" " + op + " ");
      }, this);
      where = array_to_query(query != null ? query : []);
      order = [];
      if (options != null ? options.order : void 0) {
        arr = options != null ? options.order : void 0;
        if (!$.isArray(arr)) {
          arr = [arr];
        }
        for (_i = 0, _len = arr.length; _i < _len; _i++) {
          ar = arr[_i];
          if (fields[ar]) {
            order.push(fields[ar]);
          }
        }
      }
      order.push('id');
      return this.db.query('select data from data where stream=? and status<>? ' + (where ? 'and ' + where : '') + ' order by ' + (order.join(',')), values, __bind(function(err, data) {
        var item, result, _j, _len2;
        if (err) {
          return handler(err);
        }
        result = [];
        for (_j = 0, _len2 = data.length; _j < _len2; _j++) {
          item = data[_j];
          try {
            result.push(JSON.parse(item.data));
          } catch (err) {

          }
        }
        return handler(null, result);
      }, this));
    };
    return StorageProvider;
  })();
  DataManager = (function() {
    function DataManager(storage) {
      this.storage = storage;
    }
    DataManager.prototype.place_field = 'place';
    DataManager.prototype.place_step = 100;
    DataManager.prototype.sync_timeout = 30;
    DataManager.prototype.timeout_id = null;
    DataManager.prototype.open = function(handler) {
      return this.storage.open(__bind(function(err) {
        log('Open result', err);
        if (err) {
          return handler(err);
        }
        this.storage.on_change = __bind(function() {
          return this.schedule_sync(null);
        }, this);
        try {
          this.storage.schema = JSON.parse(this.get('schema'));
        } catch (e) {

        }
        return handler(null);
      }, this));
    };
    DataManager.prototype.unschedule_sync = function() {
      log('Terminating schedule', this.timeout_id);
      if (this.timeout_id) {
        clearTimeout(this.timeout_id);
        return this.timeout_id = null;
      }
    };
    DataManager.prototype.schedule_sync = function() {
      this.unschedule_sync(null);
      log('Scheduling sync', this.sync_timeout);
      return this.timeout_id = setTimeout(__bind(function() {
        return this.on_scheduled_sync(null);
      }, this), 1000 * this.sync_timeout);
    };
    DataManager.prototype.on_scheduled_sync = function() {};
    DataManager.prototype._resort = function(array, result) {
      var item, place, _i, _len, _results;
      place = this.place_step;
      _results = [];
      for (_i = 0, _len = array.length; _i < _len; _i++) {
        item = array[_i];
        item.place = place;
        place += this.place_step;
        _results.push(result.push(item));
      }
      return _results;
    };
    DataManager.prototype.sortArray = function(array, item, before) {
      var bbefore, bbefore_place, before_place, el, i, result;
      result = [];
      if (array.length === 0) {
        item[this.place_field] = this.place_step;
        return result;
      }
      if (!before) {
        if (array[array.length - 1][this.place_field]) {
          item[this.place_field] = this.place_step + array[array.length - 1][this.place_field];
        }
        return result;
      }
      bbefore = null;
      for (i in array) {
        el = array[i];
        if (el === before) {
          if (i > 0) {
            bbefore = array[i - 1];
          }
          break;
        }
      }
      if (!before.place || (bbefore && !bbefore.place)) {
        this._resort(array, result);
      }
      before_place = before[this.place_field];
      bbefore_place = bbefore ? bbefore[this.place_field] : 0;
      if (before_place - bbefore_place < 2) {
        this._resort(array, result);
        before_place = before[this.place_field];
        bbefore_place = bbefore ? bbefore[this.place_field] : 0;
      }
      item[this.place_field] = Math.floor((before_place - bbefore_place) / 2) + bbefore_place;
      return result;
    };
    DataManager.prototype.getTemplates = function(handler) {
      return this.storage.select('templates', [], __bind(function(err, data) {
        if (err) {
          return handler(err);
        }
        return handler(null, data);
      }, this));
    };
    DataManager.prototype.findSheet = function(query, handler) {
      return this.storage.select('sheets', query, __bind(function(err, data) {
        if (err) {
          return handler(err);
        }
        return handler(null, data);
      }, this));
    };
    DataManager.prototype.getSheets = function(handler) {
      return this.storage.select('sheets', [], __bind(function(err, data) {
        if (err) {
          return handler(err);
        }
        return handler(null, data);
      }, this), {
        order: 'place'
      });
    };
    DataManager.prototype.getNotes = function(sheet_id, area, handler) {
      var params;
      params = ['sheet_id', sheet_id];
      if (area) {
        params.push('area', area);
      }
      return this.storage.select('notes', params, __bind(function(err, data) {
        if (err) {
          return handler(err);
        }
        return handler(null, data);
      }, this), {
        order: 'place'
      });
    };
    DataManager.prototype.removeTemplate = function(object, handler) {
      return this.storage.select('sheets', ['template_id', object.id], __bind(function(err, data) {
        var item, _i, _len;
        if (err) {
          return handler(err);
        }
        for (_i = 0, _len = data.length; _i < _len; _i++) {
          item = data[_i];
          this.removeSheet(item, __bind(function() {}, this));
        }
        return this.storage.remove('templates', object, __bind(function(err) {
          if (err) {
            return handler(err);
          }
          return handler(null, object);
        }, this));
      }, this));
    };
    DataManager.prototype.removeNote = function(object, handler) {
      return this.storage.remove('notes', object, __bind(function(err) {
        if (err) {
          return handler(err);
        }
        return handler(null, object);
      }, this));
    };
    DataManager.prototype.removeSheet = function(object, handler) {
      return this.storage.select('notes', ['sheet_id', object.id], __bind(function(err, data) {
        var item, _i, _len;
        if (err) {
          return handler(err);
        }
        for (_i = 0, _len = data.length; _i < _len; _i++) {
          item = data[_i];
          this.removeNote(item, __bind(function() {}, this));
        }
        return this.storage.remove('sheets', object, __bind(function(err) {
          if (err) {
            return handler(err);
          }
          return handler(null, object);
        }, this));
      }, this));
    };
    DataManager.prototype._save = function(stream, object, handler) {
      if (!object.id) {
        return this.storage.create(stream, object, __bind(function(err) {
          if (err) {
            return handler(err);
          }
          return handler(null, object);
        }, this));
      } else {
        return this.storage.update(stream, object, __bind(function(err) {
          if (err) {
            return handler(err);
          }
          return handler(null, object);
        }, this));
      }
    };
    DataManager.prototype.saveTemplate = function(object, handler) {
      return this._save('templates', object, handler);
    };
    DataManager.prototype.saveSheet = function(object, handler) {
      return this._save('sheets', object, handler);
    };
    DataManager.prototype.saveNote = function(object, handler) {
      return this._save('notes', object, handler);
    };
    DataManager.prototype.get = function(name, def) {
      return this.storage.db.get(name, def);
    };
    DataManager.prototype.set = function(name, value) {
      return this.storage.db.set(name, value);
    };
    DataManager.prototype.sync = function(oauth, handler) {
      return this.storage.sync('lima1', oauth, __bind(function(err, data) {
        if (!err && this.timeout_id) {
          this.unschedule_sync(null);
        }
        return handler(err, data);
      }, this));
    };
    return DataManager;
  })();
  window.HTML5Provider = HTML5Provider;
  window.StorageProvider = StorageProvider;
  window.DataManager = DataManager;
}).call(this);
