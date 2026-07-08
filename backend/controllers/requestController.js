const dbHelper = require('../db/database');

exports.sendRequest = async (req, res) => {
  const senderId = req.user.id;
  const { receiverId } = req.body;

  if (!receiverId) {
    return res.status(400).json({ error: 'Receiver ID is required' });
  }

  if (senderId === parseInt(receiverId)) {
    return res.status(400).json({ error: 'You cannot send a roommate request to yourself' });
  }

  try {
    // Check if a request already exists
    const existing = await dbHelper.get(
      `SELECT * FROM requests 
       WHERE (sender_id = ? AND receiver_id = ?) 
          OR (sender_id = ? AND receiver_id = ?)`
      , [senderId, receiverId, receiverId, senderId]
    );

    if (existing) {
      return res.status(400).json({ error: `Request already exists with status: ${existing.status}` });
    }

    await dbHelper.run(
      "INSERT INTO requests (sender_id, receiver_id, status) VALUES (?, ?, 'pending')",
      [senderId, receiverId]
    );

    res.json({ ok: true, message: 'Roommate request sent successfully' });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Database error' });
  }
};

exports.getRequests = async (req, res) => {
  const userId = req.user.id;

  try {
    // Incoming requests (people who want to room with me)
    const incoming = await dbHelper.query(
      `SELECT r.id, r.sender_id, r.status, r.created_at, u.name, u.email, u.gender, p.hostel, p.room_preference, p.bio
       FROM requests r
       JOIN users u ON r.sender_id = u.id
       JOIN profiles p ON r.sender_id = p.user_id
       WHERE r.receiver_id = ? AND r.status = 'pending'`,
      [userId]
    );

    // Sent requests (people I asked)
    const sent = await dbHelper.query(
      `SELECT r.id, r.receiver_id, r.status, r.created_at, u.name, u.email, u.gender, p.hostel, p.room_preference, p.bio
       FROM requests r
       JOIN users u ON r.receiver_id = u.id
       JOIN profiles p ON r.receiver_id = p.user_id
       WHERE r.sender_id = ?`,
      [userId]
    );

    res.json({ incoming, sent });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Database error' });
  }
};

exports.respondToRequest = async (req, res) => {
  const userId = req.user.id;
  const { requestId, status } = req.body; // status should be 'accepted' or 'rejected'

  if (!requestId || !['accepted', 'rejected'].includes(status)) {
    return res.status(400).json({ error: 'Request ID and valid status (accepted/rejected) are required' });
  }

  try {
    // Verify the request is sent to the caller
    const request = await dbHelper.get(
      "SELECT * FROM requests WHERE id = ? AND receiver_id = ?",
      [requestId, userId]
    );

    if (!request) {
      return res.status(404).json({ error: 'Roommate request not found or not addressed to you' });
    }

    await dbHelper.run(
      "UPDATE requests SET status = ? WHERE id = ?",
      [status, requestId]
    );

    res.json({ ok: true, message: `Roommate request ${status} successfully` });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Database error' });
  }
};
