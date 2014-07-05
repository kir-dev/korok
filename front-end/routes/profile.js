/**
 * Created by Kresshy on 2014.06.29..
 */

var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res) {

    var user = 'Róka Edina';

    res.render('index', {
        title: user + ' - Profil'
    });
});

module.exports = router;

