const { query, get } = require('../db/database');
const jwt = require('jsonwebtoken');

function getToken(req) {
  const auth = req.headers.authorization || '';
  return auth.replace('Bearer ', '');
}

function verifyToken(token) {
  return jwt.verify(token, process.env.JWT_SECRET || 'homigo_secret_2024');
}

// GET /api/dashboard
exports.getDashboard = async (req, res) => {
  try {
    const token = getToken(req);
    const decoded = verifyToken(token);
    const userId = decoded.id;

    // Profile completion score
    const profile = await get(`
      SELECT p.*, u.name, u.gender FROM profiles p
      JOIN users u ON u.id = p.user_id
      WHERE p.user_id = ?`, [userId]);

    let completion = 0;
    if (profile) {
      const fields = ['bio', 'branch', 'year', 'semester', 'languages', 'hometown',
        'interests', 'budget_min', 'budget_max', 'sleep_schedule', 'food_preference',
        'cleanliness', 'college', 'hostel', 'course', 'roll_number', 'section'];
      const filled = fields.filter(f => profile[f] !== null && profile[f] !== undefined && profile[f] !== '').length;
      completion = Math.round((filled / fields.length) * 100);
    }

    // Top 3 matches
    const allProfiles = await query(`
      SELECT p.*, u.name, u.gender FROM profiles p
      JOIN users u ON u.id = p.user_id
      WHERE p.user_id != ?
      AND (p.college = ? OR ? IS NULL)
      LIMIT 20`,
      [userId, profile?.college || null, profile?.college || null]
    );

    const myGender = profile?.gender || 'male';
    const scored = allProfiles
      .filter(p => p.gender === myGender)
      .map(p => {
        let score = 50;
        if (p.sleep_schedule === profile?.sleep_schedule) score += 15;
        if (p.food_preference === profile?.food_preference) score += 10;
        if (p.cleanliness === profile?.cleanliness) score += 10;
        if (p.smoking === profile?.smoking) score += 10;
        if (p.drinking === profile?.drinking) score += 5;
        score = Math.min(99, score);
        return { ...p, overallScore: score };
      })
      .sort((a, b) => b.overallScore - a.overallScore)
      .slice(0, 5);

    // New incoming requests count
    const reqRow = await get(`SELECT COUNT(*) as count FROM requests WHERE receiver_id = ? AND status = 'pending'`, [userId]);
    const newRequestsCount = reqRow?.count || 0;

    // Latest notices
    const notices = await query(`SELECT * FROM notices ORDER BY created_at DESC LIMIT 3`);

    // Upcoming events
    const events = await query(`SELECT * FROM events ORDER BY date ASC LIMIT 3`);

    // Marketplace listings (latest 4)
    const marketplace = await query(`
      SELECT m.*, u.name as seller_name FROM marketplace m
      LEFT JOIN users u ON u.id = m.seller_id
      WHERE m.is_sold = 0
      ORDER BY m.created_at DESC LIMIT 4`);

    // Expense summary for this month
    const now = new Date();
    const startOfMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-01`;
    const expenses = await query(`
      SELECT e.category, SUM(s.share) as total
      FROM expenses e
      JOIN expense_splits s ON s.expense_id = e.id
      WHERE s.user_id = ? AND e.created_at >= ?
      GROUP BY e.category`, [userId, startOfMonth]);
    const expenseTotal = expenses.reduce((sum, e) => sum + (e.total || 0), 0);

    // AI suggestion
    let aiSuggestion = null;
    if (scored.length > 0) {
      const top = scored[0];
      aiSuggestion = `A student with ${top.overallScore}% compatibility is in ${top.hostel?.toUpperCase()}! Send a roommate request.`;
    } else if (completion < 70) {
      aiSuggestion = `Complete your profile to get better roommate matches. You're at ${completion}%!`;
    } else {
      aiSuggestion = `You're all set! Check the notice board for the latest hostel updates.`;
    }

    res.json({
      profileCompletion: completion,
      userName: profile?.name || 'Student',
      college: profile?.college || '',
      hostel: profile?.hostel || '',
      gender: profile?.gender || 'male',
      topMatches: scored,
      newRequestsCount,
      notices,
      events,
      marketplace,
      expenseBreakdown: expenses,
      expenseTotal: Math.round(expenseTotal),
      aiSuggestion
    });
  } catch (err) {
    console.error('Dashboard error:', err.message);
    res.status(500).json({ error: err.message });
  }
};
