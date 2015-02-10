/*global angular, Parse */

Parse.initialize("qJglDDzyCTc8qUB2Z5KdqvD5IUQmbUWiHJ0fNeIW", "eRgIH4su5FsFRmPq2ubWTyBiiusqTrQygj7ZL6l9");

angular.module('Quizz', [
    'ngRoute',
    'xeditable',
    'ngStorage',
    'ui.bootstrap',
    'pascalprecht.translate',
    'ngAnimate',
    'ngTagsInput',
    'hc.marked',
]);

angular.module('Quizz').config(['$routeProvider', '$translateProvider', 'ParseProvider', function ($routeProvider, $translateProvider, ParseProvider) {
    'use strict';
    $routeProvider.when('/', {
        templateUrl: 'views/quiz.html',
        controller: 'QuizCtrl'
    }).when('/backoffice', {
        templateUrl: 'views/backoffice.html',
        controller: 'BackofficeCtrl'
    }).otherwise({
        redirectTo: '/'
    });
    
    $translateProvider.useStaticFilesLoader({
      prefix: '/data/',
      suffix: '.json'
    });
    
    $translateProvider.preferredLanguage('en');
    
}]);

angular.module('Quizz').run(['editableOptions', function (editableOptions) {
    'use strict';
    editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
}]);