<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<html>
<head>
	<title>ezScrum, SSLab NTUT</title>
	<link rel="shortcut icon" href="images/scrum_16.png"/>
</head>


<!-- extjs -->
<script type="text/javascript" src="javascript/ext-base.js"></script>
<script type="text/javascript" src="javascript/ext-all.js"></script>

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
<script type="text/javascript" src="javascript/ezScrumDataModel/ManagementAccounts.js"></script>

<!-- Widget -->
<script type="text/javascript" src="javascript/ezScrumWidget/EditUserAccountInformation.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/EditUserAccountPassword.js"></script>

<!-- Page -->
<script type="text/javascript" src="javascript/ezScrumPage/UserInformationManagement.js"></script>


<!-- Content Panel -->
<script type="text/javascript" src="javascript/ezScrumLayout/UserManagementLeftTreePanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumLayout/UserManagementLeftTreePanelBtnEvent.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/Top_Panel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/User_Management_Content_Panel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/Footer_Panel.js"></script>


<!-- Base layout -->
<script type="text/javascript" src="javascript/ezScrumLayout/ezScrumUserManagementUI.js"></script>
<link rel="stylesheet" type="text/css" href="css/ext/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="css/ezScrum/TopPanel.css" />
<link rel="stylesheet" type="text/css" href="css/ezScrum/TreePanel.css" />

<!--check session  -->
<script type="text/javascript" src="javascript/ezScrumPage/ValidateUserEvent.js"></script>

<!-- ezScrum import -->

<script type="text/javascript">
	Ext.onReady(function() {
		/*
		 * 針對 session 過期先作判斷, 若過期則無需跟 server 要資料.
		 * 例如在此頁 logout 後, 回上一頁(此頁)會掛掉
		 * 所以利用 check session, 若過期則導回 logon 頁面
		 * ezScrumContent(Summary), ViewList, ezScrumUserManagementUI 都先暫時用此方法
		 * note: 若在 before render event 時 check session, 還是會先有 init 的動作, 會浪費資源
		 */
		checkUserSession();
		
		Ext.QuickTips.init();
		
		var ezScrumUserManagementContent = new ezScrum.UserManagementMaiUI();
		ezScrumUserManagementContent.render("content");
		listenSessionForUserInformationManagement();
	});
</script>

<div id="content"></div>
</html>