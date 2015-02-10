/*global angular */
angular.module('Quizz').provider('upload', [function () {

    this.$get = ['$http', '$q', function ($http, $q) {

        return function (resource, file, data) {
            var defered = $q.defer();
            var request = angular.extend({
                _ApplicationId: Parse.applicationId,
                _ClientVersion: Parse.VERSION,
                _InstallationId: Parse._installationId,
                _JavaScriptKey: Parse.javaScriptKey,
                _SessionToken: Parse.User._currentUser._sessionToken
            }, data);
            var url = 'https://api.parse.com/1/functions/' + resource;
            console.log(file);
            var reader = new FileReader();
            reader.onload = function (e) {
                var dataURL = reader.result;
                console.log(dataURL);
                $http.post(url, angular.extend({
                    file: dataURL,
                    filename: file.name
                }, request), {}).then(function (data) {
                    defered.resolve(data);
                }, function (err) {
                    defered.reject(err);
                });
            };
            reader.readAsDataURL(file);

            return defered.promise;
        };
}];

}]);