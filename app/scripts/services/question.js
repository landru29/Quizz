/*global angular */
angular.module('Quizz').provider('Question', [function () {

    this.$get = ['Parse', function ($Parse) {
        return {
            getQuestions: function() {
                return $Parse({method: 'POST', data:{}, resource:'getQuestions'});
            },
            addQuestion: function(data) {
                return $Parse({method: 'POST', data:data, resource:'addQuestion'});
            },
            updateQuestion: function(data) {
                return $Parse({method: 'POST', data:data, resource:'updateQuestion'});
            },
            addChoice: function(data) {
                return $Parse({method: 'POST', data:data, resource:'addChoice'});
            },
            updateChoice: function(data) {
                return $Parse({method: 'POST', data:data, resource:'updateChoice'});
            },
            deleteQuestion: function(data) {
                return $Parse({method: 'POST', data:data, resource:'deleteQuestion'});
            },
            deleteChoice: function(data) {
                return $Parse({method: 'POST', data:data, resource:'deleteChoice'});
            }
        };
}];

}]);