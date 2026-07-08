const express = require('express');
const router = express.Router();
const chatController = require('../controllers/chatController');
const { requireAuth } = require('../services/authMiddleware');

router.get('/list', requireAuth, chatController.getChatList);
router.get('/messages/:otherUserId', requireAuth, chatController.getMessages);
router.post('/send', requireAuth, chatController.sendMessage);

module.exports = router;
