var VelocityPage = new Ext.Panel({
	id			: 'Velocity_Page',
	layout		: 'anchor',
	autoScroll	: true,
	items : [
	    { ref: 'VelocityChartForm_ID', xtype : 'VelocityChartForm' }
	],
	listeners : {
		'show' : function() {
			//this.VelocityChartsForm_ID.initComponent();
		}
	}
});