const express = require('express');
const router = express.Router();
const aiController = require('../controllers/aiController');
const { requireAuth } = require('../services/authMiddleware');

router.post('/generate-bio', requireAuth, aiController.generateBio);
router.post('/chatbot', requireAuth, aiController.chatbot);
router.get('/detect-fake/:targetUserId', requireAuth, aiController.detectFakeProfile);

module.exports = router;
