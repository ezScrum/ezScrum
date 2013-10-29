var AccountManagementPage = new Ext.Panel({
	id			: 'Account_Management_Page',
	layout		: 'fit',
	autoScroll	: true,
	items : [
	    { xtype: 'Management_AccountGrid', ref: 'Management_AccountGrid_refID' }
	],
	listeners : {
		'show' : function() {
			this.Management_AccountGrid_refID.loadDataModel();
		}
	}
});