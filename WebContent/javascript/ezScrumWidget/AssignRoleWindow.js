// Assign Role Information
var AssignRoleRecord = Ext.data.Record.create(['Resource', 'ResourceId', 'Operation']);

var AssignRoleReader = new Ext.data.XmlReader({
	record: 'Assigned'
}, AssignRoleRecord);

var AssignRoleStore = new Ext.data.Store({
	fields: [{
		name: 'Resource'
	}, {
		name: 'ResourceId'
	}, {
		name: 'Operation'
	}],
	reader: AssignRoleReader
});

var AssignRoleManagement_RoleColumnModel = new Ext.grid.ColumnModel([{
	header: "Project",
	width: 150,
	dataIndex: 'Resource'
}, {
	header: "Role",
	width: 150,
	dataIndex: 'Operation'
}]);

// UnAssign Role Information
var UnAssignRoleRecord = Ext.data.Record.create(['Resource', 'ResourceId']);

var UnAssignRoleReader = new Ext.data.XmlReader({
	record: 'Unassigned'
}, UnAssignRoleRecord);

var UnAssignRoleStore = new Ext.data.Store({
	fields: [{
		name: 'Resource'
	}, {
		name: 'ResourceId'
	}],
	reader: UnAssignRoleReader
});

var AssignRoleManagement_UnAssignRoleCombo = new Ext.form.ComboBox({
	fieldLabel: 'Unassigned Project',
	name: 'unassignRole',
	editable: false,
	triggerAction: 'all',
	forceSelection: true,
	mode: 'local',
	displayField: 'Resource',
	valueField: 'ResourceId',
	store: UnAssignRoleStore,
	listeners: {
		'expand': function(combo) {
			var blurField = function(el) {
				el.blur();
			}
			blurField.defer(10, this, [combo.el]);
		},
		'collapse': function(combo) {
			var blurField = function(el) {
				el.blur();
			}
			blurField.defer(10, this, [combo.el]);
		},
		'select': function(combo) {
			// Check system Role
//			if (combo.getValue() == "system") {
			if (combo.lastSelectionText == "system") {
				AssignRoleManagement_RoleCombo.setDisabled(false);
				AssignRoleManagement_RoleCombo.disabled = true;
				AssignRoleManagement_RoleCombo.originalValue = "admin";
				AssignRoleManagement_RoleCombo.reset();
			} else {
				AssignRoleManagement_RoleCombo.setDisabled(false);
				AssignRoleManagement_RoleCombo.originalValue = "";
				AssignRoleManagement_RoleCombo.reset();
			}

			// Check add user button disable
			if ((combo.getValue() != "") && (AssignRoleManagement_RoleCombo.getValue() != "")) {
				Ext.getCmp("AssignRoleManagement_AddRoleBtn").setDisabled(false);
			} else {
				Ext.getCmp("AssignRoleManagement_AddRoleBtn").setDisabled(true);
			}
		}
	}
});

var AssignRoleManagement_RoleCombo = new Ext.form.ComboBox({
	fieldLabel: 'Role',
	name: 'Role',
	editable: false,
	disabled: 'true',
	mode: 'local',
	displayField: 'Operation',
	valueField: 'Operation',
	triggerAction: 'all',
	forceSelection: true,
	store: new Ext.data.ArrayStore({
		fields: ['Operation'],
		data: [['ProductOwner'], ['ScrumMaster'], ['ScrumTeam'], ['Stakeholder'], ['Guest']]
	}),
	listeners: {
		'expand': function(combo) {
			var blurField = function(el) {
				el.blur();
			}
			blurField.defer(10, this, [combo.el]);
		},
		'collapse': function(combo) {
			var blurField = function(el) {
				el.blur();
			}
			blurField.defer(10, this, [combo.el]);
		},
		'select': function(combo) {
			// Check add user button disable
			if ((AssignRoleManagement_UnAssignRoleCombo.getValue() != "") && (combo.getValue() != "")) Ext.getCmp("AssignRoleManagement_AddRoleBtn").setDisabled(false);
			else Ext.getCmp("AssignRoleManagement_AddRoleBtn").setDisabled(true);
		}
	}
});

