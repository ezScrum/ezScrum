<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<link rel="stylesheet" type="text/css" href="css/ext/css/ext-all.css" />
<script type="text/javascript" src="javascript/ext-base-debug.js"></script>
<script type="text/javascript" src="javascript/ext-all-debug.js"></script> 
<script type="text/javascript" src="javascript/AjaxWidget/Message.js"></script>
<link rel="stylesheet" type="text/css" href="css/Message.css"/>
<link rel="stylesheet" type="text/css" href="css/TaskBoard.css"/>

<script type="text/javascript">
	
Ext.ns('ezScrum');

var SprintInfoForm = new Ext.form.FormPanel({
	id			: 'permissionDenied',
	bodyStyle	: 'padding:25px',
	frame		: false,
	width		: '35%',
	height		: 100,
	border		: true,	
	title		: 'Permission Denied',
   	LabelAlign  : 'left',
	region		: 'center',
	defaultType	: 'textfield',
	defaults	: {
       	msgTarget	: 'side'
   	},
   	monitorValid  : true,
   	items : [{
   		xtype: 'label',
        name: 'message',
        text: 'Permission denied, please contact with system manager.',
        anchor: '95%'
       }]
});

Ext.onReady(function() {
	SprintInfoForm.render('SprintInfo_content');
});

</script>

<table width="100%" height="100%">
  <tr>
    <td align="center" valign="middle">
    	<div align="center" valign="middle" id="SprintInfo_content"></div>
    </td>
  </tr>
</table>


