var SprintPlanRecord = Ext.data.Record.create([
	{name:'Id', sortType:'asInt'}, 'Info', 'Goal', 'StartDate', 'Interval','DueDate', 'Members', 
	'AvaliableDays', 'FocusFactor', 'DailyScrum', 'DemoDate', 'DemoPlace', 'Check' ,'Edit'                                       
]);

var SprintPlanJsonReader = new Ext.data.JsonReader({
	root: 'Sprints',
	id: "Id"
}, SprintPlanRecord);

var ThisSprintPlanReader = new Ext.data.JsonReader({
	root: 'CurrentSprint',
	id: 'Id'
}, SprintPlanRecord);

var ThisSprintStore = new Ext.data.Store({
	fields:[
		{name : 'Id'}, 
		{name : 'Info'},
		{name : 'Edit'}
	],
   	reader : ThisSprintPlanReader
});

var SprintPlanStore = new Ext.data.Store({
    fields : [
        { name : 'Id', type : 'int'}, 
        { name : 'Goal'}, 
        { name : 'StartDate'},
        { name : 'Interval'},
        { name : 'DueDate'},
        { name : 'Members'}, 
        { name : 'AvaliableDays'},
        { name : 'FocusFactor'}, 
        { name : 'DailyScrum'},
        { name : 'DemoDate'},
        { name : 'DemoPlace'}
    ],
    reader : SprintPlanJsonReader
});

var SprintColumnModel = function() {
	var columns = [ {
		dataIndex : 'Id',
		header : 'ID',
		width : 50,
		sortable : 'true'
	}, {
		dataIndex : 'Goal',
		header : 'Sprint Goal',
		width : 350
	}, {
		dataIndex : 'StartDate',
		header : 'Start Date',
		width : 90
	}, {
		dataIndex : 'Interval',
		header : 'Interval',
		width : 90
	}, {
		dataIndex : 'DueDate',
		header : 'Due Date',
		width : 90
	}, {
		dataIndex : 'DemoDate',
		header : 'Demo Date',
		width : 90
	}, {
		dataIndex : 'Members',
		header : 'Members',
		width : 90
	}, {
		dataIndex : 'AvaliableDays',
		header : 'Hours to Commit',
		width : 150
	}, {
		dataIndex : 'FocusFactor',
		header : 'Focus Factor',
		width : 100
	}, {
		dataIndex : 'DailyScrum',
		header : 'Daily Meeting',
		width : 150
	}, {
		dataIndex : 'DemoPlace',
		header : 'Demo Place',
		width : 100
	} ];

	return new Ext.grid.ColumnModel({
		columns : columns,
		defaults : {
			sortable : true
		}
	});
};