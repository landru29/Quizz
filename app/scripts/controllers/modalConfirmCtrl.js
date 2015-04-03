/*global angular */
angular.module('Quizz').controller('ModalConfirmCtrl', ['$scope', '$modalInstance', '$filter', 'options',
    function ($scope, $modalInstance, $filter, options) {
        'use strict';
                                 
        $scope.format = function(formatted, params) {
            for (var i = 0; i < params.length; i++) {
                var regexp = new RegExp('\\{'+i+'\\}', 'gi');
                formatted = formatted.replace(regexp, params[i]);
            }
            return formatted;
        };
        
        $scope.title = (options.title ? options.title : 'Warning');
        $scope.message = $scope.format($filter('translate')(options.message), options.params);

        $scope.close = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.accept = function() {
            $modalInstance.close(options.data);
        };

    }]);