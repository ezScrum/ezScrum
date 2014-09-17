<!doctype html>
<html ng-app="ezScrum">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<!-- <link rel="stylesheet" href="css/bootstrap/bootstrap.min.css">  -->
	<link href="//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css" rel="stylesheet">
	<link rel="stylesheet" href="css/bootstrap/angular-multi-select.css">
	<link rel="stylesheet" href="css/ezScrum/productbacklogv2.css">
	
	
	<!-- jQuery Lib -->
	<script type="text/javascript" src="javascript/jquery/jquery-1.11.0.min.js"></script>
	<script type="text/javascript" src="javascript/jquery/jquery-ui-1.10.4.min.js"></script>
	<script type="text/javascript" src="javascript/jquery/bootstrap-3.1.1.min.js"></script>
	<script type="text/javascript" src="javascript/jquery/bootstrap-multiselect.js"></script>
	
	<!-- AngularJs Lib & Modules -->
	<script type="text/javascript" src="javascript/angularjs/angular.min.js"></script>
	<script type="text/javascript" src="javascript/angularjs/ng-context-menu.min.js"></script>
	<script type="text/javascript" src="javascript/angularjs/ui-utils.js"></script>
	<script type="text/javascript" src="javascript/angularjs/ui-bootstrap-tpls-0.11.0.min.js"></script>
	<script type="text/javascript" src="javascript/angularjs/ngDialog.js"></script>
	<script type="text/javascript" src="javascript/angularjs/angular-multi-select.js"></script>

	<!-- Angular App -->
	<script type="text/javascript" src="javascript/angularjs/app.js"></script>
	<script type="text/javascript" src="javascript/utils.js"></script>
	
</head>
<body ng-controller="ProductBacklogController" ui-keyup="{'esc':'escTriggered()'}">
	<div class="navbar navbar-default navbar-fixed-top" role="navigation">
		<div class="container">
			<div class="navbar-header">
				<a class="navbar-brand" href="#">ezScrum Product Backlog v2.0</a>
			</div>
			<div class="navbar-collapse collapse">
				<ul class="nav navbar-nav navbar-right">
					<li><a href="" ng-click="enterCreateMode()"><span class="glyphicon glyphicon-plus"></span> Add Story</a></li>
					<li>
						<a><div>
							<span class="glyphicon glyphicon-search searchbar-icon"></span><input class="searchbar" type="text" ng-model="search" placeholder="Click to search">
						</div></a>
					</li>
					<li><a href="" ng-click="goBack()">< Back to original ProductBacklog</a></li>
				</ul>
			</div>
		</div>
	</div>
	<div id="productbacklog" class="container-fluid">
		<div class="col-md-3 col-sm-4" ng-repeat="story in storyList | filter:search" >
			<div class="story-item no-padding paenl panel-default" ng-click="enterEditMode(story)" context-menu="onRightClick(story);" data-target="myMenu">
				<div class="col-md-12" style="font-size: 1.2em;">{{ '#'+story.id }}</div>
				<div class="col-md-12">
					<div class="text-box">
						<span class="text" style="font-size: 1.5em;">
							{{ story.name }}
						</span>
					</div>
				</div>
				<div class="md-md-12">
					<div class="value-item green">
						<div>Val.</div>
						<strong class="value">{{ story.value }}</strong>
					</div>
					<div class="value-item blue">
						<div>Est.</div>
						<strong class="value">{{ story.estimation }}</strong>
					</div>
					<div class="value-item yellow">
						<div>Imp.</div>
						<strong class="value">{{ story.importance }}</strong>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="dropdown position-fixed" id="myMenu">
		<ul class="dropdown-menu" role="menu">
			<li>
				<a class="pointer" role="menuitem" ng-click="delete(currentStory)">Delete</a>
			</li>
		</ul>
	</div>
	<div class="overlay" ng-show="isEditMode || isCreateMode" ng-click="cancel()"></div>
	<div class="task-container col-sm-10 col-sm-offset-1 col-md-6 col-md-offset-3 col-xs-12" ng-show="isEditMode || isCreateMode">
		<div class="col-md-12">
			<div class="ui-box">
				<div class="ui-box-title">{{ boxTitle }}</div>
					<div class="ui-box-content" style="padding-right: 50px;">
						<div class="row">
						<div class="col-md-3 text-right">Name</div>
						<div class="col-md-9">
							<textarea rows="3" style="width: 100%;resize: none;" ng-model="tmpStory.name"></textarea>
						</div>
					</div>
					<br>
					<div class="row">
						<div class="col-md-3 text-right">Tag</div>
						<div class="col-md-9">
							<div
							    multi-select
							    input-model="tagList"
							    output-model="tmpStory.tags"
							    button-label="name"
							    item-label="name"
							    max-labels="7"
							    tick-property="ticked"
							    helper-elements=""
							></div>
						</div>
					</div>
					<br>
					<div class="row">
						<div class="col-md-3 text-right">How To Demo</div>
						<div class="col-md-9">
							<textarea rows="5" style="width: 100%;resize: none;" ng-model="tmpStory.howToDemo"></textarea>
						</div>
					</div>
					<br>
					<div class="row">
						<div class="col-md-3 text-right">Note</div>
						<div class="col-md-9">
							<textarea rows="5" style="width: 100%;resize: none;" ng-model="tmpStory.notes"></textarea>
						</div>
					</div>
					<br>
					<div class="row">
						<div class="col-md-9 col-md-offset-3">
							<div class="col-md-3 no-padding">
								Imp.<input type="text" class="input-field" ng-model="tmpStory.importance" onkeypress="return isNumberKey(this, event)">
							</div>
							<div class="col-md-3 col-md-offset-1 no-padding">
								Est.<input type="text" class="input-field" ng-model="tmpStory.estimation" onkeypress="return isNumberKey(this, event)">
							</div>
							<div class="col-md-3 col-md-offset-1 no-padding">
								Val.<input type="text" class="input-field" ng-model="tmpStory.value" onkeypress="return isNumberKey(this, event)">							</div>
						</div>
					</div>
					<br>
					<div class="col-md-5 col-md-offset-7 no-padding col-sm-12" style="text-align: right;">
						<button class="button pull-right" ng-click="save(tmpStory)" style="margin-left: 10px;">Save</button>
						<button class="button pull-right" ng-show="isEditMode" ng-click="apply(tmpStory)" style="margin-left: 10px;">Apply</button>
						<button class="button pull-right" ng-click="cancel()">Cancel</button>
					</div>
					<br><br>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
		$(document).ready(function() {
			//$('#multiselect').multiselect();
		});
	</script>
</body>
</html>
