<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<script type="text/javascript" src="javascript/CommonUtility.js"></script>
<script type="text/javascript" src="javascript/ext-base-debug.js"></script>
<script type="text/javascript" src="javascript/ext-all-debug.js"></script>

<script type="text/javascript" src="javascript/AjaxWidget/Common.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/CommonFormVTypes.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/EditStoryWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/EditTaskWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/CheckOutTaskWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/ReCheckOutTaskWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/DoneIssueWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/ReOpenIssueWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/BurndownChartWidget.js"></script>
<script type="text/javascript" src="javascript/ux/fileuploadfield/FileUploadField.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/AttachFileWidget.js"></script>



<script type="text/javascript" src="javascript/AjaxWidget/Message.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/TaskBoard/StatusPanel.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/TaskBoard/TaskBoard_DD.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/TaskBoard/TaskBoardWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/TaskBoard/StoryCard.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/TaskBoard/TaskCard.js"></script>



<link rel="stylesheet" type="text/css" href="css/Message.css"/>
<link rel="stylesheet" type="text/css" href="css/TaskBoard.css"/>
<link rel="stylesheet" type="text/css" href="javascript/ux/fileuploadfield/css/fileuploadfield.css"/>

<script type="text/javascript">
Ext.ns('ezScrum');

function show(){
	var element = document.getElementById("ShowSprint");	
	var handler = document.getElementById("Handler");
	document.location.href = "<html:rewrite action="/showTaskBoard" />?sprintID="+element.value+"&UserID="+handler.value;
}

function showHistory(issueID){
	var element = document.getElementById("ShowSprint");	
	document.location.href  = "<html:rewrite action="/showIssueHistory" />?issueID="+issueID+"&type=sprint&sprintID=${TaskBoard.sprintID}";
}

function showTaskByUser(){
	var element = document.getElementById("ShowSprint");
	var handler = document.getElementById("Handler");	
	document.location.href = "<html:rewrite action="/showTaskBoard" />?sprintID="+element.value+"&UserID="+handler.value;
}
/**
 * 傳入的參數第一個為要執行的Function，後面為他的參數， 如果使用者按下確認要繼續執行，那麼才會執行這個參數
 */
function checkIsCurrentSprint()
{
    // 將所有參數轉為真正的Array
    var args = Array.prototype.slice.call(arguments);
    var fun = args.shift();
    
    var checkCurrent = thisSprintStore.getAt(0).get('IsCurrentSprint');
    
    if(checkCurrent)
    {
       fun.apply(this,args);
    }
    else
    {
       Ext.MessageBox.confirm("Warning!",'此Sprint並非目前正在進行中的Sprint!',function(btn)
       {
        // 如果使用者按下Yes才會繼續執行動作
            if(btn == 'yes')
            {
              fun.apply(this,args);
            }
       });
    }
}

// show edit story
function editStory(id) {
	var sprintValue = document.getElementById("CurrentSprintID").value;
	var element = sprintComboStore.find('Name', sprintValue) + 1;
	var handler = document.getElementById("AssignedTo");

	var editStoryWidget = new ezScrum.EditStoryWidget({
		listeners:{
			LoadSuccess:function(win, form, response, record) {
				// Ext.example.msg('Load Story', 'Load Story Success');
			},
			LoadFailure:function(win, form, response, issueId) {
				this.hide();
				Ext.MessageBox.confirm('Load Story', 'Load Story Failure');
			},
			EditSuccess:function(win, form, response, record) {
				this.hide();
				Ext.example.msg('Edit Story', 'Edit Story Success');
				document.location.href = "<html:rewrite action="/showTaskBoard" />?sprintID="+element+"&UserID="+handler.value;
			},
			EditFailure:function(win, form, response, issueId) {
				this.hide();
				Ext.MessageBox.confirm('Edit Story', 'Edit Story Failure');
			}
		}
	});
	editStoryWidget.loadEditStory(id);
}

// show edit task
function editTask(id) {
	var sprintValue = document.getElementById("CurrentSprintID").value;
	var element = sprintComboStore.find('Name', sprintValue) + 1;
	var handler = document.getElementById("AssignedTo");

	var editTaskWidget = new ezScrum.EditTaskWidget({
		listeners:{
			LoadSuccess:function(win, form, response, record){
				// Ext.example.msg('Load Task', 'Load Task Success');
			},
			LoadFailure:function(win, form, response, issueId){
				this.hide();
				Ext.MessageBox.confirm('Load Task', 'Load Task Failure');
			},
			EditSuccess:function(win, form, response, record){              
				this.hide();
				Ext.example.msg('Edit Task', 'Edit Task Success');
				document.location.href = "<html:rewrite action="/showTaskBoard" />?sprintID="+element+"&UserID="+handler.value;
			},
			EditFailure:function(win, form, response, issueId){
				this.hide();
				Ext.MessageBox.confirm('Edit Task', 'Edit Task Failure');
			}
		}
	});
	editTaskWidget.loadEditTask(element,id);
}

