'use strict';

/* index */

var app = {
  initialize: function() {
    this.bindEvents();
  },
  bindEvents: function() {
    $(document).ready(this.onLoad);
  },
  onLoad: function() {
    angular.element(document).ready(function() {
      angular.bootstrap(document, ['wePromote']);
    });
  }
};