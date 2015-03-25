/*global angular */
angular.module('Quizz').provider('Animation', [function () {

    this.$get = ['Parse', function ($Parse) {
        return {
            getAnimations: function () {
                return $Parse({
                    resource: 'getAnimations'
                });
            },
            deleteAnimation: function (options) {
                return $Parse({
                    data: options,
                    resource: 'deleteAnimation'
                });
            },
            addAnimation: function (options) {
                return $Parse({
                    data: options,
                    resource: 'addAnimation'
                });
            },
            updateAnimation: function (options) {
                return $Parse({
                    data: options,
                    resource: 'updateAnimation'
                });
            }
        };
}];

}]);