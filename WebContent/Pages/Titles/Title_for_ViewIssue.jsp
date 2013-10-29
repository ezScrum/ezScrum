<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>

<link rel="stylesheet" type="text/css" href="style.css">
<table width="100%" height="55"  border="0" cellpadding="0" cellspacing="0" >
  <tr>
    <td rowspan="2" align="left" scope="col"><img height="55" src="images/Title_Caption.gif" width="411"><img height="55" src="images/Title_Caption_RightBlock2.gif" width="51"></td>
    <td width="100%" height="27" align="right" valign="middle" background="images/Title_RightTopBG.gif" scope="col" >&nbsp;	</td>
  </tr>
  <tr>
    <td valign="middle" background="images/Title_RightDownBG.gif" scope="col">
    	<table height="28" border="0" cellpadding="0" cellspacing="0" >
      		<tr>
        		<td background="images/TopMenu_UnSelectBG.gif"><div Class="TopMenuUnSelect">Project List</div> </td>
		        <td><img height="28" src="images/TopMenu_UnSelectRight.gif" width="23"></td>
		        <td background="images/TopMenu_UnSelectBG.gif" class="TopMenuUnSelect"><div>Management</div> </td>
		        <td><img height="28" src="images/TopMenu_UnSelectEnd.gif" width="23"></td>
		        <td>&nbsp;</td>
		        <td>&nbsp;</td>
    		  	<td align="right">
    				<html:link action="/logout"> back</html:link>
    			</td>
      		</tr>
     	</table>
  	</td>
  </tr>
</table>