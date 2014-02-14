// not refactor yet
var storyStore = new Ext.data.Store({
	fields : [ {
		name : 'Id',
		type : 'int'
	}, {
		name : 'Tag'
	}, {
		name : 'Link'
	}, {
		name : 'Name'
	}, {
		name : 'Value',
		type : 'int'
	}, {
		name : 'Estimate',
		type : 'float'
	}, {
		name : 'Status'
	}, {
		name : 'Importance',
		type : 'int'
	}, {
		name : 'Release'
	}, {
		name : 'Sprint'
	}, {
		name : 'Notes'
	}, {
		name : 'HowToDemo'
	} ],
	reader : myReader
});

var createStoryCloumns = function() {
	var columns = [
			{ dataIndex : 'Id',	header : 'ID', width : 50/*,renderer : makeIssueDetailUrl*/ }, 	//	story URL 顯示於 sprint plan pag
			{ dataIndex : 'Tag', header : 'Tag', width : 90}, 
			{ dataIndex : 'Name', header : 'Story Name', width : 400},
			{ dataIndex : 'Value', header : 'Value', width : 90}, 
			{ dataIndex : 'Estimate', header : 'Estimate', width : 90},
			{ dataIndex : 'Importance', header : 'Importance', width : 90},
			{ dataIndex : 'Status', header : 'Status', width : 90},
			{ dataIndex : 'Release' ,header : 'Release', hidden : true,width : 90}
	];

	return new Ext.grid.ColumnModel({
		columns : columns,
		defaults : {
			sortable : true
		}
	});
};