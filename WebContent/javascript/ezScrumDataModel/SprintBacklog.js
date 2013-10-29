// ================ Sprint Combobox ================
var forSprintBacklogThisSprintStore = new Ext.data.Store({
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


//================ Date Column Store ==================
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

//================= Tree Structure info ================ 
var SprintBacklogColumns = 
[
	{dataIndex: 'ID', header: 'ID', align: 'center', width: 80, filterable: true/*, renderer: makeIssueDetailUrl*/},
	{dataIndex: 'Tag', header: 'Tag', align: 'center', width: 100},
	{dataIndex: 'Name', header: 'Name', align: 'left', width: 400},
	{dataIndex: 'Importance', header: 'Importance',	align: 'center', width: 70},
	{dataIndex: 'Value', header: 'Value', align: 'center', width: 70},
	{dataIndex: 'Estimate', header: 'Estimate', align: 'center', width: 70},
	{dataIndex: 'Handler', header: 'Handler', align: 'center', width: 100},
	{dataIndex: 'Status', header: 'Status',	align: 'center', width: 70}
];