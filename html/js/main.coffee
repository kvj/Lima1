class DBProvider
	constructor: (@name, @version = '1')->

	open: (clean = true, handler) ->
		handler 'open: Not implemented'

	verify: (schema, handler) ->
		handler 'Not implemented'

	query: (line, params, handler) ->
		handler 'Not implemented'

	get: (name, def) ->
		null

	set: (name, value) ->
		null

class HTML5Provider extends DBProvider
	open: (clean = true, handler) ->
		return handler 'HTML5 DB not supported' unless window and window.openDatabase
		log 'Ready to open'
		try
			@db = window.openDatabase @name, '', @name, 1024 * 1024 * 10
			log 'Opened', @db.version, @version
			@version_match = @db.version is @version
			handler null
		catch error
			handler error.message

	_query: (query, params, transaction, handler) ->
		log 'SQL:', query, params
		transaction.executeSql query, params, (transaction, result) =>
			# log 'Query result:', result, query
			data = []
			for i in [0...result.rows.length]
				obj = {}
				for own key, value of result.rows.item i
					obj[key] = value if value
				data.push obj
			handler null, data, transaction
		, (transaction, error) =>
			log 'Error SQL', error
			handler error.message

	query: (query, params, handler, transaction) ->
		return handler "DB isn't opened" unless @db
		if transaction
			@_query query, params, transaction, handler
		else
			@db.transaction (transaction) =>
				# log 'Ready to query', transaction
				@_query query, params, transaction, handler
			, (error) =>
				log 'Error transaction', error
				handler error.message

	verify: (schema, handler) ->
		# log 'verify', schema
		@query 'select name, type from sqlite_master where type=? or type=?', ['table', 'index'], (err, res, tr) =>
			log	'SQL result', err, res, tr
			if err
				return handler err
			if not @version_match
				# drop tables/etc
				create_at = (index) =>
					if index < schema.length
						@query schema[index], [], (err) =>
							if err
								return handler err
							create_at index+1
						, tr
					else
						log 'Changing version', @db.version, '=>', @version
						if not @version_match
							@db.changeVersion @db.version, @version, (tr) =>
								handler null, true
							, (err) =>
								log 'Version change error', err
								handler err
						else
							handler null, false
				drop_at = (index) =>
					if index < res.length
						if res[index].name.substr(0, 2) is '__' or res[index].name.substr(0, 7) is 'sqlite_'
							return drop_at index+1
						@query 'drop '+res[index].type+' '+res[index].name, [], (err) =>
							if err
								return handler err
							drop_at index+1
						, tr
					else
						# drop complete
						create_at 0
				drop_at 0
			else
				handler null, false

class StorageProvider

	schema: ['create table if not exists updates (id integer primary key, version_in integer, version_out integer, version text)', 'create table if not exists data (id integer primary key, status integer default 0, updated integer default 0, own integer default 1, stream text, data text, i0 integer, i1 integer, i2 integer, i3 integer, i4 integer, i5 integer, i6 integer, i7 integer, i8 integer, i9 integer, t0 text, t1 text, t2 text, t3 text, t4 text, t5 text, t6 text, t7 text, t8 text, t9 text)']

	constructor: (@connection, @db) ->

	open: (handler) ->
		@db.open true, (err) =>
			log 'Open result:', err
			if not err
				@db.verify @schema, (err, reset) =>
					log 'Verify result', err, reset
					handler err
	
	create: (stream, object, handler) ->
		if not @schema[stream]
			return handler 'Unsupported stream'
		if not object.id
		  object.id = new Date().getTime()+(@schema[stream].index ? 0)
		questions = '?, ?, ?, ?, ?, ?'
		fields = 'id, status, updated, own, stream, data'
		values = [object.id, 1, object.id, 1, stream, JSON.stringify(object)]
		numbers = @schema[stream].numbers ? []
		texts = @schema[stream].texts ? []
		for i in [0...numbers.length]
			questions += ', ?'
			fields += ', i'+i
			values.push object[numbers[i]] ? null
		for i in [0...texts.length]
			questions += ', ?'
			fields += ', t'+i
			values.push object[texts[i]] ? null
		@db.query 'insert into data ('+fields+') values ('+questions+')', values, (err) =>
			if err
				handler err
			else handler null, object

	update: (stream, object, handler) ->
		if not @schema[stream]
			return handler 'Unsupported stream'
		if not object or not object.id
			return handler 'Invalid object ID'
		# prepare SQL
		fields = 'status=?, updated=?, own=?, data=?'
		values = [2, new Date().getTime(), 1, JSON.stringify(object)]
		numbers = @schema[stream].numbers ? []
		texts = @schema[stream].texts ? []
		for i in [0...numbers.length]
			fields += ', i'+i+'=?'
			values.push object[numbers[i]] ? null
		for i in [0...texts.length]
			fields += ', t'+i+'=?'
			values.push object[texts[i]] ? null
		values.push object.id
		values.push stream
		@db.query 'update data set '+fields+' where id=? and stream=?', values, (err) =>
			handler err

	remove: (stream, object, handler) ->
		if not @schema[stream]
		  return handler 'Unsupported stream'
		if not object or not object.id
			return handler 'Invalid object ID'
		@db.query 'update data set status=?, updated=?, own=? where  id=? and stream=?', [3, new Date().getTime(), 1, object.id, stream], (err) =>
			handler err

	select: (stream, query, handler, options) ->
		if not @schema[stream]
			return handler 'Unsupported stream'
		numbers = @schema[stream].numbers ? []
		fields = id: 'id'
		for own i, name of @schema[stream].texts ? []
			fields[name] = 't'+i
		for own i, name of @schema[stream].numbers ? []
			fields[name] = 'i'+i
		values = [stream, 3]
		array_to_query = (arr = [], op = 'and') =>
			result = []
			for i in [0...arr.length]
				name = arr[i]
				if name?.op
					res = @array_to_query name.var ? [], name.op
					if res
						result.push res
				else
					if fields[name]
						value = arr[i+1]
						if value?.op
							# custom op
							result.push fields[name]+value.op+'?'
							values.push value.var ? null
						else
							#equal
							result.push fields[name]+'=?'
							values.push value ? null
					i++
			result.join " #{op} "
		where = array_to_query query ? []
		order = []
		if options?.order
			arr = options?.order
			if not $.isArray(arr)
				arr = [arr]
			for ar in arr
				if fields[ar] then order.push fields[ar]
		order.push 'id'
		@db.query 'select data from data where stream=? and status<>? '+(if where then 'and '+where else '')+' order by '+(order.join ','), values, (err, data) =>
			if err then return handler err
			result = []
			for item in data
				try
					result.push JSON.parse(item.data)
				catch err
			handler null, result		

