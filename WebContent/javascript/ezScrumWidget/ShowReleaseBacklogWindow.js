/**
 * for Release Backlog page Show Release Backlog
 */

Ext.ns('ezScrum');
Ext.ns('ezScrum.layout');
Ext.ns('ezScrum.window');

ezScrum.window.ShowReleaseBacklogWindow = Ext.extend(ezScrum.layout.Window, {
	id: 'ShowReleaseBacklog_Window_ID',
	layout: 'anchor',
	resizable: true,
	frame: true,
	border: true,
	autoScroll: true,
	initComponent: function() {
		var config = {
			items: [{
				ref: 'ReleaseStoryBurndownChart_refID',
				xtype: 'ReleaseStoryBurndownChart'
			}, {
				ref: 'ReleaseBacklogGridPanel_refID',
				xtype: 'ReleaseBacklogGridPanel'
			}],
			buttons: [{
				text: 'Close',
				scope: this,
				handler: function() {
					this.hide();
				}
			}]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.window.ShowReleaseBacklogWindow.superclass.initComponent.apply(this, arguments);
	},
	showWindow: function(releaseID, releaseGoal) {
		// 先show, 個別元件才能 mask.show
		this.show();

		this.setTitle(releaseGoal);

		// Release Story Burndown Chart
		this.ReleaseStoryBurndownChart_refID.setReleaseID(releaseID);
		this.ReleaseStoryBurndownChart_refID.loadDataModel();

		// Release Backlog Story List
		this.ReleaseBacklogGridPanel_refID.setReleaseID(releaseID);
		this.ReleaseBacklogGridPanel_refID.loadDataModel();

	}

});

var ShowReleaseBacklog_Window = new ezScrum.window.ShowReleaseBacklogWindow();
