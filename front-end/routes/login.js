'use strict';

var express = require('express');
var auth = require('../utils/auth');

var router = express.Router();

router.use(function(req, res, next) {

    // write your middleware here

    next();
});

/* GET home page. */
router.get('/', function(req, res, next) {

    var code = req.query.code;
    var state = req.query.state;

    if (code === '') {
        res.send(500, 'Something bad happened');
        throw new Error('Code cannot be empty');
    }

    if (state === '') {
        res.send(500, 'Something bad happened');
        throw new Error('State cannot be empty');
    }

    // TODO: verify received state
    auth.loginUser(req, res, next, code);
});

router.post('/', function(req, res, next) {

    res.send(200, 'stuff');
});

module.exports = router;
