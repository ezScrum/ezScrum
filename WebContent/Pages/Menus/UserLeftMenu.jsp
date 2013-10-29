<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<link rel="stylesheet" type="text/css" href="css/ext/css/ext-all.css" />
<script type="text/javascript" src="javascript/ext-base.js"></script>
<script type="text/javascript" src="javascript/ext-all.js"></script>
<script type="text/javascript" src="javascript/LoadMaskUtil.js"></script>

<script type="text/javascript">
	Ext.onReady(function() {
		var UserPanel = new Ext.Panel({
	    	frame: true,
	    	title: 'User Information',
	    	collapsible: true,
	    	contentEl: 'user_info',
	    	titleCollapse: true
	    });
	    
		var contentWidget = new Ext.Panel({
	    	split:true,
	    	collapsible: true,
	    	collapseMode: 'mini',
	    	header: false,
	    	border: false,
			height: 800,
			renderTo: 'sidebar',
			items: [UserPanel]
		});
	
		Ext.DomHelper.applyStyles(Ext.getDom("user_info"), "line-height: 200%; display: block;");
		
		contentWidget.doLayout();
	});
</script>

<div id = "sidebar"></div>

<ul id="user_info" style="line-height: 200%; display: none;">
	<li>
		<img src="images/Bullet.gif">&nbsp;<html:link action="/accountManage">My Account</html:link>
	</li>
</ul>