ezScrum.User_ManagementLeftPanel_Event = Ext.extend(ezScrum.User_Management_LeftTreePanel, {
	Page_Index : 0, 
	initComponent: function() {

		ezScrum.User_ManagementLeftPanel_Event.superclass.initComponent.call(this);
    },
    listeners: {
		click: function(node, event) {
			var obj = this;
			
			if (node.leaf) {
				obj.fireTheEvent(node);			// check which node is, and trigger the mapping event
				obj.notify_Main_Content();
			}
		}
	},
	fireTheEvent: function(node) {
		var obj = this;
		if (node.parentNode.id == "UserInfo") {
			// user information event
			obj.event_UserInformation(node);
		}
	},
	event_UserInformation: function(node) {
		if (node.id == "UserInfoUrl") {
			this.Page_Index = 0;
		}
	},
	notify_Main_Content: function() {
		Ext.getCmp('User_Management_content_panel').layout.setActiveItem(this.Page_Index);
	}
});

ezScrum.User_Management_LeftPanel = new ezScrum.User_ManagementLeftPanel_Event({
	region		: 'west',		// position

	id			: 'User_Management_left_panel',
	width		: '15%'
});