/* 定義 WorkItem 資料欄位 */
var WorkItem = Ext.data.Record.create([
   {name:'Id', sortType:'asInt'}, 'Name', 'Type', 'Status', 'Priority', 
   {name:'Size', sortType:'asInt'}, 'Handler', 'WorkState', 'Deadline',
   'Description', 'Tag', 'Link', 'Attach', 'AttachFileList'
]);
/* 定義 Status 資料欄位 */
var Status = Ext.data.Record.create([
	{name:'Id', sortType:'asInt'}, 'Name', 'Description', 'Limit'
]);
/* 定義 Type + Status + Priority + Handler 資料欄位 */
var Common = Ext.data.Record.create([
	'Name'
]);
/* 定義 WorkItem TypeID 資料欄位 */
var TypeID = Ext.data.Record.create([
	{name:'Id', sortType:'asInt'}
]);

/* WorkItem XML Parser */
var workItemReader = new Ext.data.XmlReader({
	record: 'WorkItem',
	idPath : 'Id',
	successProperty: 'Result'
}, WorkItem);

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

/* Type XML Parser */
var typeReader = new Ext.data.XmlReader({
	record:'Type',
	idPath:'Name',
	successProperty: 'Result'	
}, Common);

/* WorkState XML Parser */
var workstateReader = new Ext.data.XmlReader({
	record:'WorkState',
	idPath:'Name',
	successProperty: 'Result'	
}, Common);

/* 定義 Issue Tag 資料欄位 */
var IssueTag = Ext.data.Record.create([
   {name:'Id', sortType:'asInt'}, 'Name'
]);

/* IssueType WorkItem Parser */
var jsonWorkItemReader = new Ext.data.JsonReader({
   root: 'WorkItems',
   idProperty : 'Id',
   id : 'Id',
   totalProperty: 'Total'
}, WorkItem);

/* IssueType Status Parser */
var jsonStatusReader = new Ext.data.JsonReader({
   root: 'Statuses',
   idProperty : 'Id',
   id : 'Id',
   totalProperty: 'Total'
}, Status);

/* IssueType Json Parser */
var jsonIssueTypeReader = new Ext.data.JsonReader({
   root: 'IssueType',
   idProperty : 'Id'
}, TypeID);

/* Kanban Backlog */
var KBGPermission = Ext.data.Record.create([
   'AddWorkItem', 'EditWorkItem', 'DeleteWorkItem', 'CreateRelation', 'DropRelation', 'ImportWorkItem'
]);

var KBGPermissionReader = new Ext.data.XmlReader({
   record: 'Function'
}, KBGPermission);

/* Kanban Board */
var KBDPermission = Ext.data.Record.create([
   'AddWorkItem', 'EditWorkItem', 'DeleteWorkItem'
]);

var KBDPermissionReader = new Ext.data.XmlReader({
   record: 'Function'
}, KBDPermission);

/* Manage Status */
var MASPermission = Ext.data.Record.create([
   'AddStatus', 'EditStatus', 'DeleteStatus', 'SaveStatus'
]);

var MASPermissionReader = new Ext.data.XmlReader({
   record: 'Function'
}, MASPermission);


 /* Tag Reader */
/*
 *	Example Data
 *
 *	<Root>
 *		<Result>Success</Result>
 *		<IssueTag>
 *			<Id>0</Id>
 *			<Name>Req</Name>
 *		</IssueTag>
 *		<IssueTag>
 *			<Id>1</Id>
 *			<Name>Bug</Name>
 *		</IssueTag>
 *		<IssueTag>
 *			...
 *		</IssueTag>
 *	</Root>
 */
var tagReader = new Ext.data.XmlReader({
   record: 'IssueTag',
   idPath : 'Id',
   successProperty: 'Result'
}, IssueTag);

/* Panel Header 顏色 */
var panelColors = new Array();
panelColors[0] = 'background-color: LightGrey; background-repeat: no-repeat;';
panelColors[1] = 'background-color: Yellow; background-repeat: no-repeat;';
panelColors[2] = 'background-color: LawnGreen; background-repeat: no-repeat;';
panelColors[3] = 'background-color: Violet; background-repeat: no-repeat;';
panelColors[4] = 'background-color: Tomato; background-repeat: no-repeat;';
panelColors[5] = 'background-color: Thistle; background-repeat: no-repeat;';
panelColors[6] = 'background-color: Orange; background-repeat: no-repeat;';
var storyPanelStyle = 'background-color: Wheat; background-repeat: no-repeat; cursor:move';
