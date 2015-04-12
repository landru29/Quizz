var BackupInstance = function (backupFolder) {
    var Parse = require('parse').Parse;
    Parse.initialize("qJglDDzyCTc8qUB2Z5KdqvD5IUQmbUWiHJ0fNeIW", "eRgIH4su5FsFRmPq2ubWTyBiiusqTrQygj7ZL6l9");
    var Question = require('./model/question.js')(Parse);
    var Animation = require('./model/animation.js')(Parse);
    var q = require('q');

    var backup = function () {
        var promises = [];
        var defered = q.defer();

        // push tasks
        promises.push(Question.getAll(backupFolder));
        promises.push(Animation.getAll());

        q.all(promises).then(function (data) {
            // render tasks results
            defered.resolve({
                questions: data[0],
                animations: data[1]
            });
        }, function (err) {
            defered.reject(err);
        });

        return defered.promise;
    };

    return backup();
};


var mainProcess = function (backupDir)  {

    var d = new Date();
    var folder = d.getFullYear() + '-' + ('00' + (d.getMonth() + 1)).slice(-2) + '-' + ('00' + d.getDate()).slice(-2)

    var fs = require('fs');
    if (!fs.existsSync(backupDir + folder + '/')) {
        fs.mkdirSync(backupDir + folder + '/');
    }

    (new BackupInstance(backupDir + folder + '/')).then(function (results) {
        fs.writeFile(backupDir + folder + '/backup.json', JSON.stringify(results), function (err) {
            if (err) {
                console.log(err);
            } else {
                console.log('Data backup is in folder ' + backupDir + folder);
            }
        });
    }, function (err) {
        console.log(err);
    });
};

mainProcess('./backup-files/');