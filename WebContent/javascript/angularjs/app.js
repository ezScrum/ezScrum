'use strict';

var ezScrum = angular.module('ezScrum', []);

ezScrum.controller('ProductBacklogController', function($scope, $http) {
	$scope.isEditMode = false;
	$scope.isCreateMode = false;
	
	var init = function() {
		$http({method: 'GET', url: '/ezScrum/web-service/' + getQueryStringByName('PID') + '/product-backlog/storylist?userName=' + getCookie('username') + '&password=' + getCookie('userpwd')}).
		    success(function(data, status, headers, config) {
		    	$scope.storyList = data;
		    }).
		    error(function(data, status, headers, config) {
		    });
		
		$http({method: 'GET', url: '/ezScrum/web-service/' + getQueryStringByName('PID') + '/product-backlog/taglist?userName=' + getCookie('username') + '&password=' + getCookie('userpwd')}).
		    success(function(data, status, headers, config) {
		    	$scope.tagList = data;
		    }).
		    error(function(data, status, headers, config) {
		    });
	}
	
	$scope.enterCreateMode = function() {
		$scope.isCreateMode = true;
		$scope.boxTitle = 'Create new story';
	}
	
	$scope.enterEditMode = function(story) {
		
	}
	
	$scope.save = function(tmpStory) {
		var data = {
				name: tmpStory.name,
				value: tmpStory.value,
				estimation: tmpStory.estimation,
				notes: tmpStory.note,
				importance: tmpStory.importance,
				howToDemo: tmpStory.howToDemo
		}
		$http.post('/ezScrum/web-service/' + getQueryStringByName('PID') + '/story/create?userName=' + getCookie('username') + '&password=' + getCookie('userpwd'), data).
				success(function(data, status, headers, config) {
			    	init();
			    	$scope.cancel();
			    });
	}
	
	$scope.apply = function(tmpStory) {
		
	}
	
	$scope.cancel = function() {
		$scope.isEditMode = false;
		$scope.isCreateMode = false;
		
		$scope.tmpStory = {};
	}
	
	init();
});