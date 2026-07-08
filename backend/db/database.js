const sqlite3 = require('sqlite3').verbose();
const path = require('path');
const bcrypt = require('bcryptjs');

const dbPath = path.resolve(__dirname, '../homigo.db');
const db = new sqlite3.Database(dbPath, (err) => {
  if (err) {
    console.error('Error opening database:', err.message);
  } else {
    console.log('Connected to SQLite database at:', dbPath);
    db.serialize(() => {
      createTables();
    });
  }
});

function createTables() {
  // Users table
  db.run(`CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    gender TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
  )`);

  // Profiles table
  db.run(`CREATE TABLE IF NOT EXISTS profiles (
    user_id INTEGER PRIMARY KEY,
    college TEXT NOT NULL,
    hostel TEXT NOT NULL,
    room_preference TEXT NOT NULL,
    course TEXT DEFAULT 'B.Tech',
    branch TEXT,
    year TEXT,
    semester INTEGER,
    section TEXT,
    roll_number TEXT,
    school TEXT,
    looking_for TEXT,
    preferred_hostel TEXT,
    current_hostel TEXT,
    room_number TEXT,
    move_in_date TEXT,
    interests TEXT,
    languages TEXT,
    hometown TEXT,
    budget_min INTEGER DEFAULT 0,
    budget_max INTEGER DEFAULT 100000,
    sleep_schedule TEXT NOT NULL, -- 'early_bird', 'night_owl', 'flexible'
    smoking TEXT NOT NULL,        -- 'yes', 'no'
    drinking TEXT NOT NULL,       -- 'yes', 'no'
    food_preference TEXT NOT NULL,-- 'veg', 'non_veg', 'any'
    cleanliness TEXT NOT NULL,    -- 'high', 'moderate', 'low'
    pets TEXT NOT NULL,           -- 'yes', 'no'
    guests TEXT NOT NULL,         -- 'frequent', 'rare', 'no'
    bio TEXT,
    is_verified INTEGER DEFAULT 0, -- 0 for false, 1 for true
    id_proof_url TEXT,
    fake_risk_score REAL DEFAULT 0.0,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
  )`);

  // Roommate Requests table
  db.run(`CREATE TABLE IF NOT EXISTS requests (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sender_id INTEGER NOT NULL,
    receiver_id INTEGER NOT NULL,
    status TEXT NOT NULL DEFAULT 'pending', -- 'pending', 'accepted', 'rejected'
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY(receiver_id) REFERENCES users(id) ON DELETE CASCADE
  )`);

  // Chats table
  db.run(`CREATE TABLE IF NOT EXISTS chats (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sender_id INTEGER NOT NULL,
    receiver_id INTEGER NOT NULL,
    message TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY(receiver_id) REFERENCES users(id) ON DELETE CASCADE
  )`);

  // Expenses table
  db.run(`CREATE TABLE IF NOT EXISTS expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    creator_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    amount REAL NOT NULL,
    category TEXT NOT NULL, -- 'Rent', 'Electricity', 'WiFi', 'Grocery', 'Miscellaneous'
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(creator_id) REFERENCES users(id) ON DELETE CASCADE
  )`);

  // Expense splits table
  db.run(`CREATE TABLE IF NOT EXISTS expense_splits (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    expense_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    share REAL NOT NULL,
    is_paid INTEGER DEFAULT 0, -- 0 for false, 1 for true
    FOREIGN KEY(expense_id) REFERENCES expenses(id) ON DELETE CASCADE,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
  )`);

  // Reviews table
  db.run(`CREATE TABLE IF NOT EXISTS reviews (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    reviewer_id INTEGER NOT NULL,
    reviewee_id INTEGER NOT NULL,
    cleanliness INTEGER NOT NULL, -- 1 to 5
    respect INTEGER NOT NULL,     -- 1 to 5
    timeliness INTEGER NOT NULL,  -- 1 to 5
    noise INTEGER NOT NULL,       -- 1 to 5
    comment TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(reviewer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY(reviewee_id) REFERENCES users(id) ON DELETE CASCADE
  )`);

  // Check if we need to seed mock data
  db.get("SELECT COUNT(*) as count FROM users", (err, row) => {
    if (row && row.count === 0) {
      seedData();
    }
  });
}

