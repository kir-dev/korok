/**
 * Created by Edina on 2014.07.08..
 */

var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res) {

    var user = 'Róka Edina';

    res.render('groups-profile', {
        title: user + ' - Profil'
    });
});

module.exports = router;

