<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html ng-app="ezScrum">
<head>
	<link rel="stylesheet" href="css/bootstrap/bootstrap.min.css">
	<link rel="stylesheet" href="css/ezScrum/productbacklogv2.css">
	<script type="text/javascript" src="javascript/utils.js"></script>
	<script type="text/javascript" src="javascript/angularjs/angular.min.js"></script>
	<script type="text/javascript" src="javascript/angularjs/app.js"></script>
	<script type="text/javascript" src="javascript/angularjs/controllers.js"></script>
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
		<div class="col-md-3 story-item no-padding" ng-repeat="story in storyList">
			<div class="col-md-12" style="font-size: 1.2em;">{{ '#'+story.id }}</div>
			<div class="col-md-12">
				<span style="font-size: 1.5em;">
					{{ story.name }}
				</span>
			</div>
			<div class="md-md-12">
				<div class="value-item yellow">
					<div>Imp.</div>
					<strong class="value">{{ story.importance }}</strong>
				</div>
				<div class="value-item blue">
					<div>Est.</div>
					<strong class="value">{{ story.estimation }}</strong>
				</div>
				<div class="value-item green">
					<div>Val.</div>
					<strong class="value">{{ story.value }}</strong>
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
