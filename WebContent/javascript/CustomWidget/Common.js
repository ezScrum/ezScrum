/* defined Custom Issue Category data */
var CustomIssueCategory = Ext.data.Record.create([
   {name:'TypeId', sortType:'asInt'}, 'TypeName', 'IsPublic'
]);

/* custom issue category XML Parser */
var customIssueCategoryReader = new Ext.data.XmlReader({
   record: 'IssueType',
   idpath: 'TypeId',
   successProperty: 'Result'
}, CustomIssueCategory);


/* defined Scrum Issue data */
var scrumIssue = Ext.data.Record.create([
   {name:'Id', sortType:'asInt'}, 'Name', 'Category', {name:'Estimate', sortType:'asFloat'}, 'Status', 'Notes', {name:'Sprint', sortType:'asInt'}, 'Link', 'Attach', 'AttachFileList'
]);
/* Scrum Issue reader*/
var jsonScrumIssueReader = new Ext.data.JsonReader({
   root: 'ScrumIssues',
   idProperty : 'Id',
   id : 'Id',
   totalProperty: 'Total'
}, scrumIssue);


/* defined Custom Issue data */
var customIssue = Ext.data.Record.create([
   {name:'Id', sortType:'asInt'}, 'ProjectName', 'Name', 'Category', 'Priority', 'Status', 'Handled', 'Comment','ReportUserName', 'Email', 'Handler', 'Description', 'Link', 'Attach', 'AttachFileList'
]);
/* Custom Issue reader*/
var jsonCustomIssueReader = new Ext.data.JsonReader({
   root: 'CustomIssues',
   idProperty : 'Id',
   id : 'Id',
   totalProperty: 'Total'
}, customIssue);

var Common = Ext.data.Record.create([
	'Name'
]);

/* Priority XML Parser */
var priorityReader = new Ext.data.XmlReader({
	record:'Priority',
	idPath:'Name',
	successProperty: 'Result'	
}, Common);

/* Status XML Parser */
var statusReader = new Ext.data.XmlReader({
	record:'Status',
	idPath:'Name',
	successProperty: 'Result'	
}, Common);

/* Handler XML Parser */
var handlerReader = new Ext.data.XmlReader({
	record:'Handler',
	idPath:'Name',
	successProperty: 'Result'	
}, Common);


var Relationship = Ext.data.Record.create([
   'IssueID', 'RelationID', 'RelationType', 'RelationissueName'
]);

/* Relationship XML Parser */
var relationshipReader = new Ext.data.XmlReader({
	record:'Relationship',
	successProperty: 'Result'	
}, Relationship);

var AttachFile = Ext.data.Record.create([
   'FileID', 'IssueID', 'Title', 'Description', 'Diskfile', 'Filename', 'Folder', 'Filesize', 'Filetype', 'Date'
]);

/* attachfile XML Parser */
var attachFileReader = new Ext.data.XmlReader({
	record:'AttachFile',
	successProperty: 'Result'	
}, AttachFile);


var History = Ext.data.Record.create([
   'Date', 'Field', 'Description'
]);

/* attachfile XML Parser */
var historyReader = new Ext.data.XmlReader({
	record:'History',
	successProperty: 'Result'	
}, History);


/*
var Permission = Ext.data.Record.create([
   'AddWorkItem', 'EditWorkItem', 'ImportWorkItem', 'DeleteWorkItem', 'ShowWorkItemHistory', 'TagWorkItem'
]);
*/

var Permission = Ext.data.Record.create([
   'AddStory', 'EditStory', 'ImportStory', 'DeleteStory', 'ShowStoryHistory', 'TagStory'
]);

var permissionReader = new Ext.data.XmlReader({
   record: 'Function'
}, Permission);

/* project Reader */
var ProjectReader = new Ext.data.XmlReader({
	record			: 'Projects',
	idPath			: 'Name',
	successProperty	: 'Result'	
}, Common);
