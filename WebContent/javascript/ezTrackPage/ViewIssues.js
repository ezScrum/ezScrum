var ViewIssueTabForm = {
	xtype: 'tabpanel',
	activeTab: 0,
	items:[
	]		
}

var ViewIssuesPage = new Ext.Panel({
	id			: 'ViewIssues_Page',
    autoScroll	: true,
    layout		: 'fit',
    frame		: false,
    items:[
         ViewIssueTabForm
	]
});
