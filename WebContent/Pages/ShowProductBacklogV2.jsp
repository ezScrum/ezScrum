<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html ng-app="ezScrum">
<head>
	<link rel="stylesheet" href="css/bootstrap/bootstrap.min.css">
	<script type="text/javascript" src="javascript/angularjs/angular.min.js"></script>
	<script type="text/javascript" src="javascript/angularjs/app.js"></script>
	<script type="text/javascript" src="javascript/angularjs/controllers.js"></script>
	<style type="text/css">
		body {
			font-family: '微軟正黑體';
		}

		.navbar {
			background-color: rgba(0, 0, 0, 0.7);
		}

		#productbacklog {
			padding-top: 60px;
		}

		.add-story {
			cursor: pointer;
			margin-right: 10px;
			padding: 30px 0px 30px 0px;
			border-style: dotted;
			border-color: #bdc3c7;
			border-width: 1px;
			/*border-radius: 5px;*/
		}

		.add-story:active {
			background-color: #bdc3c7;
		}

		.story-item {
			cursor: pointer;
			margin: 0px 10px 10px 0px;
			background: #fffffe;
			/*border-radius: 3px;*/
			border-style: solid;
			border-width: 1px;
			border-color: #bdc3c7;
			padding: 5px 0px 5px 0px;
			box-shadow: 0px 1px 0px #CFCECE;
		}

		.story-item:active {
			background: #eeeeee;
			box-shadow: 0px 0px 0px #fff;
		}

		.yellow {
			background: #FFF792;
		}

		.blue {
			background: #97C9FF;
		}

		.green {
			background: #A1E8B9;
		}
		
		.value-item {
			float: right;
			width: 70px;
			margin-right: 5px;
			margin-bottom: 5px;
			padding: 0px 5px 0px 5px;
			border-style: solid;
			border-width: 1px;
			border-color: #CCC;
		}

		.value {
			font-size: 2em;
			float: right;
		}

		.no-padding {
			padding-right: 0px;
			padding-left: 0px;
		}

		.overlay {
			position: fixed;
			width: 100%;
			height: 100%;
			top: 0;
			left: 0;
			background-color: rgba(0, 0, 0, 0.5);
			/*display: none;*/
		}

		.task-container {
			position: relative;
			margin-top: 100px;
		}

		/* Plus icon */
		#cross {
			width: 100px;
			height: 100px;
			position: relative;
			margin:10px;
			background-size:cover ;
			-webkit-filter: drop-shadow(4px 0px 1px black);
			-moz-filter: drop-shadow(4px 0px 1px black);
			filter: drop-shadow(4px 0px 1px black);
		}

		#cross:before, #cross:after {
			content: "";
			position: absolute;
			z-index: 0;
			background: #94ABA9;
			transform: rotate(0deg);
			border-radius:15px;
		}

		#cross:before {
			left: 50%;
			width: 30%;
			margin-left: 35%;
			height: 100%;
		}

		#cross:after {
			top: 50%;
			height: 30%;
			margin-top: -15%;
			width: 100%;
		}

		.input-field {
			width: 100%;
			height: 34px;
			padding: 5px;
			font-size: 14px;
		}

		.button {
			padding: 10px;
			border-radius: 0px;
			color: #333;
			border-style: solid;
			border-width: 1px;
			border-color: #bdc3c7;
			background-color: #fff;
		}

		.button:hover {
			-webkit-transition: background-color 0.3s ease-in;
			-moz-transition: background-color 0.3s ease-in;
			-ms-transition: background-color 0.3s ease-in;
			-o-transition: background-color 0.3s ease-in;
			transition: background-color 0.3s ease-in;
			
			background-color: #555;
			color: #fff;
		}

		.ui-box {
			background: #FFF;
			margin-bottom: 20px;
			overflow-y: auto;
		}

		.ui-box-title {
			border-bottom: 1px solid #DDD;
			color: #FFF;
			background-color: #3498db;
			border-color: #bce8f1;
			font-size: 16px;
			padding: 0 10px;
			font-size: 14px;
			line-height: 40px;
			font-weight: normal;
			margin: 0;
		}

		.ui-box-content {
			padding: 10px;
		}
		
		.task-container {
			width: 690px;
			height: 100%;
			padding-left: 15px;
			padding-right: 15px;
			margin-left: auto;
			margin-right: auto;
		}
	</style>
