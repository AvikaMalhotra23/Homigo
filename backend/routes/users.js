const express = require('express');
const router = express.Router();
const userController = require('../controllers/userController');
const { requireAuth } = require('../services/authMiddleware');

router.get('/search', requireAuth, userController.searchUsers);
router.get('/username/:username', requireAuth, userController.getUserByUsername);
router.post('/check-username', requireAuth, userController.checkUsername);

module.exports = router;
