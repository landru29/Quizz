var QuestionModule = function (parseInstance) {
    'use strict';
    var q = require('q');
    var fs = require('fs');
    var request = require('request');
    var Choice = require('./choice.js');
    var parse = parseInstance;

    var resolveQuestion = function (question) {
        var defered = q.defer();
        var relation = question.relation('choice');
        var promises = [];
        relation.query().find({
            success: function (choices) {
                defered.resolve(choices);
            },
            error: function (error) {
                defered.reject(error);
            }
        });
        return defered.promise;
    };

    var download = function (uri, filePrefix) {
        var defered = q.defer();
        if (uri) {
            request.head(uri, function (err, res, body) {
                var matcher = uri.split('/');
                var baseName = matcher[matcher.length - 1];
                var filename = filePrefix + baseName;
                request(uri).pipe(fs.createWriteStream(filename)).on('close', function () {
                    defered.resolve(baseName);
                });
            });
        } else {
            defered.resolve('');
        }
        return defered.promise;
    };

    this.getAll = function (imagePrefix) {
        var defered = q.defer();
        var Question = parse.Object.extend("Question");
        var questionQuery = new parse.Query(Question);
        questionQuery.descending('createdAt');
        questionQuery.find({
            success: function (results) {
                var promises = [];
                var returnData = [];
                for (var i = 0; i < results.length; i++) {
                    var thisQuestion = JSON.parse(JSON.stringify(results[i]));
                    returnData.push(thisQuestion);
                    promises.push(resolveQuestion(results[i]));
                }
                q.all(promises).then(function (choicesArray) {
                    var imgPromises = [];
                    for (var i = 0; i < returnData.length; i++) {
                        returnData[i].choice = JSON.parse(JSON.stringify(choicesArray[i]));
                        imgPromises.push(download((returnData[i].image ? returnData[i].image.url : ''), imagePrefix));
                    }
                    q.all(imgPromises).then(function (imgData) {
                        for (var i = 0; i < returnData.length; i++) {
                            if (imgData[i]) {
                                returnData[i].image = {
                                    file: imgData[i]
                                };
                            }
                        }
                        defered.resolve(returnData);
                    }, function (err) {
                        defered.reject(err);
                    });

                }, function (err) {
                    defered.reject(err);
                });
            },
            error: function (error) {
                defered.reject(err);
            }
        });
        return defered.promise;
    };
};

module.exports = function (parseInstance) {
    return new QuestionModule(parseInstance);
};