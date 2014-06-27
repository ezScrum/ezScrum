Ext.ns('ezScrum');

//ezScrum.ContentPanel = new Ext.Panel({
ezScrum.ContentPanel = new ezScrum.projectLeftTree.contentPanel({
	region		: 'center',		// position
	id			: 'content_panel',
	layout		: 'card',
	margins		: '3 0 0 0',
	activeItem	: 0,
    collapsible	: false,
	border		: false,
	frame		: false,
	items : [
	    // Project Configuration index
	    // 0,        1,                2,
	    SummaryPage, ModifyConfigPage, MembersPage,
	    
	    // Project Management index
	    // 3,               4,			     5,              6,                 7,             8,                 9,             10,
	    ProductBacklogPage, ReleasePlanPage, SprintPlanPage, SprintBacklogPage, TaskBoardPage, RetrospectivePage, UnplannedPage, ezScrumReportPage
	]
});