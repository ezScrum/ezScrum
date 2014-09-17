ezScrum.TaskBoardSprintForm = Ext.extend(Ext.form.FormPanel, {
	sprintID 	: undefined,
	id			: 'TaskBoardSprintDesc',
	url			: 'GetSprintInfoForTaskBoard.do',
	border		: true,
    frame		: true,
    store		: TaskBoardSprintStore,
    title		: 'Sprint Information',
    labelAlign	: 'right',
    labelWidth	: 150,
    bodyStyle   : 'padding:5px',
    layout		: 'anchor',
    initComponent : function() {
		var config = {
			items : [TaskBoard_SprintItem]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.TaskBoardSprintForm.superclass.initComponent.apply(this, arguments);
		
		// set default value
		this.TaskBoard_HandlerCombo.store = AllHandlerComboStore;
		this.TaskBoard_HandlerCombo.selectedIndex = 0;
		this.TaskBoard_HandlerCombo.originalValue = 'ALL';
		this.TaskBoard_HandlerCombo.reset();
		
		// add listeners to combo
		this.setComboListenerInit();
    },
	loadDataModel: function() {
		var obj = this;
		Ext.Ajax.request({
			url : obj.url,
			params: {SprintID: obj.sprintID},
			success: function(response) {
	    		// set sprint store info
	    		TaskBoardSprintStore.loadData(Ext.decode(response.responseText));
	    		var record = TaskBoardSprintStore.getAt(0);
    			obj.setDataModel(record);
			},
			failure: function(response) {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');				
			}
		});
		
		if (this.sprintID == undefined || this.sprintID == '') {
			this.TaskBoard_SprintIDCombo.loadDataModel();
		}
	},
	setDataModel: function(record) {
		var replaced_goal = replaceJsonSpecialChar(record.get('SprintGoal')); 
			
		this.getForm().setValues({
			SprintGoal: replaced_goal,
			CurrentStoryPoint: record.get('CurrentStoryPoint'), 
			CurrentTaskPoint: record.get('CurrentTaskPoint'),
			ReleaseID: record.get('ReleaseID')
		});
		// reset combobox
		this.TaskBoard_HandlerCombo.selectedIndex = 0;
		this.TaskBoard_HandlerCombo.originalValue = 'ALL';
		this.TaskBoard_HandlerCombo.reset();
	},
	setSprintID: function(sID) {
		this.sprintID = sID;
	},
	getSprintID: function() {
		return this.sprintID;
	},
	isCurrentSprint: function() {
		return TaskBoardSprintStore.getAt(0).get('isCurrentSprint');
	},
	getCombo_HandlerID: function() {
		return this.TaskBoard_HandlerCombo.getValue();
	},
	getCombo_SprintID: function() {
		var selectedIndex = this.TaskBoard_SprintIDCombo.selectedIndex;
		return this.TaskBoard_SprintIDCombo.getStore().getAt(selectedIndex).get('Id');
	},
	setComboListenerInit: function() {
		var obj = this;
		
		this.TaskBoard_HandlerCombo.addListener(
			'select', function() {
				var sprintID = obj.getCombo_SprintID();
				var userID = obj.getCombo_HandlerID();
				
				// 請待 Task Board Plugin 串起來, 再修改
				// update taskboard card info
				Ext.getCmp('TaskBoard_Page').reloadTaskBoardCard(sprintID, userID);
			}
		);
		
		this.TaskBoard_SprintIDCombo.addListener(
			'select', function() {
				var sprintID = obj.getCombo_SprintID();
				var userID = obj.getCombo_HandlerID();
	
				// 請待 Task Board Plugin 串起來, 再修改
				// update all form info
				Ext.getCmp('TaskBoard_Page').reloadAllForm(sprintID, userID);
			}
		);
	}
});
Ext.reg('TaskBoard_SprintDescForm', ezScrum.TaskBoardSprintForm);