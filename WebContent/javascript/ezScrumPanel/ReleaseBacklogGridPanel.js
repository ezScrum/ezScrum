/**
 * 
 */
Ext.ns('ezScrum');

ezScrum.ReleaseBacklogGrid = Ext.extend(Ext.grid.GridPanel, {
	id: 'releaseBacklogGridPanel',
	title: 'Story List',
	store: ReleaseBacklogStoryStore,
	colModel: ReleaseBacklogColumns(),
	plugins: [ReleasebacklogGridFilter, ReleaseBacklogGridExpander],
	layout: 'fit',
	height: 460,
	releaseID: '-1',
	initComponent: function() {
		var config = {
			bbar: new Ext.PagingToolbar({
				pageSize: pageSize_ReleaseBacklog,
				store: ReleaseBacklogStoryStore,
				displayInfo: true,
				displayMsg: 'Displaying topics {0} - {1} of {2}',
				emptyMsg: "No topics to display",
				items: [{
					text: 'Expand All',
					icon: 'images/folder_out.png',
					handler: function() {
						ReleaseBacklogGridExpander.expandAll();
					}
				}, {
					text: 'Collapse All',
					icon: 'images/folder_into.png',
					handler: function() {
						ReleaseBacklogGridExpander.collapseAll();
					}
				}]
			})
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.ReleaseBacklogGrid.superclass.initComponent.apply(this, arguments);
	},
	setReleaseID: function(rID) {
		this.releaseID = rID;
	},
	loadDataModel: function() {

		var obj = this;
		var loadmask = new Ext.LoadMask(this.getEl(), {
			msg: "loading info..."
		});
		loadmask.show();

		Ext.Ajax.request({
			url: 'AjaxShowStoryfromRelease.do?Rid=' + this.releaseID,
			success: function(response) {

				ReleaseBacklogStoryStore.proxy.data = response;
				ReleaseBacklogStoryStore.proxy.reload = true;

				ReleaseBacklogStoryStore.load({
					params: {
						start: 0,
						limit: pageSize_ReleaseBacklog
					}
				});

				loadmask.hide();
			},
			failure: function(response) {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
				loadmask.hide();
			}
		});
	}
});
Ext.reg('ReleaseBacklogGridPanel', ezScrum.ReleaseBacklogGrid);