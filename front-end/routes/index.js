var express = require('express');
var router = express.Router();
var auth = require('../utils/auth.js');

router.use(function(req, res, next) {

    // write your middleware here

    next();
});

/* GET home page. */
router.get('/', auth.getToken, function(req, res) {
      res.render('index', {
          title: 'Profil és Körök - Kir-Dev'
      });
});

router.post('/', auth.checkAuth, function(req, res) {
   res.send('index post handler');
});

module.exports = router;
