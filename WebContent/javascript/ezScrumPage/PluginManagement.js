var PluginManagementPage = new Ext.Panel({
	id			: 'Plugin_Management_Page',
	layout		: 'fit',
	autoScroll	: true,
	items : [
	 	    { xtype: 'Management_PluginGrid', ref: 'Management_PluginGrid_refID' }
	 	],
	 	listeners : {
	 		'show' : function() {
	 			this.Management_PluginGrid_refID.loadDataModel();
	 		}
	 	}
});