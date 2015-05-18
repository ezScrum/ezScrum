/* 定義 Sptint 資料欄位 */
/* Sprint Reader */
/*
 *	Example Data
 *
 *	<Root>
 *		<Result>Success</Result>
 *		<Sprint>
 *			<Id>2388</Id>
 *			<Goal>利用開發新功能以達成交接的目的</Goal>
 *			<StartDate>2008/07/02</StartDate>
 *			<Interval>2 week(s)</Interval>
 *			<Members>2 person(s)</Members>
 *			<AvaliableDays>20 days</AvaliableDays>
 *			<FocusFactor>70 %</FocusFactor>
 *			<DailyScrum>
 *				科研館1321
 * 				am 9:30   	
 *			</DailyScrum>
 *			<DemoDate>2008/07/14</DemoDate>
 *			<DemoPlace>科研館1321</DemoPlace>
 *		</Sprint>
 *		<Sprint>
 *			...
 *		</Sprint>
 *	</Root>
 */
var Sprint = Ext.data.Record.create([
   {name:'Id', sortType:'asInt'}, 'Goal', 'StartDate', 'Interval', 
   'Members', 'AvaliableDays', 'FocusFactor',
   'DailyScrum', 'DemoDate', 'DemoPlace', 'Check'
]);
var SprintReader = new Ext.data.XmlReader({
	   record: 'Sprint',
	   idPath : 'Id',
	   successProperty: 'Result',
	   totalProperty:"StoryPoint"
	}, Sprint);

/* 定義 Story 資料欄位 */
var Story = Ext.data.Record.create([
   {name:'Id', sortType:'asInt'}, 'Name',{name:'Value',sortType:'asInt'}, {name:'Importance', sortType:'asInt'}, {name:'Estimate', sortType:'asFloat'}, 'Status', 'Notes', 'HowToDemo', {name:'Release', sortType:'asInt'}, {name:'Sprint', sortType:'asInt'}, 'Tag', 'Link', 'Attach', 'AttachFileList', 'FilterType'
]);

/* 定義 Task 資料欄位 */
var Task = Ext.data.Record.create([
   {name:'Id', sortType:'asInt'}, 'Link', 'Name', 'Status', {name:'Estimate', sortType:'asFloat'}, 'Actual', 'Handler', 'Partners', 'Notes', 'Actors', 'Remains'
]);

/* 定義 Issue Tag 資料欄位 */
var IssueTag = Ext.data.Record.create([
   {name:'Id', sortType:'asInt'}, 'Name'
]);

var Permission = Ext.data.Record.create([
   'AddStory', 'EditStory', 'ImportStory', 'DeleteStory', 'ShowStoryHistory', 'TagStory'
]);

/* Story Reader */
/*
 *	Example Data
 *
 *	<Root>
 *		<Result>Success</Result>
 *		<Story>
 *			<Id>2388</Id>
 *			<Link>http://140.124.181.123/mantis/view.php?id=2388</Link>
 *			<Name>As a user, I can create product backlog</Name>
 *			<Importance>80</Importance>
 *			<Estimate>1</Estimate>
 *			<Status>new</Status>
 *			<Notes>
 *				1. Name 不可為空
 *				2. Importance 必輸為數字
 *			</Notes>
 *			<HowToDemo>
 *				1. 選擇新增 Product Backlog
 *				2. 輸入資料並新增
 *				3. 顯示新增後的資料
 *			</HowToDemo>
 *			<Release>1</Release>
 *			<Sprint>1</Sprint>
 *			<Tag>Req</Tag>
 *		</Story>
 *		<Story>
 *			...
 *		</Sotry>
 *	</Root>
 */
var myReader = new Ext.data.XmlReader({
   record: 'Story',
   idPath : 'Id',
   successProperty: 'Result',
   totalProperty: 'Total'
}, Story);

var jsonStoryReader = new Ext.data.JsonReader({
   root: 'Stories',
   idProperty : 'Id',
   id : 'Id',
   totalProperty: 'Total'
}, Story);

/* Task XML Parser */
var taskReader = new Ext.data.XmlReader({
   record: 'Task',
   idPath : 'Id'
}, Task);

/* Task Jason Parser */
var taskJSReader = new Ext.data.JsonReader({
   root: 'Task',
   id : 'Id',
   totalProperty: 'Total'
}, Task);

/* Story Jason Parser */
var storyJSReader = new Ext.data.JsonReader({
   root: 'Story',
   id : 'Id',
   totalProperty: 'Total'
}, Story);

/* Issue Jason Parser, include Story and Task */
var jsonIssueReader = new Ext.data.JsonReader({
   root: 'Issue',
   id : 'Id'
}, Task);

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

var permissionReader = new Ext.data.XmlReader({
   record: 'Function'
}, Permission);

/* Retrospective */
/* 定義 Retrospective 資料欄位 */
var Retrospective = Ext.data.Record.create([
   {name:'Id', sortType:'asInt'}, 'Link', 'SprintID', 'Name', 'Type', 'Description', 'Status'
]);

/* 定義 Retrospective Permission 欄位 */
var retPermission = Ext.data.Record.create([
   'AddRetrospective', 'EditRetrospective', 'DeleteRetrospective'
]);

