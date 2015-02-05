/*global angular */
angular.module('Quizz').controller('MenuCtrl', ['$scope', '$rootScope', '$localStorage', '$translate', function ($scope, $rootScope, $localStorage, $translate) {
    "use strict";
    
    $scope.printing = false;
    
    $scope.togglePrint = function () {
        $scope.printing = !($scope.printing);
        $scope.triggerAction('print');
    };
    
    $scope.triggerAction = function(action) {
        $rootScope.$broadcast('menu-trigger', {action:action});
    };
    
    $scope.changeLang = function(lang) {
      $translate.use(lang);
      $localStorage.language = lang;
    };

    var lang = $localStorage.language;
    $scope.changeLang((!lang) ? (navigator.language || navigator.userLanguage) : lang);
}]);