<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<title>Account Management</title>

<script type="text/javascript" src="javascript/ext-base.js"></script>
<script type="text/javascript" src="javascript/ext-all-debug.js"></script>

<script type="text/javascript" src="javascript/AjaxWidget/Common.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/CommonFormVTypes.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/Message.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/CreateAccountWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/EditAccountWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/AssignRoleWidget.js"></script>
<link rel="stylesheet" type="text/css" href="css/Message.css"/>

<script type="text/javascript">

	Ext.ns('ezScrum');

	var Account = Ext.data.Record.create([
	 	  'ID', 'Name', 'Mail', 'Roles', 'Enable'
	]);
	
	var accountReader = new Ext.data.XmlReader({
		record: 'Account',
 		idPath : 'ID'
 		//successProperty: 'Result'
	}, Account);
	
	var accountStore = new Ext.data.Store({
		fields:[
    	{name : 'Name'},
		{name : 'ID'},
		{name : 'Mail'},
		{name : 'Roles'},
		{name : 'Enable'}		
		],
		reader : accountReader
	});
	
	var CheckCreateAccount = Ext.data.Record.create([
	   'Check'
	]);
	
	var checkReader = new Ext.data.XmlReader({
	   record: 'CheckCreateAccount'
	}, CheckCreateAccount);
	
	var checkStore = new Ext.data.Store({
    	fields:[
			{name : 'Check'}
		],
		reader : checkReader
	});
	
	function showMask(targetId, msg)
	{
		new Ext.LoadMask(Ext.get(targetId), {msg:msg}).show();
	}
	
	function hideMask(targetId)
	{
		new Ext.LoadMask(Ext.get(targetId)).hide();
	}
	
	function successFn(response)
	{
		accountStore.loadData(response.responseXML);
 		hideMask('AccountsWidget');
	}
	
	function failureFn()
	{
		alert('Failure');
		hideMask('AccountsWidget');
	}
	
	Ext.onReady(function() {

		Ext.Ajax.request({
			url:'getAccountList.do',
			success:function(response){
				accountStore.loadData(response.responseXML);
			},
			failure:function(){
				alert('Get Account List Fail!!');
			}
		});
		
		function checkUser(val) {
			if (eval(val)) {
				return '<center><img title="usable" src="images/ok.png" /></center>'
			} else {
				return '<center><img title="unusable" src="images/fail.png" /></center>'
			}
		}
		// Create Account Widget
		var CreateAccountWindow = new ezScrum.CreateAccountWidget({
			listeners:{
				CreateSuccess: function(win, response, record) {
					// add the record into first
					var s = accountsWidget.getStore();
					s.insert(0, record);
					
					// focus on the record
					accountsWidget.getSelectionModel().selectRow(0);
			 		accountsWidget.getView().focusRow(0);
					
					this.hide();
					Ext.example.msg('Create Account', 'Create Account Success');
				},
				CreateFailure: function(win, response) {
					Ext.example.msg('Create Account', 'Create Account Failure.');
				}
			}
		});
		
		// Edit Account Widget
		var editAccountWidget = new ezScrum.EditAccountWidget({
			listeners:{
				LoadSuccess:function(win, form, response, record){
					// Load Account Success
				},
				LoadFailure:function(win, form, response, accountID){
					// Load Account Error
				},
				EditSuccess:function(win, form, response, record){
					// Edit Account Item Success
					var s = accountsWidget.getStore();
					var index = (s.findExact('ID', record.data['ID']));
					s.removeAt(index);
					s.insert(index, record);
					
					// focus on the record
					accountsWidget.getSelectionModel().selectRow(index);
			 		accountsWidget.getView().focusRow(index);
					
			 		this.hide();
			 		Ext.example.msg('Edit Account', 'Edit Account Success');
		 			
				},
				EditFailure:function(win, form, response, issueId){
					// Edit Unplanned Item Error
					Ext.example.msg('Edit Account', 'Edit Account Failure.');
				}
			}
		
		});	
		
		// Assign Role Widget
		var assignRoleWidget = new ezScrum.AssignRoleWidget({
			listeners:{
				LoadSuccess:function(win, form, response, record){
					// Load Role Success
				},
				LoadFailure:function(win, form, response){
					Ext.example.msg('Load Account', 'Load Account Failure.');
				},
				UpdateSuccess:function(win, form, response, record){

					var s = accountsWidget.getStore();
					var index = (s.findExact('ID', record.data['ID']));
					s.removeAt(index);
					s.insert(index, record);
					
					// focus on the record
					accountsWidget.getSelectionModel().selectRow(index);
			 		accountsWidget.getView().focusRow(index);
			 		
			 		
					Ext.example.msg('Assign Role', 'Update Role Success');
				},
				UpdateFailure:function(win, form, response){
					Ext.example.msg('Assign Role', 'Update Role Fail! Please try again.');
				}
			}
		
		});
		
		var accountsWidget = new Ext.grid.GridPanel({
			id : 'AccountsWidget',
			region : 'center',
			store : accountStore,
			viewConfig: {
	            forceFit:true
	        },
			colModel: new Ext.grid.ColumnModel({
				 columns: [
				 	{dataIndex: 'ID',header: 'User ID', width: 80},
					{dataIndex: 'Name',header: 'Name', width: 80},		            
		            {dataIndex: 'Mail',header: 'E-mail', width: 100},
		            {dataIndex: 'Roles',header: 'Roles', width: 120},
		            {dataIndex: 'Enable',header: 'Enable', renderer: checkUser, width: 20}		          
				]
			}),
			sm: new Ext.grid.RowSelectionModel({
		    	singleSelect:true
		    }),
		    stripeRows: true,
		    frame: true,
		    deleteRecord : function (record) {
		    	this.getStore().remove(record);
		    },
		    addRecord : function (record) {
		    	this.getStore().add(record);
		    }
		    
		});
				
		var contentWidget = new Ext.Panel({
			height	: 600,
			layout	: 'border',
			title	: 'Account List',
			renderTo: 'content',
			// Assign Role Action
			assignRole : function()
			{
				if(accountsWidget.getSelectionModel().getSelected() != null)
				{
					var id = accountsWidget.getSelectionModel().getSelected().data['ID'];
					assignRoleWidget.loadAssignRole(id);		
				}
				
			},
			editAccount : function()
			{
				if(accountsWidget.getSelectionModel().getSelected() != null)
				{
					var id = accountsWidget.getSelectionModel().getSelected().data['ID'];
					editAccountWidget.loadEditAccount(id);		
				}
			},
			tbar: [
				{
					id		: 'createAccountBtn', 
					text	: 'Create Account', 
					icon	: 'images/add3.png',						
					handler	: function() {
						CreateAccountWindow.showWidget();
					}			
				},
				{
					id		: 'editAccountbtn',
					text	: 'Edit Account',
					icon	: 'images/edit.png',
					disabled: 'true',
					handler	: function() {
					/*
						var record = accountsWidget.getSelectionModel().getSelected();
						document.location.href = "./showAccountInfo.do?id=" + record.data['ID'];
						*/
						contentWidget.editAccount();
					}
				},
				{
					id		: 'deleteAccountbtn',
					text	: 'Delete Account',
					icon	: 'images/delete.png',
					disabled: 'true',
					handler	: function() {
						Ext.MessageBox.confirm('Delete User', 'Are you sure to delete the user ?', 
							function(btn) {
								if (btn == 'yes') {
									var record = accountsWidget.getSelectionModel().getSelected();
									Ext.Ajax.request({
										url		: 'deleteAccount.do',
										params	: {id: record.data['ID']},
										success	: function(response) {
											Ext.getCmp('AccountsWidget').deleteRecord(record);
											Ext.example.msg('Delete User', 'Delete User Success.');
										},
										failure	: function() {
											Ext.MessageBox.alert('Delate User', 'Sorry, delete user failure');
										}
									});
								}
						});
					}
				},
				{id:'assignRoleBtn', disabled:true, text:'Assign Role', icon:'images/userIcon.png', handler:function(){contentWidget.assignRole();}}		
			],
			items : [accountsWidget],
			selectionChange : function() {
				var single = accountsWidget.getSelectionModel().getCount()==1;
				if(single) {					
					this.getTopToolbar().get('editAccountbtn').setDisabled(false);
					this.getTopToolbar().get('deleteAccountbtn').setDisabled(false);
					this.getTopToolbar().get('assignRoleBtn').setDisabled(false);
					//var record = accountsWidget.getSelectionModel().getSelected();					

					this.getTopToolbar().get('deleteAccountbtn').setDisabled(false);										
				}
			}
		});
		accountsWidget.getSelectionModel().on({'selectionchange':{buffer:10, fn:function(){contentWidget.selectionChange();}}});						
	});
	
</script>

<div id = "content"></div>