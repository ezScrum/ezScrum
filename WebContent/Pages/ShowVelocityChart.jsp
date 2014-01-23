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
	$(document).ready(function() {
		console.log(document.URL.split("?")[0]);
		$.ajax({
			url : "/ezScrum/ajaxGetVelocity.do?" + document.URL.split("?")[0],
			type : "GET",
			success : function(data) {
				var sprints = data.Sprints;
				var steps = sprints.length;
				var velocitys = [];
				var averages = [];
				var labelname = [];
				var max = 0;
				for (var i = 0; i < sprints.length; i++) {
					velocitys[i] = sprints[i].Velocity;
					averages[i] = data.Average;
					labelname[i] = sprints[i].Name;
					if (sprints[i].Velocity > max) {
						max = sprints[i].Velocity;
					}
				};
				var lineChartData = {
					labels : labelname,
					datasets : [
						{ // ideal line
							fillColor : "rgba(255,255,255,0.0)",
							strokeColor : "rgba(52, 152, 219,1)",
							pointColor : "rgba(52, 152, 219,1)",
							data : velocitys
						},
						{ // averages line
							fillColor : "rgba(255,255,255,0.0)",
							strokeColor : "rgba(231, 76, 60,1)",
							pointColor : "rgba(231, 76, 60,1)",
							data : averages
						}
					]
				}
				var width = 0;
				if (max <= 24) {
					width = 2;
				} else {
					width = 5;
				}
				var options = {
						scaleGridLineColor : "rgba(0,0,0,.15)",
						scaleOverride: true,
						scaleSteps: Math.ceil((max*1.2) / width),
						scaleStepWidth: width,
						scaleStartValue: 0,
						bezierCurve : false
				}
				var myLine = new Chart(document.getElementById("canvas").getContext("2d")).Line(lineChartData, options);
			},
			dataType : "json"
		});
		
		$("#download").click(function() {
			var canvas = document.getElementById("canvas");
			var img    = canvas.toDataURL("image/png");
			document.write('<img src="'+img+'"/>');
		});
	});
</script>
</html>
