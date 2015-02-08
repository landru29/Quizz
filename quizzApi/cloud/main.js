var choiceDelete = function (choiceId) {
    var choiceQuery = new Parse.Query('Choice');
    var promise = new Parse.Promise();
    choiceQuery.get(choiceId, {
        success: function (choice) {
            choice.destroy({
                success: function (obj) {
                    promise.resolve({
                        status: 'success',
                        message: 'Choice deleted'
                    });
                },
                error: function (obj, error) {
                    promise.reject({
                        status: 'error',
                        message: 'Could not delete choice'
                    });
                }
            });
        },
        error: function (choice, error) {
            promise.reject({
                status: 'error',
                message: 'Could not find choice'
            });
        }
    });
    return promise;
}

var getQuestions = function (options) {
    var questionQuery = new Parse.Query('Question');
    var mainPromise = new Parse.Promise();
    if (options.tag) {
        questionQuery.contains('tags', options.tag);
    }
    questionQuery.find({
        success: function (questions) {
            var result = [];
            var promises = [];
            for (var i = 0; i < questions.length; i++) {
                var relation = questions[i].relation('choice');
                var questionResult = {
                    text: questions[i].get('text'),
                    tags: questions[i].get('tags'),
                    objectId: questions[i].id,
                    multiAnswerCounter: 0,
                    choices: []
                };
                result.push(questionResult);

                promises.push((function (quest) {
                    return relation.query().find({
                        success: function (choices) {
                            for (var j = 0; j < choices.length; j++) {
                                quest.multiAnswerCounter += (choices[j].get('scoring') > 0 ? 1 : 0);
                                if (options.user) {
                                    quest.choices.push({
                                        text: choices[j].get('text'),
                                        scoring: choices[j].get('scoring'),
                                        objectId: choices[j].id,
                                        explaination: choices[j].get('explaination')
                                    });
                                } else {
                                    quest.choices.push({
                                        text: choices[j].get('text'),
                                        objectId: choices[j].id,
                                        explaination: choices[j].get('explaination')
                                    });
                                }
                            }
                        },
                        error: function (choices, error) {
                            mainPromise.reject({
                                status: 'error',
                                message: 'An error occured when reading choices'
                            });
                        }
                    });
                })(questionResult));
            }

            Parse.Promise.when(promises).then(function () {
                /* Compute multi choice */
                for (var qIndex in result) {
                    result[qIndex].multiAnswer = (result[qIndex].multiAnswerCounter > 1);
                    delete result[qIndex].multiAnswerCounter;
                }
                /* Check if randomized questions */
                if (options.randomize) {
                    var list = [];
                    var randomizedQuestions = []
                    for (var i = 0; i < options.randomize; i++) {
                        var num;
                        do {
                            num = Math.floor((Math.random() * result.length));
                        } while (list.indexOf(num) > -1);
                        list.push(num);
                        randomizedQuestions.push(result[num]);
                    }
                    mainPromise.resolve({
                        status: 'success',
                        data: randomizedQuestions
                    });
                } else {
                    mainPromise.resolve({
                        status: 'success',
                        data: result
                    });
                }
            }, function () {
                mainPromise.reject({
                    status: 'error',
                    message: 'Could not get all the quizz choices'
                })
            });
        },
        error: function (error) {
            mainPromise.reject({
                status: 'error',
                message: 'Could not get all the quizz questions'
            });
        }
    });
    return mainPromise;
};


Parse.Cloud.define('getQuestions', function (request, response) {
    getQuestions({
        user: request.user
    }).then(function (data) {
        response.success(data);
    }, function (err) {
        response.error(err);
    });
});

Parse.Cloud.define('addQuestion', function (request, response) {
    var Question = Parse.Object.extend('Question');
    var thisQuestion = new Question();
    thisQuestion.set('text', request.params.text);
    thisQuestion.set('tags', request.params.tags);
    thisQuestion.save(null, {
        success: function (question) {
            response.success({
                status: 'ok',
                data: {
                    text: question.get('text'),
                    tags: question.get('tags'),
                    objectId: question.id,
                    choices: []
                }
            });
        },
        error: function (question, err) {
            console.log(err);
            response.error({
                status: 'error',
                message: 'Could not create question'
            });
        }
    });
});

