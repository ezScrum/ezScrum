'use strict';

function isNumberKey(evt){
    var charCode = (evt.which) ? evt.which : event.keyCode
    if (charCode > 31 && (charCode < 48 || charCode > 57))
        return false;
    return true;
}

var ezScrum = angular.module('ezScrum', ['ng-context-menu', 'ui.utils'])
	.directive('draggable', ['$document' , function($document) {
	  return {
	    restrict: 'A',
	    link: function(scope, elm, attrs) {
	      var startX, startY, initialMouseX, initialMouseY;
	      elm.css({position: 'absolute'});
	
	      elm.bind('mousedown', function($event) {
	        startX = elm.prop('offsetLeft');
	        startY = elm.prop('offsetTop');
	        initialMouseX = $event.clientX;
	        initialMouseY = $event.clientY;
	        $document.bind('mousemove', mousemove);
	        $document.bind('mouseup', mouseup);
	        return false;
	      });
	
	      function mousemove($event) {
	        var dx = $event.clientX - initialMouseX;
	        var dy = $event.clientY - initialMouseY;
	        elm.css({
	          top:  startY + dy + 'px',
	          left: startX + dx + 'px'
	        });
	        return false;
	      }
	
	      function mouseup() {
	        $document.unbind('mousemove', mousemove);
	        $document.unbind('mouseup', mouseup);
	      }
	    }
	  };
	}]);

ezScrum.controller('ProductBacklogController', function($scope, $http) {
	$scope.isEditMode = false;
	$scope.isCreateMode = false;
	$scope.currentStory = {};
	
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
	
	var updateStory = function(tmpStory) {
		var data = {
			name: tmpStory.name,
			value: tmpStory.value,
			estimation: tmpStory.estimation,
			notes: tmpStory.notes,
			importance: tmpStory.importance,
			howToDemo: tmpStory.howToDemo,
			id: tmpStory.id
		}
		$http.put('/ezScrum/web-service/' + getQueryStringByName('PID') + '/story/update?userName=' + getCookie('username') + '&password=' + getCookie('userpwd'), data).
			success(function(data, status, headers, config) {
		    	init();
		    });
	}
	
	var createStory = function(tmpStory) {
		var data = {
			name: tmpStory.name,
			value: tmpStory.value,
			estimation: tmpStory.estimation,
			notes: tmpStory.notes,
			importance: tmpStory.importance,
			howToDemo: tmpStory.howToDemo
		}
		$http.post('/ezScrum/web-service/' + getQueryStringByName('PID') + '/story/create?userName=' + getCookie('username') + '&password=' + getCookie('userpwd'), data).
			success(function(data, status, headers, config) {
		    	init();
		    });
	}
	
	var deleteStory = function(story) {
		$http.delete('/ezScrum/web-service/' + getQueryStringByName('PID') + '/product-backlog/storylist/' + story.id + '?userName=' + getCookie('username') + '&password=' + getCookie('userpwd')).
			success(function(data, status, headers, config) {
		    	init();
		    });
	}
	
	$scope.enterCreateMode = function() {
		$scope.boxTitle = 'Create new story';
		$scope.isCreateMode = true;
	}
	
	$scope.enterEditMode = function(story) {
		$scope.boxTitle = 'Story #' + story.id;
		
		$scope.tmpStory = story;
		$scope.tmpStory.estimation = parseInt(story.estimation);
		$scope.tmpStory.value = parseInt(story.value);
		$scope.tmpStory.importance = parseInt(story.importance);
		
		$scope.isEditMode = true;
	}
	
	$scope.save = function(tmpStory) {
		if($scope.isEditMode) {
			updateStory(tmpStory);
		} else if($scope.isCreateMode) {
			createStory(tmpStory);
		}
		$scope.cancel();
	}
	
	$scope.apply = function(tmpStory) {
		updateStory(tmpStory);
	}
	
	$scope.cancel = function() {
		$scope.isEditMode = false;
		$scope.isCreateMode = false;
		
		$scope.tmpStory = {};
	}

	$scope.onRightClick = function(story) {
		$scope.currentStory = story;
	}
	
	$scope.isNumber = function(input) {
		if(!isNumber(input)) {
			input = 0;
		}
	}
	
	$scope.delete = function(story) {
		if(confirm('Delete this story?')) {
			deleteStory(story);
		}
	}
	
	$scope.goBack = function() {
		location.assign('/ezScrum/viewProject.do?PID=' + getQueryStringByName('PID'));
	}
	
	$scope.escTriggered = function() {
		$scope.cancel();
	}
	
	init();
});