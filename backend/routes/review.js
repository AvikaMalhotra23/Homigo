const express = require('express');
const router = express.Router();
const reviewController = require('../controllers/reviewController');
const { requireAuth } = require('../services/authMiddleware');

router.post('/add', requireAuth, reviewController.addReview);
router.get('/list/:userId', requireAuth, reviewController.getReviews);

module.exports = router;
