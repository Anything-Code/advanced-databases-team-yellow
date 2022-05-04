const express = require('express');
const {getVolunteers} = require('../controller/volunteers');

const router = express.Router();

router.route('/').get(getVolunteers);

module.exports = router;