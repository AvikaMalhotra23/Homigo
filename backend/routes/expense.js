const express = require('express');
const router = express.Router();
const expenseController = require('../controllers/expenseController');
const { requireAuth } = require('../services/authMiddleware');

router.post('/add', requireAuth, expenseController.addExpense);
router.get('/list', requireAuth, expenseController.getExpenses);
router.post('/pay', requireAuth, expenseController.paySplit);
router.get('/summary', requireAuth, expenseController.getSummary);

module.exports = router;
