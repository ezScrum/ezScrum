//the form for Velocity Chart Page
VelocityChartFormLayout = Ext.extend(Ext.form.FormPanel, {
	id				: 'VelocityChart_Form',
	border			: false,
	frame			: true,
	layout			: 'anchor',
	store			: VelocityStore,
	title			: 'Velocity Chart Export',
	bodyStyle		: 'padding: 0px',
	labelAlign		: 'right',
	buttonAlign		: 'left',
	initComponent : function() {
		var config = {
			url			: '.do',
			modify_url	: '.do',	
			items   : [],
			buttons : [{
				scope	: this,
				text 	: 'Export',
				handler	: this.doExport
			}]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		VelocityChartFormLayout.superclass.initComponent.apply(this, arguments);
		this.createCheckboxs();
	},
	// loadDataModel: function() {
	// 	var obj = this;
	// 	var loadmask = new Ext.LoadMask(this.getEl(), {msg: "loading info..."});
	// 	loadmask.show();
	// 	this.createCheckboxs();
	// 	loadmask.hide();
	// },
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
	},
	doExport: function() {
		var obj = this;
		var checked = [];
		var queryString = "PID=test02&releases=";
		for(var i=0;i<this.items.length;i++) {
			if(this.get(i).checked) {
				checked.push(this.get(i).releaseId);
			}
		}
		for (var i = 0; i < checked.length; i++) {
			queryString += checked[i];
			if (i != checked.length - 1) {
				queryString += ",";
			}
		};

		if (checked.length != 0) {
			// var EObj = document.getElementById("scheduleReport");
			// console.log(EObj);
			// if (EObj != null) {
			// 	console.log("here");
			// 	EObj.parentNode.removeChild(EObj);
			// }
			obj.add({
				id	: 'scheduleReport',
				html: '<iframe id="scheduleReport" name="scheduleReport" src="showVelocityChart.do?' + queryString + '" width="650" height="550" frameborder="0" scrolling="auto"></iframe>'
			});
		}
		this.doLayout();
	}
});

Ext.reg('VelocityChartForm', VelocityChartFormLayout);