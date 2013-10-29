var ProjectRecord = Ext.data.Record.create([ 'ProjectName', 'ProjectDisplayName', 'AttachFileSize',	'Commnet', 'ProjectManager', 'ProjectCreateDate' ]);

var ProjectReader = new Ext.data.JsonReader({
	id: "ID"
}, ProjectRecord);

var ProjectModifyStore = new Ext.data.Store({
	fields : [
		{name : 'ProjectName'}, 
		{name : 'ProjectDisplayName'},
		{name : 'AttachFileSize'},
		{name : 'Commnet'},
		{name : 'ProjectManager'}
	],
	reader : ProjectReader
});

var ProjectDescStore = new Ext.data.Store({
	fields : [
		{name : 'Commnet'},
		{name : 'ProjectManager'},
		{name : 'ProjectCreateDate'}
	],
	reader : ProjectReader
});

var ProjectDescItem = [
	{fieldLabel: 'Comment', name: 'Commnet', xtype:'textfield', anchor: '50%', readOnly: true},
	{fieldLabel: 'Project Manager', name: 'ProjectManager', xtype:'textfield', anchor: '50%', readOnly: true},
	{fieldLabel: 'Project CreateDate', name: 'ProjectCreateDate', xtype:'textfield', anchor: '50%', readOnly: true}
];

var ProjectDescModifyItem = [
	{fieldLabel: 'Project Name', name: 'ProjectName', xtype:'textfield', readOnly: true, anchor: '50%'},
	{fieldLabel: 'Project Dislpay Name', name: 'ProjectDisplayName', xtype:'textfield', anchor: '50%'},
   	{fieldLabel: 'Comment', name: 'Commnet', xtype:'textarea', anchor: '50%', height: 50},
   	{fieldLabel: 'Project Manager', name: 'ProjectManager', xtype:'textfield', anchor: '50%'},
   	{fieldLabel: 'Attach File Max Size (Default: 2MB)', name: 'AttachFileSize', xtype:'numberfield', anchor: '50%'}
];