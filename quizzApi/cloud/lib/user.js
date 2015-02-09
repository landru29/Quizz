/*global module */

/*********************************************************************/
/*********************************************************************/

Parse.Cloud.define('getRoles', function (request, response) {
    var roleNames = ['guest'];
    if (!request.user) {
        response.success({
            status: 'success',
            data: roleNames
        });
    } else {
        (new Parse.Query(Parse.Role)).equalTo("users", request.user).find({
            success: function (roles) {
                for (var i in roles) {
                    roleNames.push(roles[i].get('name'));
                }
                response.success({
                    status: 'success',
                    data: roleNames
                });
            },
            error: function (obj, err) {
                response.error({
                    status: 'error',
                    message: 'Could not read roles'
                });
            }
        });
    }
});

/*********************************************************************/
/*********************************************************************/

module.exports = {};