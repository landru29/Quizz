/**
 * Delete an animation
 * @param   String animationId identifier of the animation
 * @returns Promise
 */
var animationDelete = function (animationId) {
    var animationQuery = new Parse.Query('Animation');
    var promise = new Parse.Promise();
    animationQuery.get(animationId, {
        success: function (animation) {
            animation.destroy({
                success: function (obj) {
                    promise.resolve({
                        status: 'success',
                        message: 'Animation deleted'
                    });
                },
                error: function (obj, error) {
                    promise.reject({
                        status: 'error',
                        message: 'Could not delete animation'
                    });
                }
            });
        },
        error: function (animation, error) {
            promise.reject({
                status: 'error',
                message: 'Could not find animation'
            });
        }
    });
    return promise;
};


/*********************************************************************/
/*********************************************************************/

Parse.Cloud.define('getAnimations', function(request, response) {
    var animationQuery = new Parse.Query('Animation');
    animationQuery.ascending('order');
    animationQuery.find({
        success: function (animations) {
            var result = [];
            for (var i in animations) {
                result.push({
                    id: animations[i].id,
                    title: animations[i].get('title'),
                    order: animations[i].get('order'),
                    explaination: animations[i].get('explaination'),
                    data: JSON.parse(animations[i].get('data'))
                });
            }
            response.success({
                status: 'success',
                data: result
            });
        },
        error: function (error) {
            response.error({
                status: 'error',
                message: 'Could not get animations'
            });
        }
    });
});


Parse.Cloud.define('addAnimation', function (request, response) {
    var Animation = Parse.Object.extend('Animation');
    var thisAnimation = new Animation();
    thisAnimation.set('title', request.params.title);
    thisAnimation.set('explaination', request.params.explaination);
    thisAnimation.set('data', JSON.stringify(request.params.data));
    thisAnimation.set('order', (request.params.order ? request.params.order : 0));
    thisAnimation.save(null, {
        success: function (animation) {
            response.success({
                status: 'success',
                data: {
                    id: animation.id,
                    title: animation.get('text'),
                    explaination: animation.get('explaination'),
                    order: animation.get('order'),
                    data: JSON.parse(animation.get('data'))
                }
            });
        },
        error: function (animation, error) {
            response.error({
                status: 'error',
                message: 'Could not insert an animation'
            });
        }
    });
});

Parse.Cloud.define('updateAnimation', function (request, response) {
    var animationQuery = new Parse.Query('Animation');
    animationQuery.get(request.params.animationId, {
        success: function (animation) {
            if (request.params.data) {
                animation.set('data', JSON.stringify(request.params.data));
            }
            if (request.params.title) {
                animation.set('title', request.params.title);
            }
            if (request.params.explaination) {
                animation.set('explaination', request.params.explaination);
            }
            if (request.params.order) {
                animation.set('order', request.params.order);
            }
            animation.save(null, {
                success: function (obj) {
                    response.success({
                        status: 'success',
                        message: 'Animation updated'
                    });
                },
                error: function (animation, error) {
                    response.error({
                        status: 'error',
                        message: 'Could not update animation'
                    });
                }
            });
        },
        error: function (animation, error) {
            response.error({
                status: 'error',
                message: 'Could not find animation'
            });
        }
    });
});


Parse.Cloud.define('deleteAnimation', function (request, response) {
    animationDelete(request.params.animationId).then(function (data) {
        response.success(data);
    }, function (error) {
        response.error(error);
    });
});