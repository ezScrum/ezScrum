<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html>
<head>
<script type="text/javascript" src="javascript/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="javascript/Chart.js"></script>
</head>
<body>
<canvas id="canvas" height="600" width="800" style="background: #fff;"></canvas>
</body>
<script>
	$(document).ready(function() {

		$.ajax({
			url : "/ezScrum/ajaxGetStoryCount.do?" + document.URL.split("?")[1],
			type : "GET",
			dataType : "json",
			success : function(data) {
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
							document.write('<img src="'+canvasToImage(document.getElementById("canvas"), "#FFF")+'"/>');
						}
				}
				var storyCountChart = new Chart(document.getElementById("canvas").getContext("2d")).Line(lineChartData, options);
			}
		});

		function canvasToImage(canvas, backgroundColor)
		{
			var context = canvas.getContext("2d");

		    //cache height and width        
		    var w = canvas.width;
		    var h = canvas.height;

		    var data;       

		    if(backgroundColor)
		    {
		        //get the current ImageData for the canvas.
		        data = context.getImageData(0, 0, w, h);

		        //store the current globalCompositeOperation
		        var compositeOperation = context.globalCompositeOperation;

		        //set to draw behind current content
		        context.globalCompositeOperation = "destination-over";

		        //set background color
		        context.fillStyle = backgroundColor;

		        //draw background / rect on entire canvas
		        context.fillRect(0,0,w,h);
		    }

		    //get the image data from the canvas
		    var imageData = this.canvas.toDataURL("image/png");

		    if(backgroundColor)
		    {
		        //clear the canvas
		        context.clearRect (0,0,w,h);

		        //restore it with original / cached ImageData
		        context.putImageData(data, 0,0);        

		        //reset the globalCompositeOperation to what it was
		        context.globalCompositeOperation = compositeOperation;
		    }

		    //return the Base64 encoded data url string
		    return imageData;
		}
	});
</script>
</html>
