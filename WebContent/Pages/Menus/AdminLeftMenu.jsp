<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<link rel="stylesheet" type="text/css" href="css/ext/css/ext-all.css" />
<script type="text/javascript" src="javascript/ext-base.js"></script>
<script type="text/javascript" src="javascript/ext-all.js"></script>
<script type="text/javascript" src="javascript/LoadMaskUtil.js"></script>


<script type="text/javascript">
	Ext.onReady(function() {
		var RoleMgtPanel = new Ext.Panel({
	    	frame: true,
	    	title: 'RBAC Management',
	    	collapsible: true,
	    	contentEl: 'role_mgt',
	    	titleCollapse: true
	    });
	    
	    var UpdateMgtPanel = new Ext.Panel({
	    	frame: true,
	    	title: 'Update Management',
	    	collapsible: true,
	    	contentEl: 'update_mgt',
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
			items: [RoleMgtPanel, UpdateMgtPanel]
		});
	
		Ext.DomHelper.applyStyles(Ext.getDom("role_mgt"), "line-height: 200%; display: block;");
		Ext.DomHelper.applyStyles(Ext.getDom("update_mgt"), "line-height: 200%; display: block;");
		
		contentWidget.doLayout();
	});
</script>

<div id = "sidebar"></div>

<ul id="role_mgt" style="line-height: 200%; display: none;">
	<li>
		<img src="images/Bullet.gif">&nbsp;<html:link action="/accountManage">Account Management</html:link>
	</li>
	<li>
		<img src="images/Bullet.gif">&nbsp;<html:link action="/scumRoleManage">Scrum Role Management</html:link>
	</li>
</ul>

<ul id="update_mgt" style="line-height: 200%; display: none;">
	<li>
		<img src="images/Bullet.gif">&nbsp;<html:link action="/showCheckUpdate">Check Update</html:link>
	</li>
	<li>
		<img src="images/Bullet.gif">&nbsp;<html:link action="/showRestartManager">Server Manager</html:link>
	</li>
</ul>