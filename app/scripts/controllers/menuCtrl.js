/*global angular */
angular.module('Quizz').controller('MenuCtrl', ['$scope', '$rootScope', '$localStorage', '$translate', '$modal', function ($scope, $rootScope, $localStorage, $translate, $modal) {
    "use strict";

    $scope.printing = false;

    $scope.togglePrint = function () {
        $scope.printing = !($scope.printing);
        $scope.triggerAction('print');
    };

    $scope.triggerAction = function (action) {
        $rootScope.$broadcast('menu-trigger', {
            action: action
        });
    };

    $scope.changeLang = function (lang) {
        $translate.use(lang);
        $localStorage.language = lang;
    };

    $scope.connect = function () {
        var modalInstance = $modal.open({
            templateUrl: 'views/modal-connect.html',
            controller: 'ModalConnectCtrl',
            size: '',
            resolve: {}
        });
    };
    
    $scope.disconnect = function() {
        Parse.User.logOut();
    };
    
    $scope.isConnected = function() {
        return (Parse.User.current());
    };

    var lang = $localStorage.language;
    $scope.changeLang((!lang) ? (navigator.language || navigator.userLanguage) : lang);
}]);