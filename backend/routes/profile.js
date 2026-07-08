const express = require('express');
const router = express.Router();
const profileController = require('../controllers/profileController');
const { requireAuth } = require('../services/authMiddleware');

const { COLLEGES } = require('../config/colleges');

router.get('/me', requireAuth, profileController.getMe);
router.post('/update', requireAuth, profileController.updateProfile);
router.post('/verify', requireAuth, profileController.uploadIdProof);
router.get('/colleges', (req, res) => {
  res.json(COLLEGES);
});

module.exports = router;
