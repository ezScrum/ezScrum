Ext.ns('ezScrum');

// =============== other widget define =================
var createStoryWidget = new ezScrum.AddNewStoryWidget({
	listeners:{
		CreateSuccess:function(win, obj, response, record) {
			this.hide();
			Ext.example.msg('Create Story', 'Create Story Success.');
			
			comboInGrid.fireEvent('updateRoot');
		},
		CreateFailure:function(win, obj, response, record) {
			this.hide();
			alert('Create Story Failure.');
			//Ext.example.msg('Create Story', 'Create Story Failure.');
		}
	}
});

var editStoryWidget = new ezScrum.EditStoryWidget({
	listeners:{
		LoadSuccess:function(win, form, response, record) {
			// Load Story Success
		},
		LoadFailure:function(win, form, response, issueId) {
			this.hide();
			alert('Load Story Failure.');
		},
		EditSuccess:function(win, form, response, record) {
			this.hide();
			Ext.example.msg('Edit Story', 'Edit Story Success.');
			
			comboInGrid.fireEvent('updateRoot');
		},
		EditFailure:function(win, form, response, issueId) {
			this.hide();
			alert('Edit Story Failure.');
		}
	}
});

var dropStoryWidget = new ezScrum.DropStoryWidget({
	listeners:{
		DropSuccess:function(win, response, issueId){
			this.hide();
			Ext.example.msg('Drop Story', 'Drop Story Success.');
			
			comboInGrid.fireEvent('updateRoot');
		},
		DropFailure:function(win, response, issueId){
			this.hide();
			alert('Drop Story Failure.');
		}
	}
});

var moveStoryWidget = new ezScrum.MoveStoreWidget({
	listeners:{
		MoveSuccess:function(issueId){
			this.hide();
			Ext.example.msg('Move Story','Move Story Success.');
			comboInGrid.fireEvent('updateRoot');
		},
		MoveFailure:function(issueId){
			this.hide();
		}
	}
});

var createTaskWidget = new ezScrum.AddNewTaskWidget({
	listeners:{
		CreateSuccess:function(win, form, response, record){
			this.hide();
			Ext.example.msg('Create Task', 'Create Task Success.');
			comboInGrid.fireEvent('updateRoot');
		},
		CreateFailure:function(win, form, response, issueId){
			this.hide();
			alert('Create Task Failure.');
		}
	}
});

var editTaskWidget = new ezScrum.EditTaskWidget({
	listeners:{
		LoadSuccess:function(win, form, response, record){
			// Load Task Success
		},
		LoadFailure:function(win, form, response, issueId){
			Ext.example.msg('Load Task', 'Load Task Failure.');
		},
		EditSuccess:function(win, form, response, record){              
			this.hide();
			Ext.example.msg('Edit Task', 'Edit Task Success.');
			
			comboInGrid.fireEvent('updateRoot');
		},
		EditFailure:function(win, form, response, issueId){
			this.hide();
			alert('Edit Task Failure.');
		}
	}
});

var dropTaskWidget = new ezScrum.DropTaskWidget({
	listeners:{
		DropSuccess:function(win, response, issueId) {
			this.hide();
			Ext.example.msg('Drop Task', 'Drop Task Success.');
			
			comboInGrid.fireEvent('updateRoot');
		},
		DropFailure:function(win, response, issueId){
			this.hide();
			alert('Drop Task Failure.');
		}
	}
});

var detailWin = new ezScrum.ShowSprintDetailWin({
	listeners:{
		Success:function(obj, response, values) {
			this.hide();
			Ext.example.msg('Edit Sprint', 'Edit Sprint Success.');
			
			comboInGrid.fireEvent('updateRoot');
		},
		Failure:function(obj, response, values) {
			this.hide();
			alert('Edit Sprint Failure.');
		}
	}
});
// =============== other widget define =================

