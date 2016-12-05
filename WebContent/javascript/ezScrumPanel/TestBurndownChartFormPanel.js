Ext.ns('ezScrum');

//StoryBurndowChart
ezScrum.StoryTestBurndownChart = Ext.extend(Ext.Panel, {
	url			: 'getSprintBurndownChartData.do',
	title		: 'Story Burndown Chart',
    sprintID	: '-1',
	style: 'background-color: white',
	style: 'float: left; width: 50%',
    initComponent : function() {
    	
    	this.StoryStore = new Ext.data.JsonStore({
    		root:'Points',
    		fields: ['Date', 'IdealPoint', 'RealPoint']
    	})
    	
    	var storyChart = {
    	        labels: ["11/01","11/02","11/03","11/04","11/05","11/08","11/09","11/10","11/11","11/12","11/13"],
    	        datasets: [{
    	            label: 'RealPoint',
    		    fill: false,
    		    lineTension: 0,
    	            backgroundColor: "rgba(0, 0, 0, 0)",
    	            borderColor: "rgba(255, 0, 0, 0.5)",
    		    borderWidth: 3.5,
    	            pointRadius: 0,
    	            pointBackgroundColor: "#fff",
    	            data: [10,9,8,7,6,5,4,3,2,1,0],
    	        },{
    	            label: 'IdealPoint',
    		    fill: false,
    		    lineTension: 0,
    	            backgroundColor: "rgba(0, 0, 0, 0)",
    	            borderColor: "rgba(180, 150, 130, 1)",
    		    borderWidth: 4,
    	            pointRadius: 3,
    	            pointBackgroundColor: "rgba(180, 150, 130, 1)",
    	            data: [10,10,10,5,5,5,3,3,3,1,0]
    	        }]
    	    }
    	console.log("SprintID: " + this.sprintID);
    	
    	var options = {
    	        scales: {
    	            yAxes: [{
    	                ticks: {
    	                    beginAtZero:true
    	                }
    	            }]
    	        }
    	    }
    	
    	
    	this.redraw = function() {
    		var Date = [];
    		var IdealPoint = [];
    		var RealPoint = [];
    		for(var i=0;i < this.StoryStore.getCount(); i++)
    		{
    			Date[i] = this.StoryStore.getAt(i).get('Date');
    			IdealPoint[i] = this.StoryStore.getAt(i).get('IdealPoint');
    			console.log(this.StoryStore.getAt(i).get('RealPoint'));
    			if(this.StoryStore.getAt(i).get('RealPoint') != 'null')
    				RealPoint[i] = this.StoryStore.getAt(i).get('RealPoint');
    		}
    		storyChart.labels = Date;
    		storyChart.datasets[0].data = RealPoint;
    		storyChart.datasets[1].data = IdealPoint;
    		
    	}
    	
		var config = {
    			items: [{
    		        xtype: 'box',
    		        store: this.StoryStore,
    		        autoEl:{
    					tag: 'canvas'
//    					,height:150
    				}
    				,listeners:{
    					render:{
    						scope:this
    						,fn:function(){
    							console.log(this.items.items[0].el.dom);
    							var canvas = this.items.items[0].el.dom;
    							var ctx = canvas.getContext("2d");
    							var myLine = new Chart(ctx,{
    								store: this.StoryStore,
    					    		type: 'line',
    					    		data: storyChart,
    					    		options: options
    					    	})
    						}
    					}
    		        }
    			}]
		}
		
    	Ext.apply(this, config);
		ezScrum.StoryTestBurndownChart.superclass.initComponent.apply(this, arguments);
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
				obj.redraw();
			},
			failure: function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
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

TestChartWithCanvasPanel = new Ext.Panel({
	title:'Test',
	frame:true,
	items:{
		xtype: 'box',
		autoEL:{
			tag:'canvas'
		},
		Listeners:{
			render:{
				scope:this,
				fn:function(){
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
				ref: 'StoryChart',
				xtype: 'StoryTestBurndownChart'
		    },
		    {
//				xtype: 'TaskTestBurndownChart'
		    }]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.BurndownChartTestForm.superclass.initComponent.apply(this, arguments);
	},
	setSprintID: function(ID) {
		console.log("OutSideSetId");
		this.sprintID = ID;
	},
	loadDataModel: function() {
		console.log("OutSideCallLoadData");
		this.StoryChart.setSprintID(this.sprintID);
		this.StoryChart.loadDataModel();
		
//		this.TaskChart.setSprintID(this.sprintID);
//		this.TaskChart.loadDataModel();
	}
	
});
Ext.reg('BurndownChartTestForm', ezScrum.BurndownChartTestForm);