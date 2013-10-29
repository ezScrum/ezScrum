ezScrum.UserManagementMaiUI = Ext.extend(Ext.Viewport, {
	id: 'UserManagementMainLayout',
	layout: 'border',
	initComponent: function() {
        this.items = [
			ezScrum.Management_TopPanel,			// src="javascript/ezScrumPanel/Top_Panel.js"
			ezScrum.User_Management_LeftPanel,		// src="javascript/ezScrumPanel/UserManagementLeftTreePanelBtnEvent.js"
			ezScrum.User_Management_ContentPanel,	// src="javascript/ezScrumPanel/User_Management_Content_Panel.js"
			ezScrum.FooterPanel						// src="javascript/ezScrumPanel/Footer_Panel.js"
        ];
        
        ezScrum.UserManagementMaiUI.superclass.initComponent.call(this);
    }
});
