/*global angular */
angular.module('Quizz').provider('Parse', [function () {

    this.$get = ['$q', function ($q) {

        return function (req) {
            var defered = $q.defer();
            Parse.Cloud.run(req.resource, req.data, {
                success: function (result) {
                    defered.resolve(result);
                },
                error: function (error) {
                    defered.reject(error);
                }
            });
            return defered.promise;
        };
}];

}]);