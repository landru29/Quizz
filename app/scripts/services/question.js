/*global angular */
angular.module('Quizz').provider('Question', [function () {

    this.$get = ['Parse', function ($Parse) {
        return {
            getQuestions: function () {
                return $Parse({
                    method: 'POST',
                    data: {},
                    resource: 'getQuestions'
                });
            },
            addQuestion: function (data) {
                return $Parse({
                    method: 'POST',
                    data: data,
                    resource: 'addQuestion'
                });
            },
            updateQuestion: function (data) {
                var clonedData = JSON.parse(JSON.stringify(data));
                if (clonedData.tags) {
                    for (var i in clonedData.tags) {
                        clonedData.tags[i] = clonedData.tags[i].text;
                    }
                    clonedData.tags = clonedData.tags.join(',');
                }
                return $Parse({
                    method: 'POST',
                    data: clonedData,
                    resource: 'updateQuestion'
                });
            },
            addChoice: function (data) {
                return $Parse({
                    method: 'POST',
                    data: data,
                    resource: 'addChoice'
                });
            },
            updateChoice: function (data) {
                return $Parse({
                    method: 'POST',
                    data: data,
                    resource: 'updateChoice'
                });
            },
            deleteQuestion: function (data) {
                return $Parse({
                    method: 'POST',
                    data: data,
                    resource: 'deleteQuestion'
                });
            },
            deleteChoice: function (data) {
                return $Parse({
                    method: 'POST',
                    data: data,
                    resource: 'deleteChoice'
                });
            }
        };
}];

}]);