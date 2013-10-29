Ext.ns('ezScrum');

// this sprint info
var thisSprintStore = new Ext.data.Store({
	idIndex	: 0,
	id		: 0,
	fields:[
		{name : 'Id', sortType:'asInt'},
		{name : 'Name'},
		{name : 'InitialPoint'},
		{name : 'CurrentPoint'},
		{name : 'InitialHours'},
		{name : 'CurrentHours'},
		{name : 'ReleaseID'},
		{name : 'SprintGoal'},
		{name : 'StoryChartUrl'},
		{name : 'TaskChartUrl'},
        {name : 'IsCurrentSprint'}
			],
	reader	: jsonSprintReader
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

var hanlderComboStore = new Ext.data.Store({
    id		: 0,
    idIndex	: 0,
    fields	: [{name: 'Name'}],
    reader	: ActorJSReader
});

//sprint combo
var comboInGrid = new Ext.form.ComboBox({
	typeAhead		: true,
	triggerAction	: 'all',
	lazyRender		: true,
	editable		: false,
	mode			: 'local',
	store			: sprintComboStore,
	fieldLabel		: 'Current Sprint ID',
	valueField		: 'Id',
	displayField	: 'Name',
	id				: 'CurrentSprintID',
	anchor			: '50%',
	listeners		: {
		select: function() {
			var record = this.getStore().getAt(this.selectedIndex);
			var newSprintID = record.data['Id'];
			
			setSprintFormInfo(newSprintID, 'ALL');
			initStatusTable(newSprintID);
		}
	}
});

// Sprint Title Info form
var SprintInfoForm = new Ext.form.FormPanel({
	id			: 'TaskBoard_SprintInfo',
	bodyStyle	: 'padding:15px',
	labelAlign	: 'right',
	frame		: true,
	labelWidth	: 250,
	border		: false,	
	title		: 'Sprint Info Report',
	region		: 'center',
	defaultType	: 'textfield',
	store		: thisSprintStore,
	defaults	: {
       	width		: '100%',
       	msgTarget	: 'side'
   	},
   	monitorValid  : true,
	items	: [{
               	fieldLabel	: 'Sprint Goal',
				name      	: 'SprintGoal',
				readOnly	: true,
				width 		: '95%'
			},
			{
				fieldLabel	: 'Current Story (Point)',
				name      	: 'StoryPoint',
				readOnly	: true,
				width 		: '95%'
			},
			{ 
				fieldLabel	: 'Current Task (Hours)',
				name      	: 'TaskHours',
				readOnly	: true,
				width 		: '95%'
			},
			{ 
				fieldLabel	: 'Release ID',
				name      	: 'ReleaseID',
				readOnly	: true,
				width 		: '95%'
			},
			comboInGrid
			
		],
	loadStore: function(response) {
		thisSprintStore.loadData(Ext.decode(response.responseText));
	
		var record = thisSprintStore.getAt(0);
		if(record) {
			var story_point = record.get('CurrentPoint');
			var task_hrs = record.get('CurrentHours');
			
			var goal = record.get('SprintGoal');
			goal = goal.replace(/&lt;/ig, "<");
			goal = goal.replace(/&gt;/ig, ">");
			goal = goal.replace(/&apos;/ig, "'");
			goal = goal.replace(/&quot;/ig, "\"");
			goal = goal.replace(/&amp;/ig, "&");

			this.getForm().setValues({
				SprintGoal : goal,
				StoryPoint : story_point, 
				TaskHours : task_hrs, 
				ReleaseID : record.get('ReleaseID'),
				AssignedTo: 'All'
			});
		}
	},
	setSprintID: function(id) {
		this.getForm().setValues({
			CurrentSprintID: id
		});		
	}
});

