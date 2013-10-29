var AddWidget = new ezScrum.CreateIssueTypeWidget({
	listeners : {
		CreateSuccess : function(win, form, response) {
			this.hide();
			CustomIssueTypeStore.load(response);
			Ext.example.msg('Create issue type', 'Create issuetype Success.');
		},
		CreateFailure : function(win, form, response) {
			this.hide();
			Ext.example.msg('Create issue type', 'Create issuetype Failure.');
		}
	}
});

ManageIssueTypePanelLayout = Ext.extend(Ext.Panel, {
	id: 'ManageIssueType_Panel',
	layout: 'fit',
	title: 'Manage Issue Type',
	initComponent : function() {
		var config = {
			items: [
			    ManageIssueTypeGridPanel
		    ],
			tbar: [{
				id			: 'AddIssueTypeBtn',
				icon		: 'images/add3.png',
				text		: 'Add Issue Type',
				disabled 	: false,
				handler		: this.doAddIssueType
			}, {
				id			: 'DeleteIssueTypeBtn',
				icon		: 'images/delete.png',
				text		: 'Delete Issue Type',
				disabled 	: true,
				handler		: this.doDeleteIssueType
			}, {
				id			: 'ManageStatusBtn',
				icon		: 'images/magic-wand.png',
				text		: 'Manage Status',
				disabled 	: true,
				handler: this.doShowManageStatus
			}]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ManageIssueTypePanelLayout.superclass.initComponent.apply(this, arguments);
	}
});

var ManageIssueTypePanelEvent = new ManageIssueTypePanelLayout({
	doAddIssueType: function() {
		AddWidget.showWidget();
	},
	doDeleteIssueType: function() {
		var typeID = ManageIssueTypeGridPanel.getSelectionModel().getSelected().data['TypeId'];
		Ext.MessageBox.confirm('Confirm', 'Are you sure you want to do that?', function(btn) {
			if(btn == 'yes'){
				Ext.Ajax.request({
					url : 'ajaxDeleteIssueType.do',
					params : {typeID: typeID},
					success : function(response){
						var index = CustomIssueTypeStore.find("TypeId", typeID);
						if(index!=-1) {
						 	var record = CustomIssueTypeStore.getAt(index);
						 	CustomIssueTypeStore.remove(record);
							Ext.example.msg('Delete Issue Type', 'Delete Issue Type Success.');
						}
					},
					failure : function(response){
						Ext.example.msg('Delete Issue Type', 'Delete Issue Type Failure.');
					}
				});
			}
		});
	},
	doShowManageStatus: function() {
		var typeName = ManageIssueTypeGridPanel.getSelectionModel().getSelected().data['TypeName'];
		StatusWin.showWidget(typeName);
	},
	checkToolBarPermission: function() {
		this.getTopToolbar().get('DeleteIssueTypeBtn').setDisabled(false);
		this.getTopToolbar().get('ManageStatusBtn').setDisabled(false);
	}
});

//add item click event
ManageIssueTypeGridPanel.addListener('rowclick', function(record) {
	ManageIssueTypePanelEvent.checkToolBarPermission();
});