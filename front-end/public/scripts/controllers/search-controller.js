'use strict';

angular.module('pekFrontendApp').controller('SearchCtrl', ['$scope', '$rootScope', 'Search',
    function ($scope, $rootScope, Search) {

        $scope.helpVisible = false;

        $scope.toggleHelp = function() {
            $scope.helpVisible = !$scope.helpVisible;
        };

        $scope.search = function(term) {

        }
        $scope.predicate = 'display';
        $scope.results = [
            {
                type: 'profile',
                id: '1',
                display: 'Váradi Szabolcs',
                img: '/profile/pic/valami.png',
                roomNumber: 'SCH 613'
            },
            {
                type: 'profile',
                id: '2',
                display: 'Nagy Pista',
                img: '/profile/pic/valami.png',
                roomNumber: 'TTNY 1610'
            },
            {
                type: 'group',
                id: '1',
                display: 'Kir-Dev',
                img: '/profile/pic/valami.png',
                roomNumber: null
            },
            {
                type: 'group',
                id: '2',
                display: 'SEM',
                img: '/profile/pic/valami.png',
                roomNumber: null
            },
            {
                type: 'group',
                id: '2',
                display: 'SEM',
                img: '/profile/pic/valami.png',
                roomNumber: null
            }
        ]
    }
]);