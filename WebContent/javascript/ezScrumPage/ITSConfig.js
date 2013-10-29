var ITSConfigPage = new Ext.Panel({
	id			: 'ITSConfig_Page',
	layout		: 'anchor',
	autoScroll	: true,
	items : [
	    { ref: 'ITSConfigModifyForm_ID', xtype : 'ITSConfigModifyForm' }
	],
	listeners : {
		'show' : function() {
			this.ITSConfigModifyForm_ID.loadDataModel();
		}
	}
});