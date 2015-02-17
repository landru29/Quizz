var ChoiceModule = function (parseInstance) {
    'use strict';
    var q = require('q');
    var parse = parseInstance;

    this.get = function (objectId) {
        var defered = q.defer();
        var Choice = parse.Object.extend("Choice");
        var choiceQuery = new parse.Query(Choice);
        choiceQuery.descending('createdAt');
        choiceQuery.equalTo('objectId', objectId);
        choiceQuery.find({
            success: function (results) {
                defered.resolve(results);
            },
            error: function (error) {
                defered.reject(err);
            }
        });
        return defered.promise;
    };
};

module.exports =function (parseInstance) {
    return new ChoiceModule(parseInstance);
};