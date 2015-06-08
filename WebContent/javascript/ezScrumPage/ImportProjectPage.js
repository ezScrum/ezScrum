var ImportProjectPage = new Ext.Panel({
	id			: 'ImportProject_Page',
	layout		: 'anchor',
	autoScroll	: true,
	items : [
	    { ref: 'ImportProjectForm_ID', xtype : 'ImportProjectForm' }
	],
	listeners : {
		'show' : function() {
			this.ImportProjectForm_ID.loadDataModel();
		}
	}
});