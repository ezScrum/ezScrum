Ext.ns('ezScrum');

var TaskBoard_Story = Ext.data.Record.create( [ {
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

var TaskBoard_StoryReader = new Ext.data.JsonReader( {
	root : 'Stories',
	idProperty : 'Id',
	id : 'Id',
	totalProperty : 'Total'
}, TaskBoard_Story);

var storiesStore = new Ext.data.Store( {
	autoDestroy : true,
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

var issueStatus = [{
	baseCls : 'TaskBoard_Non_Checkout_Header',
	html : '<p><h1>Not Checked Out</h1></p>',
	height : 50
}, {
	baseCls : 'TaskBoard_Checkout_Header',
	html : '<p><h1>Checked Out</h1></p>',
	height : 50
}, {
	baseCls : 'TaskBoard_Done_Header',
	html : '<p><h1>Done</h1></p>',
	height : 50
}];

var taskboard = new Ext.Panel( {
	layout : 'table',
	title : 'Task Board',
	defaults : {
		bodyStyle : 'padding:5px;'
	},
	layoutConfig : {
		tableAttrs : {
			style : {
				width : '100%',
				align : 'center',
				cellpadding : 5,
				cellspacing : 5,
				'border-collapse' : 'collapse',
				'table-layout' : 'fixed'

			}
		},
		columns : 3
	}
});

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

var initialTaskBoard = function() {

	taskboard.removeAll();
	taskboard.add(issueStatus);
	
	for ( var i = 0; i < storiesStore.getCount(); i++) {
		var story = storiesStore.getAt(i);

		var statusPanel = createStoryStatusPanel(story.id);
		taskboard.add(statusPanel);
		
		var storyCard = createStoryCard(story);

		statusPanel.get(story.id + '_' + story.get('Status')).add(storyCard);

		var tasks = story.get('Tasks');
		for ( var k = tasks.length - 1; k >= 0; k--) {
			var task = tasks[k];
			var taskCard = createTaskCard(task,story.id);
			statusPanel.get(story.id + '_' + task.Status).add(taskCard);
		}
		taskboard.doLayout();
		statusPanel.resetCellHeight();
	}
}

var taskboard_Table = new Ext.Panel( {
	header : false,
	layout:'anchor',
	items : [ taskboard ]
});