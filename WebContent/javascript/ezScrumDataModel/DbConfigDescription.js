var DBConfigRecord = Ext.data.Record.create([ 'ServerUrl', 'DBAccount',
		'DbPassword', 'DBType', 'DBName' ]);

var DBConfigReader = new Ext.data.JsonReader({
	id : "ID"
}, DBConfigRecord);

var DBConfigStore = new Ext.data.Store({
	fields : [ {
		name : 'ServerUrl'
	}, {
		name : 'DBAccount'
	}, {
		name : 'DbPassword'
	}, {
		name : 'DBType'
	}, {
		name : 'DBName'
	} ],
	reader : DBConfigReader
});

// DataBase選單
var sqlList = [ [ 'MySQL' ] ];
var sqlDataType = new Ext.data.ArrayStore({
	autoDestory : true,
	storeId : 'sqlDataType',
	idIndex : 0,
	fields : [ 'SQLName' ],
	data : sqlList
});
var sqlCombo = new Ext.form.ComboBox({
	store : sqlDataType,
	displayField : 'SQLName',
	fieldLabel : 'DB Type',
	typeAhead : true,
	mode : 'local',
	triggerAction : 'all',
	emptyText : 'Select a DataBase',
	selectOnFocus : true,
	allowBlank : false,
	name : 'DBType'
});

var DBConfigItem = [ {
	fieldLabel : 'Server Url',
	name : 'ServerUrl',
	xtype : 'textfield',
	allowBlank : false,
	anchor : '50%'
}, {
	fieldLabel : 'DB Account',
	name : 'DBAccount',
	xtype : 'textfield',
	allowBlank : false,
	anchor : '50%'
}, {
	fieldLabel : 'DB Password',
	name : 'DBPassword',
	xtype : 'textfield',
	allowBlank : false,
	anchor : '50%',
	inputType : 'password'
}, sqlCombo
, {
	fieldLabel : 'DB Name',
	name : 'DBName',
	xtype : 'textfield',
	allowBlank : false,
	anchor : '50%'
}, {
	xtype : 'RequireFieldLabel',
	style : 'margin-left:60px;color:red;'
} ];