<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>


<link rel="stylesheet" type="text/css" href="css/ext/css/ext-all.css" />
<script type="text/javascript" src="javascript/ext-base-debug.js"></script>
<script type="text/javascript" src="javascript/ext-all-debug.js"></script> 
<script type="text/javascript" src="javascript/AjaxWidget/Message.js"></script>
<link rel="stylesheet" type="text/css" href="css/Message.css"/>
<link rel="stylesheet" type="text/css" href="css/TaskBoard.css"/>

<script type="text/javascript">
	
Ext.ns('ezScrum');

var SprintInfoForm = new Ext.form.FormPanel({
	id			: 'InvalidTenancy',
	bodyStyle	: 'padding:25px',
	frame		: false,
	width		: '35%',
	height		: 100,
	border		: true,	
	title		: 'Invalid Tenancy',
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
        text: 'Invalid tenancy, please contact with system manager.',
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


