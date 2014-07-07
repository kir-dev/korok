/**
 * Created by Kresshy on 2014.06.29..
 */

var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res) {

    var user = 'Róka Edina';

    res.render('profile', {
        title: user + ' - Profil'
    });
});

router.post('/', function(req, res) {

    var groups = {
        groups: [
            {
                name: 'Kir-Dev',
                id: 1,
                members: [
                    {
                        name: 'Szabolcs Varadi',
                        id: '1',
                        position: 'PR Felelős, fejlesztő',
                        joined: '2000.10.10',

                    },
                    {
                        name: 'Szabolcs Varadi',
                        id: '1',
                        position: 'PR Felelős, fejlesztő',
                        joined: '2000.10.10',

                    },
                    {
                        name: 'Szabolcs Varadi',
                        id: '1',
                        position: 'PR Felelős, fejlesztő',
                        joined: '2000.10.10',

                    }
                ]
            },
            {
                name: 'Kir-Dev',
                id: 1,
                members: [
                    {
                        name: 'Szabolcs Varadi',
                        id: '1',
                        position: 'PR Felelős, fejlesztő',
                        joined: '2000.10.10',

                    },
                    {
                        name: 'Szabolcs Varadi',
                        id: '1',
                        position: 'PR Felelős, fejlesztő',
                        joined: '2000.10.10',

                    },
                    {
                        name: 'Szabolcs Varadi',
                        id: '1',
                        position: 'PR Felelős, fejlesztő',
                        joined: '2000.10.10',

                    }
                ]
            },
            {
                name: 'Kir-Dev',
                id: 1,
                members: [
                    {
                        name: 'Szabolcs Varadi',
                        id: '1',
                        position: 'PR Felelős, fejlesztő',
                        joined: '2000.10.10',

                    },
                    {
                        name: 'Szabolcs Varadi',
                        id: '1',
                        position: 'PR Felelős, fejlesztő',
                        joined: '2000.10.10',

                    },
                    {
                        name: 'Szabolcs Varadi',
                        id: '1',
                        position: 'PR Felelős, fejlesztő',
                        joined: '2000.10.10',

                    }
                ]
            },
        ]
    };

    res.send(groups);
});

module.exports = router;