// ================ Sprint Combobox ================
var thisSprintStore = new Ext.data.Store({
	idIndex: 0,
	id: 0,
	fields:[
		{name : 'Id', sortType:'asInt'},
		{name : 'Name'},
		{name : 'CurrentPoint'},
		{name : 'LimitedPoint'},
		{name : 'TaskPoint'},
		{name : 'ReleaseID'},
		{name : 'SprintGoal'},
		{name : 'Interval'},
		{name : 'StartDate'}
			],
	reader:jsonSprintReader
});

var sprintComboStore = new Ext.data.Store({
	idIndex: 0,
	id: 0,
		fields: [
			{name: 'Id', type: 'int'},
			{name: 'Name'},
			{name: 'Start'},
			{name: 'Edit'},
			{name: 'Goal'}
			],
	reader: sprintForComboReader
});

var comboInGrid = new Ext.form.ComboBox({
	typeAhead: true,
	triggerAction: 'all',
	lazyRender: true,
	editable: false,
	mode: 'local',
	store: sprintComboStore,
	valueField: 'Id',
	displayField: 'Name',
	id: 'SprintCombo',
	width: 150,
	listeners:{
		selectIndex: function(index) {
			this.selectedIndex = index;
			var record = this.getStore().getAt(index);
			var newSprintID = record.data['Id'];	
			document.location.href = "showSprintBacklog.do?sprintID=" + newSprintID;
		},
		
		select: function() {
			var record = this.getStore().getAt(this.selectedIndex);
			var newSprintID = record.data['Id'];
			document.location.href = "showSprintBacklog.do?sprintID=" + newSprintID;
		},
		
		updateRoot: function() {
			TreeWidget.updateRootData();
		}
	}
});
// ================ Sprint Combobox ================    


// ================ Date Column Store ==================
var SprintBacklogDate = Ext.data.Record.create(['Id', 'Name']);

var jsonDateColumnReader = new Ext.data.JsonReader({
	root: 'Dates',
	idProperty : 'Id'
}, SprintBacklogDate);

var DateColumnStore = new Ext.data.Store({
	idIndex: 0,
	id: 0,
		fields: [
			{name: 'Id'},
			{name: 'Name'}
		],
	reader: jsonDateColumnReader
});

// ================ Date Column Store ==================

// ================= Tree Structure info ================ 
var SprintBacklogColumns = 
[
	{dataIndex: 'ID', header: 'ID', align: 'center', width: 80, 
		filterable: true,
		renderer: function(value, metaData, record, rowIndex, colIndex, store) {
			return "<a href=\"" + record.data['Link'] + "\" target=\"_blank\">" + value + "</a>";
		}
	},
	{dataIndex: 'Tag', header: 'Tag', align: 'center', width: 100},
	{dataIndex: 'Name', header: 'Name', align: 'left', width: 400},
	{dataIndex: 'Importance', header: 'Importance',	align: 'center', width: 70},
	{dataIndex: 'Value', header: 'Value', align: 'center', width: 70},
	{dataIndex: 'Estimate', header: 'Estimate', align: 'center', width: 70},
	{dataIndex: 'Handler', header: 'Handler', align: 'center', width: 100},
	{dataIndex: 'Status', header: 'Status',	align: 'center', width: 70}
];

ezScrum.Treepannel = Ext.extend(Ext.ux.tree.TreeGrid, {
    id: 'SprintBacklogTreePannel',
    initComponent: function () {
        ezScrum.Treepannel.superclass.initComponent.apply(this, arguments);
    }
});

