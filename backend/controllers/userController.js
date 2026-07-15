const dbHelper = require('../db/database');
const { calculateCompatibility } = require('./matchController');

const RESERVED_USERNAMES = ['admin', 'support', 'homigo', 'ai', 'system'];

// Validate username based on Instagram rules
function validateUsernameFormat(username) {
  if (!username) {
    return { valid: false, error: 'Username is required' };
  }
  
  let clean = username.trim().toLowerCase();
  if (clean.startsWith('@')) {
    clean = clean.substring(1);
  }

  if (clean.length < 1 || clean.length > 30) {
    return { valid: false, error: 'Username must be between 1 and 30 characters' };
  }

  // Allowed: a-z, 0-9, _, .
  const regex = /^[a-z0-9_.]+$/;
  if (!regex.test(clean)) {
    return { valid: false, error: 'Username can only contain letters, numbers, underscores, and dots' };
  }

  if (RESERVED_USERNAMES.includes(clean)) {
    return { valid: false, error: 'This username is reserved' };
  }

  return { valid: true, clean };
}

// Generate Instagram-style natural suggestions
async function generateInstagramSuggestions(cleanUsername, displayName) {
  const suggestions = [];
  const base = cleanUsername;

  // Extract parts of display name if available
  let first = '';
  let last = '';
  if (displayName) {
    const parts = displayName.toLowerCase().trim().split(/\s+/);
    if (parts.length > 0) first = parts[0].replace(/[^a-z0-9]/g, '');
    if (parts.length > 1) last = parts[parts.length - 1].replace(/[^a-z0-9]/g, '');
  }

  const candidates = [];

  // Pattern 1: username + number (e.g. harshit304)
  candidates.push(`${base}304`);
  candidates.push(`${base}04`);
  
  // Pattern 2: its_username (e.g. its_harshit)
  candidates.push(`its_${base}`);

  // Pattern 3: username_official (e.g. harshit_official)
  candidates.push(`${base}_official`);

  // Pattern 4: username.cse or username_lpu
  candidates.push(`${base}.cse`);
  candidates.push(`${base}_lpu`);

  // Pattern 5: first + last name (e.g. harshit_garg or harshitgarg)
  if (first && last) {
    candidates.push(`${first}_${last}`);
    candidates.push(`${first}${last}`);
  }

  // Pattern 6: username_2007 or similar
  candidates.push(`${base}_2007`);

  // Check availability for candidates
  for (const cand of candidates) {
    if (suggestions.length >= 5) break;
    const existing = await dbHelper.get("SELECT id FROM users WHERE username = ?", [cand]);
    if (!existing && !RESERVED_USERNAMES.includes(cand) && !suggestions.includes(cand)) {
      suggestions.push(cand);
    }
  }

  // Fallback if not enough suggestions
  let counter = 1;
  while (suggestions.length < 5 && counter < 20) {
    const cand = `${base}${counter}`;
    const existing = await dbHelper.get("SELECT id FROM users WHERE username = ?", [cand]);
    if (!existing && !suggestions.includes(cand)) {
      suggestions.push(cand);
    }
    counter++;
  }

  // Format suggestions with @ prefix
  return suggestions.map(s => `@${s}`);
}

exports.checkUsername = async (req, res) => {
  let { username } = req.body;
  
  const validation = validateUsernameFormat(username);
  if (!validation.valid) {
    return res.status(400).json({ error: validation.error });
  }

  const { clean } = validation;

  try {
    const existingUser = await dbHelper.get("SELECT id FROM users WHERE username = ?", [clean]);
    if (existingUser) {
      // Generate suggestions using user's name if authenticated, otherwise clean base
      const userId = req.user ? req.user.id : null;
      let displayName = '';
      if (userId) {
        const u = await dbHelper.get("SELECT name FROM users WHERE id = ?", [userId]);
        if (u) displayName = u.name;
      }

      const suggestions = await generateInstagramSuggestions(clean, displayName);
      return res.status(200).json({
        available: false,
        suggestions
      });
    }

    res.status(200).json({
      available: true
    });
  } catch (err) {
    console.error('[userController.checkUsername ERROR]', err);
    res.status(500).json({ error: 'Database error checking username availability' });
  }
};

