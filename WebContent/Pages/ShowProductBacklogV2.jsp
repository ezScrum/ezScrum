<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<!doctype html>
<html ng-app="ezScrum">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
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
					<li><a href="" ng-click="enterCreateMode()">+</a></li>
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
	<div class="overlay" ng-show="isEditMode || isCreateMode">
		<div class="task-container">
			<div class="col-md-12">
				<div class="ui-box">
					<div class="ui-box-title">{{ boxTitle }}</div>
						<div class="ui-box-content" style="padding-right: 50px;">
							<div class="row">
							<div class="col-md-3 text-right">Name</div>
							<div class="col-md-9">
								<input type="input" class="input-field" ng-model="tmpStory.name">
							</div>
						</div>
						<br>
						<div class="row">
							<div class="col-md-3 text-right">Tag</div>
							<div class="col-md-9">
								<select class="input-field" ng-model="tmpStory.tag">
									<option value=""></option>
									<option value="{{ tag.tagName }}" ng-repeat="tag in tagList">{{ tag.tagName }}</option>
								</select>
							</div>
						</div>
						<br>
						<div class="row">
							<div class="col-md-3 text-right">How To Demo</div>
							<div class="col-md-9">
								<textarea rows="8" style="width: 100%;" ng-model="tmpStory.howToDemo"></textarea>
							</div>
						</div>
						<br>
						<div class="row">
							<div class="col-md-3 text-right">Note</div>
							<div class="col-md-9">
								<textarea rows="8" style="width: 100%;" ng-model="tmpStory.note"></textarea>
							</div>
						</div>
						<br>
						<div class="row">
							<div class="col-md-9 col-md-offset-3">
								<div class="col-md-3 no-padding">
									Imp.<input type="number" class="input-field" ng-model="tmpStory.importance">
								</div>
								<div class="col-md-3 col-md-offset-1 no-padding">
									Est.<input type="number" class="input-field" ng-model="tmpStory.estimation">
								</div>
								<div class="col-md-3 col-md-offset-1 no-padding">
									Val.<input type="number" class="input-field" ng-model="tmpStory.value">
								</div>
							</div>
						</div>
						<br>
						<div class="col-md-5 col-md-offset-7 no-padding" style="text-align: right;">
							<button class="button pull-right" ng-click="save(tmpStory)" style="margin-left: 10px;">Save</button>
							<button class="button pull-right" ng-click="apply(tmpStory)" style="margin-left: 10px;">Apply</button>
							<button class="button pull-right" ng-click="cancel()">Cancel</button>
						</div>
						<br><br>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
