'use strict';

angular.module('pekFrontendApp').controller('SettingsCtrl', ['$scope', '$rootScope', 'ProfileSettings',
    function ($scope, $rootScope, ProfileSettings) {

        $scope.user = {
            name: 'Váradi Szabolcs',
            room: '613',
            dormitory: 'SCH',
            firstname: 'Váradi',
            lastname: 'Szabolcs',
            sex: 'férfi',
            birthdate: '1990.12.11',
            address: '4031 Debrecen Kantár utca 12/A',
            mobile: '+36303966340',
            website: 'www.crashdesign.deviantart.com',
            ims: [
                {
                    name: 'skype',
                    nick: 'kresshy'
                }
            ],
            pic: '/profile/1/pic.png'
        }
    }
]);