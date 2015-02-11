/*global module */

/**
 * Get questions with choices
 * @param   Object   options options to pass
 * @returns Promise
 */
var getQuestions = function (options) {
    var questionQuery = new Parse.Query('Question');
    var mainPromise = new Parse.Promise();
    if (options.paginate) {
        questionQuery.limit(options.paginate.limit);
        questionQuery.skip((options.paginate.page - 1) * options.paginate.limit);
    }
    if (options.filter) {
        for (var i in options.filter) {
            questionQuery.equalTo(i, options.filter[i]);
        }
        delete(options.filter);
    }
    if (options.search) {
        if (options.search.tag) {
            questionQuery.contains('tags', options.search.tag);
        }
        if (options.search.fullText) {
            questionQuery.contains('text', options.search.fullText);
        }
        if (options.search.fullExplaination) {
            questionQuery.contains('explaination', options.search.fullExplaination);
        }
    }
    questionQuery.descending('updatedAt');
    questionQuery.find({
        success: function (questions) {
            var result = [];
            var promises = [];
            for (var i = 0; i < questions.length; i++) {
                var relation = questions[i].relation('choice');
                var questionResult = {
                    text: questions[i].get('text'),
                    tags: questions[i].get('tags'),
                    image: (questions[i].get('image') ? questions[i].get('image')._url || questions[i].get('image').url : null),
                    objectId: questions[i].id,
                    choices: []
                };
                if ((options.user) || (options.asAdmin)) {
                    questionResult.explaination = questions[i].get('explaination');
                }
                result.push(questionResult);

                promises.push(getChoices(questions[i], questionResult, options));
            }

            Parse.Promise.when(promises).then(function () {
                mainPromise.resolve({
                    status: 'success',
                    data: result
                });
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

/**
 * Get all the choices of a question
 * @param   Question question Question whose choices must be retrieved
 * @param   Object   dest     Destination object
 * @param   Object   options  Options to pass {user}
 * @returns Promise
 */
var getChoices = function (question, dest, options) {
    var relation = question.relation('choice');
    return relation.query().find({
        success: function (choices) {
            var multiAnswerCounter = 0;
            for (var j = 0; j < choices.length; j++) {
                multiAnswerCounter += (choices[j].get('scoring') > 0 ? 1 : 0);
                if ((options.user) || (options.asAdmin)) {
                    dest.choices.push({
                        text: choices[j].get('text'),
                        scoring: (choices[j].get('scoring') ? choices[j].get('scoring') : 0),
                        objectId: choices[j].id
                    });
                } else {
                    dest.choices.push({
                        text: choices[j].get('text'),
                        objectId: choices[j].id
                    });
                }
            }
            dest.multiAnswer = (multiAnswerCounter > 1);
        },
        error: function (choices, error) {
            mainPromise.reject({
                status: 'error',
                message: 'An error occured when reading choices'
            });
        }
    });
};

var checkAnswer = function (data) {
    var promise = new Parse.Promise();
    getQuestions({
        filter: {
            objectId: data.questionId
        },
        asAdmin: true
    }).then(function (resp) {
        var question = resp.data[0];
        question.scoring = 0;
        question.maxScoring = 0;
        question.check = true;
        for (var i in question.choices) {
            question.choices[i].answered = (data.answer.indexOf(question.choices[i].objectId) > -1);
            question.scoring += (question.choices[i].answered ? question.choices[i].scoring : 0);
            question.maxScoring += (question.choices[i].scoring > 0 ? question.choices[i].scoring : 0);
            if (((question.choices[i].answered) && (question.choices[i].scoring > 0)) || ((!question.choices[i].answered) && (question.choices[i].scoring <= 0))) {
                question.choices[i].check = true;
            } else {
                question.choices[i].check = false;
            }
            if (!question.choices[i].check) {
                question.check = false;
            }
        }
        promise.resolve(question);
    }, function () {
        promise.reject();
    });
    return promise;
};

/*********************************************************************/
/*********************************************************************/

Parse.Cloud.define('getQuestions', function (request, response) {
    getQuestions({
        user: request.user,
        paginate: request.params.paginate,
        search: request.params.search
    }).then(function (data) {
        response.success(data);
    }, function (err) {
        response.error(err);
    });
});

Parse.Cloud.define('randomQuestions', function (request, response) {
    var questionQuery = new Parse.Query('Question');
    questionQuery.count({
        success: function (data) {
            var list = [];
            var promises = [];
            if (request.params.count < data) {
                for (var i = 0; i < request.params.count; i++) {
                    var num;
                    do {
                        num = Math.floor((Math.random() * data + 1));
                    } while (list.indexOf(num) > -1);
                    list.push(num);
                    promises.push(getQuestions({
                        user: request.user,
                        paginate: {
                            limit: 1,
                            page: num
                        }
                    }));
                }
                Parse.Promise.when(promises).then(function () {
                    var result = [];
                    for (var i = 0; i < arguments.length; i++) {
                        result.push(arguments[i].data[0]);
                    }
                    response.success({
                        status: 'success',
                        data: result
                    });
                }, function () {
                    response.error({
                        status: 'error',
                        message: 'Could not random questions'
                    })
                });
            } else {
                getQuestions({
                    user: request.user,
                }).then(function (data) {
                    response.success({
                        status: 'success',
                        data: data.data
                    });
                }, function () {
                    response.error({
                        status: 'error',
                        message: 'Could not random questions'
                    });
                });
            }
        },
        error: function () {
            response.error({
                status: 'error',
                message: 'Could not count questions'
            });
        }
    });
});

Parse.Cloud.define('countQuestions', function (request, response) {
    var questionQuery = new Parse.Query('Question');
    if (request.params.search) {
        if (request.params.search.tag) {
            questionQuery.contains('tags', request.params.search.tag);
        }
        if (request.params.search.fullText) {
            questionQuery.contains('text', request.params.search.fullText);
        }
        if (request.params.search.fullExplaination) {
            questionQuery.contains('explaination', request.params.search.fullExplaination);
        }
    }
    questionQuery.count({
        success: function (data) {
            var result = {
                total: data
            };
            if (request.params.pageSize) {
                result.pages = Math.ceil(data / request.params.pageSize);
            }
            response.success({
                status: 'ok',
                data: result
            });
        },
        error: function () {
            response.error({
                status: 'error',
                message: 'Could not count questions'
            });
        }
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
            if (request.params.explaination) {
                question.set('explaination', request.params.explaination);
            }
            if ((request.params.file) && (request.params.filename)) {
                var imageBase64 = request.params.file.replace(/^data:image\/(png|jpeg);base64,/, "");
                question.set('image', new Parse.File(request.params.filename, {
                    base64: imageBase64
                }));
            }
            if (request.params.unsetFile) {
                question.set('image', null);
            }
            question.save(null, {
                success: function (obj) {
                    getQuestions({
                        user: request.user,
                        filter:  {
                            objectId: request.params.questionId
                        }
                    }).then(function (data) {
                        response.success({
                            status: 'success',
                            message: 'Question updated',
                            data: data.data[0]
                        });

                    }, function (err) {
                        response.error({
                            status: 'error',
                            message: 'Your question just disappeared !'
                        });
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

Parse.Cloud.define('checkAnswers', function (request, response) {
    var answers = request.params.answers;
    var promises = [];
    for (var i in answers) {
        promises.push(checkAnswer(answers[i]));
    }
    Parse.Promise.when(promises).then(function () {
        response.success({
            status: 'success',
            data: arguments
        });
    }, function () {
        response.error({
            status: 'error',
            message: 'Could not check the answers'
        });
    });
});

/*********************************************************************/
/*********************************************************************/

module.exports = {
    getQuestions: getQuestions
};