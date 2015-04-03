/*global angular */
angular.module('Quizz').controller('ModalPlayStoreCtrl', ['$scope', '$modalInstance',
    function ($scope, $modalInstance) {
        'use strict';

        $scope.close = function () {
            $modalInstance.dismiss('cancel');
        };

    }]);