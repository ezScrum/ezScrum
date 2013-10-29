ezScrum.User_Management_ContentPanel = new Ext.Panel({
	region		: 'center',		// position
	
	id			: 'User_Management_content_panel',
	autoScroll	: true,
	autoHeight	: true,
	layout		: 'card',
	margins		: '3 0 0 0',
	activeItem	: 0,
    collapsible	: false,
	border		: false,
	frame		: false,
    items: [
        UserInformationManagementPage
    ]
});