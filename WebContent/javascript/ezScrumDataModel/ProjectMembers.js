var MemberRecord = Ext.data.Record.create([
	'ID', 'Name', 'Role', 'Enable'
]);

var MemberReader = new Ext.data.XmlReader({
	record: 'Member',
	idPath : 'ID'
}, MemberRecord);

var MemberStore = new Ext.data.Store({
	fields:[
		{name : 'ID'},
	   	{name : 'Name'},	
		{name : 'Role'},
		{name : 'Enable'}
	],
	reader : MemberReader
});

// for members renderer
function checkUser(val) {
	if (eval(val)) {
		return '<center><img title="usable" src="images/ok.png" /></center>'
	} else {
		return '<center><img title="unusable" src="images/fail.png" /></center>'
	}
}

var MemberColumnModel = new Ext.grid.ColumnModel({
	columns: [ 
		{dataIndex: 'ID',header: 'User ID', width: 200},
		{dataIndex: 'Name',header: 'User Name', width: 200},		            
		{dataIndex: 'Role',header: 'Role', width: 200},
		{dataIndex: 'Enable',header: 'Enable', renderer: checkUser, width: 70}		          
	]
});