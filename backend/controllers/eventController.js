const { query, run } = require('../db/database');

// GET /api/events
exports.getEvents = async (req, res) => {
  try {
    const today = new Date().toISOString().split('T')[0];
    const events = await query(
      `SELECT * FROM events WHERE date >= ? ORDER BY date ASC LIMIT 20`,
      [today]
    );
    res.json(events);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

// POST /api/events/add
exports.addEvent = async (req, res) => {
  try {
    const { name, description, date, time, location, type = 'general', organizer } = req.body;
    if (!name || !date || !time || !location) {
      return res.status(400).json({ error: 'name, date, time, and location are required' });
    }
    await run(
      `INSERT INTO events (name, description, date, time, location, type, organizer) VALUES (?, ?, ?, ?, ?, ?, ?)`,
      [name, description, date, time, location, type, organizer]
    );
    res.json({ ok: true, message: 'Event added successfully' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};
