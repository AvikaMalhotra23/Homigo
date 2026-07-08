const dbHelper = require('../db/database');

// Call Gemini API or fallback
async function callGemini(prompt) {
  const apiKey = process.env.GEMINI_API_KEY;
  if (!apiKey) {
    console.log("No GEMINI_API_KEY found, using local fallback");
    return null;
  }

  try {
    const fetch = (...args) => import('node-fetch').then(({default: fetch}) => fetch(...args));
    const response = await fetch(
      `https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=${apiKey}`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          contents: [{ parts: [{ text: prompt }] }]
        })
      }
    );

    const json = await response.json();
    if (json.candidates && json.candidates[0] && json.candidates[0].content && json.candidates[0].content.parts[0]) {
      return json.candidates[0].content.parts[0].text;
    }
  } catch (err) {
    console.error("Gemini API call failed, falling back:", err.message);
  }
  return null;
}

exports.generateBio = async (req, res) => {
  const { input } = req.body;
  if (!input || input.trim() === '') {
    return res.status(400).json({ error: 'Input description is required' });
  }

  const prompt = `Write a short, engaging, and friendly roommate profile bio (2-3 sentences max) based on this input: "${input}". Emphasize positive roommate qualities like responsibility, cleanliness, and compatibility.`;

  try {
    const aiResponse = await callGemini(prompt);
    if (aiResponse) {
      return res.json({ bio: aiResponse.trim() });
    }

    // Local Fallback Rule-based generator
    let bio = "Friendly ";
    const cleanWords = ["clean", "tidy", "organized", "hygienic"];
    const quietWords = ["quiet", "peace", "silent", "calm", "study"];
    const activeWords = ["gamer", "music", "guitar", "outgoing", "netflix", "chill"];

    const isClean = cleanWords.some(w => input.toLowerCase().includes(w));
    const isQuiet = quietWords.some(w => input.toLowerCase().includes(w));
    const isActive = activeWords.some(w => input.toLowerCase().includes(w));

    if (input.toLowerCase().includes("student")) {
      bio += "student looking for a ";
    } else {
      bio += "professional looking for a ";
    }

    if (isClean && isQuiet) {
      bio += "clean, responsible, and quiet roommate. I prefer peaceful environments and respect private study hours.";
    } else if (isActive) {
      bio += "friendly, outgoing room partner who shares interest in music/games, and values open communication.";
    } else {
      bio += "respectful roommate who believes in maintaining a clean room, sharing expenses timely, and friendly boundaries.";
    }

    res.json({ bio });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'AI generation error' });
  }
};

exports.chatbot = async (req, res) => {
  const { message } = req.body;
  if (!message || message.trim() === '') {
    return res.status(400).json({ error: 'Message is required' });
  }

  const systemContext = `You are Homigo, a smart AI roommate coordinator assistant. Answer this roommate search/rental question concisely: "${message}"`;

  try {
    const aiResponse = await callGemini(systemContext);
    if (aiResponse) {
      return res.json({ reply: aiResponse.trim() });
    }

    // Local Fallback Chatbot responses
    const lowercaseMsg = message.toLowerCase();
    let reply = "I'm Homigo, your roommate finder assistant. Can you tell me more about your hostel preferences or budget?";

    if (lowercaseMsg.includes("question") || lowercaseMsg.includes("ask")) {
      reply = `Before moving in with a roommate, you should always ask:
1. What is your sleep schedule? (Early bird vs Night owl)
2. How do you prefer splitting monthly grocery, WiFi, and utility bills?
3. How often do you allow guests or friends in the hostel room?
4. What is your definition of room cleanliness (daily cleaning vs weekly)?`;
    } else if (lowercaseMsg.includes("budget") || lowercaseMsg.includes("rent") || lowercaseMsg.includes("split")) {
      reply = `To manage your room budget effectively:
1. Always discuss and agree on exact rent shares beforehand.
2. Use Homigo's Expense Splitter to log and divide shared expenses (WiFi, groceries, AC bills).
3. Try to establish a payment deadline (e.g., 5th of every month) for outstanding dues.`;
    } else if (lowercaseMsg.includes("suit") || lowercaseMsg.includes("match") || lowercaseMsg.includes("compatible")) {
      reply = `In Homigo, we calculate compatibility based on:
- Budget overlaps (20% weight)
- Sleep schedule match (20% weight)
- Lifestyle habits like smoking/drinking (20% weight)
- Cleanliness matching (20% weight)
Check the 'Explore' tab to see your top matches, and tap on a profile to see the breakdown!`;
    } else if (lowercaseMsg.includes("tip") || lowercaseMsg.includes("advice")) {
      reply = `Here are some quick roommate coexistence tips:
- Set up ground rules for sharing items (induction cooktop, utensils, laundry).
- Respect quiet hours during exams.
- Communication is key! Address small issues before they build up.`;
    }

    res.json({ reply });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'AI chatbot error' });
  }
};

exports.detectFakeProfile = async (req, res) => {
  const { targetUserId } = req.params;

  try {
    const profile = await dbHelper.get(
      "SELECT p.*, u.name FROM profiles p JOIN users u ON p.user_id = u.id WHERE p.user_id = ?",
      [targetUserId]
    );

    if (!profile) {
      return res.status(404).json({ error: 'Profile not found' });
    }

    // Logic based risk factors
    const factors = [];
    let score = 0.0;

    // Check Bio
    if (!profile.bio || profile.bio.trim().length === 0) {
      factors.push("Missing biography (bio)");
      score += 0.4;
    } else if (profile.bio.trim().length < 15) {
      factors.push("Extremely short biography (under 15 characters)");
      score += 0.2;
    }

    // Check Verification
    if (profile.is_verified === 0) {
      factors.push("Account is not ID-verified (no college ID / Aadhaar upload)");
      score += 0.35;
    }

    // Check Name Length or strange patterns
    if (profile.name && profile.name.trim().split(" ").length < 2) {
      factors.push("Single name entry without last name");
      score += 0.15;
    }

    // Cap score at 1.0
    score = parseFloat(Math.min(1.0, score).toFixed(2));

    let riskLevel = "Low Risk";
    if (score > 0.6) riskLevel = "High Risk (Suspicious)";
    else if (score > 0.3) riskLevel = "Medium Risk";

    res.json({
      userId: parseInt(targetUserId),
      name: profile.name,
      fakeRiskScore: score,
      riskLevel,
      factors
    });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Fake profile detection error' });
  }
};
