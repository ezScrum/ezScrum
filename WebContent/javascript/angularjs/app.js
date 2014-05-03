'use strict';

var ezScrum = angular.module('ezScrum', []);

ezScrum.controller('ProductBacklogController', function($scope, $http) {
	var init = function() {
		$http({method: 'GET', url: '/ezScrum/web-service/' + getQueryStringByName('PID') + '/product-backlog/storylist?userName=' + getCookie('username') + '&password=' + getCookie('userpwd')}).
		    success(function(data, status, headers, config) {
		    	$scope.storyList = data;
		    }).
		    error(function(data, status, headers, config) {
		    });
	}
	
	$scope.cancelEditMode = function() {
		$scope.isEditMode = false;
	}

	$scope.addStory = function() {
		$scope.isEditMode = true;
	}
	
	init();
});