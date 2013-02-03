## Lima1 - offline JSON object data storage for Android and Web ##

### Project consists of three parts:

* Server side sync service [here](kvj/Lima1OpenShift)
* Android data provider application [here](android/Lima1SyncService)
* HTML5 library [here](Whiskey2/tree/master/html/lib/lima1)

### Main features

* Data model is described in JSON format on server side
  * Streams (objects types) can have indexes, foreign keys, indexable data types
* CRUD operations on streams, remove cascade, query builder for selects
* Data model versioning, with automatic schema upgrade
* Every local data storage synchronizes it's state and data with server on regular basis (on edit/by ping)
* All data storage features available offline
* OAuth2 for authorization
* Supports file attachments

### HTML5 Web features

* Designed to run with Chrome Web applications
* Written in CoffeeScript
* Depends on jquery, underscore
* Uses HTML5 File API for attachments
* Uses relational Web DB, local storage internally

### Android features ###

* Single Application provides pluggable access to Data source via AIDL
* Single Sign On (one login for all supported applications)
* Uses SQLite internally
* Android 2.1+
* AIDL service provided: [SyncService.aidl](android/Lima1SyncService/src-aidl/org/kvj/lima1/sync/SyncService.aidl)
