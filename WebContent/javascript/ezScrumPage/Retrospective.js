// Create Retrospective Widget
var AddNewRetrospectiveWidget = new ezScrum.AddNewRetrospectiveWidget({
	listeners:{
		CreateSuccess: function(win, form, response, record) {
			this.hide();
			Ext.getCmp('Retrospective_Page_Layout').reloadAllData(record.data['SprintID']);
	 		Ext.example.msg('Add Retrospective', 'Success.');
		},
		CreateFailure:function(win, form, response, issueId){
			Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
		}
	}
});

// Edit Retrospective Widget
var EditRetrospectiveWidget = new ezScrum.EditRetrospectiveWidget({
	listeners:{
		LoadSuccess:function(win, form, response, record){
			// Load Retrospective Success
		},
		LoadFailure:function(win, form, response, issueId){
			// Load Retrospective Error
		},
		EditSuccess:function(win, form, response, record){
			this.hide();
			Ext.getCmp('Retrospective_Page_Layout').reloadAllData(record.data['SprintID']);	
	 		Ext.example.msg('Edit Retrospective', 'Success.');
		},
		EditFailure:function(win, form, response, issueId){
			// Edit Retrospective Error
		}
	}
});

// Delete Retrospective Widget
var DeleteRetrospectiveWidget = new ezScrum.DeleteRetrospectiveWidget({
	listeners:{
		DeleteSuccess:function(win, response, issueId, sprintID){
			this.hide();
			Ext.getCmp('Retrospective_Page_Layout').reloadAllData(sprintID);
	 		Ext.example.msg('Delete Retrospective', 'Success.');
		},
		DeleteFailure:function(win, response, issueId){
			// Delete Retrospective Error
		}
	}
});

// retrospective page UI
RetrospectivePageLayout = Ext.extend(Ext.Panel, {
	id 			: 'Retrospective_Page_Layout',
	title		: 'Retrospective List',
	autoScroll	: true,
	initComponent : function() {
		var config = {
			items: [{
				ref		: 'RetrospectiveGrid_ID',
				xtype	: 'RetrospectiveGridPanel'
			}],
		    tbar: [{
					id 			: 'addRetrospectiveBtn',
					disabled 	: false,
					text 		: 'Add Retrospective',
					icon 		: 'images/add3.png',
					handler 	: this.doAddRetrospective
				}, {
					id 			: 'editRetrospectiveBtn',
					disabled 	: true,
					text 		: 'Edit Retrospective',
					icon 		: 'images/edit.png',
					handler		: this.doEditRetrospective
				}, {
					id 			: 'deleteRetrospectiveBtn',
					disabled 	: true,
					text 		: 'Delete Retrospective',
					icon 		: 'images/delete.png',
					handler		: this.doDeleteRetrospective
				},
				'->',
				{
					id			: 'SprintCombo_RetrospectiveToolBar',
					xtype		: 'SprintComboAllWidget',
					ref			: '../SprintAllCombo'
				}
			]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		RetrospectivePageLayout.superclass.initComponent.apply(this, arguments);
		
		var obj = this;
		this.SprintAllCombo.addListener(
			'select', function() {
				var SprintID = this.getStore().getAt(this.selectedIndex).get('Id');
				
				obj.RetrospectiveGrid_ID.loadDataModel(SprintID);	// reload data
				obj.checkToolBarPermission(true);					// reset button permission
			}
		);
		
		this.RetrospectiveGrid_ID.addListener('rowclick', function(record) {
			obj.checkToolBarPermission(false);
		});
	},
	doAddRetrospective: function() {
		var obj = Ext.getCmp('Retrospective_Page_Layout');
		var selectedIndex = obj.SprintAllCombo.selectedIndex;
		var sprintID = obj.SprintAllCombo.getStore().getAt(selectedIndex).get('Id');
		
		AddNewRetrospectiveWidget.showWidget(sprintID);
	},
	doEditRetrospective: function() {
		var obj = Ext.getCmp('Retrospective_Page_Layout');
		var selectedIndex = obj.SprintAllCombo.selectedIndex;
		var sprintID = obj.SprintAllCombo.getStore().getAt(selectedIndex).get('Id');
		var IssueID = obj.RetrospectiveGrid_ID.getSelectionModel().getSelected().data['Id'];
		
		EditRetrospectiveWidget.loadEditRetrospective(sprintID, IssueID);
	},
	doDeleteRetrospective: function() {
		var obj = Ext.getCmp('Retrospective_Page_Layout');
		var IssueID = obj.RetrospectiveGrid_ID.getSelectionModel().getSelected().data['Id'];
		
		DeleteRetrospectiveWidget.deleteRetrospective(IssueID);
	},
	checkToolBarPermission: function(defaultState) {
		if (defaultState) {
			this.getTopToolbar().get('editRetrospectiveBtn').setDisabled(true);					
			this.getTopToolbar().get('deleteRetrospectiveBtn').setDisabled(true);	
		} else { 
			this.getTopToolbar().get('editRetrospectiveBtn').setDisabled(false);					
			this.getTopToolbar().get('deleteRetrospectiveBtn').setDisabled(false);	
		}
	},
	loadDataModel: function() {
		this.checkToolBarPermission(true);
		this.RetrospectiveGrid_ID.loadDataModel(null);
		this.SprintAllCombo.loadDataModel();
	},
	reloadAllData: function(sprintID) {
		// reset combo info
		var NewComboIndex = this.SprintAllCombo.getStore().indexOfId(sprintID);
		var NewComboValue = this.SprintAllCombo.getStore().getAt(NewComboIndex).get('Info');
		this.SprintAllCombo.selectedIndex = NewComboIndex;
		this.SprintAllCombo.originalValue = NewComboValue;
		this.SprintAllCombo.reset();
		
		// reset gridpanel info
		this.RetrospectiveGrid_ID.loadDataModel(sprintID);
		
		// reset toolbar permission
		this.checkToolBarPermission(true);
    }
});
Ext.reg('RetrospectivePage', RetrospectivePageLayout);

var RetrospectivePage = new Ext.Panel({
	id 			: 'Retrospective_Page',
	layout 		: 'fit',
    items: [
        { ref: 'RetrospectivePage_refID', xtype: 'RetrospectivePage' }
    ],
	listeners : {
		'show' : function() {
			this.RetrospectivePage_refID.loadDataModel();
		}
	}
});