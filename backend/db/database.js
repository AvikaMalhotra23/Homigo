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

  // Notices table
  db.run(`CREATE TABLE IF NOT EXISTS notices (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    hostel TEXT DEFAULT 'all',
    type TEXT DEFAULT 'general',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
  )`);

  // Events table
  db.run(`CREATE TABLE IF NOT EXISTS events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    date TEXT NOT NULL,
    time TEXT NOT NULL,
    location TEXT NOT NULL,
    type TEXT DEFAULT 'cultural',
    organizer TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
  )`);

  // Marketplace table
  db.run(`CREATE TABLE IF NOT EXISTS marketplace (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    seller_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    price REAL NOT NULL,
    category TEXT NOT NULL,
    hostel TEXT NOT NULL,
    description TEXT,
    image_url TEXT,
    is_sold INTEGER DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(seller_id) REFERENCES users(id) ON DELETE CASCADE
  )`);

  // Check if we need to seed mock data
  db.get("SELECT COUNT(*) as count FROM users", (err, row) => {
    if (row && row.count === 0) {
      seedData();
    }
  });

  // Seed notices/events/marketplace independently (won't duplicate)
  db.get("SELECT COUNT(*) as count FROM notices", (err, row) => {
    if (row && row.count === 0) {
      seedNoticesEventsMarketplace();
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

function seedNoticesEventsMarketplace() {
  console.log("Seeding notices, events, and marketplace...");

  // Notices
  const notices = [
    { title: "Water Supply Interruption", content: "Water supply will be unavailable tomorrow from 9 AM to 12 PM due to maintenance work. Please store water accordingly.", hostel: "all", type: "maintenance" },
    { title: "Cultural Night – Friday 7 PM", content: "Annual Cultural Night is happening this Friday at 7 PM in the Main Auditorium. All hostel residents are invited. Entry is free!", hostel: "all", type: "event" },
    { title: "Mess Menu Updated", content: "The weekly mess menu has been updated. Special dishes include Paneer Butter Masala on Wednesday and Biryani on Saturday.", hostel: "all", type: "mess" },
    { title: "GH-5 Room Inspection", content: "Room inspection for GH-5 will take place on Monday between 10 AM and 1 PM. Please ensure your rooms are tidy.", hostel: "gh5", type: "maintenance" },
    { title: "WiFi Upgrade Scheduled", content: "Campus WiFi will be upgraded this weekend. Expect intermittent connectivity on Saturday from 2 AM to 6 AM.", hostel: "all", type: "technical" },
    { title: "Late Night Entry Policy Reminder", content: "All residents must sign in at the gate after 10 PM. Please carry your ID card at all times.", hostel: "all", type: "general" }
  ];

  notices.forEach(n => {
    db.run("INSERT INTO notices (title, content, hostel, type) VALUES (?, ?, ?, ?)",
      [n.title, n.content, n.hostel, n.type]);
  });

  // Events
  const tomorrow = new Date(); tomorrow.setDate(tomorrow.getDate() + 1);
  const saturday = new Date(); saturday.setDate(saturday.getDate() + (6 - saturday.getDay() + 7) % 7 || 7);
  const friday = new Date(); friday.setDate(friday.getDate() + (5 - friday.getDay() + 7) % 7 || 7);
  const nextWeek = new Date(); nextWeek.setDate(nextWeek.getDate() + 8);

  const fmtDate = (d) => d.toISOString().split('T')[0];

  const events = [
    { name: "Cricket Tournament", description: "Inter-hostel cricket tournament. Register your team of 11 and compete for the trophy!", date: fmtDate(tomorrow), time: "8:00 AM", location: "Sports Ground", type: "sports", organizer: "Hostel Sports Committee" },
    { name: "Coding Hackathon", description: "24-hour hackathon open to all students. Form teams of 3-4 and build something amazing. Prizes worth ₹50,000!", date: fmtDate(saturday), time: "9:00 AM", location: "Innovation Lab, Block 32", type: "academic", organizer: "CSE Department" },
    { name: "Freshers Party 2026", description: "Welcome the new batch! DJ night, dance performances, food stalls, and lots of fun. All students welcome.", date: fmtDate(friday), time: "7:00 PM", location: "Main Auditorium", type: "cultural", organizer: "Student Council" },
    { name: "Yoga & Wellness Camp", description: "Start your morning right! Free yoga session open to all hostel residents. Bring your own mat.", date: fmtDate(nextWeek), time: "6:00 AM", location: "Hostel Lawn", type: "wellness", organizer: "NCC Wing" },
    { name: "Book Fair & Swap", description: "Bring your old textbooks and exchange them for books you need. Great way to save money and help others.", date: fmtDate(saturday), time: "11:00 AM", location: "Library Block", type: "academic", organizer: "Library Committee" }
  ];

  events.forEach(e => {
    db.run("INSERT INTO events (name, description, date, time, location, type, organizer) VALUES (?, ?, ?, ?, ?, ?, ?)",
      [e.name, e.description, e.date, e.time, e.location, e.type, e.organizer]);
  });

  // Marketplace listings (using user_id=1 as fallback seller until real users exist)
  // We delay this slightly to ensure user seeding completes first
  setTimeout(() => {
    db.get("SELECT id FROM users LIMIT 1", (err, row) => {
      const sellerId = row ? row.id : 1;
      const marketItems = [
        { title: "Hero Cycle", price: 2500, category: "Transport", hostel: "BH-3", description: "Good condition Hero cycle, 1 year old. Rarely used. Selling because going home." },
        { title: "Study Chair", price: 500, category: "Furniture", hostel: "GH-2", description: "Comfortable study chair with back support. Moving out of hostel, need to sell urgently." },
        { title: "Engineering Books – CSE 3rd Sem", price: 800, category: "Books", hostel: "BH-7", description: "Complete set of 3rd semester CSE books including DS, COA, and Math. Good condition." },
        { title: "Mini Refrigerator", price: 3200, category: "Appliances", hostel: "GH-5", description: "Haier 90L mini fridge. Works perfectly. Selling at end of semester." },
        { title: "Study Table Lamp", price: 350, category: "Electronics", hostel: "BH-1", description: "LED study lamp with USB charging port. Perfect for late night studies." },
        { title: "Badminton Rackets (Pair)", price: 600, category: "Sports", hostel: "GH-4", description: "Pair of Yonex badminton rackets with 3 shuttlecocks. Barely used." },
        { title: "Mattress – Single Bed", price: 1200, category: "Furniture", hostel: "BH-10", description: "5-inch foam mattress, single bed size. Hygienic and clean. Leaving campus." },
        { title: "Printer – HP DeskJet", price: 2800, category: "Electronics", hostel: "GH-1", description: "HP DeskJet 2331 All-in-One Printer with ink cartridges. Perfect for assignments." }
      ];

      marketItems.forEach(item => {
        db.run("INSERT INTO marketplace (seller_id, title, price, category, hostel, description) VALUES (?, ?, ?, ?, ?, ?)",
          [sellerId, item.title, item.price, item.category, item.hostel, item.description]);
      });
    });
  }, 2000);

  console.log("Notices, events, and marketplace seeding completed.");
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
