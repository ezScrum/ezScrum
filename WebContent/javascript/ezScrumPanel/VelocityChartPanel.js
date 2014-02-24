Ext.ns('ezScrum');

ezScrum.VelocityReleasePanel = Ext.extend(Ext.Panel, {
	id			: 'VelocityReleasePanel_ID',
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
		ezScrum.VelocityReleasePanel.superclass.initComponent.apply(this, arguments);
		
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
		var selectedPanel = Ext.getCmp('VelocitySelectPanel_ID');
		
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
Ext.reg('VelocityReleasePanel', ezScrum.VelocityReleasePanel);

ezScrum.VelocitySelectPanel = Ext.extend(Ext.Panel, {
	id			: 'VelocitySelectPanel_ID',
	title		: 'Selected',
	height		: 300,
	width		: '25%',
	autoScroll	: true,
	bodyStyle	: 'padding: 10px;'
});
Ext.reg('VelocitySelectPanel', ezScrum.VelocitySelectPanel);

ezScrum.VelocityControlPanel = Ext.extend(Ext.Panel, {
	id			: 'VelocityControlPanel_ID',
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
			    	xtype: 'VelocityReleasePanel', 
			    	ref: 'VelocityReleasePanel_ID'
			    }, {
			    	html: '>>',
			    	border: false,
			    	bodyStyle: 'margin:140px 50px 0px 50px'
			    }, {
			    	xtype: 'VelocitySelectPanel',
			    	ref: 'VelocitySelectPanel_ID'
			    }, {
			    	xtype: 'button',
			    	text: 'Export',
			    	handler: this.doExport,
			    	style: 'margin: 275px 0px 0px 50px;'
			    }
			]
		}
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.VelocityControlPanel.superclass.initComponent.apply(this, arguments);
	},
	doExport: function() {
		var exportPanel = Ext.getCmp('VelocityExportPanel_ID');
		var releasePanel = Ext.getCmp('VelocityReleasePanel_ID');
		
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
				html: '<iframe src="showVelocityChart.do?' + queryString + '" width="820" height="650" frameborder="0" scrolling="auto"></iframe>',
				border: false
			}
		);
		exportPanel.doLayout();
	}
});
Ext.reg('VelocityControlPanel', ezScrum.VelocityControlPanel);

ezScrum.VelocityExportPanel = Ext.extend(Ext.Panel, {
	id			: 'VelocityExportPanel_ID',
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
Ext.reg('VelocityExportPanel', ezScrum.VelocityExportPanel);

ezScrum.VelocityChartPanel = Ext.extend(Ext.Panel, {
	id			: 'VelocityChartPanel_ID',
	title		: 'Velocity Chart',
	border		: false,
	layout		: 'anchor',
	initComponent: function() {
		var config = {
			items: [{
					xtype	: 'VelocityControlPanel', 
					ref		: 'VelocityControlPanel_ID'
				}, {
					xtype	: 'VelocityExportPanel',
					ref		: 'VelocityExportPanel_ID'
				}
			]
		}
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.VelocityChartPanel.superclass.initComponent.apply(this, arguments);
	}
});
Ext.reg('VelocityChartPanel', ezScrum.VelocityChartPanel)