var TreeWidget = new ezScrum.Treepannel({
	id			: "BacklogTree",
    frame		: true,
    title		: 'Story & Task List',
    region		: 'center',
	autoScroll	: true,
	animate		: true,
	border		: false,
    enableSort	: false,
    enableHdMenu: false,
    columns		: SprintBacklogColumns,
    dataUrl		: 'showSprintBacklogTreeListInfo.do',
    singleExpand: false,
    stateEvents	: ['collapsenode', 'expandnode'],
    stateId		: 'tree-panel-state-id',
    stateful	: true,
    setNewUrl	: function() {
    	var currentSprintID = thisSprintStore.getAt(0).get('Id');

		//when no sprint exist, currentSprintID = 0
		if(currentSprintID !== 0){
    		var newUrl = 'showSprintBacklogTreeListInfo.do?sprintID=' + currentSprintID;
		}
    	else{
    		set_Sprint_Permission_disableAll(true);
    	}
		this.getLoader().dataUrl = newUrl;
		TreeWidget.expandAll(); //換一個sprint時初始將全部的story展開
    },
    getState : function () {//紀錄操作動作前樹的狀態
        var nodes = [];
        this.getRootNode().eachChild(function (child) {
        	//設定class的屬性
        	if(child.isLeaf){
        		child.ui.addClass('STORY');	
        	}
        	//function to store state of tree recursively
            var storeTreeState = function (node, expandedNodes) {
                if(node.isExpanded() ) {
                    expandedNodes.push(node.getPath());
                }
            };
            storeTreeState(child, nodes);
        }); 
        return {
            expandedNodes : nodes
        }
    },
    applyState : function (state) {//將之前樹的狀態還原
        var that = this;
        this.getLoader().on('load', function () {
            //read state in from cookie, not from what is passed in
            var cookie = Ext.state.Manager.get('tree-panel-state-id');
            var nodes = cookie.expandedNodes;
            for(var i = 0; i < nodes.length; i++) {
                if(typeof nodes[i] != 'undefined') {
                    that.expandPath(nodes[i]);
                }
            }
        });
    },
    updateRootData: function() {
    	var that = this;
    	var state = this.getState(); //抓取之前存起來的狀態
    	this.getLoader().load(this.getRootNode(), function () {
            that.applyState(state); //在樹load完之後restore樹的狀態 							
    	});
    }
});

TreeWidget.getSelectionModel().on({
    'selectionchange': {
        buffer: 25,
        fn: function () {
            var selectedNode = TreeWidget.getSelectionModel().getSelectedNode();
            if (selectedNode != null ) {
	            var type = selectedNode.attributes['Type'];
	
	            if (type == "Story") {
	            	set_Story_Permission_disable(false);
	            	set_Task_Permission_disable(true);
	            } else {
	            	set_Story_Permission_disable(true);
	            	set_Task_Permission_disable(false);
	            }
			}
        }
    }
});
// ================= Tree Structure info ================


