'use strict';

/* App Module */

var wePromote = angular.module('wePromote', [
  'ngRoute',
  'ngResource'
]);

wePromote.config(['$routeProvider', '$locationProvider',
  function($routeProvider, $locationProvider) {
    $routeProvider.
      when('/', {
        templateUrl: 'template/landing-page.html'
      }).when('/list', {
        templateUrl: 'template/list.html'
      }).
      otherwise({
        redirectTo: '/'
      });
  }]);
