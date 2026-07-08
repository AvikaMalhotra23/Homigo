const express = require('express');
const router = express.Router();
const ctrl = require('../controllers/marketplaceController');

router.get('/', ctrl.getMarketplace);
router.post('/add', ctrl.addItem);
router.delete('/:id', ctrl.deleteItem);
router.patch('/:id/sold', ctrl.markSold);

module.exports = router;
