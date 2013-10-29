var ScrumRoleManagementPage = new Ext.Panel({
	id			: 'ScrumRole_Management_Page',
	layout		: 'fit',
	autoScroll	: true,
	items : [
	    { xtype: 'Management_ScrumRolePanel', ref: 'Management_ScrumRolePanel_refID' }
	],
	listeners : {
		'show' : function() {
			this.Management_ScrumRolePanel_refID.loadDataModel();
		}
	}
});