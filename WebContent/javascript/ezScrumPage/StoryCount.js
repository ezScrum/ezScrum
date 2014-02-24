var StoryCountPage = new Ext.Panel({
	id			: 'StoryCountPage_ID',
	layout		: 'anchor',
	autoScroll	: true,
	items : [
	    { ref: 'StoryCountChartPanel_ID', xtype : 'StoryCountChartPanel' }
	],
	listeners : {
		'show' : function() {
		}
	}
});