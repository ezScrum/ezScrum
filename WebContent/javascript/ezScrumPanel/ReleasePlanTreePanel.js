ezScrum.ReleasePlan_StoryPannel = Ext.extend(Ext.grid.GridPanel, {
	id			: 'ReleasePlan_StoryGrid',
	title		: 'Story List',
	store		: releaseStoryStore,
	height		: 200,
	collapsible	: true,
	split		: true,
	frame		: false,
	border		: false,
	region		: 'south',
	margins		: '0 0 0 0',
	colModel	: createReleaseStoryCloumns(),
	sm			: new Ext.grid.RowSelectionModel({ singleSelect : true }),
	viewConfig	: { forceFit: true },
	initComponent : function() {
		ezScrum.ReleasePlan_StoryPannel.superclass.initComponent.apply(this, arguments);
		
		var StoryPanelObj = this;
		this.getSelectionModel().on({
			'selectionchange' : {
				buffer : 10,
				fn : function(sm) {
					var sel = sm.getSelections();
					
					// 檢查story是否有附屬在某個sprint下，如果沒有就是直接附屬在release下的story就要檢查該release是否已經結束，如果已經結束則不能對story做任何操作
					if (sel[0] != null && StoryPanelObj.getSelectionModel().getSelected().data["Sprint"] == "None") {
						var today = new Date();
						
						var releaseEndDate = Date.parseDate(Ext.getCmp('ReleasePlan_ReleaseTree').getSelectionModel().getSelectedNode().attributes["EndDate"], 'Y/m/d');
					}
				}
			}
		});
	},
	loadData : function(selectedNode) {
		// 如果什麼東西都沒有選擇的話，那麼就把所有的Button功能給關掉
		if (selectedNode == null) {
			Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('releaseAction').get('ReleasePlan_EditReleaseBtn').disable();
			Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('releaseAction').get('ReleasePlan_DeReleaseBtn').disable();
			Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('releaseAction').get('ReleasePlan_showReleaseBacklogBtn').disable();
			Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('releaseAction').get('ReleasePlan_showPritableBtn').disable();
			Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('sprintAction').get('ReleasePlan_editSprintBtn').disable();
			Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('sprintAction').get('ReleasePlan_addSprintBtn').enable();
			if (Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('releaseAction').get('ReleasePlan_downloadReleaseBtn'))
				Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('releaseAction').get('ReleasePlan_downloadReleaseBtn').disable();
		} else {
			// 有選擇某個項目，判斷此 Type 為 Release or Sprint
			var StoryPanelObj = this;
			var type = selectedNode.attributes['Type'];
			var id = selectedNode.attributes['ID'];
			if (type == "Release") {
				Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('releaseAction').get('ReleasePlan_EditReleaseBtn').enable();
				Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('releaseAction').get('ReleasePlan_DeReleaseBtn').enable();
				Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('releaseAction').get('ReleasePlan_showReleaseBacklogBtn').enable();
				Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('releaseAction').get('ReleasePlan_showPritableBtn').enable();
				if (Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('releaseAction').get('ReleasePlan_downloadReleaseBtn'))
					Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('releaseAction').get('ReleasePlan_downloadReleaseBtn').enable();

				Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('sprintAction').get('ReleasePlan_editSprintBtn').disable();
				
				var loadmask = new Ext.LoadMask(StoryPanelObj.getEl(), {msg:"loading info..."});
				loadmask.show();				
				Ext.Ajax.request({
					url : 'AjaxShowStoryfromRelease.do?Rid=' + id,
					success : function(response) {
						// 計算此 Release 的Story Point
						releaseStoryStore.loadData(response.responseXML);
						StoryPanelObj.setNewTitleInfo(releaseStoryStore, selectedNode);
						
						var loadmask = new Ext.LoadMask(StoryPanelObj.getEl(), {msg:"loading info..."});
						loadmask.hide();
					},
					failure: function(response) {
						var loadmask = new Ext.LoadMask(StoryPanelObj.getEl(), {msg:"loading info..."});
						loadmask.hide();
					}
				});
			} else {
				Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('releaseAction').get('ReleasePlan_EditReleaseBtn').disable();
				Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('releaseAction').get('ReleasePlan_DeReleaseBtn').disable();
				Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('releaseAction').get('ReleasePlan_showReleaseBacklogBtn').disable();
				Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('releaseAction').get('ReleasePlan_showPritableBtn').disable();
				Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('sprintAction').get('ReleasePlan_editSprintBtn').enable();
				if (Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('releaseAction').get('ReleasePlan_downloadReleaseBtn'))
					Ext.getCmp('releasePlanMasterPanel').getTopToolbar().get('releaseAction').get('ReleasePlan_downloadReleaseBtn').disable();
				
				var loadmask = new Ext.LoadMask(StoryPanelObj.getEl(), {msg:"loading info..."});
				loadmask.show();
				Ext.Ajax.request({
					url : 'AjaxShowStoryfromSprint.do?Sid=' + id,
					success : function(response) {
						// 計算此 Sprint 的Story Point
						releaseStoryStore.loadData(response.responseXML);
						StoryPanelObj.setNewTitleInfo(releaseStoryStore, selectedNode);
						
						var loadmask = new Ext.LoadMask(StoryPanelObj.getEl(), {msg:"loading info..."});
						loadmask.hide();
					},
					failure: function(response) {
						var loadmask = new Ext.LoadMask(StoryPanelObj.getEl(), {msg:"loading info..."});
						loadmask.hide();
					}
				});
			}
		}
	},
	resetData: function() {
		releaseStoryStore.removeAll();
		this.setTitle('Story List');
	},
	setNewTitleInfo: function(store, selectedNode) {
		var point = 0;
		store.each( function(rec) {
			point += (rec.get('Estimate') - 0);
		});	
		
		var goal = selectedNode.attributes['Name'];
		var id = selectedNode.attributes['ID'];
		var type = selectedNode.attributes['Type'];
		
		// title = [Story Point: 10]  Release #ID: Goal;
		var NewTitle = '[Story Point:' + point + ']\t\t' + type + ' #' + id + ': ' + goal;

		this.setTitle(NewTitle);
	}
});
Ext.reg('Release_StoryPanel', ezScrum.ReleasePlan_StoryPannel);


ezScrum.ReleasePlan_TreePanel = Ext.extend(Ext.ux.tree.TreeGrid, {
	id			: 'ReleasePlan_ReleaseTree',
	title		: 'Release & Sprint List',
	frame		: false,
	border		: false,
	region		: 'center',
	margins		: '0 0 0 0',
	enableSort	: false,
	enableHdMenu: false,
	columns		: releaseColumns,
	dataUrl		: 'showReleasePlan2.do',
	singleExpand: true,
	viewConfig	: { forceFit: true },
	initComponent: function() {
		ezScrum.ReleasePlan_TreePanel.superclass.initComponent.apply(this, arguments);
		
		var TreeObj = this;
		TreeObj.getSelectionModel().on({
			'selectionchange': {
				buffer : 25, fn : function() {
					var selectedNode = TreeObj.getSelectionModel().getSelectedNode();
					Ext.getCmp('ReleasePlan_StoryGrid').loadData(selectedNode);
				}
			}
		});
	},
	reloadTreeData: function() {
		this.getRootNode().reload();
	}
});
Ext.reg('ReleasePlan_ReleasePanel', ezScrum.ReleasePlan_TreePanel);