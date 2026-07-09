const dbHelper = require('../db/database');

exports.getMatches = async (req, res) => {
  const userId = req.user.id;
  const userGender = req.user.gender;

  try {
    // 1. Get user's own profile
    const myProfile = await dbHelper.get("SELECT * FROM profiles WHERE user_id = ?", [userId]);
    if (!myProfile) {
      return res.status(400).json({ error: 'Please set up your profile first to find matches.' });
    }

    // 2. Find other users of the same gender and college
    const queryStr = `
      SELECT p.*, u.name, u.email, u.gender,
             r.status AS request_status, r.sender_id AS request_sender
      FROM profiles p
      JOIN users u ON p.user_id = u.id
      LEFT JOIN requests r ON (
        (r.sender_id = ? AND r.receiver_id = p.user_id) OR
        (r.sender_id = p.user_id AND r.receiver_id = ?)
      )
      WHERE u.gender = ? AND p.user_id != ? AND p.college = ?
    `;

    const candidates = await dbHelper.query(queryStr, [userId, userId, userGender, userId, myProfile.college]);

    // 3. Compute compatibility for each candidate
    const matches = candidates.map(c => {
      const breakdown = calculateCompatibility(myProfile, c);
      return {
        user: {
          id: c.user_id,
          name: c.name,
          email: c.email,
          gender: c.gender,
          college: c.college,
          hostel: c.hostel,
          room_preference: c.room_preference,
          bio: c.bio,
          is_verified: c.is_verified,
          fake_risk_score: c.fake_risk_score
        },
        requestStatus: c.request_status || 'none',
        requestSender: c.request_sender || null,
        overallScore: breakdown.overall,
        breakdown
      };
    });

    // 4. Sort matches by overall score descending
    matches.sort((a, b) => b.overallScore - a.overallScore);

    res.json(matches);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Database error' });
  }
};

function calculateCompatibility(a, b) {
  // A. Budget match (20%)
  // Calculate overlap or proximity
  const minBudget = Math.max(a.budget_min, b.budget_min);
  const maxBudget = Math.min(a.budget_max, b.budget_max);
  let budgetScore = 0;
  if (minBudget <= maxBudget) {
    budgetScore = 100;
  } else {
    const diff = minBudget - maxBudget;
    const avg = (a.budget_max + b.budget_max) / 2;
    budgetScore = Math.max(0, Math.round(100 - (diff / (avg || 1)) * 100));
  }

  // B. Sleep Schedule Match (20%)
  let sleepScore = 0;
  if (a.sleep_schedule === b.sleep_schedule) {
    sleepScore = 100;
  } else if (a.sleep_schedule === 'flexible' || b.sleep_schedule === 'flexible') {
    sleepScore = 70;
  } else {
    sleepScore = 30; // Early bird vs Night owl
  }

  // C. Lifestyle: Smoking & Drinking (20%)
  let lifestyleScore = 100;
  if (a.smoking !== b.smoking) lifestyleScore -= 40;
  if (a.drinking !== b.drinking) lifestyleScore -= 40;

  // D. Cleanliness Match (20%)
  let cleanScore = 0;
  if (a.cleanliness === b.cleanliness) {
    cleanScore = 100;
  } else if (
    (a.cleanliness === 'high' && b.cleanliness === 'low') ||
    (a.cleanliness === 'low' && b.cleanliness === 'high')
  ) {
    cleanScore = 30;
  } else {
    cleanScore = 70;
  }

  // E. Social / Preferences: Food, Pets, Guests (20%)
  let socialScore = 100;
  // Food
  if (a.food_preference !== 'any' && b.food_preference !== 'any' && a.food_preference !== b.food_preference) {
    socialScore -= 30;
  }
  // Pets
  if (a.pets !== b.pets) {
    socialScore -= 30;
  }
  // Guests
  if (a.guests !== b.guests) {
    if (
      (a.guests === 'frequent' && b.guests === 'no') ||
      (a.guests === 'no' && b.guests === 'frequent')
    ) {
      socialScore -= 40;
    } else {
      socialScore -= 20;
    }
  }

  // F. Roommate Deal Breakers Penalty (Drop score automatically on conflict)
  let dealBreakerPenalty = 0;
  try {
    const aBreakers = JSON.parse(a.deal_breakers || '[]');
    const bBreakers = JSON.parse(b.deal_breakers || '[]');

    // Check A's deal breakers against B's profile
    aBreakers.forEach(db => {
      if (db.includes("No Smokers") && b.smoking !== 'no' && b.smoking !== 'Never') dealBreakerPenalty += 15;
      if (db.includes("No Alcohol") && b.drinking !== 'no' && b.drinking !== 'Never') dealBreakerPenalty += 15;
      if (db.includes("Must Keep Room Clean") && (b.cleanliness === 'low' || b.cleanliness === 'moderate' || b.cleanliness === "Doesn't Matter Much")) dealBreakerPenalty += 15;
      if (db.includes("No Pets") && b.pets === 'yes') dealBreakerPenalty += 15;
      if (db.includes("No Frequent Guests") && b.guests === 'frequent') dealBreakerPenalty += 15;
    });

    // Check B's deal breakers against A's profile
    bBreakers.forEach(db => {
      if (db.includes("No Smokers") && a.smoking !== 'no' && a.smoking !== 'Never') dealBreakerPenalty += 15;
      if (db.includes("No Alcohol") && a.drinking !== 'no' && a.drinking !== 'Never') dealBreakerPenalty += 15;
      if (db.includes("Must Keep Room Clean") && (a.cleanliness === 'low' || a.cleanliness === 'moderate' || a.cleanliness === "Doesn't Matter Much")) dealBreakerPenalty += 15;
      if (db.includes("No Pets") && a.pets === 'yes') dealBreakerPenalty += 15;
      if (db.includes("No Frequent Guests") && a.guests === 'frequent') dealBreakerPenalty += 15;
    });
  } catch (e) {
    console.error("Error calculating deal breaker penalty:", e);
  }

  // G. Language Compatibility bonus
  let languageBonus = 0;
  try {
    const aLangs = JSON.parse(a.languages || '[]');
    const bLangs = JSON.parse(b.languages || '[]');

    if (aLangs.length > 0 && bLangs.length > 0) {
      if (aLangs[0] === bLangs[0]) {
        languageBonus = 10; // Same primary language
      } else {
        const commonLangs = aLangs.filter(lang => bLangs.includes(lang));
        if (commonLangs.length > 0) {
          languageBonus = 5; // At least one common language
        }
      }
    }
  } catch (e) {
    console.error("Error calculating language bonus:", e);
  }

  const overall = Math.min(100, Math.max(0, Math.round(
    budgetScore * 0.2 +
    sleepScore * 0.2 +
    lifestyleScore * 0.2 +
    cleanScore * 0.2 +
    socialScore * 0.2 -
    dealBreakerPenalty +
    languageBonus
  )));

  return {
    overall,
    budget: budgetScore,
    sleep: sleepScore,
    lifestyle: lifestyleScore,
    cleanliness: cleanScore,
    social: socialScore,
    languageBonus: languageBonus
  };
}
