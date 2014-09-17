<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<link rel="stylesheet" type="text/css" href="style.css">
<link rel="stylesheet" type="text/css" href="form.css">
<link rel="stylesheet" type="text/css" href="report.css">
<title>Sprint Backlog #${SprintID }</title>
</head>
<body>
<table class="NoBorderTable" width="95%">
<tr>
	<td class="InformationTitle" align="center">${Project.projectDesc.displayName }, Sprint ${SprintID }
	</td>
</tr>
<tr>
	<td></td>
</tr>
<tr>
	<td class="InformationSubtitle">Sprint Goal</td>
</tr>
<tr>
	<td><ul><li>${SprintPlan.goal }</li></ul></td>
</tr>
<tr>
	<td class="InformationSubtitle">Sprint Backlog(Estimates in Parenthesis)</td>
</tr>
<tr><td><ul>
<c:forEach var="element" items="${Stories }">
	<li>${element.summary } (${element.estimated })</li>
</c:forEach>
</ul></td></tr>
<tr><td></td></tr>
<tr>
	<td>Estimated velocity : ${StoryPoint } story points</td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
	<td class="InformationSubtitle">Schedule</td>
</tr>
<tr>
	<td><ul>
		<li>Sprint period : ${SprintPeriod}</li>
		<li>Daily Scrum : ${SprintPlan.notes }</li>
		<li>Sprint Review : ${SprintPlan.demoDate } ${SprintPlan.demoPlace }</li>
	</ul></td>
</tr>
<tr>
	<td class="InformationSubtitle">Team</td>
</tr>
<tr>
	<td><ul>
	<c:forEach var="element" items="${Actors }">
		<c:if test="${element != ''}">
		<li>${element }</li>
		</c:if>
	</c:forEach>
	</ul></td>
</tr>
</table>

</body>
</html>