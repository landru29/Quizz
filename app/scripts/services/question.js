/*global angular */
angular.module('Quizz').provider('Question', [function () {

    this.$get = ['Parse', function ($Parse) {
        return {
            getQuestions: function (options) {
                return $Parse({
                    data: options,
                    resource: 'getQuestions'
                });
            },
            ramdomQuestions: function (count) {
                return $Parse({
                    data: {count:count},
                    resource: 'randomQuestions'
                });
            },
            addQuestion: function (data) {
                return $Parse({
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
                    data: clonedData,
                    resource: 'updateQuestion'
                });
            },
            countQuestions: function (pageSize) {
                return $Parse({
                    data: {
                        pageSize: pageSize
                    },
                    resource: 'countQuestions'
                });
            },
            addChoice: function (data) {
                return $Parse({
                    data: data,
                    resource: 'addChoice'
                });
            },
            updateChoice: function (data) {
                return $Parse({
                    data: data,
                    resource: 'updateChoice'
                });
            },
            deleteQuestion: function (data) {
                return $Parse({
                    data: data,
                    resource: 'deleteQuestion'
                });
            },
            deleteChoice: function (data) {
                return $Parse({
                    data: data,
                    resource: 'deleteChoice'
                });
            },
            checkAnswers: function (answers) {
                return $Parse({
                    data: {answers:answers},
                    resource: 'checkAnswers'
                });
            },
        };
}];

}]);