/* Retrospective XML Parser */
var retReader = new Ext.data.XmlReader({
   record: 'Retrospective',
   successProperty: 'Result'
}, Retrospective);

/* Retrospective Permission XML Parser */
var retPermissionReader = new Ext.data.XmlReader({
   record: 'Function'
}, retPermission);

/* 定義 Sprint 資料欄位 (for ComboBox) */
var SprintForCombo = Ext.data.Record.create([
	{name:'Id', sortType:'asInt'}, 'Name', 'InitialPoint', 'CurrentPoint', 'InitialHours', 'CurrentHours', 'LimitedPoint', 'TaskPoint' ,'Start' , 'Goal', 'ReleaseID', 'SprintGoal', 'Edit', 'StoryChartUrl', 'TaskChartUrl','IsCurrentSprint'
]);

/* Sprint XML Parser */
var sprintForComboReader = new Ext.data.XmlReader({
   record: 'Sprint',
   idPath : 'Id'
}, SprintForCombo);

/* Sprint Json Parser */
var jsonSprintReader = new Ext.data.JsonReader({
   root: 'Sprint',
   idProperty : 'Id'
}, SprintForCombo);

/* Sprint XML Parser */
var sprintForDropReader = new Ext.data.XmlReader({
   record: 'Sprint',
   successProperty: 'Result'
}, Sprint);

var Handler = Ext.data.Record.create([
	'Name'
]);

var handlerReader = new Ext.data.XmlReader({
	record:'Handler',
	idPath:'Name',
	successProperty: 'Result'	
}, Handler);

var ActorReader = new Ext.data.XmlReader({
	record:'Actors',
	idPath: 'Name',
	successProperty: 'Result'	
}, Handler);

var ActorJSReader = new Ext.data.JsonReader({
	root: 'Actors',
	idProperty : 'Id',
	id : 'Id',
	totalProperty: 'Total'
}, Handler);

/* 定義 Unplanned Item Permission 欄位 */
var UIPermission = Ext.data.Record.create([
   'AddUnplannedItem', 'EditUnplannedItem', 'DeleteUnplannedItem'
]);

/* Unplanned Item Permission XML Parser */
var UIPermissionReader = new Ext.data.XmlReader({
   record: 'Function'
}, UIPermission);

/* 定義 新增releaseID 欄位 */
var newReleaseID = Ext.data.Record.create([
   'ID'
]);

/* add new release id XML Parser */
var newReleaseIDReader = new Ext.data.XmlReader({
   record: 'Release'
}, newReleaseID);

/* 定義 ReleasePlan Permission 欄位 */
var RPPermission = Ext.data.Record.create([
   'AddRelease', 'EditRelease', 'DeleteRelease',
   'DropSprint', 'DropStory','AddStory','AddSprint',
   'ShowReleaseBacklog','ShowPrintableRelease'
]);

/* ReleasePlan Permission XML Parser */
var RPPermissionReader = new Ext.data.XmlReader({
   record: 'Function'
}, RPPermission);

/* 定義 SprintBacklog Permission 欄位 */
var spbPermission = Ext.data.Record.create([
   'AddStory', 'EditStory', 'DropStory', 'ShowStory', 'ShowSprintInfo', 'ShowPrintableStories', 'AddTask', 'EditTask', 'DropTask', 'ShowTask'
]);

/* SprintBacklog Permission XML Parser */
var spbPermissionReader = new Ext.data.XmlReader({
   record: 'Function'
}, spbPermission);

/* 定義 Account 資料欄位 */
var Account = Ext.data.Record.create([
 	  'ID', 'Name', 'Mail', 'Roles', 'Enable'
]);
	
var accountReader = new Ext.data.XmlReader({
	record: 'Account',
	idPath : 'ID'
}, Account);


// ====================== check action access permission ================================
var PermissionActionData = Ext.data.Record.create(['ActionCheck']);

var PermissionActionReader = new Ext.data.JsonReader({
	root: 'PermissionAction',
	idProperty: 'Id'
}, PermissionActionData);

var PermissionActionStore = new Ext.data.Store({
	id: 0,
	idIndex: 0,
	fields: [{name:'ActionCheck'}],
	reader: PermissionActionReader
});	

var ConfirmWidget = {
	loadSuccess: false,
	loadData: function(response) {
		// toSource() can not support Google Chrome.
		if (response.toString().match("PermissionAction")) {
			ConfirmWidget.loadSuccess = true;
			PermissionActionStore.loadData(Ext.decode(response.responseText));
		}
	},
	confirmAction: function() {
		if ( ! ConfirmWidget.loadSuccess) {
			return true;
		}
		
		if ( PermissionActionStore != null ) {
			var record = PermissionActionStore.getAt(0);
			
			if (record != null) {
				var result = eval(record.get('ActionCheck'));
				
				if (result) {
					return true;
				}
			}
		}

		Ext.example.msg('Permission Error', 'Sorry, you have no permission to do.');
		return false;
	}		
}
//====================== check action access permission ================================


function wait_msecond(millis) {
	var date = new Date();
	var curDate = null;
	
	do { curDate = new Date(); }
	while(curDate-date < millis);
} 