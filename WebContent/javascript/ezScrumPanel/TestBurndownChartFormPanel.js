Ext.ns('ezScrum');

//StoryBurndowChart
ezScrum.StoryTestBurndownChart = Ext.extend(Ext.Panel, {
	title		: 'Story Test Burndown Chart',
	style: 'background-color: white',
	style: 'float: left; width: 50%',
    initComponent : function() {
		var config = {
			items: [{
	            height: 200,
				bodyCfg: {
			        tag: 'div',
			        id: 'bek',
			        style: 'background-color: blue',
			        cls: 'x-panel-body',  // Default class not applied if Custom element specified
			        html: function(){
			        	var ctx = document.getElementById('bek');
			        	return ctx;
			        }()
			    }
			}]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.StoryTestBurndownChart.superclass.initComponent.apply(this, arguments);
    }
});
Ext.reg('StoryTestBurndownChart', ezScrum.StoryTestBurndownChart);

////TaskBurndowChart
//ezScrum.TaskTestBurndownChart = Ext.extend(Ext.Panel, {
//	title		: 'Task Test Burndown Chart',
//	style: 'background-color: white',
//	autoWidth: true,
//    initComponent : function() {
//		var config = {
//			items: [{
//				itemId: 'p1',
//	            height: 200,
//				bodyCfg: {
//			        tag: 'div',
//			        itemId: 'chart',
//			        width: 200,
//			        style: 'float: right',
//			        height: 75,
//			        style: 'background-color: red',
//			        cls: 'x-panel-body',  // Default class not applied if Custom element specified
//			        html: 'Message'
//			    }
//			}]
//		}
//		
//		Ext.apply(this, Ext.apply(this.initialConfig, config));
//		ezScrum.TaskTestBurndownChart.superclass.initComponent.apply(this, arguments);
//    }
//});
//Ext.reg('TaskTestBurndownChart', ezScrum.TaskTestBurndownChart);

ezScrum.BurndownChartTestForm = Ext.extend(Ext.Panel, {
	frame		: true,
	border		: false,
	title		: 'Test Burndown chart panel',
	sprintID	: '-1',
	height		: 350,
	layout		: 'anchor',
	bodyStyle	: 'padding: 5px',
	initComponent : function() {
		var config = {
			items: [{
				xtype: 'StoryTestBurndownChart'
		    },
		    {
//				xtype: 'TaskTestBurndownChart'
		    }]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.BurndownChartTestForm.superclass.initComponent.apply(this, arguments);
	},
	
});
Ext.reg('BurndownChartTestForm', ezScrum.BurndownChartTestForm);