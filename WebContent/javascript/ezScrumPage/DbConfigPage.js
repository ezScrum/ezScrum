var DbConfigPage = new Ext.Panel({
	id			: 'DbConfig_Page',
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