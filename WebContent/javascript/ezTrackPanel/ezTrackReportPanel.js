var ezTrackReportTabForm = {
	xtype: 'tabpanel',
	activeTab: 0,
	items:[{
    	title: 'Bar Chart',
    	html: '<iframe src=showBarChartReport.do width="100%" height="100%" frameborder="0" scrolling="auto"></iframe>'
	},
	{
    	title: 'Flow Diagram',
    	html: '<iframe src=showFlowDiagramReport.do width="100%" height="100%" frameborder="0" scrolling="auto"></iframe>'
	}]
};