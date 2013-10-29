var TenantRecord = Ext.data.Record.create([
	'ID',
	'Name',
//	'Mail',
	'Enable',
	'AdminName',
//	'ActivativeDate',
	'Description',
//	'Period'
//	, 'Password'
]);

var TenantReader = new Ext.data.XmlReader({
	record: 'Tenant',
	idPath : 'ID'
}, TenantRecord);

var TenantStore = new Ext.data.Store({
	fields:[
	    {name : 'ID'},
		{name : 'Name'},
//		{name : 'Mail'},
		{name : 'Enable'},
		{name : 'AdminName'},
		{name : 'Description'},
//		{name : 'ActivativeDate'},
//		{name : 'Period'}		
	],
	reader : TenantReader
});

// for tenant renderer
function checkUser(val) {
	if (eval(val)) {
		return '<center><img title="usable" src="images/ok.png" /></center>';
	} else {
		return '<center><img title="unusable" src="images/fail.png" /></center>';
	}
}

var TenantColumnModel = new Ext.grid.ColumnModel({
	columns: [
 	     {dataIndex: 'ID',header: 'ID', width: 150},	// alex
 	     {dataIndex: 'Name',header: 'Name', width: 150},		            
// 	     {dataIndex: 'Mail',header: 'E-mail', width: 200},
 	     {dataIndex: 'Description',header: 'Description', width: 200},
// 	     {dataIndex: 'ActivativeDate',header: 'ActivativeDate', width: 200},
 	     {dataIndex: 'AdminName',header: 'AdminName', width: 200},
// 	    {dataIndex: 'Period',header: 'Period', width: 200},
 	     {dataIndex: 'Enable',header: 'Enable', renderer: checkUser, width: 70}		          
 	]
});