// ============ main master widget ==================
var masterWidget = new Ext.Panel({
    id:'BacklogWidget',
    title : 'Sprint Backlog',
    layout: 'border',
    region : 'center',
    height: 600,
        
    addStoryPermission: false,
	editStoryPermission: false,
	dropStoryPermission: false,
    showStoryPermission: false,
	showSprintPermission: false,
	showPrintablePermission: false,
	addTaskPermission: false,
	editTaskPermission: false,
	dropTaskPermission: false,
	showTaskPermission: false,
    
    // Add Story action
    addStory:function() {
		createStoryWidget.showWidget(thisSprintStore.getAt(0).get('Id'));
    },
    
    // Edit Story action
    editStory:function() {
        if(TreeWidget.getSelectionModel().getSelectedNode() != null) {
        	var edit = true;
			var Info = "The sprint is overdue, are you sure to edit the story?";
        	var record = TreeWidget.getSelectionModel().getSelectedNode();
        	
        	this.overdueConfirm_story(Info, record, edit);
        }
    },
    
    // Drop Story action
    dropStory:function() {
        if(TreeWidget.getSelectionModel().getSelectedNode() != null) {
			var edit = false;
			var Info = "The sprint is overdue, are you sure to drop the story?";
        	var record = TreeWidget.getSelectionModel().getSelectedNode();
        	
        	this.overdueConfirm_story(Info, record, edit);
        }
    },
    
	// Add Exist Story action
    addExistStory:function() {
        document.location.href = "showExistedStory.do?sprintID=" + thisSprintStore.getAt(0).get('Id');
    },

	// move story to release or other sprint
    moveStory:function() {
        var storyRecord = TreeWidget.getSelectionModel().getSelectedNode();
		moveStoryWidget.moveStory(storyRecord.attributes['ID'], storyRecord.attributes['SprintID']);
    },
    
    // Show Sprint Information
    showSprintInfo:function() {
        window.open("showSprintInformation.do?sprintID=" + thisSprintStore.getAt(0).get('Id'));
    },

	// Show All printable Story    
    showPrintableStory:function() {
        window.open("showPrintableStory.do?sprintID=" + thisSprintStore.getAt(0).get('Id'));
    },
    
    // Show Story History action
    showHistory:function() {
        if(TreeWidget.getSelectionModel().getSelectedNode() != null) {
            var id = TreeWidget.getSelectionModel().getSelectedNode().attributes['ID'];
            document.location.href  = "showIssueHistory.do?issueID=" + id + "&issueType=" + "Story" + "&type=sprint&sprintID=" + thisSprintStore.getAt(0).get('Id');
        }
    },
    
	// edit sprint plan info
	editSprintPlan: function() {
		if ( ! getThisSprintEditable()) {
			Ext.MessageBox.confirm('Confirm', "The sprint is overdue, are you sure to edit the sprint?", function(btn) {
				if(btn == 'yes') {
					var firstRecord = comboInGrid.getStore().getAt(comboInGrid.selectedIndex);
					detailWin.autoLoadData(firstRecord.data['Id']);
					detailWin.showWidget('Edit Sprint');
					detailWin.setIsCreate(false);
				}
			});
		} else {
			var firstRecord = comboInGrid.getStore().getAt(comboInGrid.selectedIndex);
			detailWin.autoLoadData(firstRecord.data['Id']);
			detailWin.showWidget('Edit Sprint');
			detailWin.setIsCreate(false);
		} 
	},

    // Show Add Existed Task action
    addExistedTask:function() {
        if(TreeWidget.getSelectionModel().getSelectedNode() != null) {
            var id = TreeWidget.getSelectionModel().getSelectedNode().attributes['ID'];
            document.location.href  = "showAddExistedTask.do?sprintID=" + thisSprintStore.getAt(0).get('Id') + "&issueID=" + id;
        }
    },

	// Add a new task
	addTask:function() {
		if(TreeWidget.getSelectionModel().getSelectedNode() != null) {
			var id = TreeWidget.getSelectionModel().getSelectedNode().attributes['ID'];
			createTaskWidget.showWidget(thisSprintStore.getAt(0).get('Id'), id);
		}
	},
	
	// edit a task
	editTask:function() {
		var SelectedNode = TreeWidget.getSelectionModel().getSelectedNode();
		if(SelectedNode != null) {
			if(SelectedNode.parentNode != null) {
				var edit = true;
				var Info = "The sprint is overdue, are you sure to edit the task?";
	        	var record = TreeWidget.getSelectionModel().getSelectedNode();
	        	
	        	var storyId = SelectedNode.parentNode.attributes['ID'];
	        	this.overdueConfirm_task(Info, record, edit, storyId);
	        }
		}
	},
	
	// drop a task
	dropTask:function() {
		var SelectedNode = TreeWidget.getSelectionModel().getSelectedNode();
		if(SelectedNode != null) {
			if(SelectedNode.parentNode != null) {
				var edit = false;
				var Info = "The sprint is overdue, are you sure to drop the task?";
	        	var record = TreeWidget.getSelectionModel().getSelectedNode();
	        	
	        	var storyId = SelectedNode.parentNode.attributes['ID'];
	        	this.overdueConfirm_task(Info, record, edit, storyId);
			}
		}
	},
	
	// overdueConfirm action for story
    overdueConfirm_story: function(info, record, edit) {
    	if ( ! getThisSprintEditable()) {
			Ext.MessageBox.confirm('Confirm', info, function(btn) {
				if(btn == 'yes') {
					masterWidget.doAction_story(record, edit);
				}
			});
		} else {
			masterWidget.doAction_story(record, edit);
		}
    },
    
    // do action for story
    doAction_story: function(record, edit) {
    	var id = record.attributes['ID'];
    
		if (edit) {
			editStoryWidget.loadEditStory(id);
		} else {
			dropStoryWidget.dropStory(id, thisSprintStore.getAt(0).get('Id'));
		}    
    },
    
    // overdueConfirm action for task
    overdueConfirm_task: function(info, record, edit, storyID) {
    	if ( ! getThisSprintEditable()) {
	    	Ext.MessageBox.confirm('Confirm', info, function(btn) {
				if(btn == 'yes') {
					masterWidget.doAction_task(record, edit, storyID);
				}
			});
		} else {
			masterWidget.doAction_task(record, edit, storyID);
		}
    },
    
    // do action
    doAction_task: function(record, edit, storyID) {
    	var id = record.attributes['ID'];
    
		if (edit) {
			editTaskWidget.loadEditTask(thisSprintStore.getAt(0).get('Id'), id);
		} else {
			dropTaskWidget.dropTask(id, thisSprintStore.getAt(0).get('Id'), storyID);
		}    
    },
	
    tbar: [
    	{
			id:'preSprintBtn',
			icon:'images/previous.png',
            handler:function() {
				var preValue = comboInGrid.selectedIndex - 1;
				if(preValue>=0) {
					// update Sprint combobox information
					comboInGrid.fireEvent('selectIndex', preValue);
				}
			}
		},
		
		comboInGrid,
		{
			id:'nextSprintBtn',
			icon:'images/next.png',
			handler:function() {
				var nextValue = comboInGrid.selectedIndex + 1;
				if(nextValue<comboInGrid.getStore().getCount()) {
					// update Sprint combobox information
					comboInGrid.fireEvent('selectIndex', nextValue);
				}
			}
		},
		
		{
			id: 'sprintAction',
			xtype: 'buttongroup',
			columns: 3,
			title: '<u><b>Sprint Action</b></u>',
			items: [
				{id:'addStoryBtn', text:'Add Story', icon:'images/add3.png', handler: function(){masterWidget.addStory();}},
				{id:'addExistStoryBtn', text:'Add Existing Stories', icon:'images/add.gif', handler:function(){masterWidget.addExistStory();}},
				{id:'showPrintableStoryBtn', text:'Printable Stories', icon:'images/text.png', handler:function(){masterWidget.showPrintableStory();}},
				{id:'showSprintInfoBtn', text:'Sprint Information', icon:'images/clipboard.png', handler:function(){masterWidget.showSprintInfo();}},
				{id:'editSprintBtn', text:'Edit Sprint', icon:'images/edit.png', handler:function(){masterWidget.editSprintPlan();}}
				]
		},
		
		{
			id: 'storyAction',
			xtype: 'buttongroup',
			columns: 3,
			title: '<u><b>Story Action</b></u>',
			items: [
				{id:'editStoryBtn', disabled:true, text:'Edit Story', icon:'images/edit.png', handler: function(){masterWidget.editStory();}},
				{id:'droptStoryBtn', disabled:true, text:'Drop Story', icon:'images/drop2.png', handler:function(){masterWidget.dropStory();}},
				{id:'showStoryHistoryBtn', disabled:true, text:'Story History', icon:'images/history.png', handler:function(){masterWidget.showHistory();}},
				{id:'addTaskBtn', disabled:true, text:'Add Task', icon:'images/add.gif', handler:function(){masterWidget.addTask();}},
				{id:'addExistedTaskBtn', disabled:true, text:'Add Existing Task', icon:'images/add2.gif', handler: function(){masterWidget.addExistedTask();}},
				{id:'moveStoryBtn',disabled:true,text:'Move Story',icon:'images/arrow_right.png',handler:function(){masterWidget.moveStory();}}
					]
		},
		
		{
			id: 'taskAction',
			xtype: 'buttongroup',
			columns: 2,
			title: '<u><b>Task Action</b></u>',
			items: [
				{id:'editTaskBtn', disabled:true, text:'Edit Task', icon:'images/edit.png', handler: function(){masterWidget.editTask();}},
				{id:'dropTaskBtn', disabled:true, text:'Drop Task', icon:'images/drop2.png', handler:function(){masterWidget.dropTask();}},
				{id:'showTaskHistoryBtn', disabled:true, text:'Task History', icon:'images/history.png', handler:function(){masterWidget.showHistory();}}
					]
		},
		
		'->'
	],
		
	items : [TreeWidget],
	
	// update Sprint Backlog Title Information
	updateTitle: function() {
		var title = thisSprintStore.getAt(0).get('ReleaseID') + "  ;  " + thisSprintStore.getAt(0).get('Name') + " - " + thisSprintStore.getAt(0).get('SprintGoal') + 
				"  |  "  + 
				"Story Point : " + thisSprintStore.getAt(0).get('CurrentPoint') + " / " + thisSprintStore.getAt(0).get('LimitedPoint') + " ; Task Point : " + thisSprintStore.getAt(0).get('TaskPoint');
				
		this.setTitle(title);
	},
	
	// update Sprint Backlog Tree Data
	updateRootData: function() {
		TreeWidget.updateRootData();
	},
	
	// setting Tree Widget url
	setNewUrl: function() {
		TreeWidget.setNewUrl();
	}
});
// ============ main master widget ==================


