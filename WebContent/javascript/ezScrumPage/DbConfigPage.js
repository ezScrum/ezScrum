var DbConfigPage = new Ext.Panel({
	id			: 'DbConfig_Page',
	layout		: 'anchor',
	autoScroll	: true,
	items : [
	    { ref: 'DbConfigForm_ID', xtype : 'DbConfigForm' }
	],
	listeners : {
		'show' : function() {
			this.DbConfigForm_ID.loadDataModel();
		}
	}
});