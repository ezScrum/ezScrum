//var AssignRole_Window = new ezScrum.window.AssignRoleWindow({
//	listeners:{
//		UpdateSuccess:function(win, form, response, record){
//			var TenantGridCmp = Ext.getCmp('Tenant_Management_Grid_Panel');
//			TenantGridCmp.editTenantRecoed(record);
//			Ext.example.msg('Assign Role', 'Update Role Success.');
//		},
//		UpdateFailure:function(win, form, response){
//			Ext.example.msg('Assign Role', 'Sorry, update role fail. Please try again.');
//		}
//	}
//});

var TenantGridProxyStore = new Ext.data.Store({
	fields:[
	    {name : 'ID'},
		{name : 'Name'},
//		{name : 'Mail'},
//		{name : 'Roles'},
//		{name : 'ActivativeDate'},
//		{name : 'Period'},
		{name : 'Description'},
		{name : 'AdminName'},
		{name : 'Enable'}		
	],
	reader	: TenantReader,
	proxy	: new Ext.ux.data.PagingMemoryProxy()
});

ezScrum.TenantGrid = Ext.extend(Ext.grid.GridPanel, {
	id		: 'Tenant_Management_Grid_Panel',
	title	: 'Tenants List',
	url		: 'getTenantList.do',
	border	: false,
	frame	: false,
	bodyStyle	:'width:100%',
	autoWidth	: true,
	stripeRows	: true,
	store	: TenantGridProxyStore,
	colModel	: TenantColumnModel,
	viewConfig: {
        forceFit: true
    },
    sm		: new Ext.grid.RowSelectionModel({
    	singleSelect: true
    }),
    initComponent : function() {
    	var config = {
			tbar: [
			    {id: 'TenantManagement_addTenantBtn', ref: '../TenantManagement_addTenantBtn_refID', disabled:false, text:'Add Tenant', icon:'images/add3.png', scope: this, handler: function() { Tenant_Modify_Window.showTheWindow_Add(this); } }
			    ,{id: 'TenantManagement_editTenantBtn', ref: '../TenantManagement_editTenantBtn_refID', disabled:true, text:'Edit Tenant', scope: this, icon:'images/edit.png', handler: function() { Tenant_Modify_Window.showTheWindow_Edit(this, this.getSelectionModel().getSelected().data['ID']); } }
			    ,{id: 'TenantManagement_deleteTenantBtn', ref: '../TenantManagement_deleteTenantBtn_refID', disabled:true, text:'Stop Tenant', icon:'images/drop2.png', handler: this.checkDeletTenant }
			    ,{id: 'TenantManagement_renewTenantBtn', ref: '../TenantManagement_renewTenantBtn_refID', disabled:true, text:'Renew Tenant', icon:'images/add3.png', handler: this.checkRenewTenant }
//			    ,{id: 'TenantManagement_assignRoleBtn', ref: '../TenantManagement_assignRoleBtn_refID', disabled:true, text:'Assign Role', scope: this, icon:'images/userIcon.png', handler: function() { AssignRole_Window.loadAssignRole(this.getSelectionModel().getSelected().data['ID']); } }
			],
		    bbar : new Ext.PagingToolbar({
				pageSize	: 15,
				store		: TenantGridProxyStore,
				displayInfo	: true,
				displayMsg	: 'Displaying topics {0} - {1} of {2}',
				emptyMsg	: "No topics to display"
			})
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.TenantGrid.superclass.initComponent.apply(this, arguments);
		
		this.getSelectionModel().on({
			'selectionchange': {
				scope:	this,
				buffer:10, fn:function() {
					var selected = this.getSelectionModel().getCount()==1;
					if (selected) {
						this.TenantManagement_editTenantBtn_refID.setDisabled(false);
						this.TenantManagement_deleteTenantBtn_refID.setDisabled(false);
						this.TenantManagement_renewTenantBtn_refID.setDisabled(false);
//						this.TenantManagement_assignRoleBtn_refID.setDisabled(false);
					}
				}
			}
		});
	},	
	addTenantRecoed: function(record) {
	    this.getStore().insert(0, record);
	    this.getSelectionModel().selectRow(0);
 		this.getView().focusRow(0);
 		this.getStore().proxy.insertRecord(record);
	},
	editTenantRecoed: function(record) {
		var index = this.getStore().findExact('ID', record.data['ID']);
		this.getStore().removeAt(index);
		this.getStore().insert(index, record);
		this.getSelectionModel().selectRow(index);
 		this.getView().focusRow(index);
 		this.getStore().proxy.updateRecord(record);
	},
	deleteTenantRecord: function(record) {
//		var id = record.data['ID'];
//		this.getStore().remove(record);
//    	this.getStore().proxy.deleteRecord(id);		// proxy delete record, might do not work
		var index = this.getStore().findExact('ID', record.data['ID']);
		this.getStore().removeAt(index);
		this.getStore().insert(index, record);
		this.getSelectionModel().selectRow(index);
 		this.getView().focusRow(index);
 		this.getStore().proxy.updateRecord(record);
	},    
	renewTenantRecord: function(record) {
		var index = this.getStore().findExact('ID', record.data['ID']);
		this.getStore().removeAt(index);
		this.getStore().insert(index, record);
		this.getSelectionModel().selectRow(index);
 		this.getView().focusRow(index);
 		this.getStore().proxy.updateRecord(record);
	},
	loadDataModel: function() {
		ManagementMainLoadMaskShow();
		Ext.Ajax.request({
			scope	: this,
			url		: this.url,
			success : function(response) {
				TenantGridProxyStore.loadData(response.responseXML);
				TenantGridProxyStore.proxy.data = response;
				TenantGridProxyStore.load({params:{start:0, limit:15}});
				
				ManagementMainLoadMaskHide();
			},
			failure : function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	},
	checkDeletTenant: function() {
		Ext.MessageBox.confirm('Stop Tenant', 'Are you sure to stop the tenant ?', 
			function(btn) {
				var obj = Ext.getCmp('Tenant_Management_Grid_Panel');
				if (btn == 'yes') {
					var record = obj.getSelectionModel().getSelected();
					Ext.Ajax.request({
						url		: 'deleteTenant.do',
						params	: { id: record.data['ID'] },
						success	: function(response) {
							var rs = TenantReader.readRecords(response.responseXML);
							var record = rs.records[0];
							if(rs.success){
								obj.notify_DeleteTenant("true", record);
							} else {
								obj.notify_DeleteTenant("false", record);
							}
						}
					});
				}
			}
		);
	},
	checkRenewTenant: function() {
		Ext.MessageBox.confirm('Renew Tenant', 'Are you sure to renew the tenant ?', 
				function(btn) {
					var obj = Ext.getCmp('Tenant_Management_Grid_Panel');
					if (btn == 'yes') {
						var record = obj.getSelectionModel().getSelected();
						Ext.Ajax.request({
							url		: 'renewTenant.do',
							params	: { id: record.data['ID'] },
							success	: function(response) {
								var rs = TenantReader.readRecords(response.responseXML);
								var record = rs.records[0];
								if(rs.success){
									obj.notify_RenewTenant("true", record);
								} else {
									obj.notify_RenewTenant("false", record);
								}
							}
						});
					}
				}
			);
		},
	notify_AddTenant: function(success, record) {
		var title = "Add New Tenant With Rent Service";
		if (success) {
			this.addTenantRecoed(record);
			Tenant_Modify_Window.hide();
			Ext.example.msg(title, "Success.");
		} else {
			Ext.example.msg(title, "Sorry, please try again.");
		}
	},
	notify_EditTenant: function(success, record) {
		var title = "Edit Tenant";
		if (success) {
			this.editTenantRecoed(record);
			Tenant_Modify_Window.hide();
			Ext.example.msg(title, "Success.");
		} else {
			Ext.example.msg(title, "Sorry, please try again.");
		}
	},
	notify_DeleteTenant: function(success, record) {
		var title = "Stop Tenant";
		if (success) {
			this.deleteTenantRecord(record);
			Ext.example.msg(title, "Success.");
		} else {
			Ext.example.msg(title, "Sorry, please try again.");
		}
	},
	notify_RenewTenant: function(success, record) {
		var title = "Renew Tenant";
		if (success) {
			this.renewTenantRecord(record);
			Ext.example.msg(title, "Success.");
		} else {
			Ext.example.msg(title, "Sorry, please try again.");
		}
	}
});

Ext.reg('Management_TenantGrid', ezScrum.TenantGrid);