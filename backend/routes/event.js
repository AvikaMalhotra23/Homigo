const express = require('express');
const router = express.Router();
const ctrl = require('../controllers/eventController');

router.get('/', ctrl.getEvents);
router.post('/add', ctrl.addEvent);

module.exports = router;
