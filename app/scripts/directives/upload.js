/*global angular */
angular.module('Quizz').directive('upload', [function () {
    'use strict';
    return {
        restrict: 'EA',
        replace: false,
        scope: {
            ngCaption:'@',
            upload:'&',
        },
        template: '<span>' +
            '<input type="file" name="fileUpload" style="display:none;"/>' +
            '<span class="caption btn btn-primary"></span>' +
            '</span>',
        link: function (scope, element, attrs) {
            var inputFile = element.find('input');
            var caption = element.find('span.caption');
            
            caption.html(scope.ngCaption);
            caption.on('click', function(event) {
                inputFile.trigger('click');
            });

            inputFile.change(function (event) {
                var files = inputFile.prop('files');
                if (files.length) {
                    scope.upload({$file: files[0]});
                }
            });
        }
    };
    }]);