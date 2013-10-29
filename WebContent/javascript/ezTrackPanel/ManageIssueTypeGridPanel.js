var ManageIssueTypeGridPanel = new Ext.grid.GridPanel({
	id			: 'ManageIssueType_GridPanel',
	layout		: 'fit',
	colModel	: CustomIssueTypeColModel(),
	stripeRows	: true,
	enableColumnHide : false,
	store		: CustomIssueTypeStore,
	sm			: new Ext.grid.RowSelectionModel({singleSelect : true})
});