// =============== set action permission ================
function set_Sprint_Permission_disableAll(disable) {
	Ext.getCmp('BacklogWidget').getTopToolbar().get('sprintAction').get('addStoryBtn').setDisabled(disable);
	Ext.getCmp('BacklogWidget').getTopToolbar().get('sprintAction').get('addExistStoryBtn').setDisabled(disable);
	Ext.getCmp('BacklogWidget').getTopToolbar().get('sprintAction').get('editSprintBtn').setDisabled(disable);
	Ext.getCmp('BacklogWidget').getTopToolbar().get('sprintAction').get('showPrintableStoryBtn').setDisabled(disable);
	Ext.getCmp('BacklogWidget').getTopToolbar().get('sprintAction').get('showSprintInfoBtn').setDisabled(disable);
}
 
function set_Sprint_Permission_disable(disable) {
	// sprint Action
	if (disable) {
		// sprint is not a current sprint
		Ext.getCmp('BacklogWidget').getTopToolbar().get('sprintAction').get('addStoryBtn').setDisabled(true);
		Ext.getCmp('BacklogWidget').getTopToolbar().get('sprintAction').get('addExistStoryBtn').setDisabled(true);
		
		Ext.getCmp('BacklogWidget').getTopToolbar().get('sprintAction').get('editSprintBtn').setDisabled(false);				
		Ext.getCmp('BacklogWidget').getTopToolbar().get('sprintAction').get('showPrintableStoryBtn').setDisabled(false);
		Ext.getCmp('BacklogWidget').getTopToolbar().get('sprintAction').get('showSprintInfoBtn').setDisabled(false);
	} else {
		// sprint is a current sprint or future sprint
		Ext.getCmp('BacklogWidget').getTopToolbar().get('sprintAction').get('addStoryBtn').setDisabled(false);
		Ext.getCmp('BacklogWidget').getTopToolbar().get('sprintAction').get('addExistStoryBtn').setDisabled(false);
		Ext.getCmp('BacklogWidget').getTopToolbar().get('sprintAction').get('editSprintBtn').setDisabled(false);
		
		//when no storys exists, disable showPrintableStoryBtn
		if(thisSprintStore.reader.jsonData.Total == 0){
			Ext.getCmp('BacklogWidget').getTopToolbar().get('sprintAction').get('showPrintableStoryBtn').setDisabled(true);
		}
		else{
			Ext.getCmp('BacklogWidget').getTopToolbar().get('sprintAction').get('showPrintableStoryBtn').setDisabled(false);
		}
		Ext.getCmp('BacklogWidget').getTopToolbar().get('sprintAction').get('showSprintInfoBtn').setDisabled(false);
	}
}

