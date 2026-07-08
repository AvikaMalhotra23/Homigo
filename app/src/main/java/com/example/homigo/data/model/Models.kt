package com.example.homigo.data.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val gender: String,
    val hasProfile: Boolean = false
)

data class AuthResponse(
    val token: String,
    val user: User
)

data class Profile(
    val user_id: Int,
    val name: String?,
    val email: String?,
    val gender: String?,
    val college: String,
    val hostel: String,
    val room_preference: String,
    val course: String?,
    val branch: String?,
    val year: String?,
    val semester: Int?,
    val section: String?,
    val roll_number: String?,
    val school: String?,
    val looking_for: String?,
    val preferred_hostel: String?,
    val current_hostel: String?,
    val room_number: String?,
    val move_in_date: String?,
    val interests: String?,
    val languages: String?,
    val hometown: String?,
    val budget_min: Int,
    val budget_max: Int,
    val sleep_schedule: String,
    val smoking: String,
    val drinking: String,
    val food_preference: String,
    val cleanliness: String,
    val pets: String,
    val guests: String,
    val bio: String?,
    val is_verified: Int,
    val id_proof_url: String?,
    val fake_risk_score: Double
)

data class MatchBreakdown(
    val overall: Int,
    val budget: Int,
    val sleep: Int,
    val lifestyle: Int,
    val cleanliness: Int,
    val social: Int
)

data class Match(
    val user: Profile,
    val requestStatus: String, // 'pending', 'accepted', 'rejected', 'none'
    val requestSender: Int?,
    val overallScore: Int,
    val breakdown: MatchBreakdown
)

data class RoommateRequest(
    val id: Int,
    val sender_id: Int,
    val receiver_id: Int,
    val status: String,
    val created_at: String,
    val name: String,
    val email: String,
    val gender: String,
    val hostel: String,
    val room_preference: String,
    val bio: String?
)

data class RequestsResponse(
    val incoming: List<RoommateRequest>,
    val sent: List<RoommateRequest>
)

data class ChatMessage(
    val id: Int,
    val sender_id: Int,
    val receiver_id: Int,
    val message: String,
    val created_at: String
)

data class ExpenseSplit(
    val user_id: Int,
    val share: Double,
    val is_paid: Int, // 0 or 1
    val name: String
)

data class Expense(
    val id: Int,
    val creator_id: Int,
    val title: String,
    val amount: Double,
    val category: String,
    val created_at: String,
    val creator_name: String,
    val my_share: Double,
    val my_status: Int, // 0 or 1
    val splits: List<ExpenseSplit> = emptyList()
)

data class CategoryTotal(
    val category: String,
    val total: Double
)

data class DebtorDetail(
    val split_id: Int,
    val debtor_name: String,
    val share: Double,
    val title: String,
    val created_at: String
)

data class CreditorDetail(
    val split_id: Int,
    val creditor_name: String,
    val share: Double,
    val title: String,
    val created_at: String
)

data class ExpenseSummary(
    val categoryBreakdown: List<CategoryTotal>,
    val youAreOwed: List<DebtorDetail>,
    val youOwe: List<CreditorDetail>,
    val totalOwed: Double,
    val totalOwe: Double
)

data class Review(
    val id: Int,
    val cleanliness: Int,
    val respect: Int,
    val timeliness: Int,
    val noise: Int,
    val comment: String?,
    val created_at: String,
    val reviewer_name: String
)

data class ReviewSummary(
    val averageCleanliness: Double,
    val averageRespect: Double,
    val averageTimeliness: Double,
    val averageNoise: Double,
    val totalReviews: Int,
    val reviews: List<Review>
)

data class OkResponse(
    val ok: Boolean,
    val message: String
)

data class BioResponse(
    val bio: String
)

data class ChatbotResponse(
    val reply: String
)

data class RiskFactorReport(
    val userId: Int,
    val name: String,
    val fakeRiskScore: Double,
    val riskLevel: String,
    val factors: List<String>
)

data class Hostel(
    val id: String,
    val label: String
)

data class HostelsConfig(
    val male: List<Hostel>,
    val female: List<Hostel>
)

data class College(
    val id: String,
    val name: String,
    val city: String,
    val hostels: HostelsConfig
)

// ==================== DASHBOARD MODELS ====================

data class Notice(
    val id: Int,
    val title: String,
    val content: String,
    val hostel: String,
    val type: String,
    val created_at: String
)

data class Event(
    val id: Int,
    val name: String,
    val description: String?,
    val date: String,
    val time: String,
    val location: String,
    val type: String,
    val organizer: String?
)

data class MarketplaceItem(
    val id: Int,
    val seller_id: Int,
    val seller_name: String?,
    val title: String,
    val price: Double,
    val category: String,
    val hostel: String,
    val description: String?,
    val is_sold: Int,
    val created_at: String
)

data class DashboardSummary(
    val profileCompletion: Int,
    val userName: String,
    val college: String,
    val hostel: String,
    val gender: String,
    val topMatches: List<Profile>,
    val newRequestsCount: Int,
    val notices: List<Notice>,
    val events: List<Event>,
    val marketplace: List<MarketplaceItem>,
    val expenseBreakdown: List<CategoryTotal>,
    val expenseTotal: Int,
    val aiSuggestion: String?
)
