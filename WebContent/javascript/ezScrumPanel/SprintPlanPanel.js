// delete sprint
var SprintPlan_DeleteWindow = new ezScrum.DeleteSprintWidget({
	listeners: {
		DeleteSuccess: function(obj,response) {
			Ext.getCmp('SprintPlan_Page_Layout').SprintPlanListGrid.loadDataModel();
			Ext.getCmp('SprintPlan_Page_Layout').StoryListGrid.clearData();
			Ext.getCmp('SprintPlan_Page_Layout').SelectedGridRecord = undefined;
			Ext.getCmp('SprintPlan_Page_Layout').checkToolBarPermission();
            SprintBacklogPage.fireEvent('reloadComboData');//叫spintBacklog重新載入sprint combo
            Ext.example.msg('Delete Sprint', 'Success.');
        }
	}
});

// move sprint
var SprintPlan_MoveWindow = new ezScrum.window.MoveSprintWindow({
	listeners: {
		MoveSuccess: function(obj, response) {
			Ext.getCmp('SprintPlan_Page_Layout').SprintPlanListGrid.loadDataModel();
			SprintBacklogPage.fireEvent('reloadComboData');//叫spintBacklog重新載入sprint combo
			Ext.example.msg('Move Sprint', 'Success.');
		}
	}
});

