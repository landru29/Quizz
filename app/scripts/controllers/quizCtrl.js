/*global angular */
angular.module('Quizz').controller('QuizCtrl', ['$scope', 'Question', 'marked', '$sce', '$sessionStorage', '$modal',
    function ($scope, Question, marked, $sce, $sessionStorage, $modal) {
        'use strict';


        $scope.init = function () {
            $scope.test = true;
            $scope.questions = [];
            Question.ramdomQuestions(10).then(function (response) {
                $scope.questions = response.data;
                $scope.results = [];
                $scope.test = true;
            }, function (err) {
                console.log(err);
            });
        };

        if ((navigator.userAgent.toLowerCase().indexOf("android") > -1) && (!$sessionStorage.playStoreShown)) {
            $sessionStorage.playStoreShown = true;
            $modal.open({
                templateUrl: 'views/modal-play-store.html',
                controller: 'ModalPlayStoreCtrl',
                size: '',
                resolve: {}
            });
        }

        $scope.computeAnswer = function (question) {
            question.answer = [];
            for (var i in question.choices) {
                if (question.choices[i].answer) {
                    question.answer.push(question.choices[i].objectId);
                }
            }
        };

        $scope.myMarked = function (data) {
            var markdown = marked(data);
            var targeted = markdown.replace(/<a[^>]*>/, function (capture) {
                var attrs = capture.match(/<a([^>]*)>/);
                if (attrs[1]) {
                    return '<a target="_blank"' + attrs[1] + '>';
                } else {
                    return capture;
                }
            });
            return targeted;
        };

        $scope.requestCorrection = function () {
            var request = [];
            for (var i in $scope.questions) {
                request.push({
                    questionId: $scope.questions[i].objectId,
                    answer: (Object.prototype.toString.call($scope.questions[i].answer) === '[object Array]' ? $scope.questions[i].answer : [$scope.questions[i].answer])
                });
            }
            console.log(request);
            Question.checkAnswers(request).then(function (response) {
                $scope.results = response.data;
                for (var index in $scope.results) {
                    $scope.results[index].explainationHtml = $sce.trustAsHtml($scope.myMarked($scope.results[index].explaination));
                }
                $scope.test = false;
                window.scrollTo(0, 0);
            }, function (err) {
                console.log(err);
            });
        };

        $scope.getScore = function () {
            var score = {
                correctQuestions: 0,
                points: 0,
                maxPoints: 0,
                maxQuestions: 0
            };
            for (var i in $scope.results) {
                score.maxQuestions++;
                score.maxPoints += $scope.results[i].maxScoring;
                score.points += $scope.results[i].scoring;
                score.correctQuestions += ($scope.results[i].check ? 1 : 0);
            }
            score.questionPercentage = Math.round(100 * score.correctQuestions / score.maxQuestions);
            score.scorePercentage = Math.round(100 * score.points / score.maxPoints);
            return score;
        };

        $scope.init();

    }]);