/*global angular */
angular.module('Quizz').controller('SimulatorCtrl', ['$scope', 'rollerDerbyModel', 'Animation', function ($scope, rollerDerbyModel, Animation) {
    "use strict";
    $scope.scene = new rollerDerbyModel.Scene({
        scale: 0.25
    });
    $scope.animations = [];
    $scope.index = 0;

    Animation.getAnimations().then(function (data) {
        $scope.animations = data.data;
        if ($scope.animations.length) {
            $scope.loadAnimation(0);
            $scope.index=0;
        }
    }, function (err) {});

    $scope.launchAnimation = function () {
        $scope.scene.launchAnimation(0, function() {
            console.log('DONE');
        });
    };
    
    $scope.appendAnimation = function(player, animationData) {
        player.loadAnimation(animationData);
    };

    $scope.loadAnimation = function (index) {
        var teamNames = Object.keys($scope.scene.teams);
        if (index < $scope.animations.length) {
            for (var i in $scope.animations[index].data.teams) {
                var dataTeam = $scope.animations[index].data.teams[i];
                var sceneTeam = $scope.scene.teams[teamNames[i]];
                for (var playerIndex in sceneTeam.players) {
                    var dataAnimations = dataTeam.players[playerIndex].animations;
                    for (var j in dataAnimations) {
                        $scope.appendAnimation(sceneTeam.players[playerIndex], dataAnimations[j]);
                    }
                }
            }
        }
    };

    $scope.$watch('index', function (newIndex, oldIndex) {
        $scope.loadAnimation(newIndex);
    });

}]);