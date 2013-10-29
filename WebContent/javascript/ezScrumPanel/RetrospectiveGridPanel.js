// the form is for Retrospective Page
ezScrum.RetrospectiveGrid = Ext.extend(Ext.grid.GridPanel, {
	id		: 'Retrospective_Panel',
	url		: 'showRetrospective2.do',
	border	: false,
	frame	: false,
	bodyStyle	:'width:100%',
	autoWidth	: true,
	autoHeight	: true,
	autoScroll	: true,
	stripeRows	: true,
	store	: RetrospectiveStore,
	colModel: new RetrospectiveColumnModel(),
	plugins : [RetrospectiveFilters],
	sm		: new Ext.grid.RowSelectionModel({singleSelect:true}),
	view	: new Ext.grid.GroupingView({
        forceFit: true,
        groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
    }),
	viewConfig: {
        forceFit: true
    },
	loadDataModel: function(sID) {
		var obj = this;
		
		MainLoadMaskShow();
		Ext.Ajax.request({
			url: obj.url,
			params: {sprintID: sID},
			success : function(response) {
				RetrospectiveStore.loadData(response.responseXML);
				
				MainLoadMaskHide();
			},
			failure : function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	}
});
Ext.reg('RetrospectiveGridPanel', ezScrum.RetrospectiveGrid);