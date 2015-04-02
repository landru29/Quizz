/*global angular */
angular.module('Quizz').controller('ModalHelpBoCtrl', ['$scope', '$modalInstance',
    function ($scope, $modalInstance) {
        'use strict';

        $scope.close = function () {
            $modalInstance.dismiss('cancel');
        };

    }]);