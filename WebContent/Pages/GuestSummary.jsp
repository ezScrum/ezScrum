<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<link rel="stylesheet" type="text/css" href="css/ext/css/ext-all.css" />

<script type="text/javascript" src="javascript/CommonUtility.js"></script>
<script type="text/javascript" src="javascript/ext-base-debug.js"></script>
<script type="text/javascript" src="javascript/ext-all-debug.js"></script>

<script type="text/javascript" src="javascript/AjaxWidget/Common.js"></script>

<script type="text/javascript" src="javascript/AjaxWidget/TaskBoard/StatusPanel.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/TaskBoard/TaskBoard_DD.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/TaskBoard/TaskBoardWidget_G.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/TaskBoard/StoryCard_G.js"></script>

<link rel="stylesheet" type="text/css" href="css/Message.css"/>
<link rel="stylesheet" type="text/css" href="css/TaskBoard.css"/>

<script type="text/javascript">
Ext.ns('ezScrum');

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
			
			//若sprintComboStore沒資料才load
			if(sprintComboStore.getAt(0) == null){
				// set sprint combo box
				setSprintCombo(sID);
			}else{
				updateSprintCombo(sID);
			}
		}
	});
}

function setSprintCombo(sprintID) {
	Ext.Ajax.request({
		url: 'getPastRetrospectiveInfo.do',
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

function updateSprintCombo(sprintID){
	comboInGrid.originalValue = sprintID;
	var record = comboInGrid.getStore().getById(sprintID);
	comboInGrid.selectedIndex = comboInGrid.getStore().indexOf(record);
	comboInGrid.reset();
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


function initStatusTable(sID)
{
	Ext.Ajax.request({
		url : 'getTaskBoardStoryTaskList2.do',
		params : {
			sprintID : sID,
			UserID	 : 'ALL'
		},
		async : false,
		success : function(response) {
			storiesStore.loadData(Ext.decode(response.responseText));			
			initialTaskBoard();
		}
	});
}


Ext.onReady(function() {
	var queryString = window.location.toString();
	var sprintID = getParameter(queryString, "sprintID");
	var assignto = getParameter(queryString, "UserID");
	
	// set title form information
	setSprintFormInfo(sprintID, assignto);
	
	// set sprint info form to render it
	SprintInfoForm.render('SprintInfo_content');
	
	// set StatusTable
	initStatusTable(sprintID);
	taskboard_Table.render('TaskBoard_DD');	

	Ext.Ajax.request({
		url: 'GetTopTitleInfo.do',
		success: function(response) {
			var obj = Ext.util.JSON.decode(response.responseText);
			
			var projectName = obj.ProjectName;
			var userName = obj.UserName;
			
			Ext.getDom("UserNameInfo_Project").innerHTML = userName;
			Ext.getDom("ProjectNameInfo").innerHTML = "Project&nbsp;:&nbsp;&nbsp;" + projectName;
		},
		failure : function(){
			Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
		}
	});
});

</script>

<div id="SprintInfo_content"></div>
<div id="TaskBoard_DD"></div>
