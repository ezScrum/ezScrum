//the form for StoryCount Chart Page
StoryCountChartFormLayout = Ext.extend(Ext.form.FormPanel, {
	id				: 'StoryCountChart_Form',
	border			: false,
	frame			: true,
	layout			: 'anchor',
	title			: 'StoryCount Chart Export',
	bodyStyle		: 'padding: 0px',
	labelAlign		: 'right',
	buttonAlign		: 'left',
	initComponent : function() {
		var config = {
			items   : [],
			buttons : [{
				scope	: this,
				text 	: 'Export',
				handler	: this.doExport
			}]
		}
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		StoryCountChartFormLayout.superclass.initComponent.apply(this, arguments);
		this.createCheckboxs();
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
	},
	doExport: function() {
		var checked = [];
		var queryString = "PID=" + getURLParameter("PID") + "&releases=";
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
			this.add({
				id	: 'velocityChart',
				html: '<iframe id="velocityChart" name="velocityChart" src="showVelocityChart.do?' + queryString + '" width="650" height="550" frameborder="0" scrolling="auto"></iframe>'
			});
		}
		this.doLayout();
	}
});

Ext.reg('StoryCountChartForm', StoryCountChartFormLayout);