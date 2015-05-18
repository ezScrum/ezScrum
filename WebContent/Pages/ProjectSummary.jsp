<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript" src="javascript/prototype.js"></script>
<script type="text/javascript" src="javascript/ext-base-debug.js"></script>
<script type="text/javascript" src="javascript/ext-all-debug.js"></script>
<script type="text/javascript"
	src="javascript/AjaxWidget/BurndownChartWidget.js"></script>

<script type="text/javascript">
var sprintID = ${SprintID};

Ext.onReady(function() {
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
});

</script>

<body bgcolor="#ffffff" topmargin="0" leftmargin="0">
<div class="x-box-tl">
<div class="x-box-tr">
<div class="x-box-tc"></div>
</div>
</div>
<div class="x-box-ml">
<div class="x-box-mr">
<div class="x-box-mc">
<div class="SummaryDescription">

<table width="95%" border="0" cellspacing="0" cellpadding="10">
	<tr>
		<td colspan="2">
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td class="SummaryTableCaption">Project Description</td>
			</tr>
			<tr>
				<td class="SummaryTableCaptionBorder"><img height="4"
					src="images/SummaryCaptionBar.gif" width="276"></td>
			</tr>
			<tr>
				<td class="SummaryTableBodyBorder">
				<li class="SummaryFieldText">Project comment: <span
					class="SummaryDataText">${projectObject.getComment()}</span></li>
				<li class="SummaryFieldText">Project Manager: <span
					class="SummaryDataText">${projectObject.getManager()}</span></li>
				<li class="SummaryFieldText">Create Date: <span
					class="SummaryDataText">${new Date(projectObject.getCreateTime())}</span></li>


				</td>

			</tr>
			<c:if test="${TaskBoard != null}">
				<tr>
					<td class="SummaryTableCaption">Sprint ${TaskBoard.sprintID}</td>
				</tr>
				<tr>
					<td class="SummaryTableCaptionBorder"><img height="4"
						src="images/SummaryCaptionBar.gif" width="276"></td>
				</tr>
				<tr>
					<td class="SummaryTableBodyBorder">
					<li class="SummaryFieldText">Sprint Goal: <span
						class="SummaryDataText">${TaskBoard.sprintGoal}</span></li>
					<li class="SummaryFieldText">Current Undone / Total Story
					Point: <span class="SummaryDataText">${TaskBoard.storyPoint
					}</span></li>
					<li class="SummaryFieldText">Report generated on: <span
						class="SummaryDataText">${TaskBoard.generatedTime}</span></li>
					<li class="SummaryFieldText">Report generated on: <span
						class="SummaryDataText">${TaskBoard.generatedTime}</span></li>
					</td>
				</tr>
			</c:if>
		</table>
		</td>
	</tr>

</table>
<c:if test="${TaskBoard != null}">
	<table width="95%" border="0" cellspacing="0" cellpadding="10">
		<tr>
			<td align="center">
			<div id="Story_Burndown_Chart"></div>
			</td>
			<td align="center">
			<div id="Task_Burndown_Chart"></div>
			</td>
		</tr>
	</table>
</c:if></div>
</div>
</div>
</div>
<div class="x-box-bl">
<div class="x-box-br">
<div class="x-box-bc"></div>
</div>
</div>
<div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>
<div id="SideShowItem" style="display:none;">viewProjectSummary</div>