exports.searchUsers = async (req, res) => {
  let { q } = req.query;
  if (!q) {
    return res.status(200).json([]);
  }

  let queryText = q.trim().toLowerCase();
  if (queryText.startsWith('@')) {
    queryText = queryText.substring(1);
  }

  const myUserId = req.user.id;

  try {
    // Get current user's profile to calculate compatibility
    const myProfile = await dbHelper.get("SELECT * FROM profiles WHERE user_id = ?", [myUserId]);

    // Search users by username or displayName
    const queryStr = `
      SELECT u.id, u.username, u.displayName, u.name, u.avatar, u.email, u.gender, u.isOnline, u.lastSeen,
             p.college, p.hostel, p.course, p.branch, p.year, p.is_verified,
             r.status AS request_status, r.sender_id AS request_sender
      FROM users u
      LEFT JOIN profiles p ON u.id = p.user_id
      LEFT JOIN requests r ON (
        (r.sender_id = ? AND r.receiver_id = u.id) OR
        (r.sender_id = u.id AND r.receiver_id = ?)
      )
      WHERE u.id != ? AND (
        LOWER(u.username) LIKE ? OR
        LOWER(u.displayName) LIKE ? OR
        LOWER(u.name) LIKE ?
      )
      LIMIT 30
    `;

    const lq = `%${queryText}%`;
    const searchResults = await dbHelper.query(queryStr, [myUserId, myUserId, myUserId, lq, lq, lq]);

    const results = searchResults.map(row => {
      // Calculate compatibility
      let compatibilityScore = 0;
      if (myProfile && row.college) {
        // Mock profile object for target matching
        const targetProfile = {
          budget_min: row.budget_min || 0,
          budget_max: row.budget_max || 100000,
          sleep_schedule: row.sleep_schedule || 'flexible',
          smoking: row.smoking || 'no',
          drinking: row.drinking || 'no',
          cleanliness: row.cleanliness || 'moderate',
          food_preference: row.food_preference || 'any',
          pets: row.pets || 'no',
          guests: row.guests || 'rare'
        };
        try {
          const breakdown = calculateCompatibility(myProfile, row);
          compatibilityScore = breakdown.overall;
        } catch (e) {
          // Fallback if profile fields mismatch
          compatibilityScore = 50;
        }
      }

      return {
        id: row.id,
        // Always prefix username with @ in UI responses
        username: row.username ? `@${row.username}` : null,
        displayName: row.displayName || row.name,
        name: row.name,
        avatar: row.avatar,
        email: row.email,
        gender: row.gender,
        isOnline: row.isOnline || 0,
        lastSeen: row.lastSeen,
        requestStatus: row.request_status || 'none',
        requestSender: row.request_sender || null,
        profile: row.college ? {
          college: row.college,
          hostel: row.hostel,
          course: row.course,
          branch: row.branch,
          year: row.year,
          is_verified: row.is_verified
        } : null,
        compatibilityScore
      };
    });

    res.json(results);
  } catch (err) {
    console.error('[userController.searchUsers ERROR]', err);
    res.status(500).json({ error: 'Database error searching users' });
  }
};

exports.getUserByUsername = async (req, res) => {
  let { username } = req.params;
  if (!username) {
    return res.status(400).json({ error: 'Username parameter is required' });
  }

  let clean = username.trim().toLowerCase();
  if (clean.startsWith('@')) {
    clean = clean.substring(1);
  }

  try {
    const user = await dbHelper.get("SELECT id FROM users WHERE username = ?", [clean]);
    if (!user) {
      return res.status(404).json({ error: 'User not found' });
    }

    const profile = await dbHelper.get(
      `SELECT p.*, u.username, u.displayName, u.name, u.email, u.gender, u.avatar 
       FROM profiles p 
       JOIN users u ON p.user_id = u.id 
       WHERE p.user_id = ?`,
      [user.id]
    );

    if (!profile) {
      const u = await dbHelper.get("SELECT id, username, displayName, name, email, gender, avatar FROM users WHERE id = ?", [user.id]);
      u.username = `@${u.username}`;
      return res.json({ userOnly: true, user: u });
    }

    profile.username = `@${profile.username}`;
    res.json(profile);
  } catch (err) {
    console.error('[userController.getUserByUsername ERROR]', err);
    res.status(500).json({ error: 'Database error fetching user by username' });
  }
};
