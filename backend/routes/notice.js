const express = require('express');
const router = express.Router();
const ctrl = require('../controllers/noticeController');

router.get('/', ctrl.getNotices);
router.post('/add', ctrl.addNotice);

module.exports = router;
