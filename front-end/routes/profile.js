/**
 * Created by Kresshy on 2014.06.29..
 */

var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res) {

    var user = 'Full Name';

    res.render('profile', {
        title: user + ' - Profil'
    });
});

router.post('/', function(req, res) {

    var profile = {
        name: 'Full Name',
        nick: 'NickName',
        birth: '1990.05.05',
        room: 'SCH - 111',
        phone: '+36/30-123-4567',

        groups: [
            {
                name: 'Kir-Dev',
                id: 1,
                position: 'PR felelős, fejlesztő'
            },
            {
                name: 'SDS',
                id: 1,
                position: 'webdesigner'
            },
            {
                name: 'SEM',
                id: 1,
                position: 'forrasztópáka'
            }
        ]
    };

    res.send(profile);
});

module.exports = router;