class DataManager

	constructor: (@storage) ->
	
	place_field: 'place'
	place_step: 100

	open: (handler) ->
		@storage.open (err) =>
			log 'Open result', err 
			if err then return handler err
			@storage.schema = 
				templates:
					index: 0
					texts: ['name', 'tag']
				sheets:
					index: 1
					numbers: ['template_id', 'place']
					texts: ['title', 'code']
				notes:
					index: 2
					numbers: ['sheet_id', 'place']
					texts: ['area', 'text', 'due']
			handler null

	_resort: (array, result) ->
		place = @place_step
		for item in array
			item.place = place
			place += @place_step
			result.push item

	sortArray: (array, item, before) ->
		result = []
		if array.length is 0
			item[@place_field] = @place_step
			return result
		if not before
			# Add to the end
			if array[array.length-1][@place_field]
				item[@place_field] = @place_step+array[array.length-1][@place_field]
			return result
		bbefore = null
		for i, el of array
			if el is before
				if i>0 then bbefore = array[i-1]
				break
		if not before.place or (bbefore and not bbefore.place)
			@_resort array, result
		before_place = before[@place_field]
		bbefore_place = if bbefore then bbefore[@place_field] else 0
		if before_place-bbefore_place<2
			@_resort array, result
			before_place = before[@place_field]
			bbefore_place = if bbefore then bbefore[@place_field] else 0
		item[@place_field] = Math.floor((before_place-bbefore_place)/2)+bbefore_place
		return result

	getTemplates: (handler) ->
		@storage.select 'templates', [], (err, data) =>
			if err then return handler err
			handler null, data

	findSheet: (query, handler) ->
		@storage.select 'sheets', query, (err, data) =>
			if err then return handler err
			handler null, data

	getSheets: (handler) ->
		@storage.select 'sheets', [], (err, data) =>
			if err then return handler err
			handler null, data
		, {order: 'place'}

	getNotes: (sheet_id, area, handler) ->
		params = ['sheet_id', sheet_id]
		if area
			params.push 'area', area
		@storage.select 'notes', params, (err, data) =>
			if err then return handler err
			handler null, data
		, {order: 'place'}

	removeTemplate: (object, handler) ->
		@storage.select 'sheets', ['template_id', object.id], (err, data) =>
			if err then return handler err
			for item in data
				@removeSheet item, () =>
			@storage.remove 'templates', object, (err) =>
				if err then return handler err
				handler null, object

	removeNote: (object, handler) ->
		@storage.remove 'notes', object, (err) =>
			if err then return handler err
			handler null, object

	removeSheet: (object, handler) ->
		@storage.select 'notes', ['sheet_id', object.id], (err, data) =>
			if err then return handler err
			for item in data
				@removeNote item, () =>
			@storage.remove 'sheets', object, (err) =>
				if err then return handler err
				handler null, object

	_save: (stream, object, handler) ->
		if not object.id
			@storage.create stream, object, (err) =>
				if err then return handler err
				handler null, object
		else
			@storage.update stream, object, (err) =>
				if err then return handler err
				handler null, object

	saveTemplate: (object, handler) ->
		@_save 'templates', object, handler
	
	saveSheet: (object, handler) ->
		@_save 'sheets', object, handler

	saveNote: (object, handler) ->
		@_save 'notes', object, handler

window.HTML5Provider = HTML5Provider
window.StorageProvider = StorageProvider
window.DataManager = DataManager
# db = new HTML5Provider 'test.db', '1'
# storage = new StorageProvider null, db
# manager = new DataManager storage
# ui = new UIProvider manager
# ui.start null
