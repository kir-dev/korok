'use strict';

var express = require('express');
var router = express.Router();
var auth = require('../utils/auth.js');

router.use(function(req, res, next) {

    // write your middleware here

    next();
});

/* GET home page. */
<<<<<<< HEAD
router.get('/',  function(req, res) {
=======
router.get('/', function(req, res) {
>>>>>>> profil
      res.render('index', {
          title: 'Profil és Körök - Kir-Dev'
      });
});

router.post('/', auth.checkAuth, function(req, res) {

});

module.exports = router;
