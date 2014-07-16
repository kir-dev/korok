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

applicationServices.factory('Search', ['$resource',
    function($resource) {
        return $resource(
            '/search',
            {
                term: '@term'
            },
            {
                get: {method: 'GET', isArray: false, params: {term: '@term'}}
            }
        );
    }
]);

applicationServices.factory('Profile', ['$resource',
    function($resource) {
        return $resource(
            '/profile',
            {
                post: {method: 'POST', isArray: false, params: {}}
            }
        );
    }
]);

applicationServices.factory('ProfileSettings', ['$resource',
    function($resource) {
        return $resource(
            '/profile/settings',
            {
                post: {method: 'POST', isArray: false, params: {}}
            }
        );
    }
]);

applicationServices.factory('Groups', ['$resource',
    function($resource) {
        return $resource(
            '/groups',
            {
                post: {method: 'POST', isArray: false, params: {}}
            }
        );
    }
]);

applicationServices.factory('GroupsProfile', ['$resource',
    function($resource) {
        return $resource(
            '/groups/profile',
            {
                post: {method: 'POST', isArray: false, params: {}}
            }
        );
    }
]);