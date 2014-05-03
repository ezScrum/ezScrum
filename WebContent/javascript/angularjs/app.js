'use strict';

var ezScrum = angular.module('ezScrum', []);

ezScrum.controller('ProductBacklogController', function($scope) {
	var init = function() {
		console.log('asdfasdfasdf');
	}
	
	$scope.isEditMode = false;
	$scope.cancelEditMode = function() {
		$scope.isEditMode = false;
	}

	$scope.addStory = function() {
		$scope.isEditMode = true;
	}
	
	init();
});