Parse.Cloud.define('updateQuestion', function (request, response) {
    var questionQuery = new Parse.Query('Question');
    questionQuery.get(request.params.questionId, {
        success: function (question) {
            if (request.params.text) {
                question.set('text', request.params.text);
            }
            if (request.params.tags) {
                question.set('tags', request.params.tags);
            }
            question.save(null, {
                success: function (obj) {
                    response.success({
                        status: 'success',
                        message: 'Question updated'
                    });
                },
                error: function (obj, error) {
                    response.error({
                        status: 'error',
                        message: 'Could not update question'
                    });
                }
            });
        },
        error: function (question, error) {
            response.error({
                status: 'error',
                message: 'Could not find question'
            });
        }
    });
});

Parse.Cloud.define('deleteQuestion', function (request, response) {
    var questionQuery = new Parse.Query('Question');
    questionQuery.get(request.params.questionId, {
        success: function (question) {
            var relation = question.relation('choice');
            var promises = [];
            relation.query().find({
                success: function (choices) {
                    for (var i = 0; i < choices.length; i++) {
                        promises.push(choices[i].destroy());
                    }
                    promises.push(question.destroy());
                    Parse.Promise.when(promises).then(function (obj) {
                        response.success({
                            status: 'success',
                            message: 'Question deleted'
                        });
                    }, function (err) {
                        response.error({
                            status: 'error',
                            message: 'Could not delete question'
                        });
                    });
                },
                error: function (obj, error) {
                    response.error({
                        status: 'error',
                        message: 'Could not delete linked choices'
                    });
                }
            });
        },
        error: function (question, error) {
            response.error({
                status: 'error',
                message: 'Could not find question'
            });
        }
    });
});

Parse.Cloud.define('deleteChoice', function (request, response) {
    choiceDelete(request.params.choiceId).then(function (data) {
        response.success(data);
    }, function (error) {
        response.error(error);
    });
});

Parse.Cloud.define('updateChoice', function (request, response) {
    var choiceQuery = new Parse.Query('Choice');
    choiceQuery.get(request.params.choiceId, {
        success: function (choice) {
            if (request.params.text) {
                choice.set('text', request.params.text);
            }
            if ('undefined' !== typeof request.params.scoring) {
                choice.set('scoring', parseInt(request.params.scoring, 10));
            }
            choice.save(null, {
                success: function (obj) {
                    response.success({
                        status: 'success',
                        message: 'Choice updated'
                    });
                },
                error: function (choice, error) {
                    response.error({
                        status: 'error',
                        message: 'Could not update choice'
                    });
                }
            });
        },
        error: function (choice, error) {
            response.error({
                status: 'error',
                message: 'Could not find choice'
            });
        }
    });
});

Parse.Cloud.define('addChoice', function (request, response) {
    var Choice = Parse.Object.extend('Choice');
    var thisChoice = new Choice();
    var scoring = parseInt(request.params.scoring, 10);
    if (!scoring) scoring = 0;
    thisChoice.set('text', request.params.text);
    thisChoice.set('scoring', ('number' === typeof scoring ? scoring : 0));
    thisChoice.save(null, {
        success: function (choice) {
            var questionQuery = new Parse.Query('Question');
            questionQuery.get(request.params.questionId, {
                success: function (question) {
                    question.relation('choice').add(thisChoice);
                    question.save(null, {
                        success: function (obj) {
                            response.success({
                                status: 'success',
                                data: {
                                    text: thisChoice.get('text'),
                                    objectId: thisChoice.id,
                                    scoring: thisChoice.get('scoring')
                                }
                            });
                        },
                        error: function (obj, error) {
                            response.error({
                                status: 'error',
                                message: 'Could not update question'
                            });
                        }
                    });
                },
                error: function (error) {
                    response.error({
                        status: 'error',
                        message: 'Question does not exist'
                    });
                }
            });
        },
        error: function (choice, error) {
            response.error({
                status: 'error',
                message: 'Could not insert a choice'
            });
        }
    });
});

Parse.Cloud.define('getRoles', function (request, response) {
    var roleNames = ['guest'];
    if (!request.user) {
        response.success({
            status: 'success',
            data: roleNames
        });
    } else {
        (new Parse.Query(Parse.Role)).equalTo("users", request.user).find({
            success: function (roles) {
                for (var i in roles) {
                    roleNames.push(roles[i].get('name'));
                }
                response.success({
                    status: 'success',
                    data: roleNames
                });
            },
            error: function (obj, err) {
                response.error({
                    status: 'error',
                    message: 'Could not read roles'
                });
            }
        });
    }
});