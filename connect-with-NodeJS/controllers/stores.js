const Store = require('../models/Store');

// @desc  Get all voluntarily
// @route GET /api/v1/voluntarily
// @access Public
exports.getvoluntarily = async (req, res, next) => {
  try {
    const voluntarily = await Store.find();

    return res.status(200).json({
      success: true,
      count: voluntarily.length,
      data: voluntarily
    });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Server error' });
  }
};

// @desc  Create a store
// @route POST /api/v1/voluntarily
// @access Public
exports.addStore = async (req, res, next) => {
  try {
    const store = await Store.create(req.body);

    return res.status(201).json({
      success: true,
      data: store
    });
  } catch (err) {
    console.error(err);
    if (err.code === 11000) {
      return res.status(400).json({ error: 'This store already exists' });
    }
    res.status(500).json({ error: 'Server error' });
  }
};
