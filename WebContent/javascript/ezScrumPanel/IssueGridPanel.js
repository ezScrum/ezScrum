/**
 * for ProductBacklog, ScrumIssue, CustomIssue
 */

Ext.ns('ezScrum');

// Product Backlog Panel
ezScrum.IssueGridPanel = Ext.extend(Ext.grid.GridPanel, {
	id: 'issueGridPanel', // please override
	layout: 'fit',
	enableColumnHide: false,
	viewConfig: {
		forceFit: true
	},
	plugins: [IssueGridPanelFilter, ProductBacklogExpander],
	sm: new Ext.grid.RowSelectionModel({
		singleSelect: true
	}),
	stripeRows: false,
	frame: true,
	deleteRecord: function(id) {
		var record = this.getStore().getById(id);
		this.getStore().remove(record);
		this.getStore().proxy.deleteRecord(id);
	},
	addRecord: function(record) {
		this.getStore().insert(0, record);
		var id = record.data['Id'];
		var index = this.getStore().indexOfId(id);
		this.getSelectionModel().selectRow(index);
		this.getView().focusRow(index);
		this.getStore().proxy.insertRecord(record);
	}
});
