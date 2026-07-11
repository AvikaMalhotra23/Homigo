const dbHelper = require('../db/database');

const isProduction = process.env.NODE_ENV === 'production';

exports.addExpense = async (req, res) => {
  const creatorId = req.user.id;
  const { title, amount, category, participantIds } = req.body; // participantIds is an array of IDs of roommates, excluding the creator

  if (!title || !amount || !category || !participantIds || !Array.isArray(participantIds)) {
    return res.status(400).json({ error: 'title, amount, category, and participantIds array are required' });
  }

  const allParticipants = [creatorId, ...participantIds.map(id => parseInt(id))];
  const splitShare = parseFloat((amount / allParticipants.length).toFixed(2));

  try {
    // 1. Insert the main expense
    const expenseResult = await dbHelper.run(
      "INSERT INTO expenses (creator_id, title, amount, category) VALUES (?, ?, ?, ?)",
      [creatorId, title, amount, category]
    );
    const expenseId = expenseResult.id;

    // 2. Insert splits for everyone
    for (const userId of allParticipants) {
      // If it is the creator, mark split as paid (since the creator paid upfront)
      const isPaid = userId === creatorId ? 1 : 0;
      await dbHelper.run(
        "INSERT INTO expense_splits (expense_id, user_id, share, is_paid) VALUES (?, ?, ?, ?)",
        [expenseId, userId, splitShare, isPaid]
      );
    }

    res.status(201).json({ ok: true, message: 'Expense added and split successfully', expenseId });
  } catch (err) {
    console.error('[expenseController.addExpense ERROR]', err);
    res.status(500).json({ 
      error: isProduction ? 'Database error' : `Database error: ${err.message}`, 
      stack: isProduction ? undefined : err.stack 
    });
  }
};

exports.getExpenses = async (req, res) => {
  const userId = req.user.id;

  try {
    // Get all expenses where user is a participant
    const expenses = await dbHelper.query(
      `SELECT DISTINCT e.id, e.creator_id, e.title, e.amount, e.category, e.created_at,
                       u.name AS creator_name,
                       es.share AS my_share, es.is_paid AS my_status
       FROM expenses e
       JOIN users u ON e.creator_id = u.id
       JOIN expense_splits es ON e.id = es.expense_id
       WHERE es.user_id = ?
       ORDER BY e.created_at DESC`,
      [userId]
    );

    // For each expense, load the other participant splits
    for (const exp of expenses) {
      const splits = await dbHelper.query(
        `SELECT es.user_id, es.share, es.is_paid, u.name 
         FROM expense_splits es 
         JOIN users u ON es.user_id = u.id 
         WHERE es.expense_id = ?`,
         [exp.id]
      );
      exp.splits = splits;
    }

    res.json(expenses);
  } catch (err) {
    console.error('[expenseController.getExpenses ERROR]', err);
    res.status(500).json({ 
      error: isProduction ? 'Database error' : `Database error: ${err.message}`, 
      stack: isProduction ? undefined : err.stack 
    });
  }
};

exports.paySplit = async (req, res) => {
  const userId = req.user.id;
  const { splitId } = req.body;

  if (!splitId) {
    return res.status(400).json({ error: 'Split ID is required' });
  }

  try {
    // Verify user owns the split
    const split = await dbHelper.get(
      "SELECT * FROM expense_splits WHERE id = ? AND user_id = ?",
      [splitId, userId]
    );

    if (!split) {
      return res.status(404).json({ error: 'Split not found or not belonging to you' });
    }

    await dbHelper.run(
      "UPDATE expense_splits SET is_paid = 1 WHERE id = ?",
      [splitId]
    );

    res.json({ ok: true, message: 'Expense split marked as paid' });
  } catch (err) {
    console.error('[expenseController.paySplit ERROR]', err);
    res.status(500).json({ 
      error: isProduction ? 'Database error' : `Database error: ${err.message}`, 
      stack: isProduction ? undefined : err.stack 
    });
  }
};

exports.getSummary = async (req, res) => {
  const userId = req.user.id;

  try {
    // 1. Get Category totals for the user
    // (This aggregates the user's share for each category)
    const categoryBreakdown = await dbHelper.query(
      `SELECT e.category, SUM(es.share) as total
       FROM expenses e
       JOIN expense_splits es ON e.id = es.expense_id
       WHERE es.user_id = ?
       GROUP BY e.category`,
      [userId]
    );

    // 2. Calculate balance summary
    // Who owes the user (people who need to pay back the user)
    const youAreOwed = await dbHelper.query(
      `SELECT es.id as split_id, u.name as debtor_name, es.share, e.title, e.created_at
       FROM expense_splits es
       JOIN expenses e ON es.expense_id = e.id
       JOIN users u ON es.user_id = u.id
       WHERE e.creator_id = ? AND es.user_id != ? AND es.is_paid = 0`,
      [userId, userId]
    );

    // Who the user owes (expenses created by others where user hasn't paid)
    const youOwe = await dbHelper.query(
      `SELECT es.id as split_id, u.name as creditor_name, es.share, e.title, e.created_at
       FROM expense_splits es
       JOIN expenses e ON es.expense_id = e.id
       JOIN users u ON e.creator_id = u.id
       WHERE es.user_id = ? AND e.creator_id != ? AND es.is_paid = 0`,
      [userId, userId]
    );

    const totalOwed = youAreOwed.reduce((sum, item) => sum + item.share, 0);
    const totalOwe = youOwe.reduce((sum, item) => sum + item.share, 0);

    res.json({
      categoryBreakdown,
      youAreOwed,
      youOwe,
      totalOwed: parseFloat(totalOwed.toFixed(2)),
      totalOwe: parseFloat(totalOwe.toFixed(2))
    });
  } catch (err) {
    console.error('[expenseController.getSummary ERROR]', err);
    res.status(500).json({ 
      error: isProduction ? 'Database error' : `Database error: ${err.message}`, 
      stack: isProduction ? undefined : err.stack 
    });
  }
};
