'use strict';

var ezScrum = angular.module('ezScrum', []);

ezScrum.controller('ProductBacklogController', function($scope) {
	$scope.hello = function() {
		console.log("hello");
	}
});