function set_Story_Permission_disable(disable) {
	// story Action
	Ext.getCmp('BacklogWidget').getTopToolbar().get('storyAction').get('editStoryBtn').setDisabled(disable);
	Ext.getCmp('BacklogWidget').getTopToolbar().get('storyAction').get('droptStoryBtn').setDisabled(disable);
	Ext.getCmp('BacklogWidget').getTopToolbar().get('storyAction').get('showStoryHistoryBtn').setDisabled(disable);
	Ext.getCmp('BacklogWidget').getTopToolbar().get('storyAction').get('addTaskBtn').setDisabled(disable);
	Ext.getCmp('BacklogWidget').getTopToolbar().get('storyAction').get('addExistedTaskBtn').setDisabled(disable);
	Ext.getCmp('BacklogWidget').getTopToolbar().get('storyAction').get('moveStoryBtn').setDisabled(disable);
	if (  ! getThisSprintEditable() ) {
		//Ext.getCmp('BacklogWidget').getTopToolbar().get('storyAction').get('editStoryBtn').setDisabled(true);
		//Ext.getCmp('BacklogWidget').getTopToolbar().get('storyAction').get('showStoryHistoryBtn').setDisabled(true);
		Ext.getCmp('BacklogWidget').getTopToolbar().get('storyAction').get('addTaskBtn').setDisabled(true);
		Ext.getCmp('BacklogWidget').getTopToolbar().get('storyAction').get('addExistedTaskBtn').setDisabled(true);
		Ext.getCmp('BacklogWidget').getTopToolbar().get('storyAction').get('moveStoryBtn').setDisabled(true);
		// sprint is out of date, but it can drop story
		//Ext.getCmp('BacklogWidget').getTopToolbar().get('storyAction').get('droptStoryBtn').setDisabled(disable);
	}
}

