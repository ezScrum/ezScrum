<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<html>
<head>
	<title>ezScrum, SSLab NTUT</title>
	<link rel="shortcut icon" href="images/scrum_16.png"/>
</head>


<!-- extjs -->
<script type="text/javascript" src="javascript/ext-base.js"></script>
<script type="text/javascript" src="javascript/ext-all.js"></script>

<script type="text/javascript" src="javascript/ux/PagingMemoryProxy.js"></script>

<!--ezScrum team design tools  -->
<script type="text/javascript" src="javascript/ezScrumJSTool.js"></script>

<!-- extjs -->

<script type="text/javascript">
	// namespace setting
	Ext.ns('ezScrum');
	Ext.ns('ezScrum.window');
</script>



<!-- ezScrum import -->

<!--
    ezScrum shared component, should be imported before all panel or window render.
    These componets are created for reuse it.
-->
<script type="text/javascript" src="javascript/ezScrumLayout/ezScrumLayoutSupport.js"></script>



<!-- other support -->
<script type="text/javascript" src="javascript/AjaxWidget/CommonFormVTypes.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/Message.js"></script>
<link rel="stylesheet" type="text/css" href="css/Message.css"/>


<!-- DataModel -->
<script type="text/javascript" src="javascript/ezScrumDataModel/GAE/ManagementTenants.js"></script>


<!-- Widget -->
<script type="text/javascript" src="javascript/ezScrumSharedComponent/GAE/ModifyTenantWindow.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/AssignRoleWindow.js"></script>


<!-- panel -->
<script type="text/javascript" src="javascript/ezScrumPanel/GAE/TenantGridPanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/Management_ScrumRole_Panel.js"></script>


<!-- Page -->
<script type="text/javascript" src="javascript/ezScrumPage/GAE/TenantManagement.js"></script>
<script type="text/javascript" src="javascript/ezScrumPage/ScrumRoleManagement.js"></script>
<script type="text/javascript" src="javascript/ezScrumPage/CheckUpdateManagement.js"></script>
<script type="text/javascript" src="javascript/ezScrumPage/ServerManagement.js"></script>

<!--check session  -->
<script type="text/javascript" src="javascript/ezScrumPage/ValidateUserEvent.js"></script>

<!-- Content Panel -->
<script type="text/javascript" src="javascript/ezScrumLayout/GAE/ManagementAdminLeftTreePanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumLayout/GAE/ManagementAdminLeftTreePanelBtnEvent.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/GAE/Top_AdminPanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/GAE/Management_AdminContent_Panel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/Footer_Panel.js"></script>


<!-- Base layout -->
<script type="text/javascript" src="javascript/ezScrumLayout/ezScrumManagementUI.js"></script>
<link rel="stylesheet" type="text/css" href="css/ext/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="css/ezScrum/TopPanel.css" />
<link rel="stylesheet" type="text/css" href="css/ezScrum/TreePanel.css" />


<!-- ezScrum import -->

<script type="text/javascript">
	Ext.onReady(function() {
		Ext.QuickTips.init();
		
		var ezScrumManagementContent = new ezScrum.ManagementMaiUI();
		ezScrumManagementContent.render("content");
		listenSessionForTenantManagement();
	});
</script>

<div id="content"></div>
</html>