/*global angular */
angular.module('Quizz').controller('SimulatorCtrl', ['$scope', 'rollerDerbyModel', function ($scope, rollerDerbyModel) {
    "use strict";
    $scope.scene = new rollerDerbyModel.Scene({scale:0.3});
}]);