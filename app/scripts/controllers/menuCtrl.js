/*global angular */
angular.module('Quizz').controller('MenuCtrl', ['$scope', '$rootScope', '$localStorage', '$translate', '$modal', 'Parse', '$location', '$window', function ($scope, $rootScope, $localStorage, $translate, $modal, $Parse, $location, $window) {
    "use strict";

    $scope.menuConfig = [
        {
            caption: 'Backoffice',
            action: 'backoffice',
            class: 'toolbox-icon',
            role: 'admin'
        },
        {
            caption: 'Quiz',
            action: 'quiz',
            class: 'quiz-icon',
            role: 'guest'
        },
        {
            caption: 'Simulator',
            action: 'simulator',
            class: 'simulator-icon',
            role: 'guest'
        }
    ];
    $scope.getMenu = function (roles) {
        var menu = [];
        for (var i in $scope.menuConfig) {
            if (roles.indexOf($scope.menuConfig[i].role) > -1) {
                menu.push($scope.menuConfig[i]);
            }
        }
        return menu;
    };
    $scope.menu = [];

    $scope.triggerAction = function (action) {
        $location.path('/' + action);
        /*$rootScope.$broadcast('menu-trigger', {
            action: action
        });*/
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

    $scope.disconnect = function () {
        Parse.User.logOut();
        $scope.getRoles();
    };

    $scope.isConnected = function () {
        return (Parse.User.current());
    };

    var lang = $localStorage.language;
    $scope.changeLang((!lang) ? (navigator.language || navigator.userLanguage) : lang);

    $scope.getRoles = function () {
        $Parse({
            resource: 'getRoles'
        }).then(function (response) {
            var roles = response.data;
            $rootScope.roles = roles;
            $scope.menu = $scope.getMenu(roles);
        }, function (err) {
            $scope.menu = [];
        });
    };
    $scope.$on('menu-reload', function (event, args) {
        $scope.getRoles();
    });
    
    $scope.open = function(url) {
        $window.open(url, '_blank');
    };
    
    
    $scope.getRoles();

}]);