/*global module */

/**
 * Delete a choice
 * @param   String choiceId identifier of the choice
 * @returns Promise
 */
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
};



/*********************************************************************/
/*********************************************************************/

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

module.exports = {
    choiceDelete: choiceDelete
};