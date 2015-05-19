<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<html>
<head>
	<title>ezScrum, SSLab NTUT</title>
</head>
<body>
	<%@page import="ntut.csie.ezScrum.stapler.TaskBoard"%>
	<%@page import="ntut.csie.ezScrum.web.support.SessionManager"%>
	<%@page import="ntut.csie.ezScrum.web.dataObject.ProjectObject"%>
	<%
		ProjectObject projectObject = (ProjectObject) request.getAttribute("projectObject");
		String projectName = projectObject.getName();
		TaskBoard taskBoard = new TaskBoard(projectName);
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