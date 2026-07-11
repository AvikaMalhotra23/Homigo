const dbHelper = require('../db/database');

const isProduction = process.env.NODE_ENV === 'production';

// Get list of users with whom you have an accepted roommate request
exports.getChatList = async (req, res) => {
  const userId = req.user.id;

  try {
    const list = await dbHelper.query(
      `SELECT DISTINCT u.id, u.name, u.email, u.gender, p.hostel, p.room_preference, p.is_verified
       FROM users u
       JOIN profiles p ON u.id = p.user_id
       JOIN requests r ON (
         (r.sender_id = ? AND r.receiver_id = u.id) OR
         (r.sender_id = u.id AND r.receiver_id = ?)
       )
       WHERE r.status = 'accepted' AND u.id != ?`,
      [userId, userId, userId]
    );

    res.json(list);
  } catch (err) {
    console.error('[chatController.getChatList ERROR]', err);
    res.status(500).json({ 
      error: isProduction ? 'Database error' : `Database error: ${err.message}`, 
      stack: isProduction ? undefined : err.stack 
    });
  }
};

exports.getMessages = async (req, res) => {
  const userId = req.user.id;
  const { otherUserId } = req.params;

  if (!otherUserId) {
    return res.status(400).json({ error: 'Other User ID is required' });
  }

  try {
    const messages = await dbHelper.query(
      `SELECT * FROM chats 
       WHERE (sender_id = ? AND receiver_id = ?) 
          OR (sender_id = ? AND receiver_id = ?)
       ORDER BY created_at ASC`,
      [userId, otherUserId, otherUserId, userId]
    );

    res.json(messages);
  } catch (err) {
    console.error('[chatController.getMessages ERROR]', err);
    res.status(500).json({ 
      error: isProduction ? 'Database error' : `Database error: ${err.message}`, 
      stack: isProduction ? undefined : err.stack 
    });
  }
};

exports.sendMessage = async (req, res) => {
  const senderId = req.user.id;
  const { receiverId, message } = req.body;

  if (!receiverId || !message || message.trim() === '') {
    return res.status(400).json({ error: 'Receiver ID and non-empty message are required' });
  }

  try {
    // Verify request is accepted
    const connection = await dbHelper.get(
      `SELECT * FROM requests 
       WHERE ((sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?))
         AND status = 'accepted'`,
      [senderId, receiverId, receiverId, senderId]
    );

    if (!connection) {
      return res.status(403).json({ error: 'You can only message roommates with accepted requests.' });
    }

    const result = await dbHelper.run(
      "INSERT INTO chats (sender_id, receiver_id, message) VALUES (?, ?, ?)",
      [senderId, receiverId, message]
    );

    // Notify via Socket.IO if available (will be hooked up in server.js)
    if (req.io) {
      req.io.to(`user_${receiverId}`).emit('new_message', {
        id: result.id,
        sender_id: senderId,
        receiver_id: parseInt(receiverId),
        message,
        created_at: new Date().toISOString()
      });
    }

    res.status(201).json({
      id: result.id,
      sender_id: senderId,
      receiver_id: parseInt(receiverId),
      message,
      created_at: new Date().toISOString()
    });
  } catch (err) {
    console.error('[chatController.sendMessage ERROR]', err);
    res.status(500).json({ 
      error: isProduction ? 'Database error' : `Database error: ${err.message}`, 
      stack: isProduction ? undefined : err.stack 
    });
  }
};
