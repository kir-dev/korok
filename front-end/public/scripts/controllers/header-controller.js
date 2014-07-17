'use strict';

angular.module('pekFrontendApp').controller('HeaderCtrl', ['$scope', '$rootScope', 'User',
    function ($scope, $rootScope, User) {

        User.get(function(userinfo) {

            $scope.user = userinfo.user;
            $scope.img = userinfo.img;
            $scope.reminders = userinfo.reminders;

        });

        $scope.settingsVisible = true;
        $scope.landingPage = (window.location.pathname === '/');

        $scope.toggleSettings = function() {
            $scope.settingsVisible = !$scope.settingsVisible;
        };
}
]);