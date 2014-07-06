'use strict';

angular.module('pekFrontendApp').controller('SearchCtrl', ['$scope', '$rootScope', 'Search',
    function ($scope, $rootScope, Search) {

        $scope.helpVisible = false;

        $scope.toggleHelp = function() {
            $scope.helpVisible = !$scope.helpVisible;
        };

        $scope.search = function(term) {

        };
    }
]);