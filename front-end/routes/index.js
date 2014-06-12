var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res) {
  res.render('index', {
      title: 'Profil és Körök - Kir-Dev',
      username: "Szabolcs Varadi",
      userimgsrc: "/images/stuff.jpg"
  });
});

module.exports = router;
