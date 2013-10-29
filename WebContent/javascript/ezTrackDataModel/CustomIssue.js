// Expander
var CustomIssueExpander = new Ext.ux.grid.RowExpander({
    tpl : new Ext.XTemplate(
        '<br><p><b>Name:</b><br /> {Name:nl2br}</p>',
        '<tpl if="Description"><p><b>Description:</b><br /> {Description:nl2br}</p></tpl>',
        '<tpl for="AttachFileList"><p><b>Attach Files:</b><br /><a href="{DownloadPath}" target="_blank">{FileName}</a> <tpl if="this.hasPermission()">[<a href="#" onclick="Ext.getCmp(\'customIssueMasterPanel\').deleteAttachFile({FileId}, {IssueId}); return false;">Delete</a>]</tpl><br /></tpl>',
        '<tpl if="Comment"><p><b>Comment:</b><br /> {Comment:nl2br}</p></tpl>',
        '<br />',{
        hasPermission:function(){
        	return true;
        }}
    ),
    enableCaching :false
});

//the model of column
var CustomCreateColModel = function () {
    var columns = [CustomIssueExpander,
	            {dataIndex: 'Id',header: 'Id', width: 30,filterable: true,renderer: function(value, metaData, record, rowIndex, colIndex, store){var link = "<a href=\"" + record.data['Link'] + "\" target=\"_blank\">" + value + "</a>"; return link;}},
	            {dataIndex: 'Handled',header: 'Handled'},
	            {dataIndex: 'Name',header: 'Name', width: 300,renderer: function(value, metaData, record, rowIndex, colIndex, store){if(record.data['Attach'] == 'true') return "<image src = \"./images/paperclip.png\" />" + value; return value}},
	            {dataIndex: 'Category',header: 'Category', width: 70},
	            {dataIndex: 'Status',header: 'Status', width: 70},
	            {dataIndex: 'Priority',header: 'Priority', width: 70}
	        ];

    return new Ext.grid.ColumnModel({
        columns: columns,
        defaults: {
            sortable: true
        }
    });
};

//Data store
var CustomStore = new Ext.data.Store({
	fields:[
		{name : 'Id', type:'int'},
		{name : 'Link'},
		{name : 'Handled'},
		{name : 'Name'},
		{name : 'Status'},
		{name : 'Priority'},
		{name : 'Handler'},
		{name : 'Description'},
		{name : 'Attach'},
		{name : 'Category'},
		{name : 'Comment'}
	],
	reader: jsonCustomIssueReader,
	url:'showCustomIssueAction.do',
	proxy: new Ext.ux.data.PagingMemoryProxy(null,IssueGridPanelFilter),
	remoteSort : true,
	//預設ID由大排到小
	sortInfo: {
        field: 'Id',
        direction: 'DESC'
	}
});

// create the Data Store
var CustomSprintStore = new Ext.data.Store({
	fields:[
		{name : 'Id', sortType:'asInt'},
		{name : 'Name'}
	],
	reader: SprintReader
});


