ezScrum.ManagementLeftPanel_Event = Ext.extend(ezScrum.Management_LeftTreePanel, {
	Page_Index : 0, 
	initComponent: function() {

		ezScrum.ManagementLeftPanel_Event.superclass.initComponent.call(this);
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
	// 按下node後, 根據ID呼叫event
	fireTheEvent: function(node) {
		checkUserSession();
		var obj = this;
		if (node.parentNode.id == "RBACMgt") {
			obj.event_RBACManagement(node);
		} else if(node.parentNode.id == "PluginMgt"){
			obj.event_PlugintManagement(node);
		} else if(node.parentNode.id == "DbConfigMgt") {
			obj.event_DbConfigManagement(node);
		}
		/*
		 * Management頁面，update相關功能尚未實作完，因此先註解掉
		 * 
		 else if (node.parentNode.id == "SystemUpdateMgt") {
			// Update Management event
			obj.event_SystemUpdatetManagement(node);
		}*/
	},
	event_RBACManagement: function(node) {
		if (node.id == "ScrumRoleMgtUrl") {
			this.Page_Index = 1;
		} else {
			// default is Account Management page
			this.Page_Index = 0;
		}
	},
	// 似乎沒再用了? 待確認
//	event_SystemUpdatetManagement: function(node) {
//		if (node.id == "CheckUpdateUrl") {
//			this.Page_Index = 2;
//		} else if (node.id == "ServerMgtUrl") {
//			this.Page_Index = 3;
//		} else {
//			this.Page_Index = 0;
//		}
//	},
	event_PlugintManagement: function(node) {
		if (node.id == "PluginMgtUrl") {
			this.Page_Index = 2;
		} else {
			this.Page_Index = 0;
		}
	},
	event_DbConfigManagement: function(node) {
		if (node.id == "DbConfigUrl") {
			this.Page_Index = 3;
		} else {
			this.Page_Index = 0;
		}
	},
	notify_Main_Content: function() {
		Ext.getCmp('Management_content_panel').layout.setActiveItem(this.Page_Index);
	}
});

ezScrum.Management_LeftPanel = new ezScrum.ManagementLeftPanel_Event({
	region		: 'west',		// position
	
	id			: 'Management_left_panel',
	width		: '20%'
});