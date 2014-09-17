ezScrum.Management_ScrumRole_ProjectList_Panel = Ext.extend(Ext.tree.TreePanel, {
	title: 'Project List',
	width: '30%',
	collapsible: true,
	collapseMode: 'mini',
	animCollapse: false,
	animate: false,
	hideCollapseTool: true,
	rootVisible: false,
	autoScroll: true,
	autoHeight: true,
	root: {
		nodeType: 'node',
		text: 'ProjectRoot',
		draggable: false
	},
	loader: new Ext.tree.TreeLoader({
		dataUrl: 'getResourceList.do'
	}),
	initComponent: function() {
		ezScrum.Management_ScrumRole_ProjectList_Panel.superclass.initComponent.apply(this, arguments);
	},
	listeners: {
		dblclick: function(node) {
			// do nothing
			return;
		},
		click: function(node) {
			Ext.getCmp('ScrumRole_Permission_Check_List_Panel_ID').setProjectID(node.attributes['id'], node.attributes['text']);
		}
	},
	loadDataModel: function() {
		this.getLoader().load(this.root);
	}
});
Ext.reg('Management_ScrumRole_ProjectListPanel', ezScrum.Management_ScrumRole_ProjectList_Panel);

ezScrum.Management_ScrumRole_RoleList_Panel = Ext.extend(Ext.tree.TreePanel, {
	title: 'Scrum Role List',
	width: '20%',
	collapsible: true,
	collapseMode: 'mini',
	animCollapse: false,
	animate: false,
	hideCollapseTool: true,
	rootVisible: false,
	autoScroll: true,
	root: {
		text: 'ScrumRoleRoot',
		children: [{
			text: 'Product Owner',
			value: 'ProductOwner',
			iconCls: 'leaf-icon',
			cls: 'treepanel-leaf',
			leaf: true
		}, {
			text: 'Scrum Master',
			value: 'ScrumMaster',
			iconCls: 'leaf-icon',
			cls: 'treepanel-leaf',
			leaf: true
		}, {
			text: 'Scrum Team',
			value: 'ScrumTeam',
			iconCls: 'leaf-icon',
			cls: 'treepanel-leaf',
			leaf: true
		}, {
			text: 'Stakeholder',
			value: 'Stakeholder',
			iconCls: 'leaf-icon',
			cls: 'treepanel-leaf',
			leaf: true
		}, {
			text: 'Guest',
			value: 'Guest',
			iconCls: 'leaf-icon',
			cls: 'treepanel-leaf',
			leaf: true
		}]
	},
	listeners: {
		dblclick: function(node) {
			// do nothing
			return;
		},
		click: function(node) {
			Ext.getCmp('ScrumRole_Permission_Check_List_Panel_ID').setScrumRole(node.attributes['value']);
		}
	}
});
Ext.reg('Management_ScrumRole_RoleListPanel', ezScrum.Management_ScrumRole_RoleList_Panel);

