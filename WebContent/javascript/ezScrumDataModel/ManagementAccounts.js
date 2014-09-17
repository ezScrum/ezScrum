var AccountRecord = Ext.data.Record.create([
	'ID', 'Account', 'Name', 'Mail', 'Roles', 'Enable', 'Password'
]);

var AccountReader = new Ext.data.XmlReader({
	record: 'AccountInfo',
	idPath : 'ID'
}, AccountRecord);

var AccountStore = new Ext.data.Store({
	fields:[
	    {name : 'ID'},
		{name : 'Account'},
		{name : 'Name'},
		{name : 'Mail'},
		{name : 'Roles'},
		{name : 'Enable'}		
	],
	reader : AccountReader
});

// for account renderer
function checkUser(val) {
	if (eval(val)) {
		return '<center><img title="usable" src="images/ok.png" /></center>';
	} else {
		return '<center><img title="unusable" src="images/fail.png" /></center>';
	}
}

var AccountColumnModel = new Ext.grid.ColumnModel({
	columns: [
 	     {dataIndex: 'Account',header: 'User ID', width: 150},
 	     {dataIndex: 'Name',header: 'Name', width: 150},		            
 	     {dataIndex: 'Mail',header: 'E-mail', width: 200},
 	     {dataIndex: 'Roles',header: 'Roles', width: 300},
 	     {dataIndex: 'Enable',header: 'Enable', renderer: checkUser, width: 70}		          
 	]
});