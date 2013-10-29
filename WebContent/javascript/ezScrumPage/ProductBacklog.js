var ProductBacklogPage = new Ext.Panel({
	id: 'ProductBacklog_Page',
	layout: 'fit',
	items: [{
		ref: 'ProductBacklogPage_ID',
		xtype: 'ProductBacklogPage'
	}],
	listeners: {
		'show': function() {
			this.ProductBacklogPage_ID.loadDataModel();
		}
	}
});