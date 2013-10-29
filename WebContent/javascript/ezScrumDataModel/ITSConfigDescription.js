var ITSConfigRecord = Ext.data.Record.create([ 'ServerUrl', 'ITSAccount', 'ITSPassword' ]);

var ITSConfigReader = new Ext.data.JsonReader({
	id: "ID"
}, ITSConfigRecord);

var ITSConfigStore = new Ext.data.Store({
	fields : [
		{name : 'ServerUrl'}, 
		{name : 'ITSAccount'},
		{name : 'ITSPassword'}
	],
	reader : ITSConfigReader
});

var ITSConfigItem = [
	{fieldLabel: 'Server Url', name: 'ServerUrl', xtype:'textfield', allowBlank:false, anchor: '50%'},
	{fieldLabel: 'Account of DB', name: 'ITSAccount', xtype:'textfield', allowBlank:false, anchor: '50%'},
	{fieldLabel: 'Password of DB', name: 'ITSPassword', inputType: 'password', allowBlank:false, xtype:'textfield', anchor: '50%'},
	{xtype: 'RequireFieldLabel', style: 'margin-left:60px;color:red;'}
];