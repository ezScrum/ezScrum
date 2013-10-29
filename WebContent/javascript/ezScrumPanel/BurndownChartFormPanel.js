Ext.ns('ezScrum');

// StoryBurndowChart
ezScrum.StoryBurndownChart = Ext.extend(ezScrum.layout.Chart, {
	url			: 'getSprintBurndownChartData.do',
	title		: 'Story Burndown Chart',
    sprintID	: '-1',
    initComponent : function() {
    	this.StoryStore = new Ext.data.JsonStore({
    		root:'Points',
    		fields: ['Date', 'IdealPoint', 'RealPoint']
    	});
    	
		var config = {
			items: [{
		        xtype: 'linechart',
		        store: this.StoryStore,
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
		            type:'line',
		            displayName: 'Real Point',
		            yField: 'RealPoint',
		            style: {
		                color: '#FF0000'
		        	}
		        }]
			}]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.StoryBurndownChart.superclass.initComponent.apply(this, arguments);
	},
	setSprintID: function(sID) {
		this.sprintID = sID;
	},
	loadDataModel: function() {
		var obj = this;
		Ext.Ajax.request({
			url : obj.url + '?SprintID=' + obj.sprintID + '&Type=story',
			success: function(response) {
				obj.StoryStore.loadData(Ext.decode(response.responseText));
			},
			failure: function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	}
});
Ext.reg('StoryBurndownChart', ezScrum.StoryBurndownChart);


// TaskBurndowChart
ezScrum.TaskBurndownChart =  Ext.extend(ezScrum.layout.Chart, {
	url			: 'getSprintBurndownChartData.do',
	title		: 'Task Burndown Chart',
    sprintID	: '-1',   
    initComponent : function() {
    	this.TaskStore = new Ext.data.JsonStore({
    		root:'Points',
    		fields: ['Date', 'IdealPoint', 'RealPoint']
    	});
    	
		var config = {
			items: [{
		        xtype: 'linechart',
		        store: this.TaskStore,
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
		            type:'line',
		            displayName: 'Real Point',
		            yField: 'RealPoint',
		            style: {
		                color: '#FF0000'
		        	}
		        }]
			}]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.TaskBurndownChart.superclass.initComponent.apply(this, arguments);
	},
	setSprintID: function(sID) {
		this.sprintID = sID;
	},
	loadDataModel: function() {
		var obj = this;
		Ext.Ajax.request({
			url : obj.url + '?SprintID=' + obj.sprintID + '&Type=task',
			success: function(response) {
				obj.TaskStore.loadData(Ext.decode(response.responseText));
			},
			failure: function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	}
});
Ext.reg('TaskBurndownChar', ezScrum.TaskBurndownChart);


ezScrum.BurndownChartForm = Ext.extend(Ext.Panel, {
	frame		: true,
	border		: false,
	title		: 'Burndown Chart',
	sprintID	: '-1',
	height		: 350,
	layout		: 'anchor',
	bodyStyle	: 'padding: 5px',
	initComponent : function() {
		var config = {
			items: [{
				ref: 'StoryChart',
				xtype : 'StoryBurndownChart'
			}, {
				ref: 'TaskChart',
				xtype : 'TaskBurndownChar'
			}]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.BurndownChartForm.superclass.initComponent.apply(this, arguments);
	},
	setSprintID: function(ID) {
		this.sprintID = ID;
	},
	loadDataModel: function() {
		this.StoryChart.setSprintID(this.sprintID);
		this.StoryChart.loadDataModel();
		
		this.TaskChart.setSprintID(this.sprintID);
		this.TaskChart.loadDataModel();
	}
});
Ext.reg('BurndownChartForm', ezScrum.BurndownChartForm);