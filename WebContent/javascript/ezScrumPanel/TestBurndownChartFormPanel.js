Ext.ns('ezScrum');

//StoryBurndowChart
ezScrum.StoryTestBurndownChart = Ext.extend(Ext.Panel, {
	title		: 'Story Test Burndown Chart',
	id: 'bar',
	style: 'background-color: white',
	style: 'float: left; width: 50%',
    initComponent : function() {
    	
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
    	
    	
		var config = {
    			items: [{
    		        xtype: 'box',
    		        autoEl:{
    					tag: 'canvas'
    					,height:150
    				}
    				,listeners:{
    					render:{
    						scope:this
    						,fn:function(){
    							console.log(this.items.items[0].el.dom);
    							var canvas = this.items.items[0].el.dom;
    							var ctx = canvas.getContext("2d");
    							var myLine = new Chart(ctx,{
    					    		type: 'line',
    					    		data: storyChart,
    					    		options: {
    					    	        scales: {
    					    	            yAxes: [{
    					    	                ticks: {

    					    	                    beginAtZero:true
    					    	                }
    					    	            }]
    					    	        }
    					    	    }
    					    	})
    						}
    					}
    		        }
    			}]
		}
    	
    	console.log("4");
    	console.log(document.getElementById("bek"));

		
    	Ext.apply(this, config);
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
	
});
Ext.reg('BurndownChartTestForm', ezScrum.BurndownChartTestForm);