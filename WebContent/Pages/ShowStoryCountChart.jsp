<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html>
<head>
<script type="text/javascript" src="javascript/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="javascript/Chart.min.js"></script>
</head>
<body>
<canvas id="canvas" height="450" width="600" style="background:#fcf5f2;"></canvas>
</body>
<script>
	$(document).ready(function() {
		
		$.ajax({
			url : "/ezScrum/ajaxGetStoryCount.do?" + document.URL.split("?")[1],
			type : "GET",
			dataType : "json",
			success : function(data) {
				//alert(JSON.stringify(data));
				var sprints = data.Sprints;
				var steps = sprints.length;
				var storyCounts = [];
				var ideals = [];
				var labelnames = [];
				var max = data.TotalStoryCount;
				for (var i = 0; i < sprints.length; i++) {
					storyCounts[i] = sprints[i].StoryRemainingCount;
					ideals[i] = sprints[i].StoryIdealCount;
					labelnames[i] = sprints[i].Name;
				};
				
				storyCounts.splice(0, 0, max);
				ideals.splice(0, 0, max);
				labelnames.splice(0, 0, '');;
				
				var lineChartData = {
					labels : labelnames,
					datasets : [
						{
							fillColor : "rgba(255,255,255,0.0)",
							strokeColor : "rgba(231, 76, 60,1)",
							pointColor : "rgba(231, 76, 60,1)",
							data : ideals
						},
						{
							fillColor : "rgba(255,255,255,0.0)",
							strokeColor : "rgba(52, 152, 219,1)",
							pointColor : "rgba(52, 152, 219,1)",
							data : storyCounts
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
						scaleSteps: Math.ceil(max / width),
						scaleStepWidth: width,
						scaleStartValue: 0,
						bezierCurve: false,
						onAnimationComplete: function() {
							var canvas = document.getElementById("canvas");
							var img    = canvas.toDataURL("image/png");
							document.write('<img src="'+img+'"/>');
						}
				}
				var storyCountChart = new Chart(document.getElementById("canvas").getContext("2d")).Line(lineChartData, options);
			}
		});
	});
</script>
</html>
