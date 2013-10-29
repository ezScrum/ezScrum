var ModifyConfigPage = new Ext.Panel({
	id			: 'ModifyConfig_Page',
	layout		: 'anchor',
	autoScroll	: true,
	items : [
	    { ref: 'ModifyConfig_ProjectModify_Form_ID', xtype : 'ProjectModifyForm' }
	],
	listeners : {
		'show' : function() {
			this.ModifyConfig_ProjectModify_Form_ID.loadDataModel();
		}
	}
});