'use strict';

var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res) {

    res.set('Content-Type', 'application/json');

    var userInformation = {};

    userInformation.user = 'Szabolcs Varadi' || req.session.user_name;
    userInformation.userId = '12345' || req.session.user_id;
    userInformation.img = '/user/profile-pic.png';
    userInformation.reminders = [
        {
            reminder: 'Pontozási időszak van'
        },
        {
            reminder: 'Kitöltetlen pontozás'
        }
    ]

    res.send(JSON.stringify(userInformation));
});

module.exports = router;