function set_Task_Permission_disable(disable) {
	// task Action
	Ext.getCmp('BacklogWidget').getTopToolbar().get('taskAction').get('editTaskBtn').setDisabled(disable);
	Ext.getCmp('BacklogWidget').getTopToolbar().get('taskAction').get('dropTaskBtn').setDisabled(disable);
	Ext.getCmp('BacklogWidget').getTopToolbar().get('taskAction').get('showTaskHistoryBtn').setDisabled(disable);
}
// =============== set action permission ================


// ============ Other Function =============
function hideMask(targetId) {
	new Ext.LoadMask(Ext.get(targetId), {msg:msg}).hide();
}

function showMask(targetId, msg) {
	new Ext.LoadMask(Ext.get(targetId), {msg:msg}).show();
}

function getParameter( queryString, parameterName ) {
	// Add "=" to the parameter name (i.e. parameterName=value)
	var parameterName = parameterName + "=";
	
	if ( queryString.length > 0 ) {
		// Find the beginning of the string
		begin = queryString.indexOf ( parameterName );
		// If the parameter name is not found, skip it, otherwise return the
		// value
		if ( begin != -1 ) {
		// Add the length (integer) to the beginning
			begin += parameterName.length;
			// Multiple parameters are separated by the "&" sign
			end = queryString.indexOf ( "&" , begin );
			if ( end == -1 ) {
				end = queryString.length
			}
			// Return the string
			return unescape ( queryString.substring ( begin, end ) );
		}
		// Return "null" if no parameter has been found
		return "";
	}
}

function getThisSprintEditable() {
	var sprintID = thisSprintStore.getAt(0).get('Id');
	var editable = false;
	sprintComboStore.each(function (record) {
  		  if (sprintID == record.get('Id')) {
        	 editable = eval(record.get('Edit'));
		  }
    });
	return editable;
}

function initial_Action_Permission() {
	var sprintID = thisSprintStore.getAt(0).get('Id');
	var editable = getThisSprintEditable();
	
	if (sprintID > 0) {
		set_Sprint_Permission_disable(! editable);
		set_Story_Permission_disable(true);
		set_Task_Permission_disable(true);
	} else {
		set_Sprint_Permission_disableAll(true);
		set_Story_Permission_disable(true);
		set_Task_Permission_disable(true);
	}
}

function setWidgetColumnAndRender(DynamicDateColumn) {
	Ext.getCmp("BacklogTree").columns = DynamicDateColumn;
	masterWidget.render('centent');
}

