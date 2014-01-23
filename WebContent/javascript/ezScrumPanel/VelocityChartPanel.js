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
	        	scope    : this,
	        	text     : 'Export',
	        	handler  : this.doExport
	        }]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		VelocityChartFormLayout.superclass.initComponent.apply(this, arguments);
		this.createCheckboxs();
	},
	loadDataModel: function() {
		var obj = this;
		var loadmask = new Ext.LoadMask(this.getEl(), {msg: "loading info..."});
		loadmask.show();
		this.createCheckboxs();
		loadmask.hide();
	},
	createCheckboxs: function() {
		var obj = this;
		for(var i=0;i<4;i++) {
			obj.add({xtype: 'checkbox', boxLabel:'checkbox_'+i, id: 'checkbox_id_'+i});
		}
		obj.add({
    		id   : 'scheduleReport',
    		url  : 'showVelocityChart.do?PID=Project1',
    		html : '<iframe id="scheduleReport" name="scheduleReport" src="showVelocityChart.do?PID=Project1" width="100%" height="100%" frameborder="0" scrolling="auto"></iframe>'
    	});
	},
	doExport: function() {
		var checked = [];
		for(var i=0;i<this.items.length;i++) {
			if(this.get(i).checked) {
				checked.push(this.get(i).id);
			}
		}
		console.log(checked);
		
		var chart = new Ext.chart.Chart({
			height	: 600,
			width	: 800
		});
		this.add();
	}
});

Ext.reg('VelocityChartForm', VelocityChartFormLayout);