<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html ng-app="ezScrum">
<head>
<script type="text/javascript" src="javascript/angularjs/angular.min.js"></script>
<script type="text/javascript" src="javascript/angularjs/app.js"></script>
<script type="text/javascript" src="javascript/angularjs/controllers.js"></script>
</head>
<body ng-controller="ProductBacklogController">
	<input type="text" ng-model="test" ng-change="hello()">
	<div>{{ test }}</div>
</body>
</html>
