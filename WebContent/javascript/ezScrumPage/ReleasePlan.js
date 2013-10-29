var ReleasePlanPage = new Ext.Panel({
	id: 'ReleasePlan_Page',
	layout: 'fit',
	items: [{
		ref: 'ReleasePlan_ReleasePlanPage_ID',
		xtype: 'ReleasePlanPage'
	}],
	listeners: {
		'show': function() {
			this.ReleasePlan_ReleasePlanPage_ID.loadDataModel();
		}
	}
});