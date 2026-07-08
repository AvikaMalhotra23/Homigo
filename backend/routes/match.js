const express = require('express');
const router = express.Router();
const matchController = require('../controllers/matchController');
const { requireAuth } = require('../services/authMiddleware');

router.get('/', requireAuth, matchController.getMatches);

module.exports = router;
