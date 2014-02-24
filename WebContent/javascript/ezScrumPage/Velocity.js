var VelocityPage = new Ext.Panel({
	id			: 'Velocity_Page',
	layout		: 'anchor',
	autoScroll	: true,
	items : [
	    { ref: 'VelocityChartPanel_ID', xtype : 'VelocityChartPanel' }
	],
	listeners : {
		'show' : function() {
			//this.VelocityChartsForm_ID.initComponent();
		}
	}
});