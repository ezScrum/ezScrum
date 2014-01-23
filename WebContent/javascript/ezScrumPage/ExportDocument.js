var ExportDocumentPage = new Ext.Panel({
	id			: 'Export_Document_Page',
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