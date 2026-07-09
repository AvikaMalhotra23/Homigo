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
    budget_min, budget_max, sleep_schedule, smoking, drinking, food_preference, cleanliness, pets, guests, bio,
    deal_breakers, room_purpose,
    wake_up_time, study_style, room_environment, personality_type, daily_routine, work_style
  } = req.body;

  // Use defaults for any missing fields so partial profiles save without 400 errors
  const safeCollege       = college       || '';
  const safeHostel        = hostel        || '';
  const safeRoomPref      = room_preference || 'shared';
  const safeCourse        = course        || '';
  const safeSleep         = sleep_schedule || 'flexible';
  const safeSmoking       = smoking       || 'no';
  const safeDrinking      = drinking      || 'no';
  const safeFood          = food_preference || 'any';
  const safeCleanliness   = cleanliness   || 'moderate';
  const safePets          = pets          || 'no';
  const safeGuests        = guests        || 'rare';

  // Calculate simple fake-profile risk score locally
  let riskScore = 0.0;
  if (!bio || bio.trim().length < 10) riskScore += 0.4; // Very short or missing bio
  if (!req.body.id_proof_url) riskScore += 0.3;          // Missing ID verification

  try {
    const existing = await dbHelper.get("SELECT user_id FROM profiles WHERE user_id = ?", [userId]);
    
    const interestsStr = typeof interests === 'string' ? interests : JSON.stringify(interests || []);
    const languagesStr = typeof languages === 'string' ? languages : JSON.stringify(languages || []);
    const dealBreakersStr = typeof deal_breakers === 'string' ? deal_breakers : JSON.stringify(deal_breakers || []);
    const roomPurposeStr = typeof room_purpose === 'string' ? room_purpose : JSON.stringify(room_purpose || []);

    if (existing) {
      // Update
      await dbHelper.run(
        `UPDATE profiles SET 
          college = ?, hostel = ?, room_preference = ?, course = ?, branch = ?, year = ?, semester = ?, section = ?,
          roll_number = ?, school = ?, looking_for = ?, preferred_hostel = ?, current_hostel = ?, room_number = ?,
          move_in_date = ?, interests = ?, languages = ?, hometown = ?, budget_min = ?, budget_max = ?,
          sleep_schedule = ?, smoking = ?, drinking = ?, food_preference = ?, cleanliness = ?,
          pets = ?, guests = ?, bio = ?, fake_risk_score = ?, deal_breakers = ?, room_purpose = ?,
          wake_up_time = ?, study_style = ?, room_environment = ?, personality_type = ?, daily_routine = ?, work_style = ?
         WHERE user_id = ?`,
        [
          safeCollege, safeHostel, safeRoomPref, safeCourse, branch || '', year || '', semester || 1, section || '',
          roll_number || '', school || '', looking_for || 'Roommate', preferred_hostel || safeHostel, current_hostel || safeHostel, room_number || '',
          move_in_date || '', interestsStr, languagesStr, hometown || '',
          budget_min || 0, budget_max || 100000, safeSleep, safeSmoking, safeDrinking, safeFood, safeCleanliness,
          safePets, safeGuests, bio || '', riskScore, dealBreakersStr, roomPurposeStr,
          wake_up_time || '', study_style || '', room_environment || '', personality_type || '', daily_routine || '', work_style || '',
          userId
        ]
      );
    } else {
      // Insert
      await dbHelper.run(
        `INSERT INTO profiles (
          user_id, college, hostel, room_preference, course, branch, year, semester, section, roll_number, school,
          looking_for, preferred_hostel, current_hostel, room_number, move_in_date, interests, languages, hometown,
          budget_min, budget_max, sleep_schedule, smoking, drinking, food_preference, cleanliness, pets, guests, bio, 
          is_verified, fake_risk_score, deal_breakers, room_purpose,
          wake_up_time, study_style, room_environment, personality_type, daily_routine, work_style
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
        [
          userId, safeCollege, safeHostel, safeRoomPref, safeCourse, branch || '', year || '', semester || 1, section || '',
          roll_number || '', school || '', looking_for || 'Roommate', preferred_hostel || safeHostel, current_hostel || safeHostel, room_number || '',
          move_in_date || '', interestsStr, languagesStr, hometown || '',
          budget_min || 0, budget_max || 100000, safeSleep, safeSmoking, safeDrinking, safeFood, safeCleanliness,
          safePets, safeGuests, bio || '', riskScore, dealBreakersStr, roomPurposeStr,
          wake_up_time || '', study_style || '', room_environment || '', personality_type || '', daily_routine || '', work_style || ''
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
