/*global angular */
angular.module('Quizz').controller('SimulatorCtrl', ['$scope', '$rootScope', 'rollerDerbyModel', 'Animation', '$interval', function ($scope, $rootScope, rollerDerbyModel, Animation, $interval) {
    "use strict";

    $scope.scene = new rollerDerbyModel.Scene({
        scale: 0.25
    });

    $scope.lastSave = null;

    $scope.savingFrequency = 2000;

    $scope.admin = (($rootScope.roles) && ($rootScope.roles.indexOf('admin') > -1));
    $scope.scene.setEditMode($scope.admin);
    if ($scope.admin) {
        $scope.scene.setScale(0.2);
    }

    $scope.animations = [];
    $scope.index = 1;
    
    $scope.getIndex = function() {
        return $scope.index-1;
    };

    Animation.getAnimations().then(function (data) {
        $scope.animations = data.data;
        if ($scope.animations.length) {
            $scope.loadAnimation(0);
            $scope.index = 1;
            $scope.checkSave();
            if ($scope.admin) {
                $scope.saving = $interval(function () {
                    $scope.checkSave();
                }, $scope.savingFrequency);
            }
        }
    }, function (err) {});

    $scope.add = function () {
        Animation.addAnimation({
            data: {
                teams: [{
                    color: 'red',
                    players: [{
                        animations: [],
                        name: 'B1-A',
                        position: {
                            x: 1400,
                            y: -830
                        },
                        role: 'blocker',
                        type: 'player'
            }, {
                        animations: [],
                        name: 'B2-A',
                        position: {
                            x: 1450,
                            y: -780
                        },
                        role: 'blocker',
                        type: 'player'
            }, {
                        animations: [],
                        name: 'B3-A',
                        position: {
                            x: 1500,
                            y: -730
                        },
                        role: 'blocker',
                        type: 'player'
            }, {
                        animations: [],
                        name: 'P-A',
                        position: {
                            x: 1550,
                            y: -680
                        },
                        role: 'pivot',
                        type: 'player'
            }, {
                        animations: [],
                        name: 'J-A',
                        position: {
                            x: 1600,
                            y: -630
                        },
                        role: 'jammer',
                        type: 'player'
            }],
                    position: 0
        }, {
                    color: 'green',
                    players: [{
                        animations: [],
                        name: 'B1-B',
                        position: {
                            x: 1400,
                            y: 800
                        },
                        role: 'blocker',
                        type: 'player'
            }, {
                        animations: [],
                        name: 'B2-B',
                        position: {
                            x: 1450,
                            y: 750
                        },
                        role: 'blocker',
                        type: 'player'
            }, {
                        animations: [],
                        name: 'B3-B',
                        position: {
                            x: 1500,
                            y: 700
                        },
                        role: 'blocker',
                        type: 'player'
            }, {
                        animations: [],
                        name: 'P-B',
                        position: {
                            x: 1550,
                            y: 650
                        },
                        role: 'pivot',
                        type: 'player'
            }, {
                        animations: [],
                        name: 'J-B',
                        position: {
                            x: 1600,
                            y: 600
                        },
                        role: 'jammer',
                        type: 'player'
            }],
                    position: 1
        }]
            },
            explaination: 'Explication',
            title: 'Titre',
            order: 100
        }).then(function(animation) {
            $scope.animations.push(animation.data);
            $scope.index = $scope.animations.length;
        });
    };
    
    $scope.getSize = function() {
        return $scope.animations.length;
    };

    $scope.launchAnimation = function () {
        $scope.scene.launchAnimation(0, function () {
            console.log('DONE');
        });
    };

    $scope.checkSave = function () {
        var result = false;

        if ($scope.animations.length === 0) {
            return result;
        }

        if (!$scope.admin) {
            return result;
        }

        var theTeams = [];
        for (var i in $scope.scene.teams) {
            theTeams.push($scope.scene.teams[i].stringify());
        }
        var newData = {
            data: JSON.parse('{"teams":[' + theTeams.join(',') + ']}'),
            explaination: $scope.animations[$scope.getIndex()].explaination,
            title: $scope.animations[$scope.getIndex()].title,
            order: $scope.animations[$scope.getIndex()].order,
            animationId: $scope.animations[$scope.getIndex()].id
        };

        var objComp = function (obj1, obj2) {
            if ('undefined' === typeof obj2) {
                return false;
            }
            var equ = true;
            for (var i in obj1) {
                if ('object' === typeof obj1[i]) {
                    equ &= objComp(obj1[i], obj2[i]);
                } else {
                    equ &= (obj1[i] === obj2[i]);
                }
            }
            return equ;
        };

        if (($scope.lastSave !== null) && (!objComp(newData, $scope.lastSave))) {
            result = newData;
            //Save the object
            if (newData.animationId) {
                console.log('Saving');
                Animation.updateAnimation(newData);
            }
        }
        $scope.lastSave = newData;
        return result;
    };

    $scope.$on('$destroy', function () {
        if ($scope.saving) {
            $interval.cancel($scope.saving);
            $scope.saving = null;
        }
    });

    $scope.loadAnimation = function (index) {
        var teamNames = Object.keys($scope.scene.teams);
        if (index < $scope.animations.length) {
            for (var i in $scope.animations[index].data.teams) {
                var dataTeam = $scope.animations[index].data.teams[i];
                var sceneTeam = $scope.scene.teams[teamNames[i]];
                for (var playerIndex in sceneTeam.players) {
                    sceneTeam.players[playerIndex].loadParameters(dataTeam.players[playerIndex]);
                }
            }
            $scope.scene.api.lockAllKeyframes();
            $scope.unlockKeyFrames();
        }
    };

    $scope.$watch('index', function (newIndex, oldIndex) {
        $scope.checkSave();
        $scope.lastSave = null;
        $scope.loadAnimation(newIndex-1);
    });


    $scope.$on('role', function (event, roles) {
        if (roles.indexOf('admin') > -1) {
            $scope.scene.setEditMode(true);
            $scope.admin = true;
            $scope.scene.setScale(0.2);
            if (!$scope.saving) {
                $scope.saving = $interval(function () {
                    $scope.checkSave();
                }, $scope.savingFrequency);
            }
        } else {
            $scope.scene.setEditMode(false);
            $scope.admin = false;
            $scope.scene.setScale(0.25);
            if ($scope.saving) {
                $interval.cancel($scope.saving);
                $scope.saving = null;
            }
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

    $scope.unlockKeyFrames = function () {
        if ($scope.currentPlayer.value.animations.length) {
            $scope.scene.api.unlockKeyFrames($scope.currentPlayer.value.animations[0]);
        }
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
        $scope.scene.api.lockAllKeyframes();
        $scope.unlockKeyFrames();

    });


}]);