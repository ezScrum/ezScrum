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
    	
    	var that = this;
		
		this.StoryStore.on('load', function(s, rs) {
			var label = rs.map(function(obj) {
				return obj.data.Date.substr(5)
			})

			var idealPoint = rs.map(function(obj) {
				return obj.data.IdealPoint
			})

			var realPoint = rs.map(function(obj) {
				return obj.data.RealPoint
			})
			
			var storyChart = {
				labels : label,
				datasets : [ {
					label : 'RealPoint',
					fill : false,
					lineTension : 0,
					backgroundColor : "rgba(0, 0, 0, 0)",
					borderColor : "rgba(255, 0, 0, 1)",
					borderWidth : 4,
					pointRadius : 1.5,
					pointBackgroundColor : "rgba(255, 0, 0, 1)",
					data : realPoint,
				}, {
					label : 'IdealPoint',
					fill : false,
					lineTension : 0,
					backgroundColor : "rgba(0, 0, 0, 0)",
					borderColor : "rgba(153, 187, 232, 1)",
					borderWidth : 3.5,
					pointRadius : 1.5,
					pointBackgroundColor : "#fff",
					data : idealPoint
				} ]
			}
			
			var canvas = that.items.items[0].el.dom;
			var ctx = canvas.getContext("2d");
			var myLine = new Chart(ctx, {
				type : 'line',
				data : storyChart,
				options : {
					maintainAspectRatio : false,
					scales : {
						yAxes : [{
							ticks : {
								beginAtZero : true
							}
						}]
					},
					hover : {
						defaultFontSize : 15,
						maintainAspectRatio : false
					}
				}
			})
		})
		
		var config = {
			items : [ {
				xtype : 'box',
				autoEl : {
					tag : 'canvas',
					height : '100%',
					style: "background-color: #ffffff"
				}
			} ]
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
    	
    	var that = this;
		
		this.TaskStore.on('load', function(s, rs) {
			var label = rs.map(function(obj) {
				return obj.data.Date.substr(5)
			})

			var idealPoint = rs.map(function(obj) {
				return obj.data.IdealPoint
			})

			var realPoint = rs.map(function(obj) {
				return obj.data.RealPoint
			})
			
			var taskChart = {
				labels : label,
				datasets : [ {
					label : 'RealPoint',
					fill : false,
					lineTension : 0,
					backgroundColor : "rgba(0, 0, 0, 0)",
					borderColor : "rgba(255, 0, 0, 1)",
					borderWidth : 4,
					pointRadius : 1.5,
					pointBackgroundColor : "rgba(255, 0, 0, 1)",
					data : realPoint,
				}, {
					label : 'IdealPoint',
					fill : false,
					lineTension : 0,
					backgroundColor : "rgba(0, 0, 0, 0)",
					borderColor : "rgba(153, 187, 232, 1)",
					borderWidth : 3.5,
					pointRadius : 1.5,
					pointBackgroundColor : "#fff",
					data : idealPoint
				} ]
			}
			
			var canvas = that.items.items[0].el.dom;
			var ctx = canvas.getContext("2d");
			var myLine = new Chart(ctx, {
				type : 'line',
				data : taskChart,
				options : {
					maintainAspectRatio : false,
					scales : {
						yAxes : [{
							ticks : {
								beginAtZero : true
							}
						}]
					}
				}
			})
		})
		
		var config = {
			items : [ {
				xtype : 'box',
				autoEl : {
					tag : 'canvas',
					height : '100%',
					style: "background-color: #ffffff"
				}
			} ]
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