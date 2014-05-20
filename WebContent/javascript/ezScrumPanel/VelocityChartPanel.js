Ext.ns('ezScrum');

ezScrum.VelocityReleasePanel = Ext.extend(Ext.Panel, {
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
		var username = this.getCookie("username");
		var userpwd = this.getCookie("userpwd");
		
		Ext.Ajax.request({
			url		: '/ezScrum/web-service/' + getURLParameter("PID") + '/release-plan/all?userName=' + username + '&password=' + userpwd,
			success	: function(response) {
				obj.releases = Ext.decode(response.responseText);
				for(var i=0; i<obj.releases.length; i++) {
					obj.add({
						xtype		: 'checkbox',
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
		var exportbutton = Ext.getCmp('VelocityExportButton');
		
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
		if(selectedPanel.items.length > 0){
			exportbutton.enable();
		} else {
			exportbutton.disable();
		}
		selectedPanel.doLayout();
	},
	getCookie: function(cname) {
		var name = cname + "=";
		var ca = document.cookie.split(';');
		for(var i=0; i<ca.length; i++) {
			var c = ca[i].trim();
			if (c.indexOf(name)==0) return c.substring(name.length,c.length-1).replace('"', '');
		}
		return "";
	}
});
Ext.reg('VelocityReleasePanel', ezScrum.VelocityReleasePanel);

ezScrum.VelocitySelectPanel = Ext.extend(Ext.Panel, {
	title		: 'Selected',
	height		: 300,
	width		: '25%',
	autoScroll	: true,
	bodyStyle	: 'padding: 10px;'
});
Ext.reg('VelocitySelectPanel', ezScrum.VelocitySelectPanel);

ezScrum.VelocityControlPanel = Ext.extend(Ext.Panel, {
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
			    	xtype	: 'VelocityReleasePanel', 
			    	ref		: 'VelocityReleasePanel_ID',
			    	id		: 'VelocityReleasePanel_ID'
			    }, {
			    	html	: '>>',
			    	border	: false,
			    	bodyStyle: 'margin:140px 50px 0px 50px'
			    }, {
			    	xtype	: 'VelocitySelectPanel',
			    	ref		: 'VelocitySelectPanel_ID',
			    	id		: 'VelocitySelectPanel_ID'
			    }, {
			    	xtype	: 'button',
			    	text	: 'Export',
			    	handler	: this.doExport,
			    	disabled: true,
			    	style	: 'margin: 275px 0px 0px 50px;',
			    	id      : 'VelocityExportButton'
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
				html: '<iframe src="showVelocityChart.do?' + queryString + '&PID=' + getURLParameter("PID") + '" width="820" height="650" frameborder="0" scrolling="auto"></iframe>',
				border: false
			}
		);
		exportPanel.doLayout();
	}
});
Ext.reg('VelocityControlPanel', ezScrum.VelocityControlPanel);

ezScrum.VelocityExportPanel = Ext.extend(Ext.Panel, {
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
	title		: 'Velocity Chart',
	border		: false,
	layout		: 'anchor',
	initComponent: function() {
		var config = {
			items: [{
					xtype	: 'VelocityControlPanel', 
					ref		: 'VelocityControlPanel_ID',
					id		: 'VelocityControlPanel_ID'
				}, {
					xtype	: 'VelocityExportPanel',
					ref		: 'VelocityExportPanel_ID',
					id		: 'VelocityExportPanel_ID'
				}
			]
		}
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.VelocityChartPanel.superclass.initComponent.apply(this, arguments);
	}
});
Ext.reg('VelocityChartPanel', ezScrum.VelocityChartPanel)