/*global angular */
angular.module('Quizz').controller('ModalConnectCtrl', ['$scope', '$modalInstance',
    function ($scope, $modalInstance) {
        'use strict';



        $scope.close = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.connect = function () {
            Parse.User.logIn($scope.username, $scope.password, {
                success: function (user) {
                    $scope.password = '';
                    $scope.close();
                },
                error: function (user, error) {
                    $scope.password = '';
                }
            });
        };

    }]);