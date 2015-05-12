var ExportProjectPage = new Ext.Panel({
	id			: 'ExportProject_Page',
	layout		: 'anchor',
	autoScroll	: true,
	items : [
	    { ref: 'ExportProjectForm_ID', xtype : 'ExportProjectForm' }
	],
	listeners : {
		'show' : function() {
			this.ExportProjectForm_ID.loadDataModel();
		}
	}
});