/* Assign Role Form */
ezScrum.AssignRoleManagement_AssignRoleForm = Ext.extend(Ext.form.FormPanel, {
	id: 'AssignRoleManagement_AssignRoleForm',
	accountID: '-1',
	loadUrl: 'getAssignedProject.do',
	bodyStyle: 'padding:15px',
	border: false,
	defaultType: 'textfield',
	labelAlign: 'right',
	labelWidth: 150,
	monitorValid: true,
	defaults: {
		width: 500,
		msgTarget: 'side'
	},
	initComponent: function() {
		var config = {
			items: [{
				xtype: 'fieldset',
				title: 'User Information',
				autoHeight: true,
				defaults: {
					width: 300
				},
				defaultType: 'textfield',
				items: [{
					fieldLabel: 'User ID',
					name: 'accountID',
					readOnly: true
				}, {
					fieldLabel: 'User Name',
					name: 'Name',
					readOnly: true
				}, {
					name: 'id',
					hidden: 'true',
					ref: '../AssignRoleManagement_AssignRoleForm_AccountID_refID'
				}]
			}, {
				xtype: 'fieldset',
				title: 'Unassigned  Projects',
				autoHeight: true,
				defaults: {
					width: 300
				},
				defaultType: 'textfield',
				items: [AssignRoleManagement_UnAssignRoleCombo, AssignRoleManagement_RoleCombo],
				buttons: [{
					id: 'AssignRoleManagement_AddRoleBtn',
					text: 'Add Role',
					disabled: true,
					scope: this,
					handler: function() {
						if ((AssignRoleManagement_UnAssignRoleCombo.getValue() != "") && (AssignRoleManagement_RoleCombo.getValue() != "")) {
							var cid = this.AssignRoleManagement_AssignRoleForm_AccountID_refID.value;
							var selectProject = AssignRoleManagement_UnAssignRoleCombo.getValue();
							var accessLevel = AssignRoleManagement_RoleCombo.getValue();

							ManagementMainLoadMaskShow();
							Ext.Ajax.request({
								scope: this,
								url: 'addUser.do',
								params: {
									id: cid,
									resource: selectProject,
									operation: accessLevel
								},
								success: function(response) {
									this.onUpdateSuccess(response);
								},
								failure: function(response) {
									this.onUpdateFailure(response);
								}
							});
						}
					}
				}]
			}, {
				xtype: 'fieldset',
				title: 'Assigned Projects',
				autoHeight: true,
				defaults: {
					width: 300
				},
				items: [{
					xtype: 'grid',
					title: 'Assigned Projects',
					height: 200,
					width: 460,
					border: true,
					ref: '../AssignRoleManagement_AssignedProject_Grid_refID',
					ds: AssignRoleStore,
					viewConfig: {
						forceFit: true
					},
					cm: new Ext.grid.ColumnModel([{
						header: "Project",
						width: 200,
						dataIndex: 'Resource'
					}, {
						header: "Role",
						width: 100,
						dataIndex: 'Operation'
					}]),
					sm: new Ext.grid.RowSelectionModel({
						singleSelect: true,
						listeners: {
							'selectionChange': function(sm) {
								if (sm.getSelected() != null) {
									Ext.getCmp("AssignRoleManagement_RemoveRoleBtn").setDisabled(false);
								}
							}
						}
					})
				}],
				buttons: [{
					id: 'AssignRoleManagement_RemoveRoleBtn',
					text: 'Remove Role',
					disabled: true,
					scope: this,
					handler: function() {
						var cid = this.AssignRoleManagement_AssignRoleForm_AccountID_refID.value;
						var resource = this.AssignRoleManagement_AssignedProject_Grid_refID.getSelectionModel().getSelected().data['ResourceId'];
						var operation = this.AssignRoleManagement_AssignedProject_Grid_refID.getSelectionModel().getSelected().data['Operation'];

						ManagementMainLoadMaskShow();
						Ext.Ajax.request({
							scope: this,
							url: 'removeUser.do',
							params: {
								id: cid,
								resource: resource,
								operation: operation
							},
							success: function(response) {
								this.onUpdateSuccess(response);
							},
							failure: function(response) {
								this.onUpdateFailure(response);
							}
						});
					}
				}]
			}],
			buttons: [{
				text: 'Close',
				scope: this,
				handler: function() {
					this.ownerCt.hide();
				}
			}]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AssignRoleManagement_AssignRoleForm.superclass.initComponent.apply(this, arguments);

		this.addEvents('LoadSuccess', 'LoadFailure', 'UpdateSuccess', 'UpdateFailure');
	},
	onRender: function() {
		ezScrum.AssignRoleManagement_AssignRoleForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
	// Load Role Item success
	onLoadSuccess: function(response) {
		var account = AccountReader.readRecords(response.responseXML);
		var assignRole = AssignRoleReader.readRecords(response.responseXML);
		var unassignRole = UnAssignRoleReader.readRecords(response.responseXML);

		if (account.success && assignRole.success && unassignRole.success) {
			UnAssignRoleStore.loadData(response.responseXML);
			AssignRoleStore.loadData(response.responseXML);

			var accountRecord = account.records[0];

			// Load account data
			if (accountRecord) {
				this.getForm().setValues({
					id: accountRecord.data['ID'],
					accountID: accountRecord.data['Account'],
					Name: accountRecord.data['Name']
				});
				this.fireEvent('LoadSuccess', this, response, accountRecord);
			}

			// Reset Combobox
			AssignRoleManagement_RoleCombo.originalValue = "";
			AssignRoleManagement_RoleCombo.reset();
			AssignRoleManagement_UnAssignRoleCombo.reset();
		}

		ManagementMainLoadMaskHide();
	},
	onLoadFailure: function(response) {
		ManagementMainLoadMaskHide();
		this.fireEvent('LoadFailure', this, response);
	},
	onUpdateSuccess: function(response) {
		var rs = AccountReader.readRecords(response.responseXML);
		if (rs.success) {
			var record = rs.records[0];
			if (record) {
				Ext.getCmp("AssignRoleManagement_AssignRole_Window").loadAssignRole(record.data['ID']);
				this.fireEvent('UpdateSuccess', this, response, record);
			}
		} else {
			this.fireEvent('UpdateFailure', this, response);
		}

		ManagementMainLoadMaskHide();

	},
	onUpdateFailure: function(response) {
		ManagementMainLoadMaskHide();
		this.fireEvent('UpdateFailure', this, response);
	},
	loadStore: function() {
		ManagementMainLoadMaskShow();
		Ext.Ajax.request({
			scope: this,
			url: this.loadUrl,
			params: {
				accountID: this.accountID
			},
			success: function(response) {
				this.onLoadSuccess(response);
			},
			failure: function(response) {
				this.onLoadFailure(response);
			}
		});
	},
	reset: function() {
		this.getForm().reset();
	}
});
Ext.reg('AssignRoleManagement_AssignRoleForm', ezScrum.AssignRoleManagement_AssignRoleForm);

ezScrum.window.AssignRoleWindow = Ext.extend(ezScrum.layout.Window, {
	id: 'AssignRoleManagement_AssignRole_Window',
	width: 550,
	title: 'Assign Role',
	initComponent: function() {
		var config = {
			layout: 'form',
			items: [{
				xtype: 'AssignRoleManagement_AssignRoleForm',
				ref: 'AssignRoleManagement_AssignRoleForm_refID'
			}]
		}
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.window.AssignRoleWindow.superclass.initComponent.apply(this, arguments);

		this.addEvents('LoadSuccess', 'LoadFailure', 'UpdateSuccess', 'UpdateFailure');

		this.AssignRoleManagement_AssignRoleForm_refID.on('LoadSuccess', function(obj, response, record) {
			this.fireEvent('LoadSuccess', this, obj, response, record);
		}, this);
		this.AssignRoleManagement_AssignRoleForm_refID.on('LoadFailure', function(obj, response) {
			this.fireEvent('LoadFailure', this, obj, response);
		}, this);
		this.AssignRoleManagement_AssignRoleForm_refID.on('UpdateSuccess', function(obj, response, record) {
			this.fireEvent('UpdateSuccess', this, obj, response, record);
		}, this);
		this.AssignRoleManagement_AssignRoleForm_refID.on('UpdateFailure', function(obj, response) {
			this.fireEvent('UpdateFailure', this, obj, response);
		}, this);
	},
	loadAssignRole: function(accountID) {
		this.AssignRoleManagement_AssignRoleForm_refID.accountID = accountID;
		this.AssignRoleManagement_AssignRoleForm_refID.reset();
		this.AssignRoleManagement_AssignRoleForm_refID.loadStore();
		this.show();
	}
});