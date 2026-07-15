require('dotenv').config();
const express = require('express');
const cors = require('cors');
const http = require('http');
const { Server } = require('socket.io');
const path = require('path');

const app = express();
const server = http.createServer(app);
const io = new Server(server, {
  cors: {
    origin: "*",
    methods: ["GET", "POST"]
  }
});

app.use(cors());
app.use(express.json());

// Request logging middleware for debugging
app.use((req, res, next) => {
  console.log(`\n>>> [REQUEST] ${req.method} ${req.url}`);
  if (req.body && Object.keys(req.body).length > 0) {
    // Mask password in logs
    const bodyCopy = { ...req.body };
    if (bodyCopy.password) bodyCopy.password = '********';
    console.log('Body:', JSON.stringify(bodyCopy, null, 2));
  }
  
  const originalJson = res.json;
  res.json = function(data) {
    console.log(`<<< [RESPONSE] Status ${res.statusCode}`);
    if (data && (data.error || data.message || data.token)) {
      const dataCopy = { ...data };
      if (dataCopy.token) dataCopy.token = '...masked...';
      console.log('Data:', JSON.stringify(dataCopy, null, 2));
    }
    return originalJson.apply(this, arguments);
  };
  next();
});

// Socket.IO middleware to make 'io' accessible in route controllers
app.use((req, res, next) => {
  req.io = io;
  next();
});

// Import database to trigger initialization
require('./db/database');

// Import routes
app.use('/api/auth', require('./routes/auth'));
app.use('/api/users', require('./routes/users'));
app.use('/api/profile', require('./routes/profile'));
app.use('/api/matches', require('./routes/match'));
app.use('/api/requests', require('./routes/request'));
app.use('/api/chat', require('./routes/chat'));
app.use('/api/expenses', require('./routes/expense'));
app.use('/api/reviews', require('./routes/review'));
app.use('/api/ai', require('./routes/ai'));
app.use('/api/dashboard', require('./routes/dashboard'));
app.use('/api/notices', require('./routes/notice'));
app.use('/api/events', require('./routes/event'));
app.use('/api/marketplace', require('./routes/marketplace'));

// Health check endpoint
app.get('/api/health', (req, res) => {
  res.json({ ok: true, message: 'Homigo server is healthy and running' });
});

// Direct APK Download Endpoint
app.get('/download-apk', (req, res) => {
  const apkPath = path.resolve(__dirname, '../app/build/outputs/apk/release/app-release.apk');
  res.download(apkPath, 'Homigo_v2.apk', (err) => {
    if (err) {
      console.error('Error downloading APK:', err.message);
      if (!res.headersSent) {
        res.status(404).json({ error: 'APK file not found. Make sure to build the app in Android Studio first.' });
      }
    }
  });
});

// Socket.IO Connection Handler
io.on('connection', (socket) => {
  console.log('A user connected:', socket.id);

  // User joins a personal room named user_<userId>
  socket.on('join', (userId) => {
    socket.join(`user_${userId}`);
    console.log(`User ${userId} joined room user_${userId}`);
  });

  socket.on('disconnect', () => {
    console.log('User disconnected:', socket.id);
  });
});

const PORT = process.env.PORT || 5001;
server.listen(PORT, '0.0.0.0', () => {
  console.log(`Homigo backend running on port ${PORT}`);
});
