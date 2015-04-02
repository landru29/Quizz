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
    'roller-derby'
]);

angular.module('Quizz').config(['$routeProvider', '$translateProvider', 'ParseProvider', function ($routeProvider, $translateProvider, ParseProvider) {
    'use strict';
    $routeProvider.when('/quiz', {
        templateUrl: 'views/quiz.html',
        controller: 'QuizCtrl'
    }).when('/quiz/:level', {
        templateUrl: 'views/quiz.html',
        controller: 'QuizCtrl'
    }).when('/rules2014/:section', {
        templateUrl: 'views/regles2014.html',
        controller: 'RulesCtrl'
    }).when('/simulator', {
        templateUrl: 'views/simulator.html',
        controller: 'SimulatorCtrl'
    }).when('/backoffice', {
        templateUrl: 'views/backoffice.html',
        controller: 'BackofficeCtrl'
    }).otherwise({
        redirectTo: '/quiz'
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