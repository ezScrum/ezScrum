// CRUD widget
var CreateUnplannedWidget = new ezScrum.CreateUnplannedItemWidget({
	listeners:{
		CreateSuccess: function(win, form, response, record) {
			this.hide();
			Ext.getCmp('UnplannedItem_Page_Layout').reloadAllData(record);
	 		Ext.example.msg('Add Unplanned Item', 'Success.');
		},
		CreateFailure: function(win, form, response, issueId) {
			Ext.example.msg('Add Unplanned Item', 'Failure.');
		}
	}
});

var EditUnplannedWidget = new ezScrum.EditUnplannedItemWidget({
	listeners: {
		EditSuccess: function(win, form, response, record) {
			this.hide();
			Ext.getCmp('UnplannedItem_Page_Layout').reloadAllData(record);
	 		Ext.example.msg('Edit Unplanned Item', 'Success.');
		},
		EditFailure:function(win, form, response, issueId) {
			// Edit Unplanned Item Error
			Ext.example.msg('Edit Unplanned Item', 'Failure.');
		}
	}
});

var DeleteUnplannedWidget = new ezScrum.DeleteUnplannedItemWidget({
	listeners:{
		DeleteSuccess:function(win, response, issueId){
			var s = Ext.getCmp('UnplannedItem_Page_Layout').UnplannedGrid_ID.getStore();
			var index = s.indexOfId(issueId);
			s.removeAt(index);
			
			this.hide();
			Ext.example.msg('Delete Unplanned Item', 'Success.');
		},
		DeleteFailure:function(win, response, issueId){
			Ext.example.msg('Delete Unplanned Item', 'Failure.');
		}
	}
});

// unplanned page UI
UnplannedPageLayout = Ext.extend(Ext.Panel, {
	id 			: 'UnplannedItem_Page_Layout',
	title		: 'Unplanned Item List',
	autoScroll	: true,
	initComponent : function() {
		var config = {
			items: [{
				ref		: 'UnplannedGrid_ID',
				xtype	: 'UnplannedGridPanel'
		    }],
		    tbar: [{
					id 			: 'addUnplannedItemBtn',
					disabled 	: false,
					text 		: 'Add Unplanned Item',
					icon 		: 'images/add3.png',
					handler 	: this.doAddUnplannedItem
				}, {
					id 			: 'editUnplannedItemBtn',
					disabled 	: true,
					text 		: 'Edit Unplanned Item',
					icon 		: 'images/edit.png',
					handler		: this.doEditUnplannedItem
				}, {
					id 			: 'deleteUnplannedItemBtn',
					disabled 	: true,
					text 		: 'Delete Unplanned Item',
					icon 		: 'images/delete.png',
					handler		: this.doDeleteUnplannedItem
				}, {
					id 			: 'showUnplannedItemHistoryBtn',
					disabled	: true,
					text 		: 'Unplanned Item History',
					icon		: 'images/history.png',
					handler		: this.doShowUnplannedItemHistory
				},
				'->', 
				{
					id			: 'SprintCombo_UnplannedToolBar',
					xtype		: 'SprintComboAllWidget',
					ref			: '../SprintAllCombo'
				}
			]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		UnplannedPageLayout.superclass.initComponent.apply(this, arguments);
		
		var obj = this;
		this.SprintAllCombo.addListener(
			'select', function() {
				var SprintID = this.getStore().getAt(this.selectedIndex).get('Id');
				
				obj.UnplannedGrid_ID.loadDataModel(SprintID);	// reload data
				obj.checkToolBarPermission(true);				// reset button permission
			}
		);
		
		this.UnplannedGrid_ID.addListener('rowclick', function(record) {
			obj.checkToolBarPermission(false);
		});
	},
	doAddUnplannedItem: function() {
		var obj = Ext.getCmp('UnplannedItem_Page_Layout');
		var selectedIndex = obj.SprintAllCombo.selectedIndex;
		var sprintID = obj.SprintAllCombo.getStore().getAt(selectedIndex).get('Id');
		
		CreateUnplannedWidget.showWidget(sprintID);
	},
	doEditUnplannedItem: function() {
		var obj = Ext.getCmp('UnplannedItem_Page_Layout');
		var selectedIndex = obj.SprintAllCombo.selectedIndex;
		var sprintID = obj.SprintAllCombo.getStore().getAt(selectedIndex).get('Id');
		var IssueID = obj.UnplannedGrid_ID.getSelectionModel().getSelected().data['Id'];
		
		EditUnplannedWidget.loadEditUnplannedItem(sprintID, IssueID);
	},
	doDeleteUnplannedItem: function() {
		var obj = Ext.getCmp('UnplannedItem_Page_Layout');
		var IssueID = obj.UnplannedGrid_ID.getSelectionModel().getSelected().data['Id'];
		var IssueName = obj.UnplannedGrid_ID.getSelectionModel().getSelected().data['Name'];
		
		DeleteUnplannedWidget.deleteUnplannedItem(IssueID, IssueName);
	},
	doShowUnplannedItemHistory: function() {
		var obj = Ext.getCmp('UnplannedItem_Page_Layout');
		var issueId = obj.UnplannedGrid_ID.getSelectionModel().getSelected().data['Id'];
		var issueType = obj.UnplannedGrid_ID.getSelectionModel().getSelected().data['Type'];

		IssueHistory_Window.showTheWindow(issueId, issueType);
	},
	checkToolBarPermission: function(defaultState) {
		if (defaultState) {
			this.getTopToolbar().get('editUnplannedItemBtn').setDisabled(true);					
			this.getTopToolbar().get('deleteUnplannedItemBtn').setDisabled(true);	
			this.getTopToolbar().get('showUnplannedItemHistoryBtn').setDisabled(true);
		} else { 
			this.getTopToolbar().get('editUnplannedItemBtn').setDisabled(false);					
			this.getTopToolbar().get('deleteUnplannedItemBtn').setDisabled(false);	
			this.getTopToolbar().get('showUnplannedItemHistoryBtn').setDisabled(false);		
		}
	},
	loadDataModel: function() {
		this.UnplannedGrid_ID.loadDataModel(null);
		this.SprintAllCombo.loadDataModel();
	},
    reloadAllData: function(record) {
    	var sprintID = record.data['SprintID'];
    	var issueID = record.data['Id'];
    	
    	// reset combo info
		var NewComboIndex = this.SprintAllCombo.getStore().indexOfId(sprintID);
		var NewComboValue = this.SprintAllCombo.getStore().getAt(NewComboIndex).get('Info');
		this.SprintAllCombo.selectedIndex = NewComboIndex;
		this.SprintAllCombo.originalValue = NewComboValue;
		this.SprintAllCombo.reset();
		
		// reset gridpanel info
		this.UnplannedGrid_ID.reloadDataModel(sprintID, issueID);
		
		// reset toolbar permission
		this.checkToolBarPermission(false);
    }
});
Ext.reg('UnplannedPage', UnplannedPageLayout);

var UnplannedPage = new Ext.Panel({
	id 			: 'UnplannedItem_Page',
	layout 		: 'fit',
	items: [
	     { ref: 'UnplannedPage_refID', xtype: 'UnplannedPage' }
	],
	listeners : {
		'show' : function() {
			this.UnplannedPage_refID.loadDataModel();
		}
	}
});