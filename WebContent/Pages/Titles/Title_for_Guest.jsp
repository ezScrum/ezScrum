<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<script type="text/javascript">
	function GoProjectList() {
		window.document.location = "./viewList.do" 
	}
	
	function GoManagement() {
		window.document.location = "./viewManagement.do"
	}
	
	function GoLogout() {
		window.document.location = "./logout.do"
	}
</script>


<link rel="stylesheet" type="text/css" href="style.css">
<link rel="stylesheet" type="text/css" href="css/ezScrum/TopPanel.css" />

<table width="100%" height="55" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td rowspan="2" align="left"><img height="55" width="411" src="images/Title_Caption.gif"><img height="55" src="images/Title_Caption_RightBlock.gif"></td>
		<td colspan="5" height="27" width="100%" align="right" background="images/Title_RightTopBG.gif"><div id="UserNameInfo_Project" class="UserProfile" style="width: 30%"></div></td>
	</tr>
	<tr>
		<td background="images/TopMenu_SelectBG.gif" height="28" class="TopMenu SelectColor" onclick="GoProjectList()">ProjectList</td>
		<td><img height="28" width="23" src="images/TopMenu_SelectRight.gif"></td>
		<td background="images/TopMenu_UnSelectBG.gif" class="TopMenu UnSelectColor" onclick="GoManagement()">Management</td>
		<td><img height="28" width="23" src="images/TopMenu_UnSelectEnd.gif"></td>
		<td align="right" background="images/Title_RightDownBG.gif" width="100%"><img height="18" width="45" class="TopMenu" onclick="GoLogout()" src="images/logout.gif"></td>
	</tr>
	<tr background="images/Title_RightDownBG.gif" height="30" width="100%">
		<td colspan="6"><div id="ProjectNameInfo" style="width: 30%"></div></td>
	</tr>
</table>