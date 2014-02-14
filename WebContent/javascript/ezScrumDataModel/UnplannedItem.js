var UnplannedItemRecord = Ext.data.Record.create([
	{name:'Id', sortType:'asInt'}, 'Name', 'SprintID', {name:'Estimate', sortType:'asFloat'}, 'Status', 'ActualHour', 'Handler', 'Partners', 'Notes', 'Link'
]);
	
var UnplannedItemReader = new Ext.data.XmlReader({
   record: 'UnplannedItem',
   idPath: 'Id',
   successProperty: 'Result'
}, UnplannedItemRecord);

var UnplannedItemStore = new Ext.data.Store({
   	fields:[
		{name : 'Id', type:'int'},
		{name : 'Link'},
		{name : 'Name'},
		{name : 'SprintID'},
		{name : 'Estimate', type:'float'},
		{name : 'Status'},
		{name : 'ActualHour'},
		{name : 'Handler'},
		{name : 'Partners'},
		{name : 'Notes'}
	],
	reader : UnplannedItemReader
});

var UnplannedItemColumnModel = function () {
	var columns = [
		{dataIndex: 'Id',header: 'Id', width: 50, filterable: true/*, renderer: makeIssueDetailUrl*/},	//	makeIssueDetailUrl function in IssueGridPanelSupport.js
		{dataIndex: 'Name',header: 'Name', width: 300},
		{dataIndex: 'SprintID',header: 'SprintID', width: 50},
		{dataIndex: 'Estimate',header: 'Estimate', width: 70},
		{dataIndex: 'Status',header: 'Status', width: 50},
		{dataIndex: 'ActualHour',header: 'Actual', width: 50},
		{dataIndex: 'Handler',header: 'Handler', width: 50},
		{dataIndex: 'Partners',header: 'Partners', width: 50},
		{dataIndex: 'Notes',header: 'Notes', width: 300}
	];

	return new Ext.grid.ColumnModel({
	    columns: columns,
	    defaults: {
	        sortable: true
	    }
	});
};

var UnplannedItemFilters = new Ext.ux.grid.GridFilters({
	local: true,
	filters: [{
		type: 'numeric',
		dataIndex: 'Id'
	},{
		type: 'list',
		dataIndex: 'Status',
		options: ['new', 'closed']
	}]
});