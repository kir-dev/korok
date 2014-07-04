
var request = require('request');
var crypto = require('crypto');

function checkAccessToken(accessToken, clientID, clientSecret) {
    request.post('https://auth.sch.bme.hu/oauth2/resource', {form: {access_token: accessToken}},
        function(err, res, body) {

            console.log(body);
    });
}

module.exports.getToken = function(req, res, next) {
    var clientID = '53882599638799218494';
    var clientSecret = 'VKnuk0SLOyACQrVIeEbmcWRmQfGbGFQcQ5M14fCIUCLltguOlL2DiIqyowPv3gOIbb0V8BdRDo3xLnCw';
    var accessToken = '';

    request.post('https://auth.sch.bme.hu/oauth2/token', {form: {grant_type: 'client_credentials'}},
        function(error, response, body) {

            console.log(body);

            var response = JSON.parse(body);
            accessToken = response.access_token;
            console.log(accessToken);

            checkAccessToken(accessToken, clientID, clientSecret);

            var shasum = crypto.createHash('sha1');
            shasum.update(req.get('User-Agent'));

            res.redirect(
                    'https://auth.sch.bme.hu/site/login?response_type=code&client_id='
                    + clientID +
                    '&state='
                    + shasum.digest('base64') +
                    '&scope='
                    + 'basic+displayName+sn+givenName+mail+linkedAccounts+roomNumber'
            )

    }).auth(clientID, clientSecret, true);
}

module.exports.checkAuth = function(req, res, next) {
    if (!req.session.user_id) {
        res.send('You are not authorized to view this page');
    } else {
        next();
    }
}