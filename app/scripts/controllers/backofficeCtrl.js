angular.module('Quizz').controller('BackofficeCtrl', ['$scope', '$filter', 'Question', 'upload', '$sessionStorage', '$modal',
    function ($scope, $filter, Question, upload, $sessionStorage, $modal) {
        "use strict";
        
        $scope.openHelp = function() {
            $modal.open({
                templateUrl: 'views/modal-help-bo.html',
                controller: 'ModalHelpBoCtrl',
                size: '',
                resolve: {}
            });
        };
        
        if (!$sessionStorage.boHelpShown) {
            $sessionStorage.boHelpShown = true;
            $scope.openHelp();
        }

        $scope.paginator = {
            pageSize: 5,
            page: 1
        };

        $scope.level = [
            {
                label: 'expert',
                value: 10
            },
            {
                label: 'baby',
                value: 0
            }
        ];

        $scope.filterLevel = [
            {
                label: '',
                value: -1
            }
        ];
        for (var t in $scope.level) {
            $scope.filterLevel.push($scope.level[t]);
        }

        $scope.search = {
            tag: '',
            fullText: '',
            fullExplaination: '',
            level: $scope.filterLevel[0]
        };

        $scope.init = function (killSearch) {
            if (killSearch) {
                $scope.search = {
                    tag: '',
                    fullText: '',
                    fullExplaination: '',
                    level: $scope.filterLevel[0]
                };
            }
            $scope.countQuestions().then(function () {
                $scope.pageChanged();
            });
        };

        $scope.getQuestionById = function (id) {
            for (var i in $scope.questions) {
                if ($scope.questions[i].objectId === id) {
                    return $scope.questions[i];
                }
            }
        };

        $scope.pageChanged = function () {
            $scope.loading = true;
            var search = JSON.parse(JSON.stringify($scope.search));
            if (search.level.value > -1) {
                search.level = search.level.value;
            } else {
                delete search.level;
            }
            $scope.getQuestion({
                paginate: {
                    limit: $scope.paginator.pageSize,
                    page: $scope.paginator.page
                },
                search: search
            }).then(function () {
                $scope.loading = false;
            }, function () {
                $scope.loading = false;
            });
        };
        
        $scope.reformatLevel = function(question) {
            var getLevelIndex = function (n) {
                for (var k = 0; k < $scope.level.length; k++) {
                    if ($scope.level[k].value === n) {
                        return k;
                    }
                }
                return -1;
            };
            var level = ('undefined' === typeof question.level ? 10 : question.level);
            question.level = $scope.level[getLevelIndex(level)];
        };

        $scope.getQuestion = function (options) {
            return Question.getQuestions(options).then(function (response) {
                $scope.questions = response.data;
                for (var i in $scope.questions) {
                    var tags = ($scope.questions[i].tags ? $scope.questions[i].tags.split(',') : []);
                    for (var j in tags) {
                        tags[j] = {
                            text: tags[j]
                        };
                    }
                    $scope.questions[i].tags = tags;
                    $scope.questions[i].meta = {
                        uploading: false
                    };
                    $scope.reformatLevel($scope.questions[i]);
                }
            });
        };

        $scope.countQuestions = function () {
            return Question.countQuestions($scope.paginator.pageSize, {
                search: $scope.search
            }).then(function (response) {
                $scope.paginator.pageCount = response.data.pages;
                $scope.paginator.total = response.data.total;
                $scope.paginator.page = 1;
            });
        };

        $scope.addQuestion = function () {
            $scope.addingQuestion = true;
            Question.addQuestion({
                text: $filter('translate')('Type your question')
            }).then(function (response) {
                response.data.meta = {
                    uploading: false
                };
                response.data.tags = [];
                $scope.reformatLevel(response.data);
                $scope.questions.unshift(response.data);
                $scope.addingQuestion = false;
            }, function (err) {
                $scope.addingQuestion = false;
            });
        };

        $scope.updateQuestion = function (question, data) {
            return Question.updateQuestion(angular.extend({
                questionId: question.objectId
            }, data)).then(function (resp) {
                question.image = resp.data.image;
            }, function () {});
        };

        $scope.addChoice = function (question) {
            question.$addingChoice = true;
            Question.addChoice({
                questionId: question.objectId,
                text: $filter('translate')('Type your choice')
            }).then(function (response) {
                question.choices.push(response.data);
                delete question.$addingChoice;
            }, function (err) {
                delete question.$addingChoice;
            });
        };

        $scope.updateChoice = function (data) {
            return Question.updateChoice(data);
        };

        $scope.deleteQuestion = function (question) {
            Question.deleteQuestion({
                questionId: question.objectId
            }).then(function (data) {
                $scope.questions.splice($scope.questions.indexOf(question), 1);
            }, function (err) {});
        };

        $scope.deleteChoice = function (question, choice) {
            choice.$deleted = true;
            Question.deleteChoice({
                choiceId: choice.objectId
            }).then(function (data) {
                question.choices.splice(question.choices.indexOf(choice), 1);
            }, function (err) {});
        };

        $scope.isMultichoice = function (question) {
            var positiveCounter = 0;
            for (var i in question.choices) {
                if ((question.choices[i].scoring) && (question.choices[i].scoring > 0)) {
                    positiveCounter++;
                }
            }
            return (positiveCounter !== 1);
        };

        $scope.upload = function (file, question) {
            question.meta.uploading = true;
            upload('updateQuestion', file, {
                questionId: question.objectId
            }).then(function (resp) {
                question.image = resp.data.result.data.image;
                question.meta.uploading = false;
            });
        };

        $scope.uploading = function (question) {
            return question.meta.uploading;
        };

        $scope.init();

}]);