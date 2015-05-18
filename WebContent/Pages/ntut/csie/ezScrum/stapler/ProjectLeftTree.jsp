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
	<%@page import="ntut.csie.ezScrum.stapler.Project"%>
	<%
		String projectID = request.getParameter("PID");
		ProjectObject projectObject = SessionManager.getProjectObject(request);
		
		if (projectObject != null) {
			projectID = projectObject.getName();
		}
		Project project = new Project(projectID);
	%>

	<script type="text/javascript">
		
		/* ezScrum 本身的物件更改，使其可以掛上plugin, 更改後讓原本的物件繼承 */
		
		Ext.ns('ezScrum.projectLeftTree');
		
		ezScrum.projectLeftTree.contentPanel = Ext.extend(Ext.Panel, {
			plugins: [<%=project.getProjectPagePluginString()%>],
			initComponent : function() {
				ezScrum.projectLeftTree.contentPanel.superclass.initComponent.apply(this, arguments);
			}
		});
		
		ezScrum.projectLeftTree.treePanel = Ext.extend(Ext.tree.TreePanel, {
			Page_Index: 0,	// 記錄目前在哪個頁面上( 設定在ezScrumPanel/Conten_Panel.js裡面)
			Plugin_Clicked: false,	// 記錄是否為plugin上面的node被click，如果是，則在ProjectLeftTreePanelBtnEvent.js裡面所設定Btn Event則忽略
			plugins: [<%=project.getProjectLeftTreePluginString()%>],
			initComponent : function() {
				ezScrum.projectLeftTree.treePanel.superclass.initComponent.apply(this, arguments);
			},
		});
		
	</script>
</body>
</html>