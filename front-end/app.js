'use strict';

/// required modules
var express = require('express');
var path = require('path');
var http = require('http');
var favicon = require('static-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var session = require('express-session');
var params = require('express-params');
var uuid = require('node-uuid');
var config = require('./config');

/// import routing
var index = require('./routes/index');
var profile = require('./routes/profile');
var groups = require('./routes/groups');
var valuations = require('./routes/valuations');
var login = require('./routes/login');


/// authentication modules
//var passport = require('passport');
//var BearerStrategy = require('passport-http-bearer').Strategy;
//var OAuth2Strategy = require('passport-oauth').OAuth2Strategy;

var app = express();

app.set('port', process.env.PORT || 9000);

/// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

app.use(favicon());
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded());

app.use(cookieParser(config.cookieSecret));
app.use(session({
    secret: config.sessionSecret
}));

app.use(require('less-middleware')(path.join(__dirname, 'public')));
app.use(express.static(path.join(__dirname, 'public')));

//app.use(passport.initialize());
//app.use(passport.session());

params.extend(app)

/// routing
app.param('id', /^\d+$/);

app.use('/', index);

app.use('/login', login);

app.use('/profile', profile);
app.use('/profile/settings', profile);
app.use('/profile/svie', profile);
app.use('/profile/:id', profile);

app.use('/valuations', valuations);
app.use('/valuations/:id', valuations);

app.use('/groups', groups);
app.use('/groups/new', groups);
app.use('/groups/:id', groups);
app.use('/groups/:id/settings', groups);

/// error handlers
app.use(function(err, req, res, next) {
    console.error(err.stack);
    res.send(500, 'Something broken');
    next(err);
});

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
    app.use(function(err, req, res, next) {
        res.status(err.status || 500);
        res.render('error', {
            message: err.message,
            error: err
        });
    });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
        message: err.message,
        error: {}
    });
});

module.exports = app;

/// running application
app.listen(app.get('port'), function(){
    console.log('Express server listening on port ' + app.get('port'));
});