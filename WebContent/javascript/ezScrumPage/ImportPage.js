var ImportPage = new Ext.Panel({
	id			: 'Import_Page',
	layout		: 'anchor',
	autoScroll	: true,
	items : [
	    { ref: 'ImportForm_ID', xtype : 'ImportForm' }
	],
	listeners : {
		'show' : function() {
			//this.ImportForm_ID.import();
		}
	}
});