// show check out task
function showCheckOutIssue(id,card) {
	var sprintValue = document.getElementById("CurrentSprintID").value;
	var element = sprintComboStore.find('Name', sprintValue) + 1;
	var handler = document.getElementById("AssignedTo");

	var CheckOutTaskWindow = new ezScrum.CheckOutWidget({
		storyCard:card,
		listeners:{
			LoadFailure: function(win, response) {
				Ext.MessageBox.confirm('Load Failure', 'Sorry, Load Failure');
				this.hide();
			},
			CheckOutSuccess: function(win, response) {
				this.hide();
				Ext.example.msg('Check Out Task', 'Check Out Success');
				this.storyCard.moveToTarget();
				// get new Handlr
                this.storyCard.updateData(win.getFormValues());
			},
			CheckOutFailure: function(win, response) {
				this.hide();
				Ext.MessageBox.confirm('Check Out Failure', 'Sorry, Check Out Failure');
			}
		}
	});
	CheckOutTaskWindow.showWidget(id);
}

// show Reset check out Task
function showReCheckOutTask(id,card) {
	var sprintValue = document.getElementById("CurrentSprintID").value;
	var element = sprintComboStore.find('Name', sprintValue) + 1;
	var handler = document.getElementById("AssignedTo");

	var RE_CheckOutTaskWindow = new ezScrum.ReCheckOutWidget({
		storyCard:card,
		listeners:{
			LoadFailure: function(win, response) {
				Ext.MessageBox.confirm('Load Failure', 'Sorry, Load Failure');
				this.hide();
			},
			RECheckOutSuccess: function(win, response) {
				this.hide();
				Ext.example.msg('Reset Task', 'Reset Check Out Success');
				this.storyCard.moveToTarget();
			},
			RECheckOutFailure: function(win, response) {
				this.hide();
				Ext.MessageBox.confirm('Check Out Failure', 'Sorry, Check Out Failure');
			}
		}
	});
	RE_CheckOutTaskWindow.showWidget(id);
    
}

// show done task
function showDoneIssue(id,card) {
	var sprintValue = document.getElementById("CurrentSprintID").value;
	var element = sprintComboStore.find('Name', sprintValue) + 1;
	var handler = document.getElementById("AssignedTo");

	var DoneIssueWindow = new ezScrum.DoneIssueWidget({
		storyCard:card,
		listeners:{
			LoadFailure: function(win, response) {
				Ext.MessageBox.confirm('Load Failure', 'Sorry, Load Failure');
				this.hide();
			},
			DoneSuccess: function(win, response) {
				this.hide();
				this.storyCard.moveToTarget();
				Ext.example.msg('Done Issue', 'Done Issue Success');
			},
			DoneFailure: function(win, response) {
				this.hide();
				Ext.MessageBox.confirm('Done Issue Failure', 'Sorry, Done Issue Failure');
			}
		}
	});
	DoneIssueWindow.showWidget(id);
}

// show reopen issue
function showReOpenIssue(id,card) {
	var sprintValue = document.getElementById("CurrentSprintID").value;
	var element = sprintComboStore.find('Name', sprintValue) + 1;
	var handler = document.getElementById("AssignedTo");

	var RE_OpenIssueWindow = new ezScrum.ReOpenIssueWidget({
		storyCard:card,
		listeners:{
			LoadFailure: function(win, response) {
				Ext.MessageBox.confirm('Load Failure', 'Sorry, Load Failure');
				this.hide();
			},
			ReOpenSuccess: function(win, response) {
				this.hide();
				this.storyCard.moveToTarget();
				Ext.example.msg('Re Open Issue', 'Re Open Issue Success');
			},
			ReOpenFailure: function(win, response) {
				this.hide();
				Ext.MessageBox.confirm('Re Open Issue Failure', 'Sorry, Re Open Issue Failure');
			}
		}
	});
	RE_OpenIssueWindow.showWidget(id);
}


// attach file
function attachFile(issueID) {
	var sprintValue = document.getElementById("CurrentSprintID").value;
	var element = sprintComboStore.find('Name', sprintValue) + 1;
	var handler = document.getElementById("AssignedTo");
	
	var attachFileWidget = new ezScrum.AttachFileWidget({
		listeners: {
			AttachSuccess: function(win, form, response, record) {
				this.hide();
				Ext.example.msg('Attach File', 'Attach File Success.');
				document.location.href = "<html:rewrite action="/showTaskBoard" />?sprintID="+element+"&UserID="+handler.value;
			},
			AttachFailure: function(win, form, response, msg) {
				Ext.MessageBox.confirm('Attach File Failure', msg);
			}
		}
	});
	attachFileWidget.attachFile(issueID);
}

// delete file
function deleteAttachFile(file_Id, issue_Id) {
	var sprintValue = document.getElementById("CurrentSprintID").value;
	var element = sprintComboStore.find('Name', sprintValue) + 1;
	var handler = document.getElementById("AssignedTo");
	
	Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete this attached file?', function(btn){
		if(btn === 'yes') {
			Ext.Ajax.request({
				url : 'ajaxDeleteFile.do',
				success : function(response) {
					ConfirmWidget.loadData(response);
					if (ConfirmWidget.confirmAction()) {
						Ext.example.msg('Delete File', 'Delete File Success.');
						document.location.href = "<html:rewrite action="/showTaskBoard" />?sprintID="+element+"&UserID="+handler.value;
					}
				},
				failure : function(response) {
					Ext.example.msg('Delete File', 'Delete File Failure.');
				},
				params : {fileId:file_Id, issueId:issue_Id}
			});
		}
	});
}

