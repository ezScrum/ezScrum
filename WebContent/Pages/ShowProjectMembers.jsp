<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<title>Project Members</title>

<script type="text/javascript" src="javascript/ext-base.js"></script>
<script type="text/javascript" src="javascript/ext-all-debug.js"></script>

<script type="text/javascript" src="javascript/AjaxWidget/Common.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/Message.js"></script>
<link rel="stylesheet" type="text/css" href="css/Message.css"/>

<script type="text/javascript" >
Ext.ns('ezScrum');

var Member = Ext.data.Record.create([
	'ID', 'Name', 'Role', 'Enable'
]);

var memberReader = new Ext.data.XmlReader({
	record: 'Member',
	idPath : 'ID'
}, Member);

var memberStore = new Ext.data.Store({
	fields:[
		{name : 'ID'},
	   	{name : 'Name'},	
		{name : 'Role'},
		{name : 'Enable'}
	],
	reader : memberReader
});

Ext.onReady(function() {
	Ext.Ajax.request({
		url : 'getProjectMembers.do',
		success : function(response) {
			memberStore.loadData(response.responseXML);
		},
		failure : function(){
			Ext.example.msg('Load Members', 'Load Members Failure.');
		}
	});

	function checkUser(val) {
		if (eval(val)) {
			return '<center><img title="usable" src="images/ok.png" /></center>'
		} else {
			return '<center><img title="unusable" src="images/fail.png" /></center>'
		}
	}

	var memberWidget = new Ext.grid.GridPanel({
		id : 'MemberWidget',
		region : 'center',
		store : memberStore,
		viewConfig: {
            forceFit:true
        },
		colModel: new Ext.grid.ColumnModel({
			columns: [
			 	{dataIndex: 'ID',header: 'User ID', width: 70},
				{dataIndex: 'Name',header: 'User Name', width: 100},		            
	            {dataIndex: 'Role',header: 'Role', width: 100},
	            {dataIndex: 'Enable',header: 'Enable', renderer: checkUser, width: 20}		          
			]
		})
	});

	var contentWidget = new Ext.Panel({
		height	: 500,
		layout	: 'border',
		title	: 'Member List',
		renderTo: 'content',
		items : [memberWidget]
	});
});
</script>

<div id = "content"></div>
<div id = "SideShowItem" style="display:none;">viewProjectMembers</div>