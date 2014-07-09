'use strict';

var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res) {

    var user = 'Róka Edina';

    res.render('profile', {
        title: user + ' - Profil'
    });
});

module.exports = router;

