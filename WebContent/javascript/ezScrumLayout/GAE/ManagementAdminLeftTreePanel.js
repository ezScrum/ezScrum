ezScrum.Management_LeftTreePanel = Ext.extend(Ext.tree.TreePanel, {
	collapsible		: true,
	collapseMode	: 'mini',
	animCollapse	: false,
	animate			: false,
	hideCollapseTool: true,
	id				: 'management_left_side',
	ref				: 'management_left_side_refID',
	rootVisible		: false,
	lines			: false,
	autoScroll		: true,
	root : {
		nodeType 	: 'async',
	    id 			: 'management_left_side_root',
	    text		: 'Root',
	    expanded	: true,
	    children	: [
	    {
	    	/*******************************
	    	 * RBAC Management Side
	    	 */
	    	text: 'Tenant Management',
	    	id 	: 'RBACMgt',
			expanded : true,
			iconCls:'None',
        	cls:'treepanel-parent',
			children : [
			    {
			    	id:'TenantMgtUrl',
			    	cls:'treepanel-leaf',
                	iconCls:'leaf-icon',
					text : '<u>Tenant Management</u>', 
					leaf:true
				}
//			    , 
//				{
//					id:'ScrumRoleMgtUrl',
//					cls:'treepanel-leaf',
//                	iconCls:'leaf-icon',
//					text : '<u>Scrum Role Management</u>',
//					leaf:true
//				}
			    ]
		    /**
		     * RBAC Management Side
		     *******************************/
	    }]
	    /*
	     * Management頁面，update相關功能尚未實作完，因此先註解掉
	     ,{
		    //
	    	// Update Management
	    	//
	    	text: 'System Update Management',
	    	id 	: 'SystemUpdateMgt',
			expanded : true,
			iconCls:'None',
	    	cls:'treepanel-parent',
			children : [
			    {
			    	id:'CheckUpdateUrl',
			    	cls:'treepanel-leaf',
	            	iconCls:'leaf-icon',
					text : '<u>Check Update</u>', 
					leaf:true
				}, 
				{
					id:'ServerMgtUrl',
					cls:'treepanel-leaf',
	            	iconCls:'leaf-icon',
					text : '<u>Server Manager</u>',
					leaf:true
				}]
		    //
		    // Update Management
		    //
	    }]*/
	}
});