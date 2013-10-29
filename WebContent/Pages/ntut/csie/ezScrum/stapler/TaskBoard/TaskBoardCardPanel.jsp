<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<html>
<head>
	<title>ezScrum, SSLab NTUT</title>
</head>
<body>
<%@ page import="ntut.csie.ezScrum.stapler.TaskBoard" %>
<%@ page import="ntut.csie.ezScrum.web.form.ProjectInfoForm" %>
<%@ page import="ntut.csie.ezScrum.web.internal.IProjectSummaryEnum" %>
<%
ProjectInfoForm projectInfoForm = (ProjectInfoForm)session.getAttribute( IProjectSummaryEnum.PROJECT_INFO_FORM );
String projectID = projectInfoForm.getName();
TaskBoard taskBoard = new TaskBoard( projectID ); 
%>

<script type="text/javascript">

Ext.ns('ezScrum.TaskBoard');
// Task Board include 3 panel(Sprint Information, Burndown Chart, Card Panel)
// The extension point of TaskBoard only "Card Panel"

// get Task Board plug-in ID
ezScrum.TaskBoard.CardPanel = '<%=taskBoard.getBoardPlugin()%>';

</script>
</body>
</html>