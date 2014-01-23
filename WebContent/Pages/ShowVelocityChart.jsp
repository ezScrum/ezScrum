<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html>
<head>
<script type="text/javascript" src="javascript/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="javascript/Chart.min.js"></script>
</head>
<body>
<canvas id="canvas" height="450" width="600" style="background:#fcf5f2;"></canvas>
<button id="download">Download</button>
</body>
<script>
	var sprints = ["Sprint1", "Sprint2", "Sprint3", "Sprint4", "Sprint5", "Sprint6", "Sprint7", "Sprint8", "Sprint9", "Sprint10"];
	var steps = 10;
	
	
	var lineChartData = {
		labels : sprints,
		datasets : [
			{
				fillColor : "rgba(255,255,255,0.0)",
				strokeColor : "rgba(52, 152, 219,1)",
				pointColor : "rgba(52, 152, 219,1)",
				data : [5,8,5,13,7,18,9,10,6,12]
			},
			{
				fillColor : "rgba(255,255,255,0.0)",
				strokeColor : "rgba(231, 76, 60,1)",
				pointColor : "rgba(231, 76, 60,1)",
				data : [10,10,10,10,10,10,10,10,10,10]
			}
		]
		
	}
	
	var options = {
			scaleGridLineColor : "rgba(0,0,0,.15)",
			scaleOverride: true,
		    scaleSteps: steps,
		    scaleStepWidth: Math.ceil(15 / steps),
		    scaleStartValue: 0,
		    bezierCurve : false
	}
	
	
	var myLine = new Chart(document.getElementById("canvas").getContext("2d")).Line(lineChartData, options);
	
	$(document).ready(function() {
		$("#download").click(function() {
			var canvas = document.getElementById("canvas");
			var img    = canvas.toDataURL("image/png");
			document.write('<img src="'+img+'"/>');
		});
	});
</script>
</html>
