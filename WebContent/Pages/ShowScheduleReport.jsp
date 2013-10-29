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
  
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!--ezScrum team design tools  -->
<script type="text/javascript" src="javascript/ezScrumJSTool.js"></script>
<script type="text/javascript">
	function show() {
		var sprintID = document.getElementById("ShowSprint").value;
		var pid = getURLParameter("PID");
		document.location.href = "<html:rewrite action="/showScheduleReport" />?sprintID="+sprintID+"&PID="+pid;
	}
</script>

<body>
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
				<td class="TBReportSuccessHead">Sprint Goal</td>
				<td class="TBReportHead">${report.sprintGoal} </td>
			</tr>
			<tr>
				<td class="TBReportSuccessHead">Duration</td>
				<td class="TBReportHead">${report.duration }</td>
			</tr>
			<tr>
			<td class="TBReportSuccessHead">Sprint No</td>
			<td class="TBReportHead" align="right">
				<select id="ShowSprint" onchange="show()" class="selectField">
					<logic:iterate id="element" name="SprintPlans">
						<c:choose>
						<c:when test="${element.ID == report.iteration}"><option value="${ element.ID }" selected>Sprint ${ element.ID }</option></c:when>
							<c:otherwise><option value="${ element.ID }">Sprint ${ element.ID }</option></c:otherwise>
						</c:choose>						
					</logic:iterate>
				</select>
			</td>
			</tr>
			<tr><td>&nbsp;
			</td></tr>
			<tr>
				<td colspan="2" class="TaskBoardReportBody"  align="center">
					<img id="remainWorkChartImage" src="${report.path }"/>
				</td>
			</tr>
			<tr><td>&nbsp;
			</td></tr>
			<tr>
				<td class="TBReportFailHead" width="20%" align="center">Total</td>
				<td class="TBReportFailHead" align="center">${report.storySize }</td>
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
</html>