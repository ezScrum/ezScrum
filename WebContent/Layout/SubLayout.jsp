
<%@ page language="java" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
  <head>
    <title><tiles:getAsString name="title" /> </title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">    
	<link rel="stylesheet" type="text/css" href="style.css">
	<link rel="stylesheet" type="text/css" href="form.css">
	<link rel="stylesheet" type="text/css" href="report.css">
	<link rel="shortcut icon" href="images/scrum_16.png"/>
  </head>
  
  <body bgcolor="#ffffff" topmargin="0" leftmargin="0">
 	<tiles:insert name="topMenu" />
	<tiles:insert name="caption" />
<table width="100%" height="80%" border="0" cellpadding="5" cellspacing="0">
  <tr>
    <td width="160" valign="top"><tiles:insert name="leftMenu" /></td>
    <td valign="top"><tiles:insert name="body" /></td>
  </tr>
</table>
	<tiles:insert name="footer" />
 </body>
</html:html>
