Ext.ns('ezScrum'); 
Ext.ns('ezScrum.window');

/*
 * TreeWidget is from javascript/ezScrumPanel/SprintBacklogTreePanel
 * 
 * */

// =============== CRUD Story Widgets =================
var SprintBacklog_DropStoryWidget = new ezScrum.DropStoryWidget({
	id	: 'SprintBacklog_DropStory',
	listeners:{
		DropSuccess:function(win, response, issueId){
			this.hide();
			Ext.example.msg('Drop Story','Drop Story Success.');
			
			var obj = Ext.getCmp('SprintBacklog_Page_Event');
			obj.updateTree();
			obj.updateSelectedSprintTitle();
		},
		DropFailure:function(win, response, issueId){
			this.hide();
			Ext.example.msg('Drop Story','Drop Story Failure.');
		}
	}
});

//=============== CRUD Task Widgets =================
var SprintBacklog_CreateTaskWidget = new ezScrum.AddNewTaskWidget({
	listeners:{
		CreateSuccess:function(win, form, response, record){
			this.hide();
			Ext.example.msg('Create Task', 'Create Task Success.');
			
			var obj = Ext.getCmp('SprintBacklog_Page_Event');
			obj.updateTree();
			obj.updateSelectedSprintTitle();
		},
		CreateFailure:function(win, form, response, issueId){
			this.hide();
			Ext.example.msg('Create Task','Create Task Failure.');
		}
	}
});

//EditTaskWindow
//EditTaskWindow widget from EditTaskWidget.js

var SprintBacklog_DropTaskWidget = new ezScrum.DropTaskWidget({
	listeners:{
		DropSuccess:function(win, response, issueId) {
			this.hide();
			Ext.example.msg('Drop Task', 'Drop Task Success.');
			
			var obj = Ext.getCmp('SprintBacklog_Page_Event');
			obj.updateTree();
			obj.updateSelectedSprintTitle();
		},
		DropFailure:function(win, response, issueId){
			this.hide();
			Ext.example.msg('Drop Task','Drop Task Failure.');
		}
	}
});

var SprintBacklog_DeleteTaskWidget = new ezScrum.DeleteTaskWidget({
	listeners:{
		DeleteSuccess:function(win, response, issueId) {
			this.hide();
			Ext.example.msg('Delete Task', 'Delete Task Success.');
			
			var obj = Ext.getCmp('SprintBacklog_Page_Event');
			obj.updateTree();
			obj.updateSelectedSprintTitle();
		},
		DeleteFailure:function(win, response, issueId){
			this.hide();
			Ext.example.msg('Delete Task','Delete Task Failure.');
		}
	}
});

var SprintBacklog_ExistedTaskWidget = new ezScrum.ExistedTaskWidget();

