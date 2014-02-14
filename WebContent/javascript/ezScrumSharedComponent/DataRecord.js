/**********************************************************************************************
 * This is for ezScrumSharedComponent/ModifyStoryWindow.js
 * */
var IssueTagRecord = Ext.data.Record.create([
	{name:'Id', sortType:'asInt'}, 'Name' 
]);

var IssueTagReader = new Ext.data.XmlReader({
   record: 'IssueTag',
   idPath : 'Id',
   successProperty: 'Result'
}, IssueTagRecord);
/**
 * This is for ezScrumSharedComponent/ModifyStoryWindow.js
***********************************************************************************************/


/**********************************************************************************************
 * This is for ezScrumSharedComponent/IssueHistoryWindow.js
 * */
var IssueHistoryListRecord = Ext.data.Record.create([
 	'IssueHistories', 'Description', 'HistoryType', 'ModifiedDate'
]);

var IssueHistoryListReader = new Ext.data.JsonReader({
	id: "Id",
 	root: "IssueHistories"
}, IssueHistoryListRecord);

var IssueHistoryListColumnModel = new Ext.grid.ColumnModel({
 	columns: [
 		{dataIndex: 'ModifiedDate',header: 'Modified Date', width: 100},		          
 		{dataIndex: 'HistoryType',header: 'History Type', width: 70},
 		{dataIndex: 'Description',header: 'Description', width: 300}
 	]
});

var IssueHistoryRecord = Ext.data.Record.create([
  	'Id', 'Link', 'Name', 'IssueType'
]);

var IssueHistoryReader = new Ext.data.JsonReader({
 	id: "Id"
}, IssueHistoryRecord);
/**
 * This is for ezScrumSharedComponent/IssueHistoryWindow.js
***********************************************************************************************/


/**********************************************************************************************
 * This is for ezScrumSharedComponent/ModifySprintWindow.js
 * */

var SprintPlanRecord = Ext.data.Record.create([
	{name:'Id', sortType:'asInt'}, 'Info', 'Goal', 'StartDate', 'Interval', 'Members', 
	'AvaliableDays', 'FocusFactor', 'DailyScrum', 'DemoDate', 'DemoPlace', 'Check'                                        
]);

var SprintPlanJsonReader = new Ext.data.JsonReader({
   	root: 'Sprints',
   	id: "Id"
}, SprintPlanRecord);

/**
 * This is for ezScrumSharedComponent/ModifySprintWindow.js
***********************************************************************************************/



/**********************************************************************************************
 * This is for ezScrumSharedComponent/AddExistedStoryWindow.js
 * */
var StoryRecord = Ext.data.Record.create([
	{name:'Id', sortType:'asInt'}, 'Name',{name:'Value',sortType:'asInt'}, {name:'Importance', sortType:'asInt'}, {name:'Estimate', sortType:'asFloat'}, 'Status', 'Notes', 'HowToDemo', {name:'Release', sortType:'asInt'}, {name:'Sprint', sortType:'asInt'}, 'Tag', 'Link', 'Attach', 'AttachFileList', 'FilterType'
]);


var ExistedStoryReader = new Ext.data.XmlReader({
   record: 'Story',
   idPath : 'Id',
   successProperty: 'Result',
   totalProperty: 'Total'
}, StoryRecord);

/**
* This is for ezScrumSharedComponent/AddExistedStoryWindow.js
***********************************************************************************************/