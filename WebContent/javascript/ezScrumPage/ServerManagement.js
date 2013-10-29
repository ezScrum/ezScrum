var ServerManagementPage = new Ext.Panel({
	id			: 'ServerInfo_Management_Page',
	layout		: 'anchor',
	autoScroll	: true,
	items : [
	    { html: 'Server Info Management' }
	],
	listeners : {
		'show' : function() {
//			this.ITSConfigModifyForm_ID.loadDataModel();
		}
	}
});