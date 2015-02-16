/*global angular */
angular.module('Quizz').controller('RulesCtrl', ['$scope', '$location', '$anchorScroll', '$routeParams',
    function ($scope, $location, $anchorScroll, $routeParams) {
        'use strict';

        $scope.scrollTo = function (id) {
            $location.hash(id);
            $anchorScroll();
        };
        
        $scope.scrollTo('section_' + $routeParams.section.replace(/\./g, '_'));

    }]);