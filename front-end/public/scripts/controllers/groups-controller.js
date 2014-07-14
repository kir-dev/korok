'use strict';

angular.module('pekFrontendApp').controller('GroupsCtrl', ['$scope', '$rootScope', 'Groups',
    function ($scope, $rootScope, Groups) {

        $scope.groups = [
            {
                name: 'Kir-Dev',
                id: '1',
                img: '/groups/1/logo.png'
            },
            {
                name: 'SEM',
                id: '2',
                img: '/groups/2/logo.png'
            },
            {
                name: 'KSZK',
                id: '3',
                img: '/groups/3/logo.png'
            },
            {
                name: 'SDS',
                id: '4',
                img: '/groups/4/logo.png'
            },
            {
                name: 'Simonyi Károly Szakkollégium',
                id: '5',
                img: '/groups/5/logo.png'
            },
            {
                name: 'Simonyi Konferencia',
                id: '6',
                img: '/groups/6/logo.png'
            }
        ];
    }
]);