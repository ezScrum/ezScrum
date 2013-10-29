var TenantManagementPage = new Ext.Panel({
	id			: 'Tenant_Management_Page',
	layout		: 'fit',
	autoScroll	: true,
	items : [
	    { xtype: 'Management_TenantGrid', ref: 'Management_TenantGrid_refID' }
	],
	listeners : {
		'show' : function() {
			this.Management_TenantGrid_refID.loadDataModel();
		}
	}
});