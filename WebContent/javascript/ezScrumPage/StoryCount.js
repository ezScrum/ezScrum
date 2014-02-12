var StoryCountPage = new Ext.Panel({
	id			: 'Story_Count_Page',
	layout		: 'anchor',
	autoScroll	: true,
	items : [
	    { ref: 'StoryCountChartForm_ID', xtype : 'StoryCountChartForm' }
	],
	listeners : {
		'show' : function() {
		}
	}
});