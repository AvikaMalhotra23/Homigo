const dbHelper = require('../db/database');

const isProduction = process.env.NODE_ENV === 'production';

exports.addReview = async (req, res) => {
  const reviewerId = req.user.id;
  const { revieweeId, cleanliness, respect, timeliness, noise, comment } = req.body;

  if (!revieweeId || !cleanliness || !respect || !timeliness || !noise) {
    return res.status(400).json({ error: 'revieweeId and ratings (1-5) for cleanliness, respect, timeliness, and noise are required' });
  }

  if (reviewerId === parseInt(revieweeId)) {
    return res.status(400).json({ error: 'You cannot rate yourself' });
  }

  try {
    // Verify they have stayed/connected as roommates
    const connection = await dbHelper.get(
      `SELECT * FROM requests 
       WHERE ((sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?))
         AND status = 'accepted'`,
      [reviewerId, revieweeId, revieweeId, reviewerId]
    );

    if (!connection) {
      return res.status(400).json({ error: 'You can only review roommates you have connected with' });
    }

    // Check if review already exists
    const existing = await dbHelper.get(
      "SELECT id FROM reviews WHERE reviewer_id = ? AND reviewee_id = ?",
      [reviewerId, revieweeId]
    );

    if (existing) {
      // Update
      await dbHelper.run(
        `UPDATE reviews SET cleanliness = ?, respect = ?, timeliness = ?, noise = ?, comment = ? 
         WHERE id = ?`,
        [cleanliness, respect, timeliness, noise, comment || '', existing.id]
      );
    } else {
      // Insert
      await dbHelper.run(
        `INSERT INTO reviews (reviewer_id, reviewee_id, cleanliness, respect, timeliness, noise, comment)
         VALUES (?, ?, ?, ?, ?, ?, ?)`,
        [reviewerId, revieweeId, cleanliness, respect, timeliness, noise, comment || '']
      );
    }

    res.json({ ok: true, message: 'Review submitted successfully' });
  } catch (err) {
    console.error('[reviewController.addReview ERROR]', err);
    res.status(500).json({ 
      error: isProduction ? 'Database error' : `Database error: ${err.message}`, 
      stack: isProduction ? undefined : err.stack 
    });
  }
};

exports.getReviews = async (req, res) => {
  const { userId } = req.params;

  if (!userId) {
    return res.status(400).json({ error: 'User ID is required' });
  }

  try {
    // Get average ratings
    const stats = await dbHelper.get(
      `SELECT 
        AVG(cleanliness) as avg_cleanliness,
        AVG(respect) as avg_respect,
        AVG(timeliness) as avg_timeliness,
        AVG(noise) as avg_noise,
        COUNT(*) as total_reviews
       FROM reviews 
       WHERE reviewee_id = ?`,
      [userId]
    );

    // Get individual reviews
    const list = await dbHelper.query(
      `SELECT r.id, r.cleanliness, r.respect, r.timeliness, r.noise, r.comment, r.created_at,
              u.name as reviewer_name
       FROM reviews r
       JOIN users u ON r.reviewer_id = u.id
       WHERE r.reviewee_id = ?
       ORDER BY r.created_at DESC`,
      [userId]
    );

    res.json({
      averageCleanliness: parseFloat((stats.avg_cleanliness || 0).toFixed(1)),
      averageRespect: parseFloat((stats.avg_respect || 0).toFixed(1)),
      averageTimeliness: parseFloat((stats.avg_timeliness || 0).toFixed(1)),
      averageNoise: parseFloat((stats.avg_noise || 0).toFixed(1)),
      totalReviews: stats.total_reviews,
      reviews: list
    });
  } catch (err) {
    console.error('[reviewController.getReviews ERROR]', err);
    res.status(500).json({ 
      error: isProduction ? 'Database error' : `Database error: ${err.message}`, 
      stack: isProduction ? undefined : err.stack 
    });
  }
};
