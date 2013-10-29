// ReleaseStoryBurndowChart
ezScrum.ReleaseStoryBurndownChart = Ext.extend(ezScrum.layout.Chart, {
	url: 'GetReleaseBurndownChartData.do',
	title: 'Story Counts Burndown Chart',
	releaseID: '-1',
	width: 685,
	initComponent: function() {
		this.ReleaseStoryStore = new Ext.data.JsonStore({
			root: 'Points',
			fields: ['Date', 'IdealPoint', 'RealPoint']
		});

		var config = {
			items: [{
				xtype: 'linechart',
				store: this.ReleaseStoryStore,
				xField: 'Date',
				yField: 'IdealPoint',

				series: [{
					type: 'line',
					displayName: 'Ideal Point',
					yField: 'IdealPoint',
					style: {
						color: '#99bbe8'
					}
				}, {
					type: 'line',
					displayName: 'Real Point',
					yField: 'RealPoint',
					style: {
						color: '#FF0000'
					}
				}]
			}]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.ReleaseStoryBurndownChart.superclass.initComponent.apply(this, arguments);
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
			scope: this,
			url: this.url + '?ReleaseID=' + this.releaseID,
			success: function(response) {
				this.ReleaseStoryStore.loadData(Ext.decode(response.responseText));
				loadmask.hide();
			},
			failure: function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
				loadmask.hide();
			}
		});
	}
});
Ext.reg('ReleaseStoryBurndownChart', ezScrum.ReleaseStoryBurndownChart);

ezScrum.ReleaseStoryCountBurndownChartFormPanel = Ext.extend(Ext.Panel, {
	frame: true,
	border: false,
	title: 'Story Counts Burndown Chart',
	releaseID: '-1',
	layout: 'hbox',
	bodyStyle: 'padding: 5px',
	initComponent: function() {
		var config = {
			items: [{
				ref: 'ReleaseStoryChart',
				xtype: 'ReleaseStoryBurndownChart'
			}]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.ReleaseStoryCountBurndownChartFormPanel.superclass.initComponent.apply(this, arguments);
	},
	setReleaseID: function(rID) {
		this.releaseID = rID;
	},
	loadDataModel: function() {
		this.ReleaseStoryChart.setReleaseID(this.releaseID);
		this.ReleaseStoryChart.loadDataModel();
	}
});
Ext.reg('ReleaseStoryBurndownChartForm', ezScrum.ReleaseStoryCountBurndownChartFormPanel);