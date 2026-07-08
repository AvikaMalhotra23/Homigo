const express = require('express');
const router = express.Router();
const requestController = require('../controllers/requestController');
const { requireAuth } = require('../services/authMiddleware');

router.post('/send', requireAuth, requestController.sendRequest);
router.get('/list', requireAuth, requestController.getRequests);
router.post('/respond', requireAuth, requestController.respondToRequest);

module.exports = router;