function notifyTreeWidget() {
	masterWidget.updateTitle();		// update Sprint Backlog Title
	masterWidget.setNewUrl();		// set new url to load data
}

// setting handler store data
function AjaxGetHandlers(sprintId) {
	Ext.Ajax.request({
		url:'getAddSprintTaskInfo.do?sprintId=' + sprintId,
		async: false,
		success:function(response) {
			handlerComboStoreForCreate.loadData(response.responseXML);
			handlerComboStoreForEdit.loadData(response.responseXML);
			partnerStoreForCreate.loadData(response.responseXML);
		}
	});
}

// setting combobox store data
function AjaxGetComboStore() {
	Ext.Ajax.request({
		url: 'getAddNewRetrospectiveInfo.do',
		async: false,
		success: function(response) {
			sprintComboStore.loadData(response.responseXML);
		}
	});
}

// setting sprint store data
function AjaxGetSprintStore(sprintID, setCombo, setTitle, setColumn) {
	Ext.Ajax.request({
		url:'showSprintBacklog2.do?sprintID=' + sprintID,
		async: false,
		success: function(response) {
			thisSprintStore.loadData(Ext.decode(response.responseText));
			
			// ====================================================================
			// the permission setting dependence on thisSprintStore load data first
			// accodrin this sprint permission to set buttons disable true or not
			initial_Action_Permission();
			
			
			if (setCombo) {
				var thisSprintRecord = thisSprintStore.getAt(0);
				comboInGrid.originalValue = thisSprintRecord.get('Id');
				
				var SprintID = thisSprintRecord.get('Id');
				var tmpRecord = comboInGrid.getStore().getById(SprintID);
				comboInGrid.selectedIndex = comboInGrid.getStore().indexOf(tmpRecord);
				comboInGrid.reset();
			}
			
			
			if (setColumn) {
				// ====================================================================
				// the columns loaded dependence on thisSprintStore load data first
				// dynimic set new columns of date
				AjaxSetColumnsOfDate(sprintID);
			}
			
			
			if (setTitle) {
				// ====================================================================
				// the TreeWidget setting dependence on thisSprintStore load data first			
				// When we have a current Sprint Inforamtion
				// to change the title & root data 
				notifyTreeWidget();
			}
		},
		failure: function() {
			alert('Server Failure');
		}
	});
}

// dynamic to add more columns of date in sprint backlog widget
function AjaxSetColumnsOfDate(sprintID) {
	var DynamicDateColumn;
	Ext.Ajax.request({
		url: 'AjaxGetSprintBacklogDateInfo.do?sprintID=' + sprintID,
		async: false,
		success: function(response) {
			var isSprintExist;
			if(response.responseText == '')
				isSprintExist = false;
			else{
				isSprintExist = true;
				DateColumnStore.loadData(Ext.decode(response.responseText));
			}
			
			DynamicDateColumn = SprintBacklogColumns;
			
			for (var i=0 ; i<DateColumnStore.getCount() ; i++) {
				var record = DateColumnStore.getAt(i);
				var index = record.data['Id'];
				var head = record.data['Name'];
				
				var plugin = new Ext.grid.Column({dataIndex: index, header: head, align: 'center', width: 50});
						
				DynamicDateColumn.push(plugin);
			}
			
			
			// ========================================================================
			// setting TreeWidget columns dependence on DateColumnStore load data first
			setWidgetColumnAndRender(DynamicDateColumn);
			
			if(thisSprintStore.reader.jsonData.Total == 0 && isSprintExist == true)
				alert('no topics to display !!');
		}
	});
}
// ============ Other Function =============    


Ext.onReady(function() {
	Ext.QuickTips.init();
	
	var queryString = window.location.toString();
	var sprintID = getParameter(queryString, "sprintID");
	
	// ============== initial all widget data =============
	AjaxGetComboStore(sprintID);

	// get Sprint widget information
	AjaxGetSprintStore(sprintID, true, true, true);
	
	// according to this sprint ID, to get the handlers
	AjaxGetHandlers(sprintID);
});