'use strict';
var app = angular.module('app', [ 'ui.router', 'ngMaterial' ]);

app.config(function($stateProvider, $urlRouterProvider) {

	$urlRouterProvider.otherwise('/startForm');

	$stateProvider.state('startForm', {
		url : '/startForm',
		templateUrl : 'startForm.html',
		controller : 'ctrl'
	})

	
});