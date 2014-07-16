'use strict';

var express = require('express');

var routerProfile = express.Router();
var routerSettings = express.Router();
var routerSvie = express.Router();
var routerProfileByID = express.Router();

/* GET home page. */
routerProfile.get('/', function(req, res) {

    var user = 'Full Name';

    res.render('profile', {
        title: user + ' - Profil'
    });
});

routerProfile.post('/', function(req, res) {

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

routerSettings.get('/', function(req, res) {
    var user = 'Full Name';

    res.render('settings', {
        title: user + ' - Settings'
    });
});

routerSettings.post('/', function(req, res) {

});

routerSvie.get('/', function(req, res) {

});

routerSvie.post('/', function(req, res) {

});

routerProfileByID.get('/', function(req, res) {

});

routerProfileByID.post('/', function(req, res) {

});

module.exports = {
    routerProfile: routerProfile,
    routerSettings: routerSettings,
    routerSvie: routerSvie,
    routerProfileByID: routerProfileByID
};

