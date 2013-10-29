Ext.ns('ezScrum');

var TaskBoardRecord = Ext.data.Record.create([ 
	'SprintGoal', 'Current_Story_Undone_Total_Point', 'Current_Task_Undone_Total_Point',
	'CurrentStoryPoint', 'CurrentTaskPoint', 'ReleaseID', 'isCurrentSprint'
]);

var TaskBoardReader = new Ext.data.JsonReader({
	id: "ID"
}, TaskBoardRecord);

var TaskBoardStore = new Ext.data.Store({
	fields : [
		{name : 'SprintGoal'}, 
		{name : 'Current_Story_Undone_Total_Point'},
		{name : 'Current_Task_Undone_Total_Point'}
	],
	reader : TaskBoardReader
});

var TaskBoardItem = [
	{fieldLabel: 'Sprint Goal', name: 'SprintGoal', xtype:'textfield', anchor: '50%', readOnly: true},
	{fieldLabel: 'Story Points Undone/Total ', name: 'Current_Story_Undone_Total_Point', xtype:'textfield', anchor: '50%', readOnly: true},
	{fieldLabel: 'Task Hours Undone/Total', name: 'Current_Task_Undone_Total_Point', xtype:'textfield', anchor: '50%', readOnly: true}
];

var TaskBoardSprintStore = new Ext.data.Store({
	fields : [
		{name : 'SprintGoal'}, 
		{name : 'CurrentStoryPoint'},
		{name : 'CurrentTaskPoint'},
		{name : 'ReleaseID'},
		{name : 'isCurrentSprint'}
	],
	reader : TaskBoardReader
});


var TaskBoard_Story = Ext.data.Record.create([ {
	name : 'Id',
	sortType : 'asInt'
}, 'Name', {
	name : 'Value',
	sortType : 'asInt'
}, {
	name : 'Importance',
	sortType : 'asInt'
}, {
	name : 'Estimate',
	sortType : 'asFloat'
}, 'Status', 'Notes', 'HowToDemo', {
	name : 'Release',
	sortType : 'asInt'
}, {
	name : 'Sprint',
	sortType : 'asInt'
}, 'Tag', 'Link', 'Attach', 'AttachFileList', 'Tasks' ]);

var TaskBoard_StoryReader = new Ext.data.JsonReader({
	root : 'Stories',
	idProperty : 'Id',
	id : 'Id',
	totalProperty : 'Total'
}, TaskBoard_Story);

var TaskBoard_StoriesStore = new Ext.data.Store({
	reader : TaskBoard_StoryReader
});

var TaskBoard_Task = Ext.data.Record.create( [ {
	name : 'Id',
	sortType : 'asInt'
}, 'Name', {
	name : 'Estimate',
	sortType : 'asFloat'
}, {
	name : 'RemainHours',
	sortType : 'asFloat'
}, 'Status', 'Notes', 'Partners', 'Handler', 'Actual', 'Link', 'Attach',
		'AttachFileList' ]);

var TaskBoard_TaskReader = new Ext.data.JsonReader( {
	root : 'Tasks'
}, TaskBoard_Task);

var tasksStore = new Ext.data.Store( {
	reader : TaskBoard_TaskReader
});

var TaskBoard_IssueStatus = [
	{ baseCls : 'TaskBoard_Non_Checkout_Header', html : '<p><h1><font size="5">Not Checked Out</font></h1></p>'},
	{ baseCls : 'TaskBoard_Checkout_Header', html : '<p><h1><font size="5">Checked Out</font></h1></p>'},
	{ baseCls : 'TaskBoard_Done_Header', html : '<p><h1><font size="5">Done</font></h1></p>'}
];

var tools = [ {
	id : 'gear',
	handler : function() {
		Ext.Msg.alert('Message', 'The Settings tool was clicked.');
	}
}, {
	id : 'close',
	handler : function(e, target, panel) {
		panel.ownerCt.remove(panel, true);
	}
} ];

// TaskBoard 頁面最上方的 Sprint 資訊的 Form 欄位
var TaskBoard_SprintItem = [{
		layout:'column',
		defaults:{
			columnWidth: 1.0,
			layout: 'form',
			anchor: '100%',
			border: false
		},
		items: [{
			items: [{ xtype:'textfield', fieldLabel: 'Sprint Goal', name: 'SprintGoal', anchor: '100%', readOnly: true }]
		}]
	}, {
		layout:'column',
		defaults:{
			columnWidth: 0.33,
			layout: 'form',
			anchor: '100%',
			border: false
		},
		items: [{
			items: [{ ref:'../../TaskBoard_HandlerCombo', xtype: 'HandlerComboBox', url: 'AjaxGetHandlerListAll.do', fieldLabel: 'Handler', anchor: '100%' }]
		}, {
			items: [{ xtype:'textfield', fieldLabel: 'Remain Story (Points)', name: 'CurrentStoryPoint', anchor: '100%', readOnly: true }]
		}, {
			items: [{ xtype:'textfield', fieldLabel: 'Remain Task (Hours)', name: 'CurrentTaskPoint', anchor: '100%', readOnly: true }]
		}]
	}, {
		layout:'column',
		defaults:{
			columnWidth: 0.33,
			layout: 'form',
			anchor: '100%',
			border: false
		},
		items: [{
			items: [{ ref:'../../TaskBoard_SprintIDCombo', xtype: 'SprintComboWidget', fieldLabel: 'Sprint ID', anchor: '100%' }]
		}, {
			items: [{ xtype:'textfield', fieldLabel: 'ReleaseID', name: 'ReleaseID', anchor: '100%', readOnly: true }]
		}, {
			items: [{ xtype:'textfield', fieldLabel: '', hidden: true, anchor: '100%', readOnly: true }]
		}]
	}
];