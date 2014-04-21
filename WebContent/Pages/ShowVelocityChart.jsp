<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html>
<head>
<script type="text/javascript" src="javascript/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="javascript/Chart.min.js"></script>
</head>
<body>
<div>
	<input onclick="resizeCanvas(600, 800)" type="radio" name="group" value="4*3" checked>4*3 
	<input onclick="resizeCanvas(450, 800)" type="radio" name="group" value="16*9">16*9
	<input onclick="resizeCanvas(535, 800)" type="radio" name="group" value="3*2">3*2
</div>
<div id="image">
</div>
<canvas id="canvas" height="600" width="800" style="background:#fff"></canvas>
</body>
<script>
	function canvasToImage(canvas, backgroundColor) {
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

	function getReleases() {
		var releasesInfo = [];
		var queryString = getQueryStringByName("releases").split(",");
		for(var i=0; i<queryString.length; i++) {
			releasesInfo.push(getReleaseInfo(queryString[i]));
		}
		return releasesInfo;
	}

	function getReleaseInfo(releaseId) {
		var releaseInfo = {
			sprints: [],
			stories: []
		};
		$.ajax({
			url: '/ezScrum/web-service/' + getQueryStringByName('PID') + '/release-plan/' + releaseId +'/all?userName=' + getCookie('username') + '&password=' + getCookie('userpwd'),
			type: 'GET',
			dataType: 'json',
			async: false,
			success: function(data) {
				releaseInfo.sprints = data.releasePlanDesc.sprintPlan;
				releaseInfo.stories = data.stories;
			},
			error: function(data) {
				alert('fail');
			}
		});
		return releaseInfo;
	}

	function getCookie(cname) {
		var name = cname + "=";
		var ca = document.cookie.split(';');
		for(var i=0; i<ca.length; i++) {
			var c = ca[i].trim();
			if (c.indexOf(name)==0) return c.substring(name.length,c.length-1).replace('"', '');
		}
		return "";
	}

	function getQueryStringByName(name) {
		var result = location.search.match(new RegExp("[\?\&]" + name+ "=([^\&]+)","i"));
		if(result == null || result.length < 1) {
			return "";
		}
		return result[1];
	}

	function getStories(sprintId) {
		var stories = [];
		$.ajax({
			url: '/ezScrum/web-service/' + getQueryStringByName('PID') + '/sprint-backlog/' + sprintId +'/storylist?userName=' + getCookie('username') + '&password=' + getCookie('userpwd'),
			type: 'GET',
			dataType: 'json',
			async: false,
			success: function(data) {
				stories = data.storyList;
			},
			error: function(data) {
				alert('fail');
			}
		});
		return stories;
	}

	function initChart() {
		var releases = getReleases();

		var totalVelocity = 0;
		var sprintCount = 0;
		var sprintList = [];

		for(var i=0; i<releases.length; i++) {
			for(var j=0; j<releases[i].sprints.length; j++) {
				sprintCount += 1;

				var sprint = releases[i].sprints[j];
				sprint.stories = getStories(sprint.id);

				velocity= 0;
				for(var k=0; k<releases[i].sprints[j].stories.length; k++) {
					var story = releases[i].sprints[j].stories[k];
					totalVelocity += story.point;
					velocity += story.point;
				}

				sprintList.push({id: sprint.id, velocity: velocity});
			}
		}

		var average = totalVelocity / sprintCount;

		var sprints = sprintList;
		var steps = sprints.length;
		var velocitys = [];
		var averages = [];
		var labelname = [];
		var max = 0;
		for (var i = 0; i < sprints.length; i++) {
			velocitys[i] = sprints[i].velocity;
			averages[i] = average;
			labelname[i] = 'sprint' + sprints[i].id;
			if (sprints[i].velocity > max) {
				max = sprints[i].velocity;
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
				bezierCurve: false,
				onAnimationComplete: function() {
					$('#image').empty();
					$('#image').html('<img src="'+canvasToImage(document.getElementById("canvas"), "#FFF")+'"/>');
					$('#canvas').hide();
				}
		}
		var velocityChart = new Chart(document.getElementById("canvas").getContext("2d")).Line(lineChartData, options);
	}

	function resizeCanvas(height, width) {
		$('#image').empty();
		$('#canvas').prop('height', height);
		$('#canvas').prop('width', width);
		$('#canvas').show();
		initChart();
	}

	$(document).ready(function() {
		initChart();
	});
</script>
</html>
