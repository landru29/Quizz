/*global angular */
angular.module('Quizz').controller('QuizCtrl', ['$scope', '$filter', 'Question',
    function ($scope, $filter, Question) {
        'use strict';
        
        $scope.test = true;
        
        Question.getQuestions().then(function(response){
            $scope.questions = response.data;
        }, function(err){
            console.log(err);
        });
        
        $scope.computeAnswer = function(question) {
            question.answer = [];
            for (var i in question.choices) {
                if (question.choices[i].answer) {
                    question.answer.push(question.choices[i].objectId);
                }
            }
        };
        
        $scope.requestCorrection = function() {
            var request = [];
            for (var i in $scope.questions) {
                request.push({
                    questionId: $scope.questions[i].objectId,
                    answer: (Object.prototype.toString.call($scope.questions[i].answer) === '[object Array]' ? $scope.questions[i].answer : [$scope.questions[i].answer])
                });
            }
            console.log(request);
        };
        
    }]);