ezScrum.SprintPlan_SprintList_Grid = Ext.extend(Ext.grid.GridPanel, {
	frame : false,
	region : 'center',
	margins : '0 0 0 0',
	border: false,
	store : SprintPlanStore,
	colModel : SprintColumnModel(),
	sm : new Ext.grid.RowSelectionModel({singleSelect : true}),
	viewConfig: {
        forceFit: true
    },
    loadDataModel: function() {
    	MainLoadMaskShow();
    	
		var PageObj = Ext.getCmp('SprintPlan_Page_Layout');
		
		// 在這裡使用 Tbar 去顯示 mask，因為下方的 Grid panel 還沒被產生出來，無法顯示出 mask
		var Tbar = PageObj.TopBar;
		Ext.Ajax.request({
            url : 'showAllSprintForSprintPlan.do',
            success : function(response) {
            	SprintPlanStore.loadData(Ext.decode(response.responseText));
				SprintPlanStore.sort('Id', 'DESC');
				
				PageObj.checkToolBarPermission();
				
				MainLoadMaskHide();
            },
            failure : function(){
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
        });
	}
});
Ext.reg('SprintPlanSprintListGrid', ezScrum.SprintPlan_SprintList_Grid);


// 顯示Story列表
ezScrum.SprintPlan_StoryList_Grid = Ext.extend(Ext.grid.GridPanel, {
	title : 'Story List',
	region : 'south',
	collapsible : true,
	split : false,
	frame : false,
	border: false,
	margins : '0 0 0 0',
	height : 200,
	store : storyStore,
	colModel : createStoryCloumns(),
	viewConfig: {
        forceFit: true
    },
    clearData:function(){
    	storyStore.loadData('');
    }
});
Ext.reg('SprintPlanStoryListGrid', ezScrum.SprintPlan_StoryList_Grid);


ezScrum.SprintPlanPageLayout = Ext.extend(Ext.Panel, {
	id		: 'SprintPlan_Page_Layout',
	title	: 'Sprint Plan List',
	layout	: 'border',
	frame	: false,
	border	: false,
	initComponent : function() {
		var config = {
			items : [{
				xtype : 'SprintPlanSprintListGrid'
			}, {
				xtype : 'SprintPlanStoryListGrid'
			}],
		    tbar: [{
				// add a new sprint
				id : 'SprintPlan_addSprintBtn',
				text : 'New Sprint',
				icon : 'images/add3.png',
				disabled : false,
				handler : this.doAddSprintPlan
		    }, {
		    	// edit a sprint
				id : 'SprintPlan_editSprintBtn',
				text : 'Edit Sprint',
				icon : 'images/edit.png',
				disabled : true,
				handler : this.doEditSprintPlan
		    }, {
		    	// delete a sprint
				id : 'SprintPlan_deleteSprintBtn',
				text : 'Delete Sprint',
				icon : 'images/delete.png',
				disabled : true,
				handler : this.doDeleteSprintPlan
		    }, {
		    	id : 'SprintPlan_moveSprintBtn',
				text : 'Move Sprint',
				disabled : true,
				icon : 'images/arrow_right.png',
				handler : this.doMoveSprintPlan
		    }]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.SprintPlanPageLayout.superclass.initComponent.apply(this, arguments);
		
		var Obj_SprintPlanPage = this;
		this.SprintPlanListGrid = this.items.items[0];
		this.StoryListGrid = this.items.items[1];
		this.SelectedGridRecord = this.SprintPlanListGrid.getSelectionModel().getSelected();
		this.TopBar = this.getTopToolbar();
		
		this.SprintPlanListGrid.getSelectionModel().addListener('rowselect', function(sm, rowIdx, r) {
			Obj_SprintPlanPage.SelectedGridRecord = r;
			Obj_SprintPlanPage.checkToolBarPermission();
			
		    // 更新Story List 為這個 Sprint 內的 Story
		    var Sid = r.get('Id');
		    var loadobj = Obj_SprintPlanPage.StoryListGrid;
		    var loadmask = new Ext.LoadMask(loadobj.getEl(), {msg:"loading info..."});
			loadmask.show();
			
		    Ext.Ajax.request({
		        url : 'AjaxShowStoryfromSprint.do?Sid=' + Sid,
		        success : function(response) {
		            // 計算此Sprint的Story Point
		            storyStore.loadData(response.responseXML);
		            var point = 0;
		            storyStore.each(function(rec) {
		            	point += (rec.get('Estimate') - 0);
		            });

		            // 改變Story List的title顯示Sprint的名稱與ID
		            var title = '[ID: ' + r.get('Id') + ', Story Point: ' + point + ']&#8195;' + r.get('Goal');
		            Obj_SprintPlanPage.SprintPlanListGrid.setTitle(title);
		            
		            var loadmask = new Ext.LoadMask(loadobj.getEl(), {msg:"loading info..."});
					loadmask.hide();
		        },
		        failure : function() {
					var loadmask = new Ext.LoadMask(loadobj.getEl(), {msg:"loading info..."});
					loadmask.hide();
					Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
				}
		    });

		    // 計算Due Day，與判斷此Sprint是否可編輯
		    var start = Date.parseDate(r.get('StartDate'),'Y/m/d');
		    
		    if (start != null) {
		        var today = new Date();
		        
		        if(start < today) {
		        	Obj_SprintPlanPage.TopBar.get('SprintPlan_moveSprintBtn').disable();
		        } else {
		        	Obj_SprintPlanPage.TopBar.get('SprintPlan_moveSprintBtn').enable(); 
		        }
		    }
		});
	},
	loadDataModel: function() {
		this.SprintPlanListGrid.loadDataModel();
	},
	doAddSprintPlan: function() {
		var NotifyObj = Ext.getCmp('SprintPlan_Page_Layout');
		
		SprintPlan_Window.showTheWindow_Add(NotifyObj);
	},
	doEditSprintPlan: function() {
		var PageObj = Ext.getCmp('SprintPlan_Page_Layout');
		
		var Info = "The sprint is overdue, are you sure to edit it?";
		var record = PageObj.SelectedGridRecord;
		var edit = true;
		
		PageObj.overdueConfirm(Info, record, edit);
	},
	doDeleteSprintPlan: function() {
		var PageObj = Ext.getCmp('SprintPlan_Page_Layout');
		
		var Info = "The sprint is overdue, are you sure to delete it?";
		var record = PageObj.SelectedGridRecord;
		var edit = false;
		
		PageObj.overdueConfirm(Info, record, edit);
	},
	doMoveSprintPlan: function() {
		var PageObj = Ext.getCmp('SprintPlan_Page_Layout');
		
		var record = PageObj.SelectedGridRecord;
		var selectId = record.get('Id') ^ 0;
		
		// 找出可以移動的Sprint
		var canMoveSprint = new Array();
		var count = SprintPlanStore.getCount();
		var today = new Date();
		for (i = 0; i < count; i++) {
			tmp = SprintPlanStore.getAt(i);
			s = Date.parseDate(tmp.get('StartDate'), 'Y/m/d');
			id = tmp.get('Id') ^ 0;
			if (s > today && id != selectId) {
				var value = 'Sprint #' + id;
				canMoveSprint.push([id, value]);
			}
		}
		
		SprintPlan_MoveWindow.moveSprint(canMoveSprint, selectId);
	},
	checkToolBarPermission: function() {
		var record = this.SelectedGridRecord;
		if (record !== undefined) {
			this.TopBar.get('SprintPlan_editSprintBtn').setDisabled(false);	
			this.TopBar.get('SprintPlan_deleteSprintBtn').setDisabled(false);
		} else {
			this.TopBar.get('SprintPlan_editSprintBtn').setDisabled(true);	
			this.TopBar.get('SprintPlan_deleteSprintBtn').setDisabled(true);
			this.TopBar.get('SprintPlan_moveSprintBtn').setDisabled(true);
		}
	},
	overdueConfirm : function(info, record, edit) {
		// compute overdue or not
		var obj = this;
		var start = Date.parseDate(record.get('StartDate'), 'Y/m/d');
		var today = new Date();
		var due = start.add(Date.DAY, parseInt(record.get('Interval')) * 7);

		if (due < today) {
			Ext.MessageBox.confirm('Confirm', info, function(btn) {
				if (btn == 'yes') {
					obj.doModify(record, edit);
				}
			});
		} else {
			obj.doModify(record, edit);
		}
	},
	doModify: function(record, edit) {
		var NotifyObj = Ext.getCmp('SprintPlan_Page_Layout');
		
		if (edit == true) {
			SprintPlan_Window.showTheWindow_Edit(NotifyObj, record.get('Id'));
		} else {
			SprintPlan_DeleteWindow.deleteSprint(record);
		}
	},
	notify_CreateSprint: function(success) {
		var title = 'Add New Sprint';
		if (success) {
			this.SprintPlanListGrid.loadDataModel();
			SprintPlan_Window.hide();
			Ext.example.msg(title, "Success.");
			SprintBacklogPage.fireEvent('reloadComboData');//叫spintBacklog重新載入sprint combo
		} else {
			Ext.example.msg(title, "Sorry, please try again.");
		}
	},
	notify_EditSprint: function(success) {
		var title = 'Edit Sprint';
		if (success) {
			SprintBacklogPage.fireEvent('checkPermissionByReloadComnboData');
			this.SprintPlanListGrid.loadDataModel();
			SprintBacklogPage.fireEvent('reloadComboData');//叫spintBacklog重新載入sprint combo
			SprintPlan_Window.hide();
			Ext.example.msg(title, "Success.");
		} else {
			Ext.example.msg(title, "Sorry, please try again.");
		}
	}
});

Ext.reg('SprintPlanPage', ezScrum.SprintPlanPageLayout);