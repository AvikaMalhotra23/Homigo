const dbHelper = require('../db/database');

exports.getMe = async (req, res) => {
  try {
    const profile = await dbHelper.get(
      `SELECT p.*, u.name, u.email, u.gender 
       FROM profiles p 
       JOIN users u ON p.user_id = u.id 
       WHERE p.user_id = ?`,
      [req.user.id]
    );

    if (!profile) {
      // Return user details with empty profile
      const user = await dbHelper.get("SELECT name, email, gender FROM users WHERE id = ?", [req.user.id]);
      if (!user) return res.status(404).json({ error: 'User not found' });
      return res.status(200).json({ userOnly: true, user });
    }

    res.json(profile);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Database error' });
  }
};

exports.updateProfile = async (req, res) => {
  const userId = req.user.id;
  const {
    college, hostel, room_preference, course, branch, year, semester, section, roll_number, school,
    looking_for, preferred_hostel, current_hostel, room_number, move_in_date, interests, languages, hometown,
    budget_min, budget_max, sleep_schedule, smoking, drinking, food_preference, cleanliness, pets, guests, bio
  } = req.body;

  if (!college || !hostel || !room_preference || !course || !sleep_schedule || !smoking || !drinking || !food_preference || !cleanliness || !pets || !guests) {
    return res.status(400).json({ error: 'All preference fields are required' });
  }

  // Calculate simple fake-profile risk score locally
  let riskScore = 0.0;
  if (!bio || bio.trim().length < 10) riskScore += 0.4; // Very short or missing bio
  if (!req.body.id_proof_url) riskScore += 0.3;          // Missing ID verification

  try {
    const existing = await dbHelper.get("SELECT user_id FROM profiles WHERE user_id = ?", [userId]);
    
    const interestsStr = typeof interests === 'string' ? interests : JSON.stringify(interests || []);
    const languagesStr = typeof languages === 'string' ? languages : JSON.stringify(languages || []);

    if (existing) {
      // Update
      await dbHelper.run(
        `UPDATE profiles SET 
          college = ?, hostel = ?, room_preference = ?, course = ?, branch = ?, year = ?, semester = ?, section = ?,
          roll_number = ?, school = ?, looking_for = ?, preferred_hostel = ?, current_hostel = ?, room_number = ?,
          move_in_date = ?, interests = ?, languages = ?, hometown = ?, budget_min = ?, budget_max = ?,
          sleep_schedule = ?, smoking = ?, drinking = ?, food_preference = ?, cleanliness = ?,
          pets = ?, guests = ?, bio = ?, fake_risk_score = ?
         WHERE user_id = ?`,
        [
          college, hostel, room_preference, course, branch || '', year || '', semester || 1, section || '',
          roll_number || '', school || '', looking_for || 'Roommate', preferred_hostel || hostel, current_hostel || hostel, room_number || '',
          move_in_date || '', interestsStr, languagesStr, hometown || '',
          budget_min || 0, budget_max || 100000, sleep_schedule, smoking, drinking, food_preference, cleanliness,
          pets, guests, bio || '', riskScore, userId
        ]
      );
    } else {
      // Insert
      await dbHelper.run(
        `INSERT INTO profiles (
          user_id, college, hostel, room_preference, course, branch, year, semester, section, roll_number, school,
          looking_for, preferred_hostel, current_hostel, room_number, move_in_date, interests, languages, hometown,
          budget_min, budget_max, sleep_schedule, smoking, drinking, food_preference, cleanliness, pets, guests, bio, is_verified, fake_risk_score
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?)`,
        [
          userId, college, hostel, room_preference, course, branch || '', year || '', semester || 1, section || '',
          roll_number || '', school || '', looking_for || 'Roommate', preferred_hostel || hostel, current_hostel || hostel, room_number || '',
          move_in_date || '', interestsStr, languagesStr, hometown || '',
          budget_min || 0, budget_max || 100000, sleep_schedule, smoking, drinking, food_preference, cleanliness,
          pets, guests, bio || '', riskScore
        ]
      );
    }

    res.json({ ok: true, message: 'Profile updated successfully' });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Database error' });
  }
};

exports.uploadIdProof = async (req, res) => {
  const userId = req.user.id;
  const { idProofUrl } = req.body;

  if (!idProofUrl) {
    return res.status(400).json({ error: 'ID proof URL is required' });
  }

  try {
    const profile = await dbHelper.get("SELECT user_id FROM profiles WHERE user_id = ?", [userId]);
    if (!profile) {
      return res.status(400).json({ error: 'Please set up your profile preferences first' });
    }

    // Mark as verified and decrease fake risk score
    await dbHelper.run(
      "UPDATE profiles SET is_verified = 1, id_proof_url = ?, fake_risk_score = MAX(0.0, fake_risk_score - 0.3) WHERE user_id = ?",
      [idProofUrl, userId]
    );

    res.json({ ok: true, message: 'ID proof uploaded and verified successfully' });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Database error' });
  }
};
