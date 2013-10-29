<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" type="text/css" href="style.css">
<table width="100%" height="55"  border="0" cellpadding="0" cellspacing="0" >
  <tr>
    <td rowspan="2" align="left" scope="col"><img height="55" src="images/Title_Caption.gif" width="411"><img height="55" src="images/Title_Caption_RightBlock.gif" width="51"></td>
    <td width="100%" height="27" align="right" valign="middle" background="images/Title_RightTopBG.gif" scope="col" >	<div class="UserProfile" style="width: 30%; margin-right: 10px;">
	${UserSession.account.ID}(${UserSession.account.name})
  </div>
  </td>
  </tr>
  <tr>
    <td valign="middle" background="images/Title_RightDownBG.gif" scope="col" >
    <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr>
    <td>
    <table height="28" border="0" cellpadding="0" cellspacing="0" >
      <tr>
        <td background="images/TopMenu_SelectBG.gif"><html:link action="/resetProjectSession" styleClass="TopMenuSelect">Project List</html:link></td>
       	<td><img height="28" src="images/TopMenu_SelectRight.gif" width="23"></td>
        <td background="images/TopMenu_UnSelectBG.gif" class="TopMenuUnSelect">
        	<html:link action="/accountManage"  styleClass="TopMenuUnSelect">Management</html:link>
        </td>
       	<td><img height="28" src="images/TopMenu_UnSelectEnd.gif" width="23"></td>
       </tr>
    </table>    	
    	</td>
    	<td align="right">
    	
        <html:link action="/logout"><img src="images/logout.gif" alt="Logout" border="0" /> </html:link>
    	</td>
    </tr>
    </table>
	</td>
  </tr>
</table>