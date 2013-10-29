var SummaryPage = new Ext.Panel({
	id 			: 'Summary_Page',
	layout 		: 'anchor',
	autoScroll	: true,
    items: [
		{ ref: 'Summary_ProjectDesc_Form_ID', xtype: 'ProjectDescForm'},
		{ ref: 'Summary_TaskBoardDesc_Form_ID', xtype:	'TaskBoardDescForm'},
		{ ref: 'Summary_BurndownChart_Form_ID', xtype: 'BurndownChartForm'}
    ],
	listeners : {
		'show' : function() {
			this.Summary_ProjectDesc_Form_ID.loadDataModel();
			this.Summary_TaskBoardDesc_Form_ID.loadDataModel();
			this.Summary_BurndownChart_Form_ID.loadDataModel();
		}
	}
});