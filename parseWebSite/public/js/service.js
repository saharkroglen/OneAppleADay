'use strict';

/* Services */

angular.module('wePromote.services', ['ngResource'])
.factory('Reset', function ($http, $q) {
  var rest = {};
  
  rest.getData = function () {
    return $http({
      url: ''
    });
  };
  
  return rest;
});









