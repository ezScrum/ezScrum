ezScrum.Management_ContentPanel = new Ext.Panel({
	region		: 'center',		// position
	
	id			: 'Management_content_panel',
	layout		: 'card',
	margins		: '3 0 0 0',
	activeItem	: 0,
    collapsible	: false,
	border		: false,
	frame		: false,
	items : [
	    // RBAC Management index
		// 0,                  1,                       2
	    AccountManagementPage, ScrumRoleManagementPage, PluginManagementPage,
	    
	    // System Management index
	    // 3,                      4
	    //CheckUpdateManagementPage, ServerManagementPage
	    
	    // Database Config Management index
	    // 3
	    DbConfigPage
	]
});