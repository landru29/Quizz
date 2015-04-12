var AnimationModule = function (parseInstance) {
    'use strict';
    var q = require('q');
    var parse = parseInstance;

    this.getAll = function () {
        var defered = q.defer();
        var Animation = parse.Object.extend("Animation");
        var animationQuery = new parse.Query(Animation);
        animationQuery.descending('createdAt');
        animationQuery.find({
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
    return new AnimationModule(parseInstance);
};