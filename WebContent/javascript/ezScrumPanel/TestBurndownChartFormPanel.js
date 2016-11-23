Ext.ns('ezScrum');

//StoryBurndowChart
ezScrum.StoryTestBurndownChart = Ext.extend(Ext.Panel, {
	title		: 'Story Test Burndown Chart',
	style: 'background-color: white',
	style: 'float: left; width: 50%',
    initComponent : function() {
    	var chartElement = document.createElement('canvas');
    	chartElement.id = 'bek';
    	chartElement.cls = 'x-panel-body';
    	document.body.appendChild(chartElement);
    	
    	var storyChart = {
    			label: ["11/01","11/02","11/03","11/04","11/05","11/08","11/09","11/10","11/11","11/12","11/13"],
    			dataSets:[{
    				label: 'RealPoint',
    				backgroundColor: "rgba(0, 0, 0, 0)",
    				strokeColor: "rgba(151,187,205,1)",
    				pointColor: "rgba(151,187,205,1)",
    				pointStrokeColor: "#fff",
    				data: [10,9,8,7,6,5,4,3,2,1,0]
    	          },{
    	        	label: 'IdealPoint',
    	        	backgroundColor: "rgba(0, 0, 0, 0)",
    	        	strokeColor: "rgba(255,255,205,0)",
      				pointColor: "rgba(255,187,205,0)",
      				pointStrokeColor: "#fff",
      				data: [10,10,10,5,5,5,3,3,3,1,0]
    	          }]
    	}
    	
		var config = {
			items: [{
	            height: 200,
//				bodyCfg: {
//			        tag: 'canvas',
//			        id: 'bek',
////			        cls: 'x-panel-body',  // Default class not applied if Custom element specified
//			        html: function(){
//			        	console.log("2");
//					   	console.log(document.getElementById("bek"));
//			        }()
//			    },
			    html: function(){
		        	console.log("3");
			    	console.log(document.getElementById("bek"));
			    }()
			},{
				
			}]
		}
    	
    	console.log("4");
    	console.log(document.getElementById("bek"));
    	var myLine = new Chart(chartElement,{
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