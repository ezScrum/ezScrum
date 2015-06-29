<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%@page import="ntut.csie.ezScrum.web.support.SessionManager"%>
<%@page import="ntut.csie.ezScrum.web.dataObject.ProjectObject"%>
<%@ page import="ntut.csie.ezScrum.stapler.ProductBacklog" %>
<%
String projectID = request.getParameter("PID");

ProjectObject project = SessionManager.getProjectObject(request);
if( project != null ){
	projectID = project.getName();
}

ProductBacklog p = new ProductBacklog( projectID );
%>


<script type="text/javascript">
Ext.ns('ezScrum.productBacklog');
//product backlog top bar will be a ui extension point,so separate it from productBacklog page
//top bar

ezScrum.productBacklog.TopToolbar = Ext.extend(Ext.Toolbar,{
	id:'productBacklogToolbarId',
	layout:'fit',
	plugins:[<%=p.getToolbarPluginStringList()%>],
	initComponent : function(){
		ezScrum.productBacklog.TopToolbar.superclass.initComponent.apply(this);
	}

});


</script>
</body>
</html>