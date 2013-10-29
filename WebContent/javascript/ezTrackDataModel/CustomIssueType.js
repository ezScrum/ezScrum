var CustomIssueTypeRecord = Ext.data.Record.create([
   {name:'TypeId', sortType:'asInt'}, 'TypeName', 'IsPublic'
]);

var CustomIssueTypeReader = new Ext.data.XmlReader({
   record: 'IssueType',
   idpath: 'TypeId',
   successProperty: 'Result'
}, CustomIssueTypeRecord);

var CustomIssueTypeStore = new Ext.data.Store({
	fields : [
	    { name : 'TypeId', type : 'int'},
	    { name : 'TypeName'},
	    { name : 'IsPublic'}
	],
	reader : CustomIssueTypeReader,
	url : 'AjaxGetCustomIssueType.do',
	storeId : 'TypeId',
	autoLoad : true
});

/* the issue type column  */
var CustomIssueTypeColModel = function() {
	var columns = [{
		dataIndex : 'TypeName',
		header : 'Name',
		width : 400
	}, {
		dataIndex : 'IsPublic',
		header : 'Public',
		width : 100
	}];
	
	return new Ext.grid.ColumnModel({
		columns : columns,
		defaults : {
			sortable : true
		}
	});
};