function setSprintFormInfo(sID, uID) {
	Ext.Ajax.request({
		url: 'showSprintInfobyCombo.do',
		params: {sprintID: sID},
		success: function(response) {
			SprintInfoForm.loadStore(response);
			
			// set sprint ID combo
			if (sID == '' || sID == 'ALL' || sID == undefined) {
				// set default sprint
				sID = thisSprintStore.getAt(0).get('Id');
			}
			
			// set sprint combo box
			setSprintCombo(sID);
			
			// set Assigned To user combo
			setAssignToCombo(uID);
		}
	});
}

function setSprintCombo(sprintID) {
	Ext.Ajax.request({
		url: 'getAddNewRetrospectiveInfo.do',
		async: false,
		success: function(response) {
			sprintComboStore.loadData(response.responseXML);
			
			comboInGrid.originalValue = sprintID;
			var record = comboInGrid.getStore().getById(sprintID);
			comboInGrid.selectedIndex = comboInGrid.getStore().indexOf(record);
			comboInGrid.reset();
		}
	});
}

function setAssignToCombo(userID) {
	Ext.Ajax.request({
		url: 'getAssignedToInfo.do',
		success: function(response) {
			hanlderComboStore.loadData(Ext.decode(response.responseText));

			if (userID == '' || userID == 'ALL' || userID == undefined) {
				userID = "ALL";
			}
			
			AssignToInGrid.originalValue = userID;
			var record = AssignToInGrid.getStore().getById(userID);
			AssignToInGrid.selectedIndex = comboInGrid.getStore().indexOf(record);
			AssignToInGrid.reset();
		}
	});
}

function getParameter( queryString, parameterName ) {
	// Add "=" to the parameter name (i.e. parameterName=value)
	var parameterName = parameterName + "=";
	
	if ( queryString.length > 0 ) {
		// Find the beginning of the string
		begin = queryString.indexOf ( parameterName );
		// If the parameter name is not found, skip it, otherwise return the
		// value
		if ( begin != -1 ) {
		// Add the length (integer) to the beginning
			begin += parameterName.length;
			// Multiple parameters are separated by the "&" sign
			end = queryString.indexOf ( "&" , begin );
			if ( end == -1 ) {
				end = queryString.length
			}
			// Return the string
			return unescape ( queryString.substring ( begin, end ) );
		}
		// Return "null" if no parameter has been found
		return "";
	}
}


function initStatusTable(sID, handler)
{
	Ext.Ajax.request({
		url : 'getTaskBoardStoryTaskList.do',
		params : {
			sprintID : sID,
			UserID	 : handler
		},
		async : false,
		success : function(response) {
			storiesStore.loadData(Ext
					.decode(response.responseText));
			initialTaskBoard();
		}
	});
}


Ext.onReady(function() {
	Ext.QuickTips.init();
	
	var queryString = window.location.toString();
	var sprintID = getParameter(queryString, "sprintID");
	var assignto = getParameter(queryString, "UserID");
	
	// set title form information
	setSprintFormInfo(sprintID, assignto);
	
	// set sprint info form to render it
	SprintInfoForm.render('SprintInfo_content');
	
	// load Story Burndown Chart Data
	Ext.Ajax.request({
		url:'getSprintBurndownChartData.do?SprintID=' + sprintID + '&Type=story',
		async: false,
		success: function(response) {
			if (response.responseText != "")
			{
				StoryPointsStore.loadData(Ext.decode(response.responseText));
				StoryBurndownChart.render('Story_Burndown_Chart');
			}
		},
		failure: function() {
			alert('Server Failure');
		}
	});
	
	// load Task Burndown Chart Data
	Ext.Ajax.request({
		url:'getSprintBurndownChartData.do?SprintID=' + sprintID + '&Type=task',
		async: false,
		success: function(response) {
			if (response.responseText != "")
			{
				TaskPointsStore.loadData(Ext.decode(response.responseText));
				TaskBurndownChart.render('Task_Burndown_Chart');
			}
		},
		failure: function() {
			alert('Server Failure');
		}
	});

	// set StatusTable
	initStatusTable(sprintID, assignto);
	taskboard_Table.render('TaskBoard_DD');
});

</script>

<div id="SprintInfo_content"></div>
<div id="TaskBoard_Report_Image"></div>
<table width="95%" border="0" cellspacing="0" cellpadding="10">
	<tr>
		<td align="center" >
			<div id="Story_Burndown_Chart"></div>
		</td>
		<td align="center" >
			<div id="Task_Burndown_Chart"></div>
		</td>
	</tr>
</table>
<div id="TaskBoard_DD"></div>
<!-- -->
<% session.setAttribute("currentSideItem","showTaskBoard");%>
<div id="SideShowItem" style="display:none;">showTaskBoard</div>
<!--  -->

