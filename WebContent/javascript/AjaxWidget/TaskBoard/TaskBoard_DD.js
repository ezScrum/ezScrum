Ext.ns('ezScrum');

// 瘝�颲行�雿輻Common.js鋆⊿�tory摰儔嚗��箸�鋆⊿��撣貿ask List
/* 摰儔 Story 鞈�甈� */
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
/* 摰儔Task鞈�甈� */
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
// create some portlet tools using built in Ext tool ids
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

	// remove all items (for�豢��嗅�sprint�誑AJAX�耦撘�堆��誨����耦撘� 
	taskboard.removeAll();
	taskboard.add(issueStatus);
	
	for ( var i = 0; i < storiesStore.getCount(); i++) {
		// Issue��蝔桃���'new', 'assigned', 'closed';
		var story = storiesStore.getAt(i);
		/*---------------------------------------------------------
		 *  撱箇�Story Card
		 *  CrateStoryStatusPanel�沒tatusPanel.js銝�
		 *----------------------------------------------------------*/

		// 撱箇�StatusPanel嚗roupID�靘StoryID嚗蒂銝��anel�身蝵桀��誨銵函�Status
		var statusPanel = createStoryStatusPanel(story.id);
		taskboard.add(statusPanel);
		
		var storyCard = createStoryCard(story);

		statusPanel.get(story.id + '_' + story.get('Status')).add(storyCard);
		/*---------------------------------------------------------
		 *  撱箇�Task Card
		 *  CrateTaskStatusPanel�沒tatusPanel.js銝�
		 *  �詨�Story�ask��典�銝��Panel鋆⊿
		 *----------------------------------------------------------*/
		var tasks = story.get('Tasks');
		for ( var k = tasks.length - 1; k >= 0; k--) {

			var task = tasks[k];
			var taskCard = createTaskCard(task,story.id);
			statusPanel.get(story.id + '_' + task.Status).add(taskCard);
		}
		// 霈askboard��脰�Layout隞乩噶�臭誑閮�Stroy�ask��摨佗���身�嗡�瘝��鋤tory�ask�anel
		taskboard.doLayout();
		statusPanel.resetCellHeight();
	}
}

//撠askBoard憿舐內�函�閬賣除銝�Panel
var taskboard_Table = new Ext.Panel( {
	header : false,
	layout:'anchor',
	items : [ taskboard ]
});