// ================= sprintBacklogPage UI 的排列方式 ================
SprintBacklogPageLayout = Ext.extend(Ext.Panel,{
    title : 'Sprint Backlog',
    layout: 'fit',
	prevText : "Previous Sprint",
	nextText : "Next Sprint",
	initComponent : function() {
		var config = {	
			items : [
			    { ref: 'SprintBacklog_TreeGrid_ID', xtype : 'SprintBacklog_TreeGrid' }
			],
			tbar: [{
				id	: 'SprintBacklog_preSprintBtn',
				icon: 'images/previous.png',
				tooltip : this.prevText,
	            handler : function() {
	            	var combo = Ext.getCmp('SprintBacklog_Page_Event').SprintBacklog_SprintCombo;
					var preValue = combo.selectedIndex + 1;
					
					if ( preValue < combo.getStore().getCount() ) {
						// update Sprint combobox information
						combo.fireEvent('selectIndex', preValue);
					}
				}
			}, {
				ref: '../SprintBacklog_SprintCombo', xtype : 'SprintComboWidget'
			}, {
				id	: 'SprintBacklog_nextSprintBtn',
				icon: 'images/next.png',
				tooltip : this.nextText,
				handler : function() {
					var combo = Ext.getCmp('SprintBacklog_Page_Event').SprintBacklog_SprintCombo;
					var nextValue = combo.selectedIndex - 1;
					
					if( nextValue >= 0 ) {
						// update Sprint combobox information
						combo.fireEvent('selectIndex', nextValue);
					}
				}
			}, {
				id: 'sprintAction',
				xtype: 'buttongroup',
				columns: 3,
				title: '<u><b>Sprint Backlog Action</b></u>',
				items: [
					{id:'SprintBacklog_addStoryBtn', text:'Add Story', icon:'images/add3.png', handler: function(){Ext.getCmp('SprintBacklog_Page_Event').AddStoryAction();}},
					{id:'SprintBacklog_addExistStoryBtn', text:'Add Existing Stories', icon:'images/add.gif', handler:function(){Ext.getCmp('SprintBacklog_Page_Event').addExistStory();}},
					{id:'SprintBacklog_showPrintableStoryBtn', text:'Printable Stories', icon:'images/text.png', handler:function(){Ext.getCmp('SprintBacklog_Page_Event').showPrintableStory();}},
					{id:'SprintBacklog_showSprintInfoBtn', text:'Sprint Information', icon:'images/clipboard.png', handler:function(){Ext.getCmp('SprintBacklog_Page_Event').showSprintInfo();}},
					{id:'SprintBacklog_editSprintBtn', text:'Edit Sprint', icon:'images/edit.png', handler:function(){Ext.getCmp('SprintBacklog_Page_Event').editSprintPlan();}}
//					,
//					{id:'SprintBacklog_deleteExistingTaskBtn', text:'Delete Existing Task', icon:'images/delete.png', handler:function(){Ext.getCmp('SprintBacklog_Page_Event').deleteExistingTask();}}
				]
			}, {
				id: 'storyAction',
				xtype: 'buttongroup',
				columns: 3,
				title: '<u><b>Story Action</b></u>',
				items: [
					{id:'SprintBacklog_editStoryBtn', disabled:true, text:'Edit Story', icon:'images/edit.png', handler: function(){Ext.getCmp('SprintBacklog_Page_Event').editStory();}},
					{id:'SprintBacklog_droptStoryBtn', disabled:true, text:'Drop Story', icon:'images/drop2.png', handler:function(){Ext.getCmp('SprintBacklog_Page_Event').dropStory();}},
					{id:'SprintBacklog_showStoryHistoryBtn', disabled:true, text:'Story History', icon:'images/history.png', handler:function(){Ext.getCmp('SprintBacklog_Page_Event').showHistory();}},
					{id:'SprintBacklog_addTaskBtn', disabled:true, text:'Add Task', icon:'images/add.gif', handler:function(){Ext.getCmp('SprintBacklog_Page_Event').addTask();}},
					{id:'SprintBacklog_addExistedTaskBtn', disabled:true, text:'Add Existing Task', icon:'images/add2.gif', handler: function(){Ext.getCmp('SprintBacklog_Page_Event').addExistedTask();}},
					{id:'SprintBacklog_moveStoryBtn',disabled:true,text:'Move Story',icon:'images/arrow_right.png',handler:function(){Ext.getCmp('SprintBacklog_Page_Event').moveStory();}}
				]
			}, {
				id: 'taskAction',
				xtype: 'buttongroup',
				columns: 2,
				title: '<u><b>Task Action</b></u>',
				items: [
					{id:'SprintBacklog_editTaskBtn', disabled:true, text:'Edit Task', icon:'images/edit.png', handler: function(){Ext.getCmp('SprintBacklog_Page_Event').editTask();}},
					{id:'SprintBacklog_dropTaskBtn', disabled:true, text:'Drop Task', icon:'images/drop2.png', handler:function(){Ext.getCmp('SprintBacklog_Page_Event').dropTask();}},
					{id:'SprintBacklog_showTaskHistoryBtn', disabled:true, text:'Task History', icon:'images/history.png', handler:function(){Ext.getCmp('SprintBacklog_Page_Event').showHistory();}}
//					,
//					{id:'SprintBacklog_deleteTaskBtn', disabled:true, text:'Delete Task', icon:'images/delete.png', handler:function(){Ext.getCmp('SprintBacklog_Page_Event').deleteTask();}}
						]
			},
			'->'
		]}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		SprintBacklogPageLayout.superclass.initComponent.apply(this, arguments);
		
		this.SprintBacklog_SprintCombo.addListener('select',function() {
			Ext.getCmp('SprintBacklog_Page_Event').loadDataModel();
		});
		
		this.SprintBacklog_SprintCombo.addListener('selectIndex',function( index ){
			this.selectedIndex = index;
			this.originalValue = this.getStore().getAt(this.selectedIndex).get('Info');
			this.reset();
			Ext.getCmp('SprintBacklog_Page_Event').loadDataModel();
		});
	}
});

