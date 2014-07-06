'use strict';

var express = require('express');
var router = express.Router();
var auth = require('../utils/auth.js');

router.use(function(req, res, next) {

    // write your middleware her
    next();
});

/* GET home page. */
router.get('/', auth.checkAuth, function(req, res) {



    res.send('');
});

router.post('/', auth.checkAuth, function(req, res) {

});

module.exports = router;
