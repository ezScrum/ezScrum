Ext.ns('ezScrum');

ezScrum.VelocityReleasePanel = Ext.extend(Ext.Panel, {
	id			: 'VelocityReleasePanel_ID',
	title		: 'Releases',
	height		: 300,
	width		: '25%',
	autoScroll	: true,
	bodyPadding	: 'padding: 10px;',
	initComponent: function() {
		
	},
	createCheckboxs: function() {
		var obj = this;
		var releases = [];
		Ext.Ajax.request({
			url		: 'ajaxGetReleasePlan.do',
			success	: function(response) {
				releases = Ext.decode(response.responseText).Releases;
				for(var i=0;i<releases.length;i++) {
					obj.add({
						xtype		: 'checkbox',
						id			: 'checkbox_id_'+i,
						boxLabel	: releases[i].Name,
						releaseId	: releases[i].ID
					});
				}
			}
		});
	}
});
Ext.reg('VelocityReleasePanel', ezScrum.VelocityReleasePanel);

ezScrum.VelocitySelectPanel = Ext.extend(Ext.Panel, {
	id			: 'VelocitySelectPanel_ID',
	title		: 'Selected',
	height		: 300,
	width		: '25%',
	autoScroll	: true,
	bodyPadding	: 'padding: 10px;'
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
		exportPanel.add({html:'hello'});
		exportPanel.doLayout();
	}
});
Ext.reg('VelocityControlPanel', ezScrum.VelocityControlPanel);

ezScrum.VelocityExportPanel = Ext.extend(Ext.Panel, {
	id			: 'VelocityExportPanel_ID',
	border		: true,
	layout		: 'anchor',
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