var SprintBacklog_ThisSprintComboStore = new Ext.data.Store({
	fields:[
	    {name : 'Id'}, 
	    {name : 'Info'},
	    {name : 'Edit'}
    ],
    reader : ThisSprintPlanReader
});

//================= sprintBacklogPage 可以使用的動作 (把UI排列的方式繼承下來後擴展使用功能) ================
SprintBacklogPageEvent = Ext.extend(SprintBacklogPageLayout, {
	id		: 'SprintBacklog_Page_Event',
	isInit	: false,		// 判斷是否已經初始化元件
	loadDataModel: function() {
		if ( ! this.isInit ) {
			this.init_loadFirst();
			this.isInit = true;
			
			return ;
		}
		
		var sprintID = '';
		var editable = false;
		var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
		if ( this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex) != undefined ) {
			sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
			editable = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Edit');
		}else{
			sprintID = 0;
			editable = false;
		}
		this.checkPermission( sprintID, editable );
		this.updateTitle( sprintID );
		this.SprintBacklog_TreeGrid_ID.reloadData( sprintID );
    },
    // 會寫這個函式是因為第一次讀取不見得都可以取的到 Sprint Combo 資料，所以特別寫這函式確保資料正確
    init_loadFirst: function() {
    	var obj = this;
    	Ext.Ajax.request({
			url: 'GetSprintsComboInfo.do',
			success : function(response) {
				SprintBacklog_ThisSprintComboStore.loadData(Ext.decode(response.responseText));		// get this sprint info
				
				var sprintID = SprintBacklog_ThisSprintComboStore.getAt(0).get('Id');
				var edit = SprintBacklog_ThisSprintComboStore.getAt(0).get('Edit');
				obj.updateTitle( sprintID );
				obj.checkPermission(sprintID, edit);
			},
			failure : function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
    },
    
    // Add Story action
    AddStoryAction:function() {
    	var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
		var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
		
    	Story_Window.showTheWindow_Add(this, sprintID);
    },
    notify_CreateStory: function(success, response, record) {
    	Story_Window.hide();
		var title = 'Create Story';
		if (success) {
			Ext.example.msg(title, 'Success.');
			this.SprintBacklog_TreeGrid_ID.fireEvent('updateSprintBacklogTree');

			// update title
			var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
			var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
			this.updateTitle( sprintID );
		} else {
			Ext.example.msg(title, 'Sorry, please try again.');
		}
    },
    
    // Edit Story action
    editStory:function() {
    	var record = this.SprintBacklog_TreeGrid_ID.getSelectionModel().getSelectedNode();
        if (record != null) {
        	var edit = true;
			var Info = "The sprint is overdue, are you sure to edit the story?";
			
        	this.overdueConfirm_story(Info, record, edit);
        }
    },
    
    // Drop Story action
    dropStory:function() {
    	var record = this.SprintBacklog_TreeGrid_ID.getSelectionModel().getSelectedNode();
        if(record != null) {
			var edit = false;
			var Info = "The sprint is overdue, are you sure to drop the story?";
			
        	this.overdueConfirm_story(Info, record, edit);
        }
    },
    
	// Add Exist Story action
    addExistStory:function() {
    	var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
		var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
		
    	AddExistedStory_Window.showTheWindow_Sprint(this, sprintID);
    },
    notify_AddExistedStorySuccess: function() {
    	AddExistedStory_Window.hide();
    	Ext.example.msg("Add Existed Stories", 'Success.');
    	
    	this.SprintBacklog_TreeGrid_ID.fireEvent('updateSprintBacklogTree');
    	
    	// update title
    	var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
    	var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
    	this.updateTitle( sprintID );
    },

	// move story to release or other sprint
    moveStory:function() {
    	var record = this.SprintBacklog_TreeGrid_ID.getSelectionModel().getSelectedNode();
        var issueID = record.attributes['ID'];
        var sprintID = record.attributes['SprintID'];
        var releaseID = record.attributes['ReleaseID'];
        
        MoveStory_Window.showTheWindow_MoveStory(this, issueID , sprintID , releaseID);
    },
    notify_MoveSuccess: function(response) {
    	MoveStory_Window.hide();
    	
    	Ext.example.msg("Move Story", "Success.");
		this.SprintBacklog_TreeGrid_ID.fireEvent('updateSprintBacklogTree');
		
		// update title
    	var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
		var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
		this.updateTitle( sprintID );
    },
    
    // Show Sprint Information
    showSprintInfo:function() {
    	var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
		var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
		
		openURLWithCheckSession("showSprintInformation.do?sprintID=" + sprintID);
    },

	// Show All printable Story    
    showPrintableStory:function() {
    	var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
		var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
		
		openURLWithCheckSession( "showPrintableStory.do?sprintID=" + sprintID );
    },
    
    // Show Story History action
    showHistory:function() {
    	var record = this.SprintBacklog_TreeGrid_ID.getSelectionModel().getSelectedNode();
        if(record != null) {
            var id = record.attributes['ID'];
            var type = record.attributes['Type'];
            IssueHistory_Window.showTheWindow(id, type);
        }
    },
    
	// edit sprint plan info
	editSprintPlan: function() {
		var obj = this;
		var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
		var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
		
		if ( ! this.getThisSprintEditable()) {
			Ext.MessageBox.confirm('Confirm', "The sprint is overdue, are you sure to edit the sprint?", function(btn) {
				if(btn == 'yes') {
					SprintPlan_Window.showTheWindow_Edit(obj, sprintID);
				}
			});
		} else {
			SprintPlan_Window.showTheWindow_Edit(obj, sprintID);
		}
	},

    // Show Add Existed Task action
    addExistedTask:function() {
    	var record = this.SprintBacklog_TreeGrid_ID.getSelectionModel().getSelectedNode();
        if(record != null) {
        	var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
    		var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
            var issueID = record.attributes['ID'];
            SprintBacklog_ExistedTaskWidget.addExistedTaskMode();
            SprintBacklog_ExistedTaskWidget.showWidget( this , sprintID , issueID );
        }
    },
    Notify_AddExistedTaskSuccess:function(){
    	SprintBacklog_ExistedTaskWidget.hide();
    	
    	Ext.example.msg("Add Existed Tasks", 'Success.');    	
    	this.SprintBacklog_TreeGrid_ID.fireEvent('updateSprintBacklogTree');
    	
    	// update title
    	var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
    	var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
    	this.updateTitle( sprintID );
    },

    deleteExistingTask: function() {
    	var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
		var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
        SprintBacklog_ExistedTaskWidget.deleteExistedTaskMode();
        SprintBacklog_ExistedTaskWidget.showWidget( this , sprintID , null );
    },
    Notify_DeleteExistedTaskSuccess:function(){
    	SprintBacklog_ExistedTaskWidget.hide();
    	Ext.example.msg("Delete Existed Tasks", 'Success.');    	
    	this.SprintBacklog_TreeGrid_ID.fireEvent('updateSprintBacklogTree');
    },

	// Add a new task
	addTask:function() {
		var record = this.SprintBacklog_TreeGrid_ID.getSelectionModel().getSelectedNode();
		if(record != null) {
			var issueID = record.attributes['ID'];
			var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
    		var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
			
			SprintBacklog_CreateTaskWidget.showWidget(sprintID, issueID);
		}
	},
	
	// edit a task
	editTask:function() {
		var record = this.SprintBacklog_TreeGrid_ID.getSelectionModel().getSelectedNode();
		
		if(record != null) {
			if(record.parentNode != null) {
				var edit = true;
				var Info = "The sprint is overdue, are you sure to edit the task?";
	        	var storyId = record.parentNode.attributes['ID'];
	        	
	        	this.overdueConfirm_task(Info, record, edit, storyId);
	        }
		}
	},
	
	// drop a task
	dropTask:function() {
		var record = this.SprintBacklog_TreeGrid_ID.getSelectionModel().getSelectedNode();
		if(record != null) {
			if(record.parentNode != null) {
				var edit = false;
				var Info = "The sprint is overdue, are you sure to drop the task?";
	        	
	        	var storyId = record.parentNode.attributes['ID'];
	        	this.overdueConfirm_task(Info, record, edit, storyId);
			}
		}
	},
	// delete a task
	deleteTask:function() {
		var record = this.SprintBacklog_TreeGrid_ID.getSelectionModel().getSelectedNode();
		if(record != null) {
			if(record.parentNode != null) {
				var edit = false;
				var Info = "The sprint is overdue, are you sure to delete the task?";
	        	
	        	var storyId = record.parentNode.attributes['ID'];
	        	this.overdueConfirm_deleteTask(Info, record, edit, storyId);
			}
		}
	},

	// overdueConfirm action for story
    overdueConfirm_story: function(info, record, edit) {
    	if ( ! this.getThisSprintEditable()) {
			Ext.MessageBox.confirm('Confirm', info, function(btn) {
				if(btn == 'yes') {
					Ext.getCmp('SprintBacklog_Page_Event').doAction_story(record, edit);
				}
			});
		} else {
			Ext.getCmp('SprintBacklog_Page_Event').doAction_story(record, edit);
		}
    },
    
    // do action for story
    doAction_story: function(record, edit) {
    	var id = record.attributes['ID'];
    	
    	var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
		var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
    
		if (edit) {
			Story_Window.showTheWindow_Edit(this, id);
		} else {
			SprintBacklog_DropStoryWidget.dropStory(id, sprintID);
		}
    },
    
    // overdueConfirm action for task
    overdueConfirm_task: function(info, record, edit, storyID) {
    	var obj = this;
    	if ( ! this.getThisSprintEditable()) {
	    	Ext.MessageBox.confirm('Confirm', info, function(btn) {
				if(btn == 'yes') {
					obj.doAction_task(record, edit, storyID);
				}
			});
		} else {
			obj.doAction_task(record, edit, storyID);
		}
    },

    // overdueConfirm action for task
    overdueConfirm_deleteTask: function(info, record, edit, storyID) {
    	var obj = this;
    	if ( ! this.getThisSprintEditable()) {
	    	Ext.MessageBox.confirm('Confirm', info, function(btn) {
				if(btn == 'yes') {
					obj.doAction_deleteTask(record, edit, storyID);
				}
			});
		} else {
			obj.doAction_deleteTask(record, edit, storyID);
		}
    },
    
    // do action
    doAction_deleteTask: function(record, edit, storyID) {
    	var id = record.attributes['ID'];
    	var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
		var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
    	
		if (edit) {
			EditTaskWindow.loadEditTask(sprintID, id, this);
		} else {
			SprintBacklog_DeleteTaskWidget.deleteTask(id, sprintID, storyID);
		}
    },     // do action
    doAction_task: function(record, edit, storyID) {
    	var id = record.attributes['ID'];
    	var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
		var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
    	
		if (edit) {
			EditTaskWindow.loadEditTask(sprintID, id, this);
		} else {
			SprintBacklog_DropTaskWidget.dropTask(id, sprintID, storyID);
		}
    }, 
    notify_EditStory: function(success, response, record) {
		Story_Window.hide();
		
		var title = 'Edit Story';
		if (success) {
			Ext.example.msg(title, 'Success.');
			
			this.SprintBacklog_TreeGrid_ID.fireEvent('updateSprintBacklogTree');
			
			// update title
			var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
			var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
			this.updateTitle( sprintID );
		} else {
			Ext.example.msg(title, 'Sorry, please try again.');
		}
	},
	notify_EditSprint: function(success) {
		SprintPlan_Window.hide();
		var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
		var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
		//ninja
		this.checkPermissionByReloadComboData( sprintID );
		var title = 'Edit Sprint';
		if (success) {
			Ext.example.msg(title, 'Success.');
			this.updateTitle( sprintID );
		} else {
			Ext.example.msg(title, 'Sorry, please try again.');
		}
	},
	notify_EditTask: function(){
		EditTaskWindow.hide();// share component
		Ext.example.msg('Edit Task','Edit Task Success.');
		
		var obj = Ext.getCmp('SprintBacklog_Page_Event');
		obj.updateTree();
		obj.updateSelectedSprintTitle();
	},
	updateTree: function(){
		// update tree
		this.SprintBacklog_TreeGrid_ID.fireEvent('updateSprintBacklogTree');
	},
	updateSelectedSprintTitle:function(){
		// update selected sprint title
		var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;			
		var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
		this.updateTitle( sprintID );
	},
	
	
	// update Sprint Backlog Title Information
	updateTitle: function( sprintID ) {
		var obj = this;
		Ext.Ajax.request({
			url:'showSprintBacklog2.do?sprintID=' + sprintID,//如果sprintID是空字串則預設是最近的sprint或空的sprint
			async: false,
			success: function(response) {
				MainLoadMaskShow();
				obj.checkPermission( sprintID, obj.getThisSprintEditable() );//重新設定sprint action按鈕 enable 或 disable//因為需要重新認識一次Edit必須重新載入combo的資料
				var responseTextJson = response.responseText.replace(/\n/g, " ");//把換行符號取代成空白，才可正常讀取外部檔案iterPlan.xml
				forSprintBacklogThisSprintStore.loadData(Ext.decode(responseTextJson));
				var title = forSprintBacklogThisSprintStore.getAt(0).get('ReleaseID') + "  ;  " + forSprintBacklogThisSprintStore.getAt(0).get('Name') + " - " + forSprintBacklogThisSprintStore.getAt(0).get('SprintGoal') + 
				"  |  "  + 
				"Story Point : " + forSprintBacklogThisSprintStore.getAt(0).get('CurrentPoint')+ " ; Task Hours : " + forSprintBacklogThisSprintStore.getAt(0).get('TaskPoint') + " ; Total Hours to Commit : " + forSprintBacklogThisSprintStore.getAt(0).get('LimitedPoint') ;
				
				obj.setTitle(title);		// update Sprint Backlog Title
				if(forSprintBacklogThisSprintStore.reader.jsonData.Total == 0 ){
					Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('sprintAction').get('SprintBacklog_showPrintableStoryBtn').setDisabled(true);
    				Ext.example.msg('Message','no topics to display !!');//設定title時順便跳出該sprintBacklog tree是否為空
				}else{
					Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('sprintAction').get('SprintBacklog_showPrintableStoryBtn').setDisabled(false);
				}
				
				MainLoadMaskHide();
			},
			failure: function() {
				Ext.example.msg('Server Infomation','Server Failure');
			}
		});
	},
	//================= setPermission ==================
	checkPermission:function(sprintID, editable ) {//這邊的checkPermission是從已經載好的combo中取出相對sprint的edit屬性
		var obj = this;
		if( sprintID == "" || sprintID == 0 ){//none sprint disable all
			obj.set_Sprint_Permission_disableAll(true);
			obj.set_Story_Permission_disableAll(true);
			obj.set_Task_Permission_disable(true);
		}else if ( sprintID > 0 ) {
			obj.set_Sprint_Permission_disable( !eval( editable ) );//distinguish sprint between current and past
			if( obj.SprintBacklog_TreeGrid_ID.isStoryNodeSelected() ){//選到story node
				obj.set_Story_Permission_disable( !eval( editable ) );
			}else if( obj.SprintBacklog_TreeGrid_ID.isTaskNodeSelected() ){//選到task node
				obj.set_Story_Permission_disableAll( true );//不能有任何story action
				obj.set_Task_Permission_disable( false );//enable所有task action
			}
			if( !obj.SprintBacklog_TreeGrid_ID.isTaskNodeSelected() && !obj.SprintBacklog_TreeGrid_ID.isStoryNodeSelected()){
				obj.set_Story_Permission_disableAll( true );
				obj.set_Task_Permission_disable( true );
			}
		}
	},
	checkPermissionByReloadComboData:function(sprintID){//這邊的checkPermissionByReloadComboData則是重新回後端取一次combo資料再從新的combo資料中取出相對sprint edit的屬性
		//ninja
		var obj = this;
		Ext.Ajax.request({
			url: 'GetSprintsComboInfo.do',
			success : function(response) {
				obj.SprintBacklog_SprintCombo.getStore().loadData(Ext.decode(response.responseText));	// get all sprints info
				obj.checkPermission(sprintID,obj.getThisSprintEditable);
			},
			failure : function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	},
	// =============== set action permission ================
	set_Sprint_Permission_disableAll:function(disable) {
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('sprintAction').get('SprintBacklog_addStoryBtn').setDisabled(disable);
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('sprintAction').get('SprintBacklog_addExistStoryBtn').setDisabled(disable);
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('sprintAction').get('SprintBacklog_editSprintBtn').setDisabled(disable);
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('sprintAction').get('SprintBacklog_showPrintableStoryBtn').setDisabled(disable);
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('sprintAction').get('SprintBacklog_showSprintInfoBtn').setDisabled(disable);
	},
	set_Sprint_Permission_disable:function(disable) {
		// sprint Action
		if (disable) { //disable past sprint some functionality
			Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('sprintAction').get('SprintBacklog_addStoryBtn').setDisabled(true);
			Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('sprintAction').get('SprintBacklog_addExistStoryBtn').setDisabled(true);
			
			Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('sprintAction').get('SprintBacklog_editSprintBtn').setDisabled(false);				
			Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('sprintAction').get('SprintBacklog_showSprintInfoBtn').setDisabled(false);
		} else { //enable current and future sprint functionality
			Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('sprintAction').get('SprintBacklog_addStoryBtn').setDisabled(false);
			Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('sprintAction').get('SprintBacklog_addExistStoryBtn').setDisabled(false);
			Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('sprintAction').get('SprintBacklog_editSprintBtn').setDisabled(false);
			Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('sprintAction').get('SprintBacklog_showSprintInfoBtn').setDisabled(false);
		}
	},
	set_Story_Permission_disable:function(disable) {
		// story Action
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('storyAction').get('SprintBacklog_editStoryBtn').setDisabled(disable);
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('storyAction').get('SprintBacklog_droptStoryBtn').setDisabled(disable);
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('storyAction').get('SprintBacklog_showStoryHistoryBtn').setDisabled(disable);
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('storyAction').get('SprintBacklog_addTaskBtn').setDisabled(disable);
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('storyAction').get('SprintBacklog_addExistedTaskBtn').setDisabled(disable);
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('storyAction').get('SprintBacklog_moveStoryBtn').setDisabled(disable);
		
		if ( ! this.getThisSprintEditable() ) {
			Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('storyAction').get('SprintBacklog_editStoryBtn').setDisabled(false);
			Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('storyAction').get('SprintBacklog_droptStoryBtn').setDisabled(false);
			Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('storyAction').get('SprintBacklog_showStoryHistoryBtn').setDisabled(false);
			Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('storyAction').get('SprintBacklog_addTaskBtn').setDisabled(true);
			Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('storyAction').get('SprintBacklog_addExistedTaskBtn').setDisabled(true);
			Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('storyAction').get('SprintBacklog_moveStoryBtn').setDisabled(true);
		}
	},
	set_Story_Permission_disableAll:function(){
		// story Action
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('storyAction').get('SprintBacklog_editStoryBtn').setDisabled(true);
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('storyAction').get('SprintBacklog_droptStoryBtn').setDisabled(true);
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('storyAction').get('SprintBacklog_showStoryHistoryBtn').setDisabled(true);
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('storyAction').get('SprintBacklog_addTaskBtn').setDisabled(true);
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('storyAction').get('SprintBacklog_addExistedTaskBtn').setDisabled(true);
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('storyAction').get('SprintBacklog_moveStoryBtn').setDisabled(true);
	},
	set_Task_Permission_disable:function(disable) {
		// task Action
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('taskAction').get('SprintBacklog_editTaskBtn').setDisabled(disable);
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('taskAction').get('SprintBacklog_dropTaskBtn').setDisabled(disable);
		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('taskAction').get('SprintBacklog_showTaskHistoryBtn').setDisabled(disable);
//		Ext.getCmp('SprintBacklog_Page_Event').getTopToolbar().get('taskAction').get('SprintBacklog_deleteTaskBtn').setDisabled(disable);
	},
	getThisSprintEditable : function() {
		var editable = false;
		var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
		var record = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex);
		if ( record != undefined ) {
			var record = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex);
			editable = eval(record.get('Edit'));
		}else{
			editable = false;
		}
		
		return editable;
	}
});
Ext.reg('SprintBacklogPageEvent', SprintBacklogPageEvent);

var SprintBacklogPage = new Ext.Panel({
	id		: 'SprintBacklog_Page',
	layout 	: 'fit',
	items: [
	     { ref: 'SprintBacklogPageEvent_ID', xtype: 'SprintBacklogPageEvent' }
	],
	listeners : {//這裡監聽的事件  大部份都是為了接收sprintBacklog發出的event
		'show' : function() {
			this.SprintBacklogPageEvent_ID.loadDataModel();
		},
		'reloadComboData' : function() {//讓sprint plan在對sprint 做CRUD時更新sprintBacklog的combo
			var combo = this.SprintBacklogPageEvent_ID.SprintBacklog_SprintCombo;
			combo.loadDataModel();
		},
		'checkPermissionByReloadComnboData' : function(){
			var combo = Ext.getCmp('SprintBacklog_Page_Event').SprintBacklog_SprintCombo;
		    var selectedIndex = combo.selectedIndex;
		    var selectedRecord = combo.getStore().getAt(selectedIndex);
		    if( selectedRecord != undefined ){//讓sprint plan在對sprint的timebox做更新時  要讓sprintBacklog的sprint action按鈕作出相對的enable disable
		    	var sprintID = selectedRecord.get('Id');
				Ext.getCmp('SprintBacklog_Page_Event').checkPermissionByReloadComboData( sprintID );
		    }
		}
	}
});