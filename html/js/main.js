(function() {
  var DBProvider, HTML5Provider, StorageProvider, log, storage;
  var __slice = Array.prototype.slice, __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  }, __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; };
  log = function() {
    var params;
    params = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
    return console.log.apply(console, params);
  };
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
      if (clean == null) {
        clean = true;
      }
      if (!(window && window.openDatabase)) {
        return handler('HTML5 DB not supported');
      }
      log('Ready to open');
      try {
        this.db = window.openDatabase(this.name, '', this.name, 1024 * 1024 * 10);
        log('Opened', this.db.version, this.version);
        this.version_match = this.db.version === this.version;
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
      log('verify', schema);
      return this.query('select name, type from sqlite_master where type=? or type=?', ['table', 'index'], __bind(function(err, res, tr) {
        var create_at, drop_at;
        log('SQL result', err, res, tr);
        if (err) {
          return handler(err);
        }
        if (!this.version_match) {
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
    return HTML5Provider;
  })();
  StorageProvider = (function() {
    StorageProvider.prototype.schema = ['create table if not exists updates (id integer primary key, version_in integer, version_out integer, version text)', 'create table if not exists data (id integer primary key, status integer default 0, updated integer default 0, own integer default 1, stream text, data text, i0 integer, i1 integer, i2 integer, i3 integer, i4 integer, i5 integer, i6 integer, i7 integer, i8 integer, i9 integer, t0 text, t1 text, t2 text, t3 text, t4 text, t5 text, t6 text, t7 text, t8 text, t9 text)'];
    function StorageProvider(connection) {
      this.connection = connection;
    }
    StorageProvider.prototype.open = function(handler) {
      this.db = new HTML5Provider('test.db', '1');
      return this.db.open(true, __bind(function(err) {
        log('Open result:', err);
        if (!err) {
          return this.db.verify(this.schema, __bind(function(err, reset) {
            log('Verify result', err, reset);
            return handler(err);
          }, this));
        }
      }, this));
    };
    StorageProvider.prototype.create = function(stream, object, handler) {
      var fields, i, numbers, questions, texts, values, _ref, _ref2, _ref3, _ref4, _ref5, _ref6;
      if (!this.schema[stream]) {
        return handler('Unsupported stream');
      }
      if (!object.id) {
        object.id = new Date().getTime();
      }
      questions = '?, ?, ?, ?, ?, ?';
      fields = 'id, status, updated, own, stream, data';
      values = [object.id, 1, object.id, 1, stream, JSON.stringify(object)];
      numbers = (_ref = this.schema[stream].numbers) != null ? _ref : [];
      texts = (_ref2 = this.schema[stream].texts) != null ? _ref2 : [];
      for (i = 0, _ref3 = numbers.length; 0 <= _ref3 ? i < _ref3 : i > _ref3; 0 <= _ref3 ? i++ : i--) {
        questions += ', ?';
        fields += ', i' + i;
        values.push((_ref4 = object[numbers[i]]) != null ? _ref4 : null);
      }
      for (i = 0, _ref5 = texts.length; 0 <= _ref5 ? i < _ref5 : i > _ref5; 0 <= _ref5 ? i++ : i--) {
        questions += ', ?';
        fields += ', t' + i;
        values.push((_ref6 = object[texts[i]]) != null ? _ref6 : null);
      }
      return this.db.query('insert into data (' + fields + ') values (' + questions + ')', values, __bind(function(err) {
        if (err) {
          return handler(err);
        } else {
          return handler(null, object);
        }
      }, this));
    };
    StorageProvider.prototype.update = function(stream, object, handler) {
      var fields, i, numbers, texts, values, _ref, _ref2, _ref3, _ref4, _ref5, _ref6;
      if (!this.schema[stream]) {
        return handler('Unsupported stream');
      }
      if (!object || !object.id) {
        return handler('Invalid object ID');
      }
      fields = 'status=?, updated=?, own=?, data=?';
      values = [2, new Date().getTime(), 1, JSON.stringify(object)];
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
        return handler(err);
      }, this));
    };
    StorageProvider.prototype.remove = function(stream, object, handler) {
      if (!this.schema[stream]) {
        return handler('Unsupported stream');
      }
      if (!object || !object.id) {
        return handler('Invalid object ID');
      }
      return this.db.query('update data set status=?, updated=?, own=? where  id=? and stream=?', [3, new Date().getTime(), 1, object.id, stream], __bind(function(err) {
        return handler(err);
      }, this));
    };
    StorageProvider.prototype.select = function(stream, query, handler, options) {
      var array_to_query, fields, i, name, numbers, values, where, _ref, _ref2, _ref3, _ref4, _ref5;
      if (!this.schema[stream]) {
        return handler('Unsupported stream');
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
      return this.db.query('select data from data where stream=? and status<>? ' + (where ? 'and ' + where : '') + ' order by id', values, __bind(function(err, data) {
        var item, result, _i, _len;
        if (err) {
          return handler(err);
        }
        result = [];
        for (_i = 0, _len = data.length; _i < _len; _i++) {
          item = data[_i];
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
  storage = new StorageProvider(null);
  storage.open(function(err) {
    log('Open result', err);
    storage.schema = {
      templates: {
        texts: ['name', 'tag']
      },
      sheets: {
        numbers: ['order', 'template_id'],
        texts: ['name']
      },
      notes: {
        numbers: ['place', 'sheet_id', 'date', 'link_id', 'mark'],
        texts: ['area', 'text']
      }
    };
    return storage.create('templates', {
      name: 'test 01',
      body: {
        test: 0
      }
    }, function(err) {
      log('Create result', err);
      return storage.select('templates', [
        'name', 'test 01', 'id', {
          op: '>',
          "var": 0
        }
      ], function(err, data) {
        return log('Select', err, data);
      });
    });
  });
}).call(this);
