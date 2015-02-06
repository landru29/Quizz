angular.module('Quizz').controller('BackofficeCtrl', ['$scope', '$filter', 'Question',
    function ($scope, $filter, Question) {
        "use strict";
        
        Question.getQuestions().then(function(response){
            $scope.questions = response.data;
            for (var i in $scope.questions) {
                var tags = ($scope.questions[i].tags ? $scope.questions[i].tags.split(',') : []);
                for (var j in tags) {
                    tags[j] = {text:tags[j]};
                }
                $scope.questions[i].tags = tags;
            }
        }, function(err){
            console.log(err);
        });
        
        $scope.addQuestion = function() {
            $scope.addingQuestion = true;
            Question.addQuestion({text:$filter('translate')('Type your question')}).then(function(response) {
                $scope.questions.unshift(response.data);
                $scope.addingQuestion = false;
            }, function(err) {
                $scope.addingQuestion = false;
            });
        };
        
        $scope.updateQuestion = function(data) {
            return Question.updateQuestion(data);
        };
        
        $scope.addChoice = function(question) {
            question.$addingChoice = true;
            Question.addChoice({questionId:question.objectId, text:$filter('translate')('Type your choice')}).then(function(response){
                question.choices.push(response.data);
                delete question.$addingChoice;
            }, function(err) {
                delete question.$addingChoice;
            });
        };
        
        $scope.updateChoice = function(data) {
            return Question.updateChoice(data);
        };
        
        $scope.deleteQuestion = function(question) {
            Question.deleteQuestion({questionId:question.objectId}).then(function(data) {
                $scope.questions.splice($scope.questions.indexOf(question), 1);
            }, function(err) {
            });
        };
        
        $scope.deleteChoice = function(question, choice) {
            choice.$deleted = true;
            Question.deleteChoice({choiceId:choice.objectId}).then(function(data) {
                question.choices.splice(question.choices.indexOf(choice), 1);
            }, function(err) {
            });
        };
        
        $scope.isMultichoice = function(question) {
            var positiveCounter = 0;
            for(var i in question.choices) {
                if ((question.choices[i].scoring) && (question.choices[i].scoring>0)) {
                    positiveCounter++;
                }
            }
            return (positiveCounter!==1);
        };
        
}]);