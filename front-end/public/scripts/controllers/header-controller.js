/**
 * Created by Kresshy on 2014.06.28..
 */

'use strict';

angular.module('pekFrontendApp').controller('HeaderCtrl', ['$scope', '$rootScope', 'User',
  function ($scope, $rootScope, User) {

      User.get(function(userinfo) {

          console.log(userinfo);

          $scope.user = userinfo.user;
          $scope.img = userinfo.img;
          $scope.reminders = userinfo.reminders;
      });

      $scope.settingsVisible = true;

      $scope.toggleSettings = function() {
          $scope.settingsVisible = !$scope.settingsVisible;
      }
}]);