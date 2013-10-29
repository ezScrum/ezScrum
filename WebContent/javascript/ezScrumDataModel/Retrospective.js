var RetrospectiveRecord = Ext.data.Record.create([
	{name:'Id', sortType:'asInt'}, 'Link', 'SprintID', 'Name', 'Type', 'Description', 'Status'
]);
	
var RetrospectiveReader = new Ext.data.XmlReader({
	record: 'Retrospective',
	successProperty: 'Result'
}, RetrospectiveRecord);

var RetrospectiveStore = new Ext.data.GroupingStore({
	fields:[
		{name : 'Id', type:'int'},
		{name : 'Link'},
		{name : 'SprintID'},
		{name : 'Name'},
		{name : 'Type'},
		{name : 'Description'},
		{name : 'Status'}
	],
	reader: RetrospectiveReader,
	sortInfo: {field: 'Id', direction: "ASC"},
	groupField: 'Type'
});

var RetrospectiveColumnModel = function () {
    var columns = [
        {dataIndex: 'Id', header:'Id', width: 30, filterable: true/*, renderer: makeIssueDetailUrl*/},
        {dataIndex: 'SprintID',header: 'SprintID', width: 30},
        {dataIndex: 'Name',header: 'Name', width: 200},
        {dataIndex: 'Type',hidden: true, header: 'Type', width: 30},
        {dataIndex: 'Description',header: 'Description', width: 200},
        {dataIndex: 'Status',header: 'Status', width: 30}
    ];

    return new Ext.grid.ColumnModel({
        columns: columns,
        defaults: {
            sortable: true
        }
    });
};

var RetrospectiveFilters = new Ext.ux.grid.GridFilters({
	local: true,
    filters: [{
        type: 'numeric',
        dataIndex: 'Id'
    }]
});