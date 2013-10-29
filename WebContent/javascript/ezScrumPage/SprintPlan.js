var SprintPlanPage = new Ext.Panel({
	id		: 'SprintPlan_Page',
	layout 	: 'fit',
	items : [
	    { ref: 'SprintPlan_SprintPlanPage_ID', xtype : 'SprintPlanPage' }
	],
	listeners : {
		'show' : function() {
			this.SprintPlan_SprintPlanPage_ID.loadDataModel();
		}
	}
});