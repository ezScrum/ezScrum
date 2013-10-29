/*
 * used in Summary page
 * Sprint Description
 */

TaskBoardDescForm = Ext.extend(ezScrum.layout.InfoForm, {
	id			: 'TaskBoardDesc',
	title		: 'Sprint Description',
    store		: TaskBoardStore,
    initComponent : function() {
		var config = {
			url		: 'GetTaskBoardDescription.do',
			items	: [ TaskBoardItem ]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		TaskBoardDescForm.superclass.initComponent.apply(this, arguments);
	},
	loadDataModel: function() {
		var obj = this;
		Ext.Ajax.request({
			url : obj.url,
			success: function(response) {
	    		TaskBoardStore.loadData(Ext.decode(response.responseText));
	    		var record = TaskBoardStore.getAt(0);
    			obj.setDataModel(record);
			},
			failure: function(response) {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');				
			}
		});
	},
	setDataModel: function(record) {
		var replaced_goal = replaceJsonSpecialChar(record.get('SprintGoal'));
		
		this.getForm().setValues({
			SprintGoal: replaced_goal,
			Current_Story_Undone_Total_Point: record.get('Current_Story_Undone_Total_Point'), 
			Current_Task_Undone_Total_Point: record.get('Current_Task_Undone_Total_Point')
		});
	}
});
Ext.reg('TaskBoardDescForm', TaskBoardDescForm);