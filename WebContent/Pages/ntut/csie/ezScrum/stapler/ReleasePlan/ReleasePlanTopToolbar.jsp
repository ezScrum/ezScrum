<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<%@page import="ntut.csie.ezScrum.web.support.SessionManager"%>
	<%@page import="ntut.csie.ezScrum.web.dataObject.ProjectObject"%>
	<%@page import="ntut.csie.ezScrum.stapler.ReleasePlan"%>
	<%
		String projectID = request.getParameter("PID");
		ProjectObject project = SessionManager.getProjectObject(request);
		
		if (project != null) {
			projectID = project.getName();
		}
		ReleasePlan releasePlan = new ReleasePlan(projectID);
	%>

	<script type="text/javascript">
		Ext.ns('ezScrum.releasnPlan');
		
		//releaseplan top bar(release action) will be a ui extension point,so separate it from productBacklog page top bar
		ezScrum.releasnPlan.ButtonGroup = Ext.extend(Ext.ButtonGroup, {
			plugins : [<%=releasePlan.getToolbarPluginStringList()%>],
			initComponent : function() {
				ezScrum.releasnPlan.ButtonGroup.superclass.initComponent.apply(this);
			}
		});
	</script>
</body>
</html>