function seedData() {
  console.log("Seeding initial roommate profiles for testing...");
  const passwordHash = bcrypt.hashSync("password123", 10);

  const mockUsers = [
    // Boys
    { name: "Rahul Sharma", email: "rahul@lpu.in", gender: "male" },
    { name: "Aman Verma", email: "aman@lpu.in", gender: "male" },
    { name: "Vikram Singh", email: "vikram@lpu.in", gender: "male" },
    { name: "Karan Patel", email: "karan@lpu.in", gender: "male" },
    { name: "Rohan Das", email: "rohan@lpu.in", gender: "male" },
    // Girls
    { name: "Priya Patel", email: "priya@lpu.in", gender: "female" },
    { name: "Sneha Reddy", email: "sneha@lpu.in", gender: "female" },
    { name: "Ananya Iyer", email: "ananya@lpu.in", gender: "female" },
    { name: "Mehak Kaur", email: "mehak@lpu.in", gender: "female" },
    { name: "Aditi Sen", email: "aditi@lpu.in", gender: "female" }
  ];

  const mockProfiles = [
    // Boys profiles
    {
      college: "Lovely Professional University", hostel: "bh1", room_preference: "2-seater", course: "CSE",
      budget_min: 3000, budget_max: 6000, sleep_schedule: "night_owl",
      smoking: "no", drinking: "no", food_preference: "veg", cleanliness: "high",
      pets: "no", guests: "rare", bio: "CSE student looking for a studious roommate who keeps the room clean and respects quiet hours.",
      is_verified: 1, fake_risk_score: 0.1
    },
    {
      college: "Lovely Professional University", hostel: "bh2", room_preference: "3-seater", course: "BioTech",
      budget_min: 2000, budget_max: 4000, sleep_schedule: "early_bird",
      smoking: "no", drinking: "no", food_preference: "veg", cleanliness: "high",
      pets: "no", guests: "no", bio: "BioTech major, very organized and wakes up early. Looking for a peaceful roommate.",
      is_verified: 1, fake_risk_score: 0.05
    },
    {
      college: "Lovely Professional University", hostel: "bh1", room_preference: "2-seater", course: "MBA",
      budget_min: 4000, budget_max: 8000, sleep_schedule: "flexible",
      smoking: "yes", drinking: "yes", food_preference: "non_veg", cleanliness: "moderate",
      pets: "yes", guests: "frequent", bio: "Friendly and easygoing MBA student. Love music, gaming, and don't mind late night chats.",
      is_verified: 0, fake_risk_score: 0.2
    },
    {
      college: "Lovely Professional University", hostel: "bh5", room_preference: "single", course: "B.Tech CSE",
      budget_min: 8000, budget_max: 12000, sleep_schedule: "night_owl",
      smoking: "no", drinking: "no", food_preference: "any", cleanliness: "moderate",
      pets: "no", guests: "rare", bio: "Quiet gamer looking to share a clean 2-seater or find a room partner.",
      is_verified: 1, fake_risk_score: 0.15
    },
    {
      college: "Lovely Professional University", hostel: "bh10", room_preference: "4-seater", course: "ECE",
      budget_min: 1500, budget_max: 3000, sleep_schedule: "night_owl",
      smoking: "no", drinking: "yes", food_preference: "non_veg", cleanliness: "low",
      pets: "no", guests: "frequent", bio: "Fun-loving engineering student, looking for chill roommates to hang out.",
      is_verified: 0, fake_risk_score: 0.35
    },
    // Girls profiles
    {
      college: "Lovely Professional University", hostel: "gh1", room_preference: "2-seater", course: "Architecture",
      budget_min: 3500, budget_max: 6000, sleep_schedule: "early_bird",
      smoking: "no", drinking: "no", food_preference: "veg", cleanliness: "high",
      pets: "no", guests: "no", bio: "Architecture student. I study late sometimes but generally sleep early. I keep my space very clean.",
      is_verified: 1, fake_risk_score: 0.05
    },
    {
      college: "Lovely Professional University", hostel: "gh2", room_preference: "2-seater", course: "Fashion Design",
      budget_min: 4000, budget_max: 7500, sleep_schedule: "night_owl",
      smoking: "no", drinking: "no", food_preference: "non_veg", cleanliness: "moderate",
      pets: "no", guests: "rare", bio: "Fashion Design major, outgoing and friendly. Need a roommate who likes to chat and share expenses.",
      is_verified: 1, fake_risk_score: 0.1
    },
    {
      college: "Lovely Professional University", hostel: "gh3", room_preference: "3-seater", course: "Pharmacy",
      budget_min: 2500, budget_max: 4500, sleep_schedule: "flexible",
      smoking: "no", drinking: "no", food_preference: "any", cleanliness: "moderate",
      pets: "no", guests: "rare", bio: "Pharmacy student. Casual, friendly, loves movies, and cooks occasionally.",
      is_verified: 0, fake_risk_score: 0.12
    },
    {
      college: "Lovely Professional University", hostel: "gh1", room_preference: "single", course: "M.Tech CSE",
      budget_min: 7000, budget_max: 11000, sleep_schedule: "early_bird",
      smoking: "no", drinking: "no", food_preference: "veg", cleanliness: "high",
      pets: "yes", guests: "no", bio: "Very focused CSE student, quiet, loves pets. Looking for a neat and peaceful room partner.",
      is_verified: 1, fake_risk_score: 0.05
    },
    {
      college: "Lovely Professional University", hostel: "gh9", room_preference: "2-seater", course: "BCA",
      budget_min: 3000, budget_max: 5500, sleep_schedule: "night_owl",
      smoking: "no", drinking: "yes", food_preference: "any", cleanliness: "moderate",
      pets: "no", guests: "frequent", bio: "BCA student who loves music, late-night snacking, and watching Netflix. Let's room together!",
      is_verified: 0, fake_risk_score: 0.22
    }
  ];

  mockUsers.forEach((u, i) => {
    db.run(
      "INSERT INTO users (name, email, password_hash, gender) VALUES (?, ?, ?, ?)",
      [u.name, u.email, passwordHash, u.gender],
      function (err) {
        if (err) {
          console.error("Error inserting user:", err.message);
          return;
        }
        const userId = this.lastID;
        const p = mockProfiles[i];
        db.run(
          `INSERT INTO profiles (
            user_id, college, hostel, room_preference, course, branch, year, semester, section, roll_number, school, 
            looking_for, preferred_hostel, current_hostel, room_number, move_in_date, interests, languages, hometown,
            budget_min, budget_max, sleep_schedule, smoking, drinking, food_preference, cleanliness, pets, guests, bio, is_verified, fake_risk_score
          ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
          [
            userId, p.college, p.hostel, p.room_preference, p.course, p.course || "CSE", "1st", 1, "K24AB", "12401020", "School of Computer Science and Engineering",
            "Roommate", p.hostel, p.hostel, "302", "2026-08-01", '["Gaming", "Coding"]', '["English", "Hindi"]', "Delhi",
            p.budget_min, p.budget_max, p.sleep_schedule, p.smoking, p.drinking, p.food_preference, p.cleanliness, p.pets, p.guests, p.bio, p.is_verified, p.fake_risk_score
          ]
        );
      }
    );
  });
  console.log("Mock database seeding completed.");
}

module.exports = {
  db,
  query: (sql, params = []) => {
    return new Promise((resolve, reject) => {
      db.all(sql, params, (err, rows) => {
        if (err) reject(err);
        else resolve(rows);
      });
    });
  },
  get: (sql, params = []) => {
    return new Promise((resolve, reject) => {
      db.get(sql, params, (err, row) => {
        if (err) reject(err);
        else resolve(row);
      });
    });
  },
  run: (sql, params = []) => {
    return new Promise((resolve, reject) => {
      db.run(sql, params, function (err) {
        if (err) reject(err);
        else resolve({ id: this.lastID, changes: this.changes });
      });
    });
  }
};
