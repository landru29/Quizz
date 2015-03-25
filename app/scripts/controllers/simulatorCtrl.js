/*global angular */
angular.module('Quizz').controller('SimulatorCtrl', ['$scope', '$rootScope', 'rollerDerbyModel', 'Animation', function ($scope, $rootScope, rollerDerbyModel, Animation) {
    "use strict";
    
    $scope.scene = new rollerDerbyModel.Scene({
        scale: 0.25
    });
    
    $scope.admin = false;
    
    $scope.animations = [];
    $scope.index = 0;

    Animation.getAnimations().then(function (data) {
        $scope.animations = data.data;
        if ($scope.animations.length) {
            $scope.loadAnimation(0);
            $scope.index = 0;
        }
    }, function (err) {});

    $scope.launchAnimation = function () {
        $scope.scene.launchAnimation(0, function () {
            console.log('DONE');
        });
    };

    $scope.appendAnimation = function (player, animationData) {
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
            $scope.hideAllKeyframes();
            $scope.showKeyFrames();
        }
    };

    $scope.$watch('index', function (newIndex, oldIndex) {
        $scope.loadAnimation(newIndex);
    });


    $scope.$on('role', function (event, roles) {
        if (roles.indexOf('admin') > -1) {
            $scope.scene.setEditMode(true);
            $scope.admin = true;
        } else {
            $scope.scene.setEditMode(false);
            $scope.admin = false;
        }
    });
    


    $scope.players = [];
    for (var i in $scope.scene.allHumans) {
        $scope.players.push({
            label: $scope.scene.allHumans[i].name,
            value: $scope.scene.allHumans[i]
        });
    }

    $scope.currentPlayer = $scope.players[0];

    $scope.addAnimationToCurrent = function () {
        $scope.currentPlayer.value.animations.push(new rollerDerbyModel.AnimationBezier($scope.scene));
    };

    $scope.launchPlayerAnimation = function (player, index) {
        $scope.scene.api.launchAnimation(player, index);
    };

    $scope.launchAllAnimation = function () {
        $scope.scene.api.launchAllAnimation();
    };

    $scope.showKeyFrames = function () {
        if ($scope.currentPlayer.value.animations.length) {
            $scope.scene.api.showKeyFrames($scope.currentPlayer.value.animations[0]);
        }
    };

    $scope.hideAllKeyframes = function () {
        $scope.scene.api.hideAllKeyframes();
    };

    $scope.exportTeams = function () {
        var theTeams = [];
        for (var i in $scope.scene.teams) {
            theTeams.push($scope.scene.teams[i].stringify());
        }
        console.log('{"teams":[' + theTeams.join(',') + ']}');
    };

    $scope.addKeyframe = function (animation) {
        $scope.scene.api.addKeyframe(animation);
    };

    $scope.$watch('currentPlayer', function (newVal, oldVal) {
        $scope.hideAllKeyframes();
        $scope.showKeyFrames();

    });


}]);