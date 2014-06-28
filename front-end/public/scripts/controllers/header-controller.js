/**
 * Created by Kresshy on 2014.06.28..
 */

'use strict';

angular.module('pekFrontendApp').controller('HeaderCtrl', ['$scope', '$rootScope',
  function ($scope, $rootScope) {

      $scope.user = 'Szabolcs Varadi';
      $scope.img = '/user/profile-pic.png';
      $scope.settingsVisible = true;

      $scope.reminders = [
          {
              reminder: 'Pontozási időszak van'
          },
          {
              reminder: 'Kitöltetlen pontozás'
          }
      ];

      $scope.toggleSettings = function() {
          $scope.settingsVisible = !$scope.settingsVisible;
      }
}]);