'use strict';

var request = require('request');
var crypto = require('crypto');
var config = require('../config.js')
var Promise = require('promise');

var _state = '';
var _accessToken = '';
var _userAccessToken = '';
var _refreshToken = '';

var _clientID = config.clientID;
var _clientSecret = config.clientSecret;
var _tokenURL = config.tokenURL;
var _tokenVerifyURL = config.tokenVerifyURL;
var _authenticationURL = config.authenticationURL;
var _authProviderURL = config.authProviderURL;

/// check if the user is authenticated or not
var checkAuth  = function checkAuth(req, res, next) {
    if (!req.session.user_id) {
        authenticate(req, res, next);
    } else {
        next();
    }
}

/// authenticate the user
var authenticate = function authenticate(req, res, next) {

    var isAccessTokenVerified = false;

    if (!_accessToken) {
        // access token is missing

        console.log('Access Token is missing');
        getAccessToken(req, res, next);
    } else {
        // verifying existing access token

        verifyAccessToken().then( function(data) {
            // promise resolve

            if(data.success) {
                console.log('access token is verified: ' + data.success);
                isAccessTokenVerified = true;
            } else {
                console.log('access token is verified: ' + data.success);
                isAccessTokenVerified = false;
            }

            if(isAccessTokenVerified) {
                console.log('redirecting to authURL');
                redirectAuthenticationURL(req, res, next);
            } else {
                console.log('asking for a new access token');
                getAccessToken(req, res, next);
            }

        }, function (err) {
            // promise reject

            res.send(500, 'something bad happened');
            console.log(err);
        });
    }
}

/// verifying access token
var verifyAccessToken = function verifyAccessToken(req, res, next) {
    return new Promise( function(resolve, reject) {
        request.post( _tokenVerifyURL, {form: {access_token: _accessToken}},
            function(error, response, body) {

                if(error) {
                    return reject(error);
                }

                var responseBody = JSON.parse(body);
                console.log(responseBody);

                return resolve(responseBody);
        });
    });
}

/// returning the right authentication URL and generating state from User-Agent
var getAuthenticationURL = function getAuthenticationURL(req, res, next) {
    var shasum = crypto.createHash('sha1');
    shasum.update(req.get('User-Agent'));
    setState(shasum.digest('base64'))

    return _authenticationURL +
        '?response_type=code&client_id=' +
        _clientID +
        '&state=' +
        _state +
        '&scope=basic+displayName+sn+givenName+mail+linkedAccounts+roomNumber';
}

/// get a new access token from the authentication provider
var getAccessToken = function getAccessToken(req, res, next) {
    request.post(_tokenURL, {form: {grant_type: 'client_credentials'}},
        function(error, response, body) {

             var responseBody = JSON.parse(body);
            _accessToken = responseBody.access_token;

            console.log('the access token is: ' + _accessToken);

            if(!_accessToken)
                throw new Error('Access token is missing');

            var isAccessTokenVerified = false;

            verifyAccessToken().then( function(data) {
                if(data.success) {
                    console.log('access token is verified: ' + data.success);
                    isAccessTokenVerified = true;
                } else {
                    console.log('access token is verified: ' + data.success);
                    isAccessTokenVerified = false;
                }

                if(isAccessTokenVerified) {
                    console.log('redirecting to authURL');
                    redirectAuthenticationURL(req, res, next);
                }
            }, function (err) {
                res.send(500, 'something bad happened');
                console.log(err);
            });

    }).auth(_clientID, _clientSecret, true);
}

/// redirect to authentication URL
var redirectAuthenticationURL = function redirectAuthenticationURL(req, res, next) {
    res.redirect(getAuthenticationURL(req));
}

/// set state for authentication URL
var setState = function setState(state) {
    _state = state;
}

/// get state for verification when redirected to the site
var getState = function getState(state) {
    return _state;
}

/// getting the user access token and the refresh token
var loginUser = function loginUser(req, res, next, code) {
    request.post(_tokenURL, {form: {grant_type: 'authorization_code', code: code}},
        function(error, response, body) {

            var responseBody = JSON.parse(body);
            _refreshToken = responseBody.refresh_token;
            _userAccessToken = responseBody.access_token;

            console.log(_refreshToken);
            console.log(_userAccessToken);
            console.log(body);

            getUserProfileInformation(req, res, next, _userAccessToken);

    }).auth(_clientID, _clientSecret, true);
}


/// the final step of the authentication when requesting the profile information
var getUserProfileInformation = function getUserProfileInformation(req, res, next, userAccessToken) {
    request.get(_authProviderURL + 'api/profile?access_token=' + userAccessToken,
        function(error, response, body) {
            if (error)
                console.log(error.message);

            var responseBody = JSON.parse(body);
            req.session.user_id = responseBody.internal_id;
            req.session.user_name = responseBody.displayName;

            //var minute = 60 * 1000;
            //res.cookie('user_id', responseBody.internal_id, {maxAge: minute});

            console.log(responseBody);

            // the user is authenticated redirecting to landing-page
            res.redirect('/');
    });
}

module.exports = {
    checkAuth: checkAuth,
    loginUser: loginUser
}