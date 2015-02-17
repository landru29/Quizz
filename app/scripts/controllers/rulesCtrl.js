/*global angular */
angular.module('Quizz').controller('RulesCtrl', ['$scope', '$location', '$anchorScroll', '$routeParams', '$sce', '$http',
    function ($scope, $location, $anchorScroll, $routeParams, $sce, $http) {
        'use strict';
        $scope.data = {};

        $scope.loadSection = function (id) {
            var matcher = id.split('_');
            var newId = matcher[0] + '_' + matcher[1] + (matcher[2] ? '_' + matcher[2] : '');
            matcher.splice(0, 1);
            if (matcher.length > 1) {
                $scope.load(("00" + matcher[0]).slice(-2) + '_' + ("00" + matcher[1]).slice(-2));
            }

            $location.hash(newId);
            $anchorScroll();
        };

        $scope.load = function (name) {
            var filename = 'views/regles2014/' + name + '.html';
            if (!$scope.data[name]) {
                $http.get(filename).then(function (data) {
                    $scope.data[name] = $sce.trustAsHtml(data.data);
                }, function (err) {});
            }
            console.log(filename);
        };

        $scope.loadSection('section_' + $routeParams.section.replace(/\./g, '_'));

    }]);