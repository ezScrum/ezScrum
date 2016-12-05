Ext.ns('ezScrum');

// StoryBurndowChart
ezScrum.StoryTestBurndownChart = Ext.extend(Ext.Panel, {
	title : 'Story Test Burndown Chart',
	id : 'bar',
	style : 'background-color: white',
	sprintID : '-1',
	url : 'getSprintBurndownChartData.do',
	style : 'float: left; width: 50%',
	initComponent : function() {

		this.StoryStore = new Ext.data.JsonStore({
			root : 'Points',
			fields : [ 'Date', 'IdealPoint', 'RealPoint' ]
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
					borderColor : "rgba(180, 150, 130, 1)",
					borderWidth : 4,
					pointRadius : 3,
					pointBackgroundColor : "rgba(180, 150, 130, 1)",

					data : realPoint,
				}, {
					label : 'IdealPoint',
					fill : false,
					lineTension : 0,
					backgroundColor : "rgba(0, 0, 0, 0)",
					borderColor : "rgba(255, 0, 0, 0.5)",
					borderWidth : 3.5,
					pointRadius : 0,
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
					scales : {
						yAxes : [ {
							ticks : {

								beginAtZero : true
							}
						} ]
					}
				}
			})

		})
		var config = {
			items : [ {
				xtype : 'box',
				autoEl : {
					tag : 'canvas',
					height : 150
				}
			} ]
		}

		Ext.apply(this, config);
		ezScrum.StoryTestBurndownChart.superclass.initComponent.apply(this,
				arguments);
	},
	setSprintID : function(sID) {
		this.sprintID = sID;
	},
	loadDataModel : function() {
		var obj = this;
		Ext.Ajax.request({
			url : obj.url + '?SprintID=' + obj.sprintID + '&Type=story',
			success : function(response) {
				obj.StoryStore.loadData(Ext.decode(response.responseText));
			},
			failure : function() {
				Ext.example.msg('Server Error',
						'Sorry, the connection is failure.');
			}
		});
	}
});
Ext.reg('StoryTestBurndownChart', ezScrum.StoryTestBurndownChart);

// //TaskBurndowChart
// ezScrum.TaskTestBurndownChart = Ext.extend(Ext.Panel, {
// title : 'Task Test Burndown Chart',
// style: 'background-color: white',
// autoWidth: true,
// initComponent : function() {
// var config = {
// items: [{
// itemId: 'p1',
// height: 200,
// bodyCfg: {
// tag: 'div',
// itemId: 'chart',
// width: 200,
// style: 'float: right',
// height: 75,
// style: 'background-color: red',
// cls: 'x-panel-body', // Default class not applied if Custom element specified
// html: 'Message'
// }
// }]
// }
//		
// Ext.apply(this, Ext.apply(this.initialConfig, config));
// ezScrum.TaskTestBurndownChart.superclass.initComponent.apply(this,
// arguments);
// }
// });
// Ext.reg('TaskTestBurndownChart', ezScrum.TaskTestBurndownChart);

TestChartWithCanvasPanel = new Ext.Panel(
		{
			title : 'Test',
			frame : true,
			items : {
				xtype : 'box',
				autoEL : {
					tag : 'canvas'
				},
				Listeners : {
					render : {
						scope : this,
						fn : function() {
							var canvas = TestChartWithCanvasPanel.items.items[0].el.dom;
							var ctx = canvas.getContext("2d");
							ctx.fillStyle = "red";

							ctx.beginPath();
							ctx.moveTo(30, 30);
							ctx.lineTo(150, 150);
							ctx.bezierCurveTo(60, 70, 60, 70, 70, 150);
							ctx.lineTo(30, 30);
							ctx.fill();
						}
					}
				}
			}
		});
Ext.reg('TestChartWithCanvasPanel', TestChartWithCanvasPanel);

ezScrum.BurndownChartTestForm = Ext.extend(Ext.Panel, {
	frame : true,
	border : false,
	title : 'Test Burndown chart panel',
	sprintID : '-1',
	height : 550,
	layout : 'anchor',
	bodyStyle : 'padding: 5px',
	initComponent : function() {
		var config = {
			items : [ {
				ref : 'StoryChart',
				xtype : 'StoryTestBurndownChart'
			}, {
			// xtype: 'TaskTestBurndownChart'
			} ]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.BurndownChartTestForm.superclass.initComponent.apply(this,
				arguments);
	},
	setSprintID : function(ID) {
		this.sprintID = ID;
	},
	loadDataModel : function() {
		this.StoryChart.setSprintID(this.sprintID);
		this.StoryChart.loadDataModel();

	}

});
Ext.reg('BurndownChartTestForm', ezScrum.BurndownChartTestForm);