ezScrum.Management_ScrumRole_PermissionList_Panel = Ext.extend(Ext.Panel, {
	ID: undefined,
	ProjectID: undefined,
	ScrumRole: undefined,

	id: 'ScrumRole_Permission_Check_List_Panel_ID',
	title: 'Permission Check List',
	width: '20%',
	initComponent: function() {
		var config = {
			items: [{
				xtype: 'form',
				ref: 'ScrumRole_Permission_Check_List_Form_refID',
				hideLabels: true,
				defaults: {
					xtype: 'checkbox',
					bodyStyle: 'padding:15px',
					labelAlign: 'right'
				},
				items: [{
					boxLabel: '<b>AccessProductBacklog</b>',
					inputValue: 'AccessProductBacklog',
					name: 'AccessProductBacklog',
					ref: '../ScrumRole_AccessProductBacklog_refID'
				}, {
					boxLabel: '<b>AccessReleasePlan</b>',
					inputValue: 'AccessReleasePlan',
					name: 'AccessReleasePlan',
					ref: '../ScrumRole_AccessReleasePlan_refID'
				}, {
					boxLabel: '<b>AccessSprintPlan</b>',
					inputValue: 'AccessSprintPlan',
					name: 'AccessSprintPlan',
					ref: '../ScrumRole_AccessSprintPlan_refID'
				}, {
					boxLabel: '<b>AccessSprintBacklog</b>',
					inputValue: 'AccessSprintBacklog',
					name: 'AccessSprintBacklog',
					ref: '../ScrumRole_AccessSprintBacklog_refID'
				}, {
					boxLabel: '<b>AccessTaskboard</b>',
					inputValue: 'AccessTaskboard',
					name: 'AccessTaskboard',
					ref: '../ScrumRole_AccessTaskboard_refID'
				}, {
					boxLabel: '<b>AccessRetrospective</b>',
					inputValue: 'AccessRetrospective',
					name: 'AccessRetrospective',
					ref: '../ScrumRole_AccessRetrospective_refID'
				}, {
					boxLabel: '<b>AccessUnplanned</b>',
					inputValue: 'AccessUnplanned',
					name: 'AccessUnplanned',
					ref: '../ScrumRole_AccessUnplanned_refID'
				}, {
					boxLabel: '<b>AccessReport</b>',
					inputValue: 'AccessReport',
					name: 'AccessReport',
					ref: '../ScrumRole_AccessReport_refID'
				}, {
					boxLabel: '<b>AccessEditProject</b>',
					inputValue: 'AccessEditProject',
					name: 'AccessEditProject',
					ref: '../ScrumRole_AccessEditProject_refID'
				}]
			}]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.Management_ScrumRole_PermissionList_Panel.superclass.initComponent.apply(this, arguments);
	},
	setProjectID: function(id, projectID) {
		this.ID = id;
		this.ProjectID = projectID;
		this.notify_Permisiion_List_Check();
	},
	setScrumRole: function(role) {
		this.ScrumRole = role;
		this.notify_Permisiion_List_Check();
	},
	notify_Permisiion_List_Check: function() {
		if ((this.ID !== undefined) && (this.ProjectID !== undefined) && (this.ScrumRole !== undefined)) {
			Ext.getCmp('Management_ScrumRole_Main_Panel').Management_ScrumRole_SavePermissionBtn.enable();

			var obj = this;
			var mask = new Ext.LoadMask(obj.getEl(), {
				msg: "loading info..."
			});
			mask.show();

			Ext.Ajax.request({
				scope: this,
				url: 'getScrumRolePermission.do',
				params: {
					id: this.ID,
					projectName: this.ProjectID,
					scrumRole: this.ScrumRole
				},
				success: function(response) {
					this.setAccessPermissionCheck(response);

					mask.hide();
				},
				failure: function() {
					Ext.example.msg('Server Error', 'Sorry, the connection is failure.');

					mask.hide();
				}
			});
		}
	},
	setAccessPermissionCheck: function(response) {
		var obj = Ext.util.JSON.decode(response.responseText);

		this.ScrumRole_AccessProductBacklog_refID.setValue(eval(obj.AccessProductBacklog));
		this.ScrumRole_AccessReleasePlan_refID.setValue(eval(obj.AccessReleasePlan));
		this.ScrumRole_AccessSprintPlan_refID.setValue(eval(obj.AccessSprintPlan));
		this.ScrumRole_AccessSprintBacklog_refID.setValue(eval(obj.AccessSprintBacklog));
		this.ScrumRole_AccessTaskboard_refID.setValue(eval(obj.AccessTaskboard));
		this.ScrumRole_AccessRetrospective_refID.setValue(eval(obj.AccessRetrospective));
		this.ScrumRole_AccessUnplanned_refID.setValue(eval(obj.AccessUnplanned));
		this.ScrumRole_AccessReport_refID.setValue(eval(obj.AccessReport));
		this.ScrumRole_AccessEditProject_refID.setValue(eval(obj.AccessEditProject));
	},
	getPermissionCheckList: function() {
		var PermissionList = {};

		PermissionList["AccessProductBacklog"] = this.ScrumRole_AccessProductBacklog_refID.getValue();
		PermissionList["AccessReleasePlan"] = this.ScrumRole_AccessReleasePlan_refID.getValue();
		PermissionList["AccessSprintPlan"] = this.ScrumRole_AccessSprintPlan_refID.getValue();
		PermissionList["AccessSprintBacklog"] = this.ScrumRole_AccessSprintBacklog_refID.getValue();
		PermissionList["AccessTaskboard"] = this.ScrumRole_AccessTaskboard_refID.getValue();
		PermissionList["AccessRetrospective"] = this.ScrumRole_AccessRetrospective_refID.getValue();
		PermissionList["AccessUnplanned"] = this.ScrumRole_AccessUnplanned_refID.getValue();
		PermissionList["AccessReport"] = this.ScrumRole_AccessReport_refID.getValue();
		PermissionList["AccessEditProject"] = this.ScrumRole_AccessEditProject_refID.getValue();

		return JSON.stringify(PermissionList); // 這個處理是為了讓 PermissionList 物件轉換成 Json 字串傳回給後端
	}
});
Ext.reg('Management_ScrumRole_PermissionListPanel', ezScrum.Management_ScrumRole_PermissionList_Panel);

ezScrum.Management_ScrumRole_Panel = Ext.extend(Ext.Panel, {
	id: 'Management_ScrumRole_Main_Panel',
	title: 'Scrum Role Management',
	autoScroll: true,
	layout: {
		type: 'hbox',
		pack: 'center',
		align: 'top'
	},
	initComponent: function() {
		var config = {
			tbar: [{
				text: 'Save Permission',
				icon: 'images/save.png',
				ref: '../Management_ScrumRole_SavePermissionBtn',
				disabled: true,
				scope: this,
				handler: function() {
					this.doUpdatePermission();
				}
			}],
			defaults: {
				margins: '10 5 0 5'
			},
			items: [{
				flex: 1,
				xtype: 'Management_ScrumRole_ProjectListPanel',
				ref: 'Management_ScrumRole_ProjectListPanel_refID'
			}, {
				html: '>>'
			}, {
				flex: 1,
				xtype: 'Management_ScrumRole_RoleListPanel',
				ref: 'Management_ScrumRole_RoleListPanel_refID'
			}, {
				html: '>>'
			}, {
				flex: 1,
				xtype: 'Management_ScrumRole_PermissionListPanel',
				ref: 'Management_ScrumRole_PermissionListPanel_refID'
			}]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.Management_ScrumRole_Panel.superclass.initComponent.apply(this, arguments);
	},
	doUpdatePermission: function() {
		ManagementMainLoadMaskShow();
		var PermissionCheckListPanel = Ext.getCmp('ScrumRole_Permission_Check_List_Panel_ID');
		Ext.Ajax.request({
			scope: this,
			url: 'updateScrumRole.do',
			params: {
				ID: PermissionCheckListPanel.ID,
				ProjectID: PermissionCheckListPanel.ProjectID,
				RoleName: PermissionCheckListPanel.ScrumRole,
				PermissionList: PermissionCheckListPanel.getPermissionCheckList()
			},
			success: function(response) {
				ManagementMainLoadMaskHide();
				if (eval(response)) {
					Ext.example.msg('Save Permission', 'Save Permission Success.');
				} else {
					Ext.example.msg('Save Permission', 'Save Permission Failure.');
				}
			},
			failure: function() {
				ManagementMainLoadMaskHide();
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	},
	loadDataModel: function() {
		this.Management_ScrumRole_ProjectListPanel_refID.loadDataModel();
		this.Management_ScrumRole_SavePermissionBtn.disable();
	}
});
Ext.reg('Management_ScrumRolePanel', ezScrum.Management_ScrumRole_Panel);