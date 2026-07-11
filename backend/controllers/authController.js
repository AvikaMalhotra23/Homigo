const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const dbHelper = require('../db/database');

const JWT_SECRET = process.env.JWT_SECRET || 'homigo_secret_key_12345';
const isProduction = process.env.NODE_ENV === 'production';

exports.register = async (req, res) => {
  const { name, email, password, gender } = req.body;

  if (!name || !email || !password || !gender) {
    return res.status(400).json({ error: 'All fields are required' });
  }

  try {
    // Check if email already exists
    const existingUser = await dbHelper.get("SELECT * FROM users WHERE email = ?", [email]);
    if (existingUser) {
      return res.status(400).json({ error: 'User with this email already exists' });
    }

    const passwordHash = bcrypt.hashSync(password, 10);
    const result = await dbHelper.run(
      "INSERT INTO users (name, email, password_hash, gender) VALUES (?, ?, ?, ?)",
      [name, email, passwordHash, gender.toLowerCase()]
    );

    const token = jwt.sign(
      { id: result.id, email, gender: gender.toLowerCase() },
      JWT_SECRET,
      { expiresIn: '30d' }
    );

    res.status(201).json({
      token,
      user: {
        id: result.id,
        name,
        email,
        gender: gender.toLowerCase()
      }
    });
  } catch (err) {
    console.error('[authController.register ERROR]', err);
    res.status(500).json({ 
      error: isProduction ? 'Database error occurred' : `Database error: ${err.message}`, 
      stack: isProduction ? undefined : err.stack 
    });
  }
};

exports.login = async (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    return res.status(400).json({ error: 'Email and password are required' });
  }

  try {
    const user = await dbHelper.get("SELECT * FROM users WHERE email = ?", [email]);
    if (!user) {
      return res.status(400).json({ error: 'Invalid email or password' });
    }

    const isMatch = bcrypt.compareSync(password, user.password_hash);
    if (!isMatch) {
      return res.status(400).json({ error: 'Invalid email or password' });
    }

    const token = jwt.sign(
      { id: user.id, email: user.email, gender: user.gender },
      JWT_SECRET,
      { expiresIn: '30d' }
    );

    // Check if profile exists
    const profile = await dbHelper.get("SELECT * FROM profiles WHERE user_id = ?", [user.id]);

    res.json({
      token,
      user: {
        id: user.id,
        name: user.name,
        email: user.email,
        gender: user.gender,
        hasProfile: !!profile
      }
    });
  } catch (err) {
    console.error('[authController.login ERROR]', err);
    res.status(500).json({ 
      error: isProduction ? 'Database error occurred' : `Database error: ${err.message}`, 
      stack: isProduction ? undefined : err.stack 
    });
  }
};

exports.logout = async (req, res) => {
  res.json({ success: true, message: 'Logged out successfully.' });
};

