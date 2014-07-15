'use strict';

angular.module('pekFrontendApp').directive('editableLabel', function() {
    return {
        restrict: 'E',
        scope: {
            text: '=text'
        },
        link: function postLink(scope, element, attrs) {
            scope.disabled = true;

            scope.toggle = function() {
                scope.disabled = !scope.disabled;
            };
        },
        template: '<div class="input-group col-sm-5">' +
            '<input type="text" class="form-control" ng-show="disabled" placeholder="{{text}}" disabled>' +
            '<input type="text" class="form-control" ng-hide="disabled" ng-model="text">' +
            '<span class="input-group-btn">' +
            '<button class="btn btn-default" type="button" ng-click="toggle()">' +
            '<span class="glyphicon" ng-class="{\'glyphicon-pencil\': disabled == true, \'glyphicon-ok\': disabled == false}"></span>' +
            '</button>' +
            '</span>' +
            '</div>'
    };
});