'use strict';

var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res) {

    res.render('groups', {
        title: 'Körök - PéK'
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
                        joined: '2000.10.10'

                    },
                    {
                        name: 'Szabolcs Varadi',
                        id: '1',
                        position: 'PR Felelős, fejlesztő',
                        joined: '2000.10.10'

                    },
                    {
                        name: 'Szabolcs Varadi',
                        id: '1',
                        position: 'PR Felelős, fejlesztő',
                        joined: '2000.10.10'

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
                        joined: '2000.10.10'

                    },
                    {
                        name: 'Szabolcs Varadi',
                        id: '1',
                        position: 'PR Felelős, fejlesztő',
                        joined: '2000.10.10'

                    },
                    {
                        name: 'Szabolcs Varadi',
                        id: '1',
                        position: 'PR Felelős, fejlesztő',
                        joined: '2000.10.10'

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
                        joined: '2000.10.10'

                    },
                    {
                        name: 'Szabolcs Varadi',
                        id: '1',
                        position: 'PR Felelős, fejlesztő',
                        joined: '2000.10.10'

                    },
                    {
                        name: 'Szabolcs Varadi',
                        id: '1',
                        position: 'PR Felelős, fejlesztő',
                        joined: '2000.10.10'

                    }
                ]
            }
        ]
    };

    res.send(groups);
});

module.exports = router;

