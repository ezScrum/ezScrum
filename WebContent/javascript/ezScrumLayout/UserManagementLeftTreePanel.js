ezScrum.User_Management_LeftTreePanel = Ext.extend(Ext.tree.TreePanel, {
	collapsible		: true,
	collapseMode	: 'mini',
	animCollapse	: false,
	animate			: false,
	hideCollapseTool: true,
	id				: 'user_management_left_side',
	ref				: 'user_management_left_side_refID',
	rootVisible		: false,
	lines			: false,
	autoScroll		: true,
	root : {
		nodeType 	: 'async',
	    id 			: 'user_management_left_side_root',
	    text		: 'Root',
	    expanded	: true,
	    children	: [
	    {
	    	/*******************************
	    	 * user information Side
	    	 */
	    	text: 'User Information',
	    	id 	: 'UserInfo',
			expanded : true,
			iconCls:'None',
        	cls:'treepanel-parent',
			children : [
			    {
			    	id:'UserInfoUrl',
			    	cls:'treepanel-leaf',
                	iconCls:'leaf-icon',
					text : '<u>User Information</u>', 
					leaf:true
				}]
		    /**
		     * user information Side
		     *******************************/
	    }]
	}
});