const { query, run } = require('../db/database');
const jwt = require('jsonwebtoken');

function getToken(req) {
  return (req.headers.authorization || '').replace('Bearer ', '');
}
function verifyToken(token) {
  return jwt.verify(token, process.env.JWT_SECRET || 'homigo_secret_2024');
}

// GET /api/marketplace
exports.getMarketplace = async (req, res) => {
  try {
    const { category, hostel } = req.query;
    let sql = `SELECT m.*, u.name as seller_name FROM marketplace m
               LEFT JOIN users u ON u.id = m.seller_id WHERE m.is_sold = 0`;
    const params = [];
    if (category) { sql += ` AND m.category = ?`; params.push(category); }
    if (hostel)   { sql += ` AND m.hostel = ?`;   params.push(hostel); }
    sql += ` ORDER BY m.created_at DESC LIMIT 30`;
    const items = await query(sql, params);
    res.json(items);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

// POST /api/marketplace/add
exports.addItem = async (req, res) => {
  try {
    const token = getToken(req);
    const decoded = verifyToken(token);
    const sellerId = decoded.id;
    const { title, price, category, hostel, description, image_url } = req.body;
    if (!title || !price || !category || !hostel) {
      return res.status(400).json({ error: 'title, price, category, and hostel are required' });
    }
    await run(
      `INSERT INTO marketplace (seller_id, title, price, category, hostel, description, image_url)
       VALUES (?, ?, ?, ?, ?, ?, ?)`,
      [sellerId, title, price, category, hostel, description, image_url]
    );
    res.json({ ok: true, message: 'Item listed successfully' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

// DELETE /api/marketplace/:id
exports.deleteItem = async (req, res) => {
  try {
    const token = getToken(req);
    const decoded = verifyToken(token);
    const userId = decoded.id;
    const { id } = req.params;
    const item = await run(`DELETE FROM marketplace WHERE id = ? AND seller_id = ?`, [id, userId]);
    if (item.changes === 0) return res.status(404).json({ error: 'Item not found or unauthorized' });
    res.json({ ok: true, message: 'Item removed' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

// PATCH /api/marketplace/:id/sold
exports.markSold = async (req, res) => {
  try {
    const token = getToken(req);
    const decoded = verifyToken(token);
    const userId = decoded.id;
    const { id } = req.params;
    await run(`UPDATE marketplace SET is_sold = 1 WHERE id = ? AND seller_id = ?`, [id, userId]);
    res.json({ ok: true, message: 'Marked as sold' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};
