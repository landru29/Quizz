/*global angular */
angular.module('Quizz').provider('Parse', [function () {
    var $apikey;
    var $applicationId;

    this.setRestAuth = function (applicationId, apikey) {
        $applicationId = applicationId;
        $apikey = apikey;
    };

    this.$get = ['$http', function ($http) {

        return function (req) {
            return $http({
                method: req.method,
                url: 'https://api.parse.com/1/functions/' + req.resource,
                headers: {
                    'X-Parse-Application-Id': $applicationId,
                    'X-Parse-REST-API-Key': $apikey
                },
                data: req.data
            });
        };
}];

}]);