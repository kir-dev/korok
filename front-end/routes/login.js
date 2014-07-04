/**
 * Created by Kresshy on 2014.07.04..
 */

var express = require('express');
var router = express.Router();

router.use(function(req, res, next) {

    // write your middleware here

    next();
});

/* GET home page. */
router.get('/', function(req, res) {

    console.log('login get request handler');
    console.log(req.body);

    res.send(200, 'stuff');
});

router.post('/', function(req, res) {

    console.log('login post request handler');
    console.log(req);

    res.send(200, 'stuff');
});



module.exports = router;
