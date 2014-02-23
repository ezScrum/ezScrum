var AssignRole_Window = new ezScrum.window.AssignRoleWindow({
	listeners:{
		UpdateSuccess:function(win, form, response, record){
			var AccountGridCmp = Ext.getCmp('Account_Management_Grid_Panel');
			AccountGridCmp.editAccountRecoed(record);
			Ext.example.msg('Assign Role', 'Update Role Success.');
		},
		UpdateFailure:function(win, form, response){
			Ext.example.msg('Assign Role', 'Sorry, update role fail. Please try again.');
		}
	}
});

var AccountGridProxyStore = new Ext.data.Store({
	fields:[
	    {name : 'ID'},
		{name : 'Account'},
		{name : 'Name'},
		{name : 'Mail'},
		{name : 'Roles'},
		{name : 'Enable'}		
	],
	reader	: AccountReader,
	proxy	: new Ext.ux.data.PagingMemoryProxy()
});

ezScrum.AccountGrid = Ext.extend(Ext.grid.GridPanel, {
	id		: 'Account_Management_Grid_Panel',
	title	: 'Accounts List',
	url		: 'getAccountList.do',
	border	: false,
	frame	: false,
	bodyStyle	:'width:100%',
	autoWidth	: true,
	stripeRows	: true,
	store	: AccountGridProxyStore,
	colModel	: AccountColumnModel,
	viewConfig: {
        forceFit: true
    },
    sm		: new Ext.grid.RowSelectionModel({
    	singleSelect: true
    }),
    initComponent : function() {
    	var config = {
			tbar: [
			    {id: 'AccountManagement_addAccountBtn', ref: '../AccountManagement_addAccountBtn_refID', disabled:false, text:'Add Account', icon:'images/add3.png', scope: this, handler: function() { Account_Modify_Window.showTheWindow_Add(this); } },
			    {id: 'AccountManagement_editAccountInformaitonBtn', ref: '../AccountManagement_editAccountInformaitonBtn_refID', disabled:true, text:'Edit Information', scope: this, icon:'images/edit.png', handler: function() { Account_ModifyInformation_Window.showTheWindow_EditInformaiton(this, this.getSelectionModel().getSelected()); } },
			    {id: 'AccountManagement_editAccountPasswordBtn', ref: '../AccountManagement_editAccountPasswordBtn_refID', disabled:true, text:'Change Password', scope: this, icon:'images/edit.png', handler: function() { Account_ModifyPassword_Window.showTheWindow_EditPassword(this, this.getSelectionModel().getSelected()); } },
				{id: 'AccountManagement_deleteAccountBtn', ref: '../AccountManagement_deleteAccountBtn_refID', disabled:true, text:'Delete Account', icon:'images/delete.png', handler: this.checkDeletAccount },
			    {id: 'AccountManagement_assignRoleBtn', ref: '../AccountManagement_assignRoleBtn_refID', disabled:true, text:'Assign Role', scope: this, icon:'images/userIcon.png', handler: function() { AssignRole_Window.loadAssignRole(this.getSelectionModel().getSelected().data['ID']); } }
			],
		    bbar : new Ext.PagingToolbar({
				pageSize	: 15,
				store		: AccountGridProxyStore,
				displayInfo	: true,
				displayMsg	: 'Displaying topics {0} - {1} of {2}',
				emptyMsg	: "No topics to display"
			})
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AccountGrid.superclass.initComponent.apply(this, arguments);
		
		this.getSelectionModel().on({
			'selectionchange': {
				scope:	this,
				buffer:10, fn:function() {
					var selected = this.getSelectionModel().getCount()==1;
					if (selected) {
						this.AccountManagement_assignRoleBtn_refID.setDisabled(false);
						this.AccountManagement_editAccountInformaitonBtn_refID.setDisabled(false);
						this.AccountManagement_editAccountPasswordBtn_refID.setDisabled(false);
					}
					
					//	當點選到admin時，將delete account button設定為disable以防刪除 admin 帳號.
					if(this.getSelectionModel().getSelected().data['Account'] != 'admin'){
						this.AccountManagement_deleteAccountBtn_refID.setDisabled(false);
					}else{
						this.AccountManagement_deleteAccountBtn_refID.setDisabled(true);
					}
				}
			}
		});
	},	
	addAccountRecoed: function(record) {
	    this.getStore().insert(0, record);
	    this.getSelectionModel().selectRow(0);
 		this.getView().focusRow(0);
 		this.getStore().proxy.insertRecord(record);
	},
	editAccountRecoed: function(record) {
		var index = this.getStore().findExact('Account', record.data['Account']);
		this.getStore().removeAt(index);
		this.getStore().insert(index, record);
		this.getSelectionModel().selectRow(index);
 		this.getView().focusRow(index);
 		this.getStore().proxy.updateRecord(record);
	},
	deleteAccountRecord: function(record) {
		var id = record.data['Account'];
		this.getStore().remove(record);
    	this.getStore().proxy.deleteRecord(id);		// proxy delete record, might do not work
	},        
	loadDataModel: function() {
		ManagementMainLoadMaskShow();
		Ext.Ajax.request({
			scope	: this,
			url		: this.url,
			success : function(response) {
				AccountGridProxyStore.loadData(response.responseXML);
				AccountGridProxyStore.proxy.data = response;
				AccountGridProxyStore.load({params:{start:0, limit:15}});
				
				ManagementMainLoadMaskHide();
			},
			failure : function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	},
	checkDeletAccount: function() {
		Ext.MessageBox.confirm('Delete Account', 'Are you sure to delete the account ?', 
			function(btn) {
				var obj = Ext.getCmp('Account_Management_Grid_Panel');
				if (btn == 'yes') {
					var record = obj.getSelectionModel().getSelected();
					Ext.Ajax.request({
						url		: 'deleteAccount.do',
						params	: { id: record.data['ID'] },
						success	: function(response) {
							if(eval(response)) {
								obj.notify_DeleteAccount("true", record);
							} else {
								obj.notify_DeleteAccount("false", record);
							}
						}
					});
				}
			}
		);
	},
	notify_AddAccount: function(success, record) {
		var title = "Add New Account";
		if (success) {
			this.addAccountRecoed(record);
			Account_Modify_Window.hide();
			Ext.example.msg(title, "Success.");
		} else {
			Ext.example.msg(title, "Sorry, please try again.");
		}
	},
	
	notify_EditAccountInformation: function(success, record) {
		var title = "Edit Information";
		if (success) {
			this.editAccountRecoed(record);
			Account_ModifyInformation_Window.hide();
			Ext.example.msg(title, "Success.");
		} else {
			Ext.example.msg(title, "Sorry, please try again.");
		}
	},
	
	notify_EditAccountPassword: function(success, record) {
		var title = "Change Password";
		if (success) {
			this.editAccountRecoed(record);
			Account_ModifyPassword_Window.hide();
			Ext.example.msg(title, "Success.");
		} else {
			Ext.example.msg(title, "Sorry, please try again.");
		}
	},
	
	notify_DeleteAccount: function(success, record) {
		var title = "Delete Account";
		if (success) {
			this.deleteAccountRecord(record);
			Ext.example.msg(title, "Success.");
		} else {
			Ext.example.msg(title, "Sorry, please try again.");
		}
	}
});

Ext.reg('Management_AccountGrid', ezScrum.AccountGrid);