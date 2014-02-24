Ext.ns('ezScrum');

ezScrum.StoryCountReleasePanel = Ext.extend(Ext.Panel, {
	id			: 'StoryCountReleasePanel_ID',
	title		: 'Releases',
	height		: 300,
	width		: '25%',
	autoScroll	: true,
	bodyStyle	: 'padding: 10px;',
	releases	: [],
	initComponent: function() {
		var config = {
				items:[]
		}
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.StoryCountReleasePanel.superclass.initComponent.apply(this, arguments);
		
		this.createCheckboxs();
	},
	createCheckboxs: function() {
		var obj = this;
		Ext.Ajax.request({
			url		: 'ajaxGetReleasePlan.do',
			success	: function(response) {
				obj.releases = Ext.decode(response.responseText).Releases;
				for(var i=0; i<obj.releases.length; i++) {
					obj.add({
						xtype		: 'checkbox',
						id			: 'checkbox_id_'+i,
						boxLabel	: obj.releases[i].Name,
						releaseId	: obj.releases[i].ID,
						listeners	: {
							check: function(checkbox, value) {
								obj.checkChange();
							}
						}
					});
				}
			}
		});
	},
	checkChange: function() {
		var obj = this;
		var selectedPanel = Ext.getCmp('StoryCountSelectPanel_ID');
		
		selectedPanel.removeAll();
		for(var i=0; i<obj.items.length; i++) { 
			if(obj.get(i).checked) {
				selectedPanel.add({
					html: obj.get(i).boxLabel,
					style: 'margin: 0px 0px 3px 0px;',
					border: false
				});
			}
		}
		selectedPanel.doLayout();
	}
});
Ext.reg('StoryCountReleasePanel', ezScrum.StoryCountReleasePanel);

ezScrum.StoryCountSelectPanel = Ext.extend(Ext.Panel, {
	id			: 'StoryCountSelectPanel_ID',
	title		: 'Selected',
	height		: 300,
	width		: '25%',
	autoScroll	: true,
	bodyStyle	: 'padding: 10px;'
});
Ext.reg('StoryCountSelectPanel', ezScrum.StoryCountSelectPanel);

ezScrum.StoryCountControlPanel = Ext.extend(Ext.Panel, {
	id			: 'StoryCountControlPanel_ID',
	border		: false,
	layout		: {
		type: 'hbox',
		pack: 'center',
		align: 'top'
	},
	height		: 350,
	style		: 'padding: 15px;',
	initComponent: function() {
		var config = {
			items: [{
			    	xtype: 'StoryCountReleasePanel', 
			    	ref: 'StoryCountReleasePanel_ID'
			    }, {
			    	html: '>>',
			    	border: false,
			    	bodyStyle: 'margin:140px 50px 0px 50px'
			    }, {
			    	xtype: 'StoryCountSelectPanel',
			    	ref: 'StoryCountSelectPanel_ID'
			    }, {
			    	xtype: 'button',
			    	text: 'Export',
			    	handler: this.doExport,
			    	style: 'margin: 275px 0px 0px 50px;'
			    }
			]
		}
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.StoryCountControlPanel.superclass.initComponent.apply(this, arguments);
	},
	doExport: function() {
		var exportPanel = Ext.getCmp('StoryCountExportPanel_ID');
		var releasePanel = Ext.getCmp('StoryCountReleasePanel_ID');
		
		//組合query string
		var checked = [];
		var queryString = "PID=" + getURLParameter("PID") + "&releases=";
		for(var i=0; i<releasePanel.items.length; i++) {
			if(releasePanel.get(i).checked) {
				checked.push(releasePanel.get(i).releaseId);
			}
		}
		for (var i = 0; i<checked.length; i++) {
			queryString += checked[i];
			if (i != checked.length - 1) {
				queryString += ",";
			}
		};
		
		if(checked.length === 0) {
			alert('Please select one release at least.');
			return;
		}
		
		exportPanel.removeAll();
		exportPanel.add({
				html: '<iframe src="showStoryCountChart.do?' + queryString + '" width="820" height="650" frameborder="0" scrolling="auto"></iframe>',
				border: false
			}
		);
		exportPanel.doLayout();
	}
});
Ext.reg('StoryCountControlPanel', ezScrum.StoryCountControlPanel);

ezScrum.StoryCountExportPanel = Ext.extend(Ext.Panel, {
	id			: 'StoryCountExportPanel_ID',
	border		: true,
	layout		: {
		type: 'hbox',
		pack: 'center',
		align: 'top'
	},
	autoHeight	: true,
	width		: '100%',
	autoScroll	: true
});
Ext.reg('StoryCountExportPanel', ezScrum.StoryCountExportPanel);

ezScrum.StoryCountChartPanel = Ext.extend(Ext.Panel, {
	id			: 'StoryCountChartPanel_ID',
	title		: 'Story Count Chart',
	border		: false,
	layout		: 'anchor',
	initComponent: function() {
		var config = {
			items: [{
					xtype	: 'StoryCountControlPanel', 
					ref		: 'StoryCountControlPanel_ID'
				}, {
					xtype	: 'StoryCountExportPanel',
					ref		: 'StoryCountExportPanel_ID'
				}
			]
		}
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.StoryCountChartPanel.superclass.initComponent.apply(this, arguments);
	}
});
Ext.reg('StoryCountChartPanel', ezScrum.StoryCountChartPanel)