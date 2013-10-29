// the form is for Unplanned Item Page
ezScrum.UnplannedGrid = Ext.extend(Ext.grid.GridPanel, {
	id		: 'UnplannedItem_Panel',
	url		: 'GetUnplannedItems.do',
	border	: false,
	bodyStyle	:'width:100%',
	autoWidth	: true,
	autoHeight	: true,
	stripeRows	: true,
	store	: UnplannedItemStore,
	colModel: new UnplannedItemColumnModel(),
	plugins : [UnplannedItemFilters],
	sm		: new Ext.grid.RowSelectionModel({singleSelect:true}),
	viewConfig: {
        forceFit: true
    },
	loadDataModel: function(sID) {
		MainLoadMaskShow();
		
		var obj = this;
		Ext.Ajax.request({
			url: obj.url,
			params: {SprintID: sID},
			success : function(response) {
				UnplannedItemStore.loadData(response.responseXML);
				
				MainLoadMaskHide();
			},
			failure : function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	},
	reloadDataModel: function(sID, recordID) {
		var obj = this;
		Ext.Ajax.request({
			url: obj.url,
			params: {SprintID: sID},
			success : function(response) {
				UnplannedItemStore.loadData(response.responseXML);
				
				// set highlight record
				var NewGridIndex = obj.getStore().indexOfId(recordID);
				obj.getSelectionModel().selectRow(NewGridIndex);
				obj.getView().focusRow(NewGridIndex);
			},
			failure : function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	}
});
Ext.reg('UnplannedGridPanel', ezScrum.UnplannedGrid);