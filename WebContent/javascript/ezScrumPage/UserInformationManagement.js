var UserInformationManagementPage = {
	xtype: 'tabpanel',
	autoScroll:	false,
	activeTab: 0,
	frame: true,
	items: [{
		title : 'Edit Information',
		id    : 'userEditInformationTab',
		xtype : 'Management_EditUserAccountInformationForm',
		ref   : 'UserEditInformationForm_refID'
	},{
		title: 'Change Password',
		id   : 'userChangePasswordTab',
		xtype : 'Management_ModifyUserAccountPasswordForm',
		ref   : 'UserChangePasswordForm_refID'
	}],
	listeners: {
		'tabchange': function(tabPanel, tab) {
			if( tab.getItemId() == 'userEditInformationTab' ){
				this.UserEditInformationForm_refID.loadUserDataModel();
			}else if( tab.getItemId() == 'userChangePasswordTab' ){
				this.UserChangePasswordForm_refID.loadUserDataModel();
			}
			tab.doLayout();
		}
	}
};