</head>
<body ng-controller="ProductBacklogController">
	<div class="navbar navbar-default navbar-fixed-top" role="navigation">
		<div class="container">
				<div class="navbar-header">
					<a class="navbar-brand" href="#">ezScrum Product Backlog v2.0</a>
			</div>
			<div class="navbar-collapse collapse">
				<ul class="nav navbar-nav navbar-right">
					<li><a href="" ng-click="addStory()">+</a></li>
					<li><a href="">< Back to origin ProductBacklog</a></li>
				</ul>
			</div>
		</div>
	</div>
	<div id="productbacklog" class="container-fluid">
		<!-- <div class="col-md-3 text-center add-story" id="addstory">
			<img src="ic_add.png" alt="" style="width: 100px;">
		</div> -->
		<div class="col-md-3 story-item no-padding">
			<div class="col-md-12" style="font-size: 1.2em;">#4485</div>
			<div class="col-md-12">
				<span style="font-size: 1.5em;">
					身為使用者，我希望有一個好用的編輯頁面，使得我的編輯速度加快
					身為使用者，我希望有一個好用的編輯頁面，使得我的編輯速度加快
				</span>
			</div>
			<div class="md-md-12">
				<div class="value-item yellow">
					<div>Imp.</div>
					<strong class="value">99</strong>
				</div>
				<div class="value-item blue">
					<div>Est.</div>
					<strong class="value">13</strong>
				</div>
				<div class="value-item green">
					<div>Val.</div>
					<strong class="value">13</strong>
				</div>
			</div>
		</div>
		<div class="col-md-3 story-item no-padding">
			<div class="col-md-12" style="font-size: 1.2em;">#4485</div>
			<div class="col-md-12">
				<span style="font-size: 1.5em;">
					身為使用者，我希望有一個好用的編輯頁面，使得我的編輯速度加快
					身為使用者，我希望有一個好用的編輯頁面，使得我的編輯速度加快
				</span>
			</div>
			<div class="md-md-12">
				<div class="value-item yellow">
					<div>Imp.</div>
					<strong class="value">97</strong>
				</div>
				<div class="value-item blue">
					<div>Est.</div>
					<strong class="value">13</strong>
				</div>
				<div class="value-item green">
					<div>Val.</div>
					<strong class="value">13</strong>
				</div>
			</div>
		</div>
		<div class="col-md-3 story-item no-padding">
			<div class="col-md-12" style="font-size: 1.2em;">#4485</div>
			<div class="col-md-12">
				<span style="font-size: 1.5em;">
					身為使用者，我希望有一個好用的編輯頁面，使得我的編輯速度加快
					身為使用者，我希望有一個好用的編輯頁面，使得我的編輯速度加快
				</span>
			</div>
			<div class="md-md-12">
				<div class="value-item yellow">
					<div>Imp.</div>
					<strong class="value">95</strong>
				</div>
				<div class="value-item blue">
					<div>Est.</div>
					<strong class="value">13</strong>
				</div>
				<div class="value-item green">
					<div>Val.</div>
					<strong class="value">13</strong>
				</div>
			</div>
		</div>
		<div class="col-md-3 story-item no-padding">
			<div class="col-md-12" style="font-size: 1.2em;">#4485</div>
			<div class="col-md-12">
				<span style="font-size: 1.5em;">
					身為使用者，我希望有一個好用的編輯頁面，使得我的編輯速度加快
					身為使用者，我希望有一個好用的編輯頁面，使得我的編輯速度加快
				</span>
			</div>
			<div class="md-md-12">
				<div class="value-item yellow">
					<div>Imp.</div>
					<strong class="value">93</strong>
				</div>
				<div class="value-item blue">
					<div>Est.</div>
					<strong class="value">13</strong>
				</div>
				<div class="value-item green">
					<div>Val.</div>
					<strong class="value">13</strong>
				</div>
			</div>
		</div>
	</div>
	<div class="overlay" ng-init="isEditMode = false" ng-show="isEditMode">
		<div class="task-container">
			<div class="col-md-12">
				<div class="ui-box">
					<div class="ui-box-title">Story #7723</div>
						<div class="ui-box-content" style="padding-right: 50px;">
							<div class="row">
							<div class="col-md-3 text-right">Name</div>
							<div class="col-md-9">
								<input type="input" class="input-field">
							</div>
						</div>
						<br>
						<div class="row">
							<div class="col-md-3 text-right">Tag</div>
							<div class="col-md-9">
								<select class="input-field">
									<option value="">ASD</option>
									<option value="">GFGG</option>
									<option value="">DFLKJ</option>
									<option value="">ELKJ</option>
								</select>
							</div>
						</div>
						<br>
						<div class="row">
							<div class="col-md-3 text-right">How To Demo</div>
							<div class="col-md-9">
								<textarea rows="8" style="width: 100%;"></textarea>
							</div>
						</div>
						<br>
						<div class="row">
							<div class="col-md-3 text-right">Note</div>
							<div class="col-md-9">
								<textarea rows="8" style="width: 100%;"></textarea>
							</div>
						</div>
						<br>
						<div class="row">
							<div class="col-md-9 col-md-offset-3">
								<div class="col-md-3 no-padding">
									Imp.<input type="text" class="input-field">
								</div>
								<div class="col-md-3 col-md-offset-1 no-padding">
									Est.<input type="text" class="input-field">
								</div>
								<div class="col-md-3 col-md-offset-1 no-padding">
									Val.<input type="text" class="input-field">
								</div>
							</div>
						</div>
						<br>
						<div class="col-md-5 col-md-offset-7 no-padding" style="text-align: right;">
							<button class="button pull-right" style="margin-left: 10px;">Save</button>
							<button class="button pull-right" style="margin-left: 10px;">Apply</button>
							<button class="button pull-right" ng-click="cancelEditMode()">Cancel</button>
						</div>
						<br><br>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
