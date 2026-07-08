const { query, run } = require('../db/database');

// GET /api/notices
exports.getNotices = async (req, res) => {
  try {
    const { hostel } = req.query;
    let notices;
    if (hostel) {
      notices = await query(
        `SELECT * FROM notices WHERE hostel = ? OR hostel = 'all' ORDER BY created_at DESC LIMIT 20`,
        [hostel]
      );
    } else {
      notices = await query(`SELECT * FROM notices ORDER BY created_at DESC LIMIT 20`);
    }
    res.json(notices);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

// POST /api/notices/add
exports.addNotice = async (req, res) => {
  try {
    const { title, content, hostel = 'all', type = 'general' } = req.body;
    if (!title || !content) return res.status(400).json({ error: 'title and content are required' });
    await run(
      `INSERT INTO notices (title, content, hostel, type) VALUES (?, ?, ?, ?)`,
      [title, content, hostel, type]
    );
    res.json({ ok: true, message: 'Notice added successfully' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};
