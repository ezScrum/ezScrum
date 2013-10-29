<html>
  <head>
    <title></title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">    
	<link rel="stylesheet" type="text/css" href="style.css">
	<link rel="stylesheet" type="text/css" href="form.css">
	<link rel="stylesheet" type="text/css" href="report.css">
  </head>
  

<%@ page contentType="text/html; charset=utf-8"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>







<script type="text/javascript" src="javascript/CommonUtility.js"></script>

<!-- set Date -->
<link rel="stylesheet" type="text/css" media="all" href="javascript/Calendar/calendar-win2k-cold-1.css" title="win2k-cold-1" />
<script type="text/javascript" src="javascript/Calendar/calendar.js"></script>
<script type="text/javascript" src="javascript/Calendar/calendar-en.js"></script>
<script type="text/javascript" src="javascript/Calendar/calendar-setup.js"></script>
<!-- set Date -->

<script type="text/javascript">

function showByDate() {
	var startdate = document.getElementById("startDate");
	var enddate = document.getElementById("endDate");
	var type = document.getElementById("ShowReportType");
	document.location.href = "<html:rewrite action="/showFlowDiagramReport" />?startDate=" + startdate.value + "&endDate=" + enddate.value + "&type=" + type.value;
}

function refreshImage(imageSrc)
{
	img = document.getElementById("flowDiagramChartImage");
	img.src = imageSrc+"?"+Math.random();
}

function clearStartDate() {
	document.getElementById("startDate").value = "";
}

function clearEndDate() {
	document.getElementById("endDate").value = "";
}

</script>


<BODY onload="refreshImage('${TypeReport.ezReportChartPath}')">
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
				<td class="TBReportSuccessHead">Type</td>
				<td class="TBReportHead" align="right">
					<select id="ShowReportType" class="selectField">
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
				<td colspan="3" align="center" style="font-size: medium">Generate by Date</td>
			</tr>
			<tr>
				<td class="TBReportSuccessHead">Choose Start Date</td>
				<td class="TBReportHead" align="right">
					<span><img src="images/clear2.png" id="clear" onclick="clearStartDate()" title="Clear the Date"/></span>
					<img src="images/calendar.png" id="showCalendar" style="cursor: pointer; border: 0px;" title="Calendar" />
						<input type="text" id="startDate" value="${startDate}" readonly="readonly">
				</td>
			</tr>
			<tr>
				<td class="TBReportSuccessHead">Choose End Date</td>
				<td class="TBReportHead" align="right">
					<span><img src="images/clear2.png" id="clear" onclick="clearEndDate()" title="Clear the Date"/></span>
					<img src="images/calendar.png" id="showEndCalendar" style="cursor: pointer; border: 0px;" title="Calendar" />
						<input type="text" id="endDate" value="${endDate}" readonly="readonly">
				</td>
			</tr>
			<tr>
				<td colspan="3" align="right">
					<span><input type="button" value="Generate" onclick="showByDate()"></span>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="TaskBoardReportBody"  align="center">
					<img id="flowDiagramChartImage" src="${TypeReport.ezReportChartPath}" class="LinkBorder"/>
				</td>
			</tr>
			
			<%--
			<tr>
				<td class="TBReportSuccessHead" style="font-size: medium" width="20%" align="center">Average Lead Time</td>
				<td class="TBReportSuccessHead" style="font-size: medium" align="center">${KanbanReport.averageLeadTime}</td>
			</tr>
			 --%>
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
</BODY>

<script type="text/javascript">
<!--
//the script must be under inputField and button
    Calendar.setup({
        inputField     :    "startDate",			// id of the input field
        ifFormat       :    "%Y/%m/%d-%H:%M",	// format of the input field
        showsTime      :    true,				// will display a time selector
        button         :    "showCalendar",		// trigger for the calendar (button ID)
        singleClick    :    true,				// double-click mode
        step           :    1					// show all years in drop-down boxes (instead of every other year as default)
    });
    
    Calendar.setup({
        inputField     :    "endDate",			// id of the input field
        ifFormat       :    "%Y/%m/%d-%H:%M",	// format of the input field
        showsTime      :    true,				// will display a time selector
        button         :    "showEndCalendar",		// trigger for the calendar (button ID)
        singleClick    :    true,				// double-click mode
        step           :    1					// show all years in drop-down boxes (instead of every other year as default)
    });
-->
</script>
</html>
