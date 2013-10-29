<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<link rel="stylesheet" type="text/css" href="style.css">
<link rel="stylesheet" type="text/css" href="form.css">
<link rel="stylesheet" type="text/css" href="report.css">

<script type="text/javascript" src="javascript/CommonUtility.js"></script>
<link rel="stylesheet" type="text/css" media="all" href="javascript/Calendar/calendar-win2k-cold-1.css" title="win2k-cold-1" />
<script type="text/javascript" src="javascript/Calendar/calendar.js"></script>
<script type="text/javascript" src="javascript/Calendar/calendar-en.js"></script>
<script type="text/javascript" src="javascript/Calendar/calendar-setup.js"></script>

<!-- check session -->
<script type="text/javascript" src="javascript/ext-base.js"></script>
<script type="text/javascript" src="javascript/ext-all.js"></script>
<script type="text/javascript" src="javascript/ezScrumPage/ValidateUserEvent.js"></script>
<!--ezScrum team design tools  -->
<script type="text/javascript" src="javascript/ezScrumJSTool.js"></script>

<script type="text/javascript">


function show() {
	var sprint = document.getElementById("ShowSprint");	
	var type = document.getElementById("ShowReportType");
	var pid = getURLParameter("PID");
	replaceURL( "<html:rewrite action="/showRemainingReport" />?sprintID="+sprint.value+"&type="+type.value+"&PID="+pid );
}

function show2() {
	var type = document.getElementById("ShowReportType");
	var pid = getURLParameter("PID");
	var url = "<html:rewrite action="/showRemainingReport" />?type="+type.value+"&PID="+pid;
	
	var date = document.getElementById("setDate");
	if (date.value != null && date.value != "") {
		url = url + "&Date=" + date.value;
	} else {
		var sprint = document.getElementById("ShowSprint");
		url = url + "&sprintID=" + sprint.value;
	}
	replaceURL( url );
}

function showByDate() {
	var type = document.getElementById("ShowReportType");
	var date = document.getElementById("setDate");
	var pid = getURLParameter("PID");
	replaceURL( "<html:rewrite action="/showRemainingReport" />?type="+type.value+"&Date="+date.value+"&PID="+pid );
}

function refreshImage(imageSrc)
{
	img = document.getElementById("remainWorkChartImage");
	img.src = imageSrc+"?"+Math.random();
	
	// 判斷使用者輸入的日期是否超過目前 Sprint 日期的資訊
	var OutOfDay = "<%=request.getAttribute("OutofSprint").toString() %>";

	if (OutOfDay=="true") {
		alert("The date is not in a sprint interval.");
	} else if (OutOfDay=="OutOfDay") {
		alert("The date is out of today.");
	}
}

function clearDate() {
	document.getElementById("setDate").value = "";
}

</script>

<body onload="refreshImage('${RemainingWorkReport.remainingWorkChartPath}')">
<table width="90%" height="80%" border="0" cellpadding="0"
	cellspacing="0" align="center">
	<tr>
		<td><img src="images/Design1_r5_c2.gif" width="15" height="15" /></td>
		<td background="images/Design1_r5_c3.gif"><img
			src="images/spacer.gif" width="100%" height="15" /></td>
		<td>
		<div align="right"><img src="images/Design1_r5_c6.gif"
			width="15" height="15" /></div>
		</td>
	</tr>
	<tr valign="top">
		<td background="images/Design1_r6_c2.gif"><img
			src="images/spacer.gif" width="15" height="100%" /></td>
		<td class="innerTable" height="100%"><br />
		<div>
		
		<table width="95%" align="center" cellpadding="2" cellspacing="0"
			class="TaskBoardReportBorder">
			<tr>
			<td class="TBReportSuccessHead">Sprint No</td>
			<td class="TBReportHead" align="right">
				<select id="ShowSprint" onchange="show()" class="selectField">
					<logic:iterate id="element" name="SprintPlans">
						<c:choose>
						<c:when test="${element.ID == iteration}"><option value="${ element.ID }" selected>Sprint ${ element.ID }</option></c:when>
							<c:otherwise><option value="${ element.ID }">Sprint ${ element.ID }</option></c:otherwise>
						</c:choose>						
					</logic:iterate>
				</select>
			</td>
			</tr>
			<tr>
				<td class="TBReportSuccessHead">Generate by</td>
				<td class="TBReportHead" align="right">
					<select id="ShowReportType" class="selectField"
					onchange="show2()">
					<logic:iterate id="element" name="TypeList">
						<c:choose>
							<c:when test="${element== type}">
								<option value="${ element}" selected>${ element }</option>
							</c:when>
							<c:otherwise>
								<option value="${ element }">${ element }</option>
							</c:otherwise>
						</c:choose>
					</logic:iterate>
				</select></td>
			</tr>
			
			<tr>
				<td class="TBReportSuccessHead">Generate by Date</td>
				<td class="TBReportHead" align="right">
					<span><img src="images/clear2.png" id="clear" onclick="clearDate()" title="Clear the Date"/></span>
					<img src="images/calendar.png" id="showCalendar" style="cursor: pointer; border: 0px;" title="Calendar" />
					<input type="text" id="setDate" value="${setDate}" readonly="readonly">
					<span><input type="button" value="Generate" onclick="showByDate()"></span>
				</td>
			</tr>
			
			<tr>
			<td colspan="2" class="TaskBoardReportBody"  align="center">
				<img id="remainWorkChartImage" src="${RemainingWorkReport.remainingWorkChartPath}" class="LinkBorder"/></td>
				
			</tr>
			<tr>
				<td class="TBReportFailHead" width="20%" align="center">Total</td>
				<td class="TBReportFailHead" align="center">${RemainingWorkReport.totalQuantity}</td>
			</tr>
			<tr>
				<td class="TBReportSuccessHead" width="20%" align="center">non-Assign</td>
				<td class="TBReportSuccessHead" align="center">${RemainingWorkReport.nonAssignQuantity}</td>
			</tr>
			<tr>
				<td class="TBReportSuccessHead" width="20%" align="center">Assigned</td>
				<td class="TBReportSuccessHead" align="center">${RemainingWorkReport.assignedQuantity}</td>
			</tr>
			<tr>
				<td class="TBReportWarningHead" width="20%" align="center">Done</td>
				<td class="TBReportWarningHead" align="center">${RemainingWorkReport.doneQuantity}</td>
			</tr>
			
		</table>
		</div>
		<br />
		</td>
		<td background="images/Design1_r6_c6.gif" />
	</tr>
	<tr>
		<td><img src="images/Design1_r7_c2.gif" width="15" height="15" /></td>
		<td width="100%" background="images/Design1_r7_c3.gif" />
		<td><img src="images/Design1_r7_c6.gif" width="15" height="15" /></td>
	</tr>
</table>
</body>
<script type="text/javascript">
//the script must be under inputField and button
    Calendar.setup({
        inputField     :    "setDate",			// id of the input field
        ifFormat       :    "%Y/%m/%d-%H:%M",	// format of the input field
        showsTime      :    true,				// will display a time selector
        button         :    "showCalendar",		// trigger for the calendar (button ID)
        singleClick    :    true,				// double-click mode
        step           :    1					// show all years in drop-down boxes (instead of every other year as default)
    });
</script>