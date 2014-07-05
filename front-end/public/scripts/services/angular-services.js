'use strict';

var applicationServices = angular.module('applicationServices', ['ngResource']);

applicationServices.factory('User', ['$resource',
    function ($resource) {

        return $resource(
            '/user',
            {
                get: {method: 'GET', isArray: false, params: {}}
            }
        );
    }
]);