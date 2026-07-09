package com.example.homigo.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homigo.data.repository.HomigoRepository
import com.example.homigo.ui.theme.HomigoTheme
import kotlinx.coroutines.launch

private val allColleges = listOf(
    "Lovely Professional University",
    "Thapar Institute of Engineering & Technology",
    "Indian Institute of Technology, Delhi (IITD)",
    "Delhi University (DU)",
    "BITS Pilani, Pilani",
    "Birla Institute of Technology and Science - [BITS], Hyderabad",
    "Indian Institute of Technology - [IIIT], Allahabad",
    "Birla Institute of Technology and Science - [BITS], South Goa",
    "National Institute of Technology - [NIT], Kurukshetra",
    "The LNM Institute of Information Technology - [LNMIIT], Jaipur",
    "Dhirubhai Ambani University, Gandhinagar",
    "International Institute of Information Technology - [IIIT-B], Bangalore",
    "Birla Institute of Technology - [BIT Mesra], Ranchi",
    "Vishwakarma Institute of Technology, Pune",
    "Vellore Institute of Technology - [VIT University], Vellore",
    "P.S.G College of Technology - [PSGCT], Coimbatore",
    "R V College of Engineering - [RVCE], Bangalore",
    "Harcourt Butler Technological University - [HBTU], Kanpur",
    "Symbiosis International University - [SIU], Pune",
    "BMS College of Engineering - [BMSCE], Bangalore",
    "Shiv Nadar University, Chennai",
    "Amrita Vishwa Vidyapeetham, Coimbatore",
    "SRM Institute of Science and Technology - [SRMIST], Chennai",
    "MKSSS's Cummins College of Engineering for Women, Pune",
    "Vallurupalli Nageswara Rao Vignana Jyothi Institute of Engineering and Technology - [VNR VJIET], Hyderabad",
    "Ramaiah Institute of Technology - [RIT], Bangalore",
    "PES University - [PESU], Bangalore",
    "Chennai Institute of Technology - [CIT], Chennai",
    "Chaitanya Bharathi Institute of Technology - [CBIT], Hyderabad",
    "Rajalakshmi Engineering College - [REC], Chennai",
    "Dwarkadas J Sanghvi College of Engineering - [DJSCE], Mumbai",
    "Narsee Monjee Institute of Management Studies - [NMIMS Deemed to be University], Mumbai",
    "Manipal Academy of Higher Education - [MAHE], Manipal",
    "Sri Krishna College of Engineering and Technology - [SKCET], Coimbatore",
    "Maharaja Surajmal Institute of Technology - [MSIT], New Delhi",
    "Jaypee Institute of Information Technology University - [JIIT], Noida",
    "International Institute of Information Technology - [I²IT], Pune",
    "CMR Institute of Technology - [CMRIT], Bangalore",
    "D. Y. Patil College of Engineering, Pune",
    "Siddaganga Institute of Technology - [SIT], Tumkur",
    "Manipal University - [MUJ], Jaipur",
    "Pune Institute of Computer Technology- [PICT], Pune",
    "Dayananda Sagar College of Engineering - [DSCE], Bangalore",
    "SRM University, Amaravathi",
    "Sardar Patel Institute of Technology - [SPIT], Mumbai",
    "Siksha 'O' Anusandhan University - [SOA], Bhubaneswar",
    "Nitte Meenakshi Institute of Technology - [NMIT] (Deemed to be University), Bangalore",
    "Gokaraju Rangaraju Institute of Engineering and Technology -  [GRIET], Hyderabad",
    "Amity University, Noida",
    "PSG Institute of Technology and Applied Research - [PSG iTech], Coimbatore",
    "Madan Mohan Malaviya University of Technology - [MMMUT], Gorakhpur",
    "UPES, Dehradun",
    "Kumaraguru College of Technology - [KCT], Coimbatore",
    "Pimpri Chinchwad College of Engineering - [PCCOE], Pune",
    "AISSMS Institute of Information Technology, Pune",
    "Pandit Deendayal Energy University - [PDEU], Gandhinagar",
    "AISSMS College of Engineering - [AISSMSCOE], Pune",
    "Gayatri Vidya Parishad College of Engineering, Visakhapatnam",
    "Woxsen University, Hyderabad",
    "CVR College of Engineering, Ibrahimpatnam, Rangareddy",
    "New Horizon College of Engineering - [NHCE], Bangalore",
    "Yeshwantrao Chavan College of Engineering - [YCCE], Nagpur",
    "Vishwakarma Institute of Information Technology - [VIIT], Pune",
    "Thadomal Shahani Engineering College - [TSEC], Mumbai",
    "Rajalakshmi Institute of Technology - [RIT], Chennai",
    "Shri Vishnu Engineering College for Women - [SVECW], Bhimavaram",
    "G Narayanamma Institute of Technology and Science - [GNITS], Hyderabad",
    "Bangalore Institute of Technology - [BIT], Bangalore",
    "Vasavi College of Engineering, Hyderabad",
    "Vardhaman College of Engineering - [VCE], Hyderabad",
    "Karpagam College of Engineering - [KCE], Coimbatore",
    "Dr. D. Y. Patil Institute of Technology - [DYPIT] Pimpri, Pune",
    "RVR and JC College of Engineering, Guntur",
    "Keshav Memorial Institute of Technology - [KMIT], Hyderabad",
    "Maharaja Agrasen Institute of Technology - [MAIT], New Delhi",
    "Vivekanand Education Society Institute of Technology - [VESIT], Mumbai",
    "JSPM'S Rajarshi Shahu College of Engineering - [RSCOE] Tathawade, Pune",
    "Madras Institute of Technology- [MIT], Chennai",
    "JSS Science and Technology University -[JSSSTU], Mysore",
    "North Eastern Regional Institute of Science and Technology - [NERIST], Itanagar",
    "Chandigarh University - [CU], Mohali",
    "Marathwada Mitra Mandal's College of Engineering, Pune",
    "Sri Venkateswara College of Engineering - [SVCE], Sriperumbudur",
    "KLE Technological University, Hubli",
    "Fr. C. Rodrigues Institute of Technology - [FCRIT], Navi Mumbai",
    "The National Institute of Engineering - [NIE], Mysore",
    "Institute of Chemical Technology - [ICT], Mumbai",
    "Fr. Conceicao Rodrigues College of Engineering - [CRCE], Mumbai",
    "Sant Longowal Institute of Engineering and Technology - [SLIET], Sangrur",
    "Parul University, Vadodara",
    "Sagi Ramakrishnam Raju Engineering College - [SRKR ], Bhimavaram",
    "SDM College of Engineering and Technology - [SDMCET], Dharwad"
)

private val lpuSchools = listOf(
    "School of Computer Science and Engineering",
    "School of Computing and Artificial Intelligence",
    "School of AI and Emerging Technologies",
    "Lovely School of Architecture & Design",
    "School of Bio Engineering and Biosciences",
    "School of Civil Engineering",
    "School of Computer Applications",
    "School of Electronics and Electrical Engineering",
    "School of Liberal and Creative Arts (Journalism and Mass Communication)",
    "School of Chemical Engineering and Physical Sciences",
    "School of Agriculture",
    "School of Liberal and Creative Arts (Fine Arts)",
    "School of Polytechnic",
    "School of Law",
    "School of Mechanical Engineering",
    "School of Design (Multimedia)",
    "School of Design (Interior & Product Design)",
    "Mittal School of Business",
    "School of Hotel Management and Tourism",
    "School of Design (Fashion Design & Technology)",
    "School of Liberal and Creative Arts (Film, Theatre and Music)",
    "School of Liberal and Creative Arts (Social Sciences & Languages)",
    "School of Education",
    "School of Education (Physical Education)",
    "School of Pharmaceutical Sciences",
    "School of Allied and Healthcare Sciences"
)

private val indiaStates = listOf(
    "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
    "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand",
    "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur",
    "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab",
    "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
    "Uttar Pradesh", "Uttarakhand", "West Bengal",
    "Delhi", "Jammu and Kashmir", "Ladakh", "Puducherry"
)


@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnboardingWizardScreen(
    onOnboardingComplete: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val tokenState = HomigoRepository.token.collectAsState()
    val isAlreadyLoggedIn = !tokenState.value.isNullOrBlank()

    var currentStep by remember { mutableStateOf(1) }
    var gender by remember { mutableStateOf("male") } // "male" or "female"

    // Onboarding Form States
    var selectedCollege by remember { mutableStateOf(allColleges.first()) }
    var selectedHostel by remember { mutableStateOf("BH-1") }
    var roomPreference by remember { mutableStateOf("2-seater") }

    // Basic Account Details
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Academic Details
    var selectedCourse by remember { mutableStateOf("Choose Course") }
    var selectedBranch by remember { mutableStateOf("Choose Branch") }
    var selectedYear by remember { mutableStateOf("1st") }
    var selectedSemester by remember { mutableStateOf(1) }
    var rollNumber by remember { mutableStateOf("") }
    var selectedSchool by remember { mutableStateOf("Choose School") }

    // Accommodation Setup
    var lookingFor by remember { mutableStateOf("Roommate") }
    var preferredHostel by remember { mutableStateOf("") }
    var currentHostel by remember { mutableStateOf("") }
    var currentRoomNumber by remember { mutableStateOf("") }
    var moveInDate by remember { mutableStateOf("") }

    // Lifestyle Preferences
    var foodPref by remember { mutableStateOf("") }
    var sleepSchedule by remember { mutableStateOf("") }
    var studyHabits by remember { mutableStateOf("") }
    var cleanliness by remember { mutableStateOf("") }
    var smokingPref by remember { mutableStateOf("") }
    var drinkingPref by remember { mutableStateOf("") }
    var guestsPref by remember { mutableStateOf("") }
    var wakeUpTime by remember { mutableStateOf("") }
    var roomEnvironment by remember { mutableStateOf("") }
    var personalityType by remember { mutableStateOf("") }
    var dailyRoutine by remember { mutableStateOf("") }
    var workStyle by remember { mutableStateOf("") }

    // Interests, Bio, Deal Breakers & Room Purpose
    var selectedInterests by remember { mutableStateOf(setOf<String>()) }
    var selectedDealBreakers by remember { mutableStateOf(setOf<String>()) }
    var selectedRoomPurposes by remember { mutableStateOf(setOf<String>()) }
    var monthlyBudget by remember { mutableStateOf("₹4000 — ₹6000") }
    var selectedLanguages by remember { mutableStateOf(setOf<String>()) }
    var hometownState by remember { mutableStateOf("Choose State") }
    var foreignNational by remember { mutableStateOf(false) }
    var userBio by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Auto-update hostel based on gender when entering step 4
    LaunchedEffect(gender, selectedCollege) {
        if (selectedCollege == "Lovely Professional University") {
            selectedHostel = if (gender == "female") "GH-1" else "BH-1"
        } else {
            selectedHostel = if (gender == "female") "Kailash Hostel" else "Aravali Hostel"
        }
    }

    // Keep theme as neutral general blue (male) during steps 1 to 10. Only on step 11 we apply the selected gender's theme!
    val currentThemeGender = if (currentStep < 11) "male" else gender
    val pastelBackgroundColor = Color(0xFFF8FAFC)
    val cardSelectedColor = Color(0xFFC8E6C9)

    HomigoTheme(gender = currentThemeGender) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = pastelBackgroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Top Progress indicator
                if (currentStep > 1) {
                    val animatedProgress by animateFloatAsState(
                        targetValue = currentStep / 11f,
                        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                        label = "progressAnimation"
                    )
                    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                if (currentStep > 1) {
                                    if (currentStep == 6 && isAlreadyLoggedIn) {
                                        currentStep = 4
                                    } else {
                                        currentStep--
                                    }
                                }
                            }) {
                                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                            Text(
                                text = "Step $currentStep of 11",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF2563EB)
                            )
                            Box(modifier = Modifier.size(48.dp)) // Spacer
                        }
                        LinearProgressIndicator(
                            progress = animatedProgress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF2563EB),
                            trackColor = Color(0xFF2563EB).copy(alpha = 0.1f)
                        )
                    }
                }

                // Main screen transitions container
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AnimatedContent(
                        targetState = currentStep,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally(animationSpec = tween(400)) { width -> width } + fadeIn() with
                                        slideOutHorizontally(animationSpec = tween(400)) { width -> -width } + fadeOut()
                            } else {
                                slideInHorizontally(animationSpec = tween(400)) { width -> -width } + fadeIn() with
                                        slideOutHorizontally(animationSpec = tween(400)) { width -> width } + fadeOut()
                            }
                        }
                    ) { targetStep ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            when (targetStep) {
                                1 -> StepWelcome(onStart = { currentStep = 2 }, onNavigateToLogin = onNavigateToLogin)
                                2 -> StepGenderSelection(gender = gender, onGenderChanged = { gender = it }, onNext = { currentStep = 3 })
                                3 -> StepCollegeSelection(selectedCollege = selectedCollege, onCollegeSelected = { selectedCollege = it }, onNext = { currentStep = 4 })
                                4 -> StepHostelSelection(selectedCollege = selectedCollege, gender = gender, selectedHostel = selectedHostel, onHostelSelected = { selectedHostel = it }, onNext = {
                                    if (isAlreadyLoggedIn) {
                                        currentStep = 6
                                    } else {
                                        currentStep = 5
                                    }
                                })
                                5 -> StepBasicDetails(fullName = fullName, onNameChanged = { fullName = it }, email = email, onEmailChanged = { email = it }, phone = phone, onPhoneChanged = { phone = it }, password = password, onPasswordChanged = { password = it }, confirmPassword = confirmPassword, onConfirmChanged = { confirmPassword = it }, onNext = { currentStep = 6 })
                                6 -> StepAcademicDetails(selectedCollege = selectedCollege, course = selectedCourse, onCourseChanged = { selectedCourse = it }, branch = selectedBranch, onBranchChanged = { selectedBranch = it }, year = selectedYear, onYearChanged = { selectedYear = it }, semester = selectedSemester, onSemesterChanged = { selectedSemester = it }, selectedSchool = selectedSchool, onSchoolChanged = { selectedSchool = it }, onNext = { currentStep = 7 })
                                7 -> StepAccommodationSetup(gender = gender, selectedCollege = selectedCollege, lookingFor = lookingFor, onLookingForChanged = { lookingFor = it }, preferredHostel = preferredHostel, onPreferredChanged = { preferredHostel = it }, currentHostel = currentHostel, onCurrentChanged = { currentHostel = it }, roomNumber = currentRoomNumber, onRoomChanged = { currentRoomNumber = it }, moveInDate = moveInDate, onMoveInChanged = { moveInDate = it }, onNext = { currentStep = 8 })
                                8 -> StepLifestylePreferences(
                                    foodPref = foodPref, onFoodChanged = { foodPref = it },
                                    sleepSchedule = sleepSchedule, onSleepChanged = { sleepSchedule = it },
                                    studyHabits = studyHabits, onStudyChanged = { studyHabits = it },
                                    cleanliness = cleanliness, onCleanlinessChanged = { cleanliness = it },
                                    smoking = smokingPref, onSmokingChanged = { smokingPref = it },
                                    drinking = drinkingPref, onDrinkingChanged = { drinkingPref = it },
                                    guests = guestsPref, onGuestsChanged = { guestsPref = it },
                                    wakeUpTime = wakeUpTime, onWakeUpChanged = { wakeUpTime = it },
                                    roomEnvironment = roomEnvironment, onRoomEnvChanged = { roomEnvironment = it },
                                    personalityType = personalityType, onPersonalityChanged = { personalityType = it },
                                    dailyRoutine = dailyRoutine, onDailyRoutineChanged = { dailyRoutine = it },
                                    workStyle = workStyle, onWorkStyleChanged = { workStyle = it },
                                    onNext = { currentStep = 9 }
                                )
                                9 -> StepInterestsSelection(
                                    selectedInterests = selectedInterests, onInterestsChanged = { selectedInterests = it },
                                    selectedDealBreakers = selectedDealBreakers, onDealBreakersChanged = { selectedDealBreakers = it },
                                    selectedRoomPurposes = selectedRoomPurposes, onRoomPurposesChanged = { selectedRoomPurposes = it },
                                    onNext = { currentStep = 10 }
                                )
                                10 -> StepBudgetAndBio(selectedLanguages = selectedLanguages, onLanguagesChanged = { selectedLanguages = it }, hometown = hometownState, onHometownChanged = { hometownState = it }, isForeign = foreignNational, onForeignChanged = { foreignNational = it }, bio = userBio, onBioChanged = { userBio = it }, onNext = { currentStep = 11 })
                                11 -> StepAICompatibilityCalculation(
                                    isLoading = isLoading,
                                    errorMessage = errorMessage,
                                    onComplete = {
                                        isLoading = true
                                        errorMessage = null
                                        coroutineScope.launch {
                                            try {
                                                // 1. Register User if not already logged in
                                                if (!isAlreadyLoggedIn) {
                                                    HomigoRepository.register(
                                                        mapOf(
                                                            "name" to fullName,
                                                            "email" to email,
                                                            "password" to password,
                                                            "gender" to gender
                                                        )
                                                    )
                                                }
                                                
                                                // 2. Submit Profile Details
                                                val profilePayload = mapOf(
                                                    "college" to selectedCollege,
                                                    "hostel" to selectedHostel,
                                                    "room_preference" to roomPreference,
                                                    "course" to selectedCourse,
                                                    "branch" to selectedBranch,
                                                    "year" to selectedYear,
                                                    "semester" to selectedSemester,
                                                    "roll_number" to rollNumber,
                                                    "school" to selectedSchool,
                                                    "looking_for" to lookingFor,
                                                    "preferred_hostel" to preferredHostel,
                                                    "current_hostel" to currentHostel,
                                                    "room_number" to currentRoomNumber,
                                                    "move_in_date" to moveInDate,
                                                    "interests" to selectedInterests.toList(),
                                                    "languages" to selectedLanguages.toList(),
                                                    "hometown" to hometownState,
                                                    "budget_min" to if (monthlyBudget.contains("2000")) 2000 else if (monthlyBudget.contains("4000")) 4000 else 6000,
                                                    "budget_max" to if (monthlyBudget.contains("2000")) 4000 else if (monthlyBudget.contains("4000")) 6000 else 15000,
                                                    "sleep_schedule" to (if (sleepSchedule.contains("Early")) "early_bird" else if (sleepSchedule.contains("Night")) "night_owl" else "flexible"),
                                                    "smoking" to (if (smokingPref.contains("Never")) "no" else if (smokingPref.contains("Occasionally")) "occasionally" else "yes"),
                                                    "drinking" to (if (drinkingPref.contains("Never")) "no" else if (drinkingPref.contains("Occasionally")) "occasionally" else "yes"),
                                                    "food_preference" to (if (foodPref.contains("Vegetarian")) "veg" else if (foodPref.contains("Non-Vegetarian") || foodPref.contains("Non Vegetarian")) "non_veg" else "any"),
                                                    "cleanliness" to (if (cleanliness.contains("Very")) "high" else if (cleanliness.contains("Moderately")) "moderate" else "low"),
                                                    "pets" to "no",
                                                    "guests" to (if (guestsPref.contains("Frequently")) "frequent" else if (guestsPref.contains("Occasionally")) "rare" else "no"),
                                                    "bio" to userBio,
                                                    "deal_breakers" to selectedDealBreakers.toList(),
                                                    "room_purpose" to selectedRoomPurposes.toList(),
                                                    "wake_up_time" to wakeUpTime,
                                                    "study_style" to studyHabits,
                                                    "room_environment" to roomEnvironment,
                                                    "personality_type" to personalityType,
                                                    "daily_routine" to dailyRoutine,
                                                    "work_style" to workStyle
                                                )
                                                HomigoRepository.updateProfile(profilePayload)
                                                isLoading = false
                                                onOnboardingComplete()
                                            } catch (e: Exception) {
                                                isLoading = false
                                                errorMessage = HomigoRepository.getErrorMessage(e)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== WIZARD STEP LAYOUTS ====================

@Composable
fun PremiumSelectedCard(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    // Animate scale on selection
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.04f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    // Shine animation offset (runs continuously if selected)
    val infiniteTransition = rememberInfiniteTransition(label = "shine")
    val shineOffset by if (selected) {
        infiniteTransition.animateFloat(
            initialValue = -300f,
            targetValue = 900f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shineOffset"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    Card(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            }
        ),
        border = if (selected) {
            BorderStroke(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.primary
                    )
                )
            )
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        }
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            content()

            // Shining shimmer overlay effect just like website hovers
            if (selected) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0f),
                            Color.White.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0f)
                        ),
                        start = androidx.compose.ui.geometry.Offset(shineOffset - 100f, 0f),
                        end = androidx.compose.ui.geometry.Offset(shineOffset + 100f, size.height)
                    )
                    drawRect(brush = brush)
                }
            }
        }
    }
}

@Composable
private fun StepWelcome(onStart: () -> Unit, onNavigateToLogin: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Homigo Logo",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Welcome to Homigo 👋",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Find your perfect roommate &\nmake hostel life easier.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onStart,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Get Started", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Already have an account? ")
            TextButton(onClick = onNavigateToLogin) {
                Text("Log In", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AISparkleIllustration() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val rotateAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(100.dp)
            .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
    ) {
        // Soft blue glowing background
        Canvas(modifier = Modifier.size(80.dp)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF2563EB).copy(alpha = 0.15f), Color.Transparent),
                    center = Offset(size.width / 2, size.height / 2),
                    radius = size.width / 2
                )
            )
        }

        // Animated sparks
        Canvas(modifier = Modifier.size(60.dp).graphicsLayer(rotationZ = rotateAngle)) {
            val w = size.width
            val h = size.height

            // Center large sparkle
            drawSparkle(Offset(w * 0.5f, h * 0.5f), 18f, Color(0xFF2563EB))

            // Secondary sparkle top-right
            drawSparkle(Offset(w * 0.75f, h * 0.25f), 10f, Color(0xFF3B82F6))

            // Third sparkle bottom-left
            drawSparkle(Offset(w * 0.22f, h * 0.72f), 8f, Color(0xFF60A5FA))
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSparkle(center: Offset, size: Float, color: Color) {
    val path = Path().apply {
        moveTo(center.x, center.y - size)
        quadraticBezierTo(center.x, center.y, center.x + size, center.y)
        quadraticBezierTo(center.x, center.y, center.x, center.y + size)
        quadraticBezierTo(center.x, center.y, center.x - size, center.y)
        quadraticBezierTo(center.x, center.y, center.x, center.y - size)
        close()
    }
    drawPath(path = path, color = color)
}

@Composable
fun GenderIllustration(isFemale: Boolean, isSelected: Boolean) {
    val tint = if (isSelected) Color(0xFF2563EB) else Color(0xFF64748B)
    Canvas(modifier = Modifier.size(40.dp)) {
        val w = size.width
        val h = size.height
        
        // Draw head
        drawCircle(
            color = tint,
            radius = w * 0.28f,
            center = Offset(w * 0.5f, h * 0.35f)
        )
        
        // Draw shoulders/body silhouette
        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.95f)
            quadraticBezierTo(w * 0.15f, h * 0.7f, w * 0.35f, h * 0.7f)
            lineTo(w * 0.65f, h * 0.7f)
            quadraticBezierTo(w * 0.85f, h * 0.7f, w * 0.85f, h * 0.95f)
            close()
        }
        drawPath(path = path, color = tint)

        // Draw gender details if needed
        if (isFemale) {
            // Draw longer hair silhouette behind head
            drawArc(
                color = tint,
                startAngle = 140f,
                sweepAngle = 260f,
                useCenter = true,
                topLeft = Offset(w * 0.16f, h * 0.08f),
                size = androidx.compose.ui.geometry.Size(w * 0.68f, h * 0.52f)
            )
        } else {
            // Male collar or hair spike
            val collarPath = Path().apply {
                moveTo(w * 0.45f, h * 0.7f)
                lineTo(w * 0.5f, h * 0.78f)
                lineTo(w * 0.55f, h * 0.7f)
                close()
            }
            drawPath(path = collarPath, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun PremiumGenderSelectionCard(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.03f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "genderCardScale"
    )

    Card(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .fillMaxWidth()
            .height(80.dp)
            .shadow(
                elevation = if (selected) 4.dp else 1.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false
            )
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, color = Color(0xFF2563EB).copy(alpha = 0.1f))
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFFEFF6FF) else Color.White
        ),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) Color(0xFF2563EB) else Color(0xFFE2E8F0)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                content()
            }

            // Small animated checkmark in top-right corner
            androidx.compose.animation.AnimatedVisibility(
                visible = selected,
                enter = scaleIn(animationSpec = spring()) + fadeIn(),
                exit = scaleOut() + fadeOut(),
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 10.dp, end = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color(0xFF2563EB), CircleShape)
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StepGenderSelection(gender: String, onGenderChanged: (String) -> Unit, onNext: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "btnScale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // AI themed graphics/illustration
        AISparkleIllustration()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tell us about yourself",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                textAlign = TextAlign.Center
            )
            Text(
                text = "This helps our AI recommend the most compatible roommates for you.",
                fontSize = 15.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Male Selection Card
        PremiumGenderSelectionCard(
            selected = gender == "male",
            onClick = { onGenderChanged("male") }
        ) {
            GenderIllustration(isFemale = false, isSelected = gender == "male")
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Male Student",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (gender == "male") Color(0xFF2563EB) else Color(0xFF1E293B)
            )
        }

        // Female Selection Card
        PremiumGenderSelectionCard(
            selected = gender == "female",
            onClick = { onGenderChanged("female") }
        ) {
            GenderIllustration(isFemale = true, isSelected = gender == "female")
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Female Student",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (gender == "female") Color(0xFF2563EB) else Color(0xFF1E293B)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Continue Button
        AnimatedVisibility(
            visible = gender.isNotBlank(),
            enter = slideInVertically { it / 2 } + fadeIn(),
            exit = slideOutVertically { it / 2 } + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .graphicsLayer(scaleX = buttonScale, scaleY = buttonScale)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                        )
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = rememberRipple(bounded = true, color = Color.White),
                        onClick = onNext
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Continue",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumCollegeCard(
    collegeName: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.03f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "collegeCardScale"
    )

    val city = when {
        collegeName.contains("Lovely", true) -> "Phagwara, Punjab"
        collegeName.contains("Thapar", true) -> "Patiala, Punjab"
        collegeName.contains("VIT", true) -> "Vellore, Tamil Nadu"
        collegeName.contains("SRM", true) -> "Chennai, Tamil Nadu"
        collegeName.contains("Delhi University", true) || collegeName.contains("IIT, Delhi", true) || collegeName.contains("IIT Delhi", true) -> "New Delhi"
        collegeName.contains("IIT Bombay", true) -> "Mumbai, Maharashtra"
        collegeName.contains("IIT Kanpur", true) -> "Kanpur, Uttar Pradesh"
        collegeName.contains("Manipal", true) -> "Manipal, Karnataka"
        collegeName.contains("Chandigarh", true) -> "Mohali, Punjab"
        collegeName.contains("Amity", true) -> "Noida, Uttar Pradesh"
        collegeName.contains("Bennett", true) -> "Greater Noida, UP"
        collegeName.contains("Galgotias", true) -> "Greater Noida, UP"
        collegeName.contains("KIET", true) -> "Ghaziabad, UP"
        collegeName.contains("KIIT", true) -> "Bhubaneswar, Odisha"
        else -> "India"
    }

    val count = when {
        collegeName.contains("Lovely", true) -> "35k+ Students"
        collegeName.contains("Thapar", true) -> "12k+ Students"
        collegeName.contains("VIT", true) -> "28k+ Students"
        collegeName.contains("SRM", true) -> "20k+ Students"
        collegeName.contains("Delhi University", true) -> "50k+ Students"
        collegeName.contains("IIT Delhi", true) || collegeName.contains("IIT Bombay", true) -> "10k+ Students"
        collegeName.contains("Chandigarh", true) -> "30k+ Students"
        else -> "8k+ Students"
    }

    val initial = collegeName.trim().firstOrNull() ?: 'U'
    val gradientColors = when (initial) {
        'L' -> listOf(Color(0xFFEA580C), Color(0xFFF97316))
        'T' -> listOf(Color(0xFF0284C7), Color(0xFF0EA5E9))
        'V' -> listOf(Color(0xFF16A34A), Color(0xFF22C55E))
        'S' -> listOf(Color(0xFFE11D48), Color(0xFFF43F5E))
        'I' -> listOf(Color(0xFF2563EB), Color(0xFF3B82F6))
        'D' -> listOf(Color(0xFFDB2777), Color(0xFFEC4899))
        'M' -> listOf(Color(0xFF0F766E), Color(0xFF14B8A6))
        'C' -> listOf(Color(0xFFD97706), Color(0xFFF59E0B))
        else -> listOf(Color(0xFF4F46E5), Color(0xFF6366F1))
    }

    Card(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .fillMaxWidth()
            .shadow(
                elevation = if (selected) 4.dp else 1.dp,
                shape = RoundedCornerShape(20.dp),
                clip = false
            )
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, color = Color(0xFF2563EB).copy(alpha = 0.1f))
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFFEFF6FF) else Color.White
        ),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) Color(0xFF2563EB) else Color(0xFFE2E8F0)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // University logo / letter avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.linearGradient(colors = gradientColors),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial.toString(),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = collegeName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        maxLines = 1
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = city,
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Student Count",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = count,
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            // Small animated checkmark in top-right corner
            androidx.compose.animation.AnimatedVisibility(
                visible = selected,
                enter = scaleIn(animationSpec = spring()) + fadeIn(),
                exit = scaleOut() + fadeOut(),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color(0xFF2563EB), CircleShape)
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StepCollegeSelection(selectedCollege: String, onCollegeSelected: (String) -> Unit, onNext: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredColleges = remember(searchQuery) {
        if (searchQuery.isBlank()) allColleges else allColleges.filter { it.contains(searchQuery, ignoreCase = true) }
    }
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Select College", 
                fontSize = 26.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color(0xFF1E293B)
            )
            Text(
                text = "We'll recommend roommates from your campus.",
                fontSize = 15.sp,
                color = Color(0xFF64748B)
            )
        }

        // Improved Premium Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { 
                Text(
                    text = "Search your university...", 
                    color = Color(0xFF94A3B8),
                    fontSize = 15.sp
                ) 
            },
            leadingIcon = { 
                Icon(
                    imageVector = Icons.Default.Search, 
                    contentDescription = "Search",
                    tint = if (isFocused) Color(0xFF2563EB) else Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                ) 
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear, 
                            contentDescription = "Clear",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF2563EB),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedLabelColor = Color(0xFF2563EB),
                unfocusedLabelColor = Color(0xFF94A3B8),
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (isFocused) 4.dp else 1.dp,
                    shape = RoundedCornerShape(24.dp),
                    clip = false
                )
                .onFocusChanged { isFocused = it.isFocused }
        )

        // Colleges List
        Column(
            modifier = Modifier
                .height(320.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            filteredColleges.forEach { college ->
                val isSelected = selectedCollege == college
                PremiumCollegeCard(
                    collegeName = college,
                    selected = isSelected,
                    onClick = { onCollegeSelected(college) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Continue Button
        androidx.compose.animation.AnimatedVisibility(
            visible = selectedCollege.isNotBlank(),
            enter = slideInVertically { it / 2 } + fadeIn(),
            exit = slideOutVertically { it / 2 } + fadeOut()
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val buttonScale by animateFloatAsState(
                targetValue = if (isPressed) 0.95f else 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                label = "btnScale"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .graphicsLayer(scaleX = buttonScale, scaleY = buttonScale)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                        )
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = rememberRipple(bounded = true, color = Color.White),
                        onClick = onNext
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Continue",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumHostelCard(
    hostelName: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.03f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "hostelCardScale"
    )

    val subtitle = when {
        hostelName.startsWith("BH-") -> "Boys Hostel ${hostelName.substringAfter("BH-")}"
        hostelName.startsWith("GH-") -> "Girls Hostel ${hostelName.substringAfter("GH-")}"
        hostelName.contains("Hostel", true) -> "Campus Residence"
        else -> "Student Housing"
    }

    Card(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .fillMaxWidth()
            .height(100.dp)
            .shadow(
                elevation = if (selected) 4.dp else 1.dp,
                shape = RoundedCornerShape(20.dp),
                clip = false
            )
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, color = Color(0xFF2563EB).copy(alpha = 0.1f))
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFFEFF6FF) else Color.White
        ),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) Color(0xFF2563EB) else Color(0xFFE2E8F0)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Hostel icon container with gradient background
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Brush.linearGradient(
                                colors = if (selected) {
                                    listOf(Color(0xFF2563EB), Color(0xFF3B82F6))
                                } else {
                                    listOf(Color(0xFF64748B), Color(0xFF94A3B8))
                                }
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Hostel Icon",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = hostelName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Small checkmark badge top-right
            androidx.compose.animation.AnimatedVisibility(
                visible = selected,
                enter = scaleIn(animationSpec = spring()) + fadeIn(),
                exit = scaleOut() + fadeOut(),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color(0xFF2563EB), CircleShape)
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StepHostelSelection(selectedCollege: String, gender: String, selectedHostel: String, onHostelSelected: (String) -> Unit, onNext: () -> Unit) {
    val hostels = remember(selectedCollege, gender) {
        if (selectedCollege == "Lovely Professional University") {
            if (gender == "female") (1..9).map { "GH-$it" } else (1..10).map { "BH-$it" }
        } else {
            if (gender == "female") listOf("Kailash Hostel", "Shivalik Hostel", "Himadri Hostel", "Girls Hostel A", "Girls Hostel B")
            else listOf("Aravali Hostel", "Jwalamukhi Hostel", "Karakoram Hostel", "Nilgiri Hostel", "Kumaon Hostel", "Girnar Hostel")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Select Hostel", 
                fontSize = 26.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color(0xFF1E293B)
            )
            Text(
                text = "Choose the hostel where you currently live.",
                fontSize = 15.sp,
                color = Color(0xFF64748B)
            )
        }

        // Hostels Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(340.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(hostels) { hostel ->
                val isSelected = selectedHostel == hostel
                PremiumHostelCard(
                    hostelName = hostel,
                    selected = isSelected,
                    onClick = { onHostelSelected(hostel) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Continue Button
        androidx.compose.animation.AnimatedVisibility(
            visible = selectedHostel.isNotBlank(),
            enter = slideInVertically { it / 2 } + fadeIn(),
            exit = slideOutVertically { it / 2 } + fadeOut()
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val buttonScale by animateFloatAsState(
                targetValue = if (isPressed) 0.95f else 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                label = "btnScale"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .graphicsLayer(scaleX = buttonScale, scaleY = buttonScale)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                        )
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = rememberRipple(bounded = true, color = Color.White),
                        onClick = onNext
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Continue",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AccountIllustration() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(90.dp)
            .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
    ) {
        Canvas(modifier = Modifier.size(80.dp)) {
            // Draw background soft gradient circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF2563EB).copy(alpha = 0.12f), Color.Transparent),
                    center = Offset(size.width / 2, size.height / 2),
                    radius = size.width / 2
                )
            )
        }

        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF2563EB), Color(0xFF3B82F6))
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Account Illustration",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

fun getPasswordStrength(password: String): PasswordStrength {
    if (password.isEmpty()) return PasswordStrength.EMPTY
    if (password.length < 6) return PasswordStrength.WEAK
    
    var hasLetter = false
    var hasDigit = false
    var hasSpecial = false
    
    for (char in password) {
        if (char.isLetter()) hasLetter = true
        else if (char.isDigit()) hasDigit = true
        else hasSpecial = true
    }
    
    return if (hasLetter && hasDigit && hasSpecial && password.length >= 8) {
        PasswordStrength.STRONG
    } else if (hasLetter && hasDigit) {
        PasswordStrength.MEDIUM
    } else {
        PasswordStrength.WEAK
    }
}

enum class PasswordStrength(val label: String, val color: Color, val progress: Float) {
    EMPTY("", Color.Transparent, 0.0f),
    WEAK("Weak", Color(0xFFEF4444), 0.33f),
    MEDIUM("Medium", Color(0xFFF59E0B), 0.66f),
    STRONG("Strong", Color(0xFF10B981), 1.0f)
}

@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable () -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = if (isFocused) Color(0xFF2563EB) else Color(0xFF64748B)) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF2563EB),
            unfocusedBorderColor = Color(0xFFE2E8F0),
            focusedLabelColor = Color(0xFF2563EB),
            unfocusedLabelColor = Color(0xFF64748B),
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isFocused) 3.dp else 1.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .onFocusChanged { isFocused = it.isFocused }
    )
}

@Composable
fun EyeIcon(visible: Boolean, tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        
        // Draw the main eye shape outline using paths
        val eyePath = Path().apply {
            moveTo(width * 0.1f, height * 0.5f)
                quadraticBezierTo(width * 0.5f, height * 0.15f, width * 0.9f, height * 0.5f)
            quadraticBezierTo(width * 0.5f, height * 0.85f, width * 0.1f, height * 0.5f)
            close()
        }
        
        // Draw outline path
        drawPath(
            path = eyePath,
            color = tint,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        )
        
        // Draw pupil circle
        drawCircle(
            color = tint,
            radius = width * 0.15f,
            center = Offset(width * 0.5f, height * 0.5f)
        )
        
        // Draw slash if not visible
        if (!visible) {
            drawLine(
                color = tint,
                start = Offset(width * 0.25f, height * 0.25f),
                end = Offset(width * 0.75f, height * 0.75f),
                strokeWidth = 2.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
}

@Composable
private fun StepBasicDetails(
    fullName: String, onNameChanged: (String) -> Unit,
    email: String, onEmailChanged: (String) -> Unit,
    phone: String, onPhoneChanged: (String) -> Unit,
    password: String, onPasswordChanged: (String) -> Unit,
    confirmPassword: String, onConfirmChanged: (String) -> Unit,
    onNext: () -> Unit
) {
    var validationError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val passwordStrength = remember(password) { getPasswordStrength(password) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AccountIllustration()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Create your account", 
                fontSize = 26.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color(0xFF1E293B)
            )
            Text(
                text = "Just a few details to personalize your experience.",
                fontSize = 15.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Full Name Field
        PremiumTextField(
            value = fullName,
            onValueChange = { onNameChanged(it); validationError = null },
            label = "Full Name",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person, 
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }
        )

        // Email Field
        PremiumTextField(
            value = email,
            onValueChange = { onEmailChanged(it); validationError = null },
            label = "Email Address",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email, 
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }
        )

        // Phone Field
        PremiumTextField(
            value = phone,
            onValueChange = { onPhoneChanged(it); validationError = null },
            label = "Phone Number",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Phone, 
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }
        )

        // Password Field
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PremiumTextField(
                value = password,
                onValueChange = { onPasswordChanged(it); validationError = null },
                label = "Password",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock, 
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        EyeIcon(
                            visible = passwordVisible, 
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )

            // Password strength indicator
            if (password.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { passwordStrength.progress },
                        color = passwordStrength.color,
                        trackColor = Color(0xFFE2E8F0),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                    Text(
                        text = passwordStrength.label,
                        color = passwordStrength.color,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Confirm Password Field
        PremiumTextField(
            value = confirmPassword,
            onValueChange = { onConfirmChanged(it); validationError = null },
            label = "Confirm Password",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock, 
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    EyeIcon(
                        visible = confirmPasswordVisible, 
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        if (validationError != null) {
            Text(
                text = validationError!!, 
                color = MaterialTheme.colorScheme.error, 
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        val isFormValid = fullName.isNotBlank() &&
                email.isNotBlank() && email.contains("@") && email.contains(".") &&
                phone.isNotBlank() && phone.length >= 10 &&
                password.isNotBlank() && password.length >= 6 &&
                confirmPassword == password

        Spacer(modifier = Modifier.height(16.dp))

        // Continue Button
        androidx.compose.animation.AnimatedVisibility(
            visible = isFormValid,
            enter = slideInVertically { it / 2 } + fadeIn(),
            exit = slideOutVertically { it / 2 } + fadeOut()
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val buttonScale by animateFloatAsState(
                targetValue = if (isPressed) 0.95f else 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                label = "btnScale"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .graphicsLayer(scaleX = buttonScale, scaleY = buttonScale)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                        )
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = rememberRipple(bounded = true, color = Color.White),
                        onClick = onNext
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Continue",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StepAcademicDetails(
    selectedCollege: String,
    course: String, onCourseChanged: (String) -> Unit,
    branch: String, onBranchChanged: (String) -> Unit,
    year: String, onYearChanged: (String) -> Unit,
    semester: Int, onSemesterChanged: (Int) -> Unit,
    selectedSchool: String, onSchoolChanged: (String) -> Unit,
    onNext: () -> Unit
) {
    var courseExpanded by remember { mutableStateOf(false) }
    var branchExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }
    var semExpanded by remember { mutableStateOf(false) }
    var schoolExpanded by remember { mutableStateOf(false) }

    val coursesList = listOf("B.Tech", "M.Tech", "MBA", "BCA", "MCA", "B.Sc", "BBA", "B.Com")
    val branchesList = listOf("Computer Science", "BioTech", "Mechanical", "ECE", "Civil", "Design")
    val yearsList = listOf("1st", "2nd", "3rd", "4th")
    val semestersList = listOf(1, 2, 3, 4, 5, 6, 7, 8)

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Academic Details", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))

        // 1. School Dropdown (on top if LPU)
        if (selectedCollege == "Lovely Professional University") {
            Text("School", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { schoolExpanded = !schoolExpanded }, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(if (selectedSchool == "Choose School") "Choose School" else selectedSchool, modifier = Modifier.weight(1f), maxLines = 1)
                        Text("▼")
                    }
                }
                DropdownMenu(
                    expanded = schoolExpanded,
                    onDismissRequest = { schoolExpanded = false },
                    modifier = Modifier.fillMaxWidth().height(260.dp)
                ) {
                    lpuSchools.forEach { s ->
                        DropdownMenuItem(text = { Text(s, maxLines = 1) }, onClick = { onSchoolChanged(s); schoolExpanded = false })
                    }
                }
            }
        }

        // 2. Course Dropdown
        Text("Course Domain", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { courseExpanded = !courseExpanded }, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(if (course == "Choose Course") "Choose Course" else course)
                    Text("▼")
                }
            }
            DropdownMenu(expanded = courseExpanded, onDismissRequest = { courseExpanded = false }, modifier = Modifier.fillMaxWidth()) {
                coursesList.forEach { c ->
                    DropdownMenuItem(text = { Text(c) }, onClick = { onCourseChanged(c); courseExpanded = false })
                }
            }
        }

        // 3. Branch Dropdown
        Text("Specialization/Branch", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { branchExpanded = !branchExpanded }, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(if (branch == "Choose Branch") "Choose Branch" else branch)
                    Text("▼")
                }
            }
            DropdownMenu(expanded = branchExpanded, onDismissRequest = { branchExpanded = false }, modifier = Modifier.fillMaxWidth()) {
                branchesList.forEach { b ->
                    DropdownMenuItem(text = { Text(b) }, onClick = { onBranchChanged(b); branchExpanded = false })
                }
            }
        }

        // 4. Year and Semester selector side-by-side
        Text("Academic Year & Semester", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(onClick = { yearExpanded = !yearExpanded }, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Year: ${year}")
                        Text("▼")
                    }
                }
                DropdownMenu(expanded = yearExpanded, onDismissRequest = { yearExpanded = false }) {
                    yearsList.forEach { y ->
                        DropdownMenuItem(text = { Text(y) }, onClick = { onYearChanged(y); yearExpanded = false })
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(onClick = { semExpanded = !semExpanded }, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Sem: ${semester}")
                        Text("▼")
                    }
                }
                DropdownMenu(expanded = semExpanded, onDismissRequest = { semExpanded = false }) {
                    semestersList.forEach { s ->
                        DropdownMenuItem(text = { Text(s.toString()) }, onClick = { onSemesterChanged(s); semExpanded = false })
                    }
                }
            }
        }

        val isAcademicValid = course.isNotBlank() && course != "Choose Course" &&
                branch.isNotBlank() && branch != "Choose Branch" &&
                year.isNotBlank() &&
                (if (selectedCollege == "Lovely Professional University") selectedSchool.isNotBlank() && selectedSchool != "Choose School" else true)

        Spacer(modifier = Modifier.height(16.dp))
        AnimatedVisibility(
            visible = isAcademicValid,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Button(
                onClick = onNext,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StepAccommodationSetup(
    gender: String,
    selectedCollege: String,
    lookingFor: String, onLookingForChanged: (String) -> Unit,
    preferredHostel: String, onPreferredChanged: (String) -> Unit,
    currentHostel: String, onCurrentChanged: (String) -> Unit,
    roomNumber: String, onRoomChanged: (String) -> Unit,
    moveInDate: String, onMoveInChanged: (String) -> Unit,
    onNext: () -> Unit
) {
    var prefExpanded by remember { mutableStateOf(false) }
    var currExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, yr, monthOfYear, dayOfMonth ->
                val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                onMoveInChanged("$dayOfMonth ${monthNames[monthOfYear]} $yr")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val hostels = remember(selectedCollege, gender) {
        if (selectedCollege == "Lovely Professional University") {
            if (gender == "female") (1..9).map { "GH-$it" } else (1..10).map { "BH-$it" }
        } else {
            if (gender == "female") listOf("Kailash Hostel", "Shivalik Hostel", "Himadri Hostel", "Girls Hostel A", "Girls Hostel B")
            else listOf("Aravali Hostel", "Jwalamukhi Hostel", "Karakoram Hostel", "Nilgiri Hostel", "Kumaon Hostel", "Girnar Hostel")
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Hostel Setup", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))

        Text("Looking For", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("Roommate", "Flatmate", "Both").forEach { opt ->
                val isSel = lookingFor == opt
                FilterChip(
                    selected = isSel,
                    onClick = { onLookingForChanged(opt) },
                    label = { Text(opt) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Preferred Hostel dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { prefExpanded = !prefExpanded }, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Preferred Hostel:${if (preferredHostel.isBlank()) "" else " $preferredHostel"}")
                    Text("▼")
                }
            }
            DropdownMenu(expanded = prefExpanded, onDismissRequest = { prefExpanded = false }, modifier = Modifier.fillMaxWidth()) {
                hostels.forEach { h ->
                    DropdownMenuItem(text = { Text(h) }, onClick = { onPreferredChanged(h); prefExpanded = false })
                }
            }
        }

        // Current Hostel dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { currExpanded = !currExpanded }, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Current Hostel:${if (currentHostel.isBlank()) "" else " $currentHostel"}")
                    Text("▼")
                }
            }
            DropdownMenu(expanded = currExpanded, onDismissRequest = { currExpanded = false }, modifier = Modifier.fillMaxWidth()) {
                hostels.forEach { h ->
                    DropdownMenuItem(text = { Text(h) }, onClick = { onCurrentChanged(h); currExpanded = false })
                }
            }
        }

        OutlinedTextField(
            value = roomNumber,
            onValueChange = onRoomChanged,
            label = { Text("Current Room Number (Optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() }
        ) {
            OutlinedTextField(
                value = moveInDate,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("📅 Expected Move-in Date") },
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        val isAccommodationValid = lookingFor.isNotBlank() &&
                preferredHostel.isNotBlank() &&
                currentHostel.isNotBlank() &&
                moveInDate.isNotBlank()

        Spacer(modifier = Modifier.height(16.dp))
        AnimatedVisibility(
            visible = isAccommodationValid,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Button(
                onClick = onNext,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StepLifestylePreferences(
    foodPref: String, onFoodChanged: (String) -> Unit,
    sleepSchedule: String, onSleepChanged: (String) -> Unit,
    studyHabits: String, onStudyChanged: (String) -> Unit,
    cleanliness: String, onCleanlinessChanged: (String) -> Unit,
    smoking: String, onSmokingChanged: (String) -> Unit,
    drinking: String, onDrinkingChanged: (String) -> Unit,
    guests: String, onGuestsChanged: (String) -> Unit,
    wakeUpTime: String, onWakeUpChanged: (String) -> Unit,
    roomEnvironment: String, onRoomEnvChanged: (String) -> Unit,
    personalityType: String, onPersonalityChanged: (String) -> Unit,
    dailyRoutine: String, onDailyRoutineChanged: (String) -> Unit,
    workStyle: String, onWorkStyleChanged: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Lifestyle Preferences", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("These details affect roommate compatibility. Please answer accurately.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(4.dp))

        @OptIn(ExperimentalLayoutApi::class)
        @Composable
        fun PreferenceSection(title: String, current: String, options: List<String>, onSelected: (String) -> Unit) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    options.forEach { opt ->
                        val isSelected = current == opt
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelected(opt) },
                            label = { 
                                Text(
                                    text = opt, 
                                    color = if (isSelected) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ) 
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = Color(0xFF2E7D32),
                                selectedBorderWidth = 2.dp,
                                borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                borderWidth = 1.dp
                            )
                        )
                    }
                }
            }
        }

        PreferenceSection("🌅 Sleep Schedule", sleepSchedule, listOf("🌅 Early Sleeper (Before 10 PM)", "🌃 Night Owl (After 12 AM)", "⏰ Flexible"), onSleepChanged)
        PreferenceSection("🌞 Wake-up Time", wakeUpTime, listOf("🌞 Before 6 AM", "⏰ 6–8 AM", "🌤 After 8 AM"), onWakeUpChanged)
        PreferenceSection("📚 Study Style", studyHabits, listOf("📚 Silent Study", "🎵 Study with Music", "👥 Group Study", "🌙 Late Night Study", "☀️ Morning Study"), onStudyChanged)
        PreferenceSection("🤫 Room Environment", roomEnvironment, listOf("🤫 Very Quiet", "🔊 Some Noise is Fine", "🎶 Background Music is Okay"), onRoomEnvChanged)
        PreferenceSection("✨ Cleanliness", cleanliness, listOf("✨ Very Clean", "🧹 Moderately Clean", "😅 Doesn't Matter Much"), onCleanlinessChanged)
        PreferenceSection("👥 Visitors", guests, listOf("🚫 No Visitors", "👥 Occasionally", "🎉 Frequently"), onGuestsChanged)
        PreferenceSection("🥗 Food", foodPref, listOf("🥗 Vegetarian", "🍗 Non-Vegetarian", "🥚 Eggetarian", "🌱 Vegan"), onFoodChanged)
        PreferenceSection("🚭 Smoking", smoking, listOf("🚭 Never", "🚬 Occasionally", "🚬 Regularly"), onSmokingChanged)
        PreferenceSection("🍹 Drinking", drinking, listOf("❌ Never", "🍹 Occasionally", "🍺 Socially"), onDrinkingChanged)
        PreferenceSection("😄 Personality", personalityType, listOf("😄 Extrovert", "😊 Introvert", "😌 Ambivert"), onPersonalityChanged)
        PreferenceSection("📅 Daily Routine", dailyRoutine, listOf("📅 Highly Organized", "⚖️ Balanced", "🌊 Go With the Flow"), onDailyRoutineChanged)
        PreferenceSection("💼 Work Style", workStyle, listOf("💻 Remote Internships", "📖 Academics First", "🚀 Startup Focus", "💼 Preparing for Placements", "📚 Higher Studies"), onWorkStyleChanged)

        val isLifestyleValid = foodPref.isNotBlank() &&
                sleepSchedule.isNotBlank() &&
                studyHabits.isNotBlank() &&
                cleanliness.isNotBlank() &&
                smoking.isNotBlank() &&
                drinking.isNotBlank() &&
                guests.isNotBlank() &&
                wakeUpTime.isNotBlank() &&
                roomEnvironment.isNotBlank() &&
                personalityType.isNotBlank() &&
                dailyRoutine.isNotBlank() &&
                workStyle.isNotBlank()

        Spacer(modifier = Modifier.height(24.dp))
        AnimatedVisibility(
            visible = isLifestyleValid,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Button(
                onClick = onNext,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StepInterestsSelection(
    selectedInterests: Set<String>, onInterestsChanged: (Set<String>) -> Unit,
    selectedDealBreakers: Set<String>, onDealBreakersChanged: (Set<String>) -> Unit,
    selectedRoomPurposes: Set<String>, onRoomPurposesChanged: (Set<String>) -> Unit,
    onNext: () -> Unit
) {
    var currentPage by remember { mutableStateOf(1) }

    val categoriesMap = mapOf(
        "💻 Tech & Career" to listOf("💻 Coding", "🤖 Artificial Intelligence", "🎮 Game Development", "🌐 Web Development", "📱 App Development", "🔐 Cybersecurity", "☁️ Cloud Computing", "📊 Data Science", "🎨 UI/UX Design", "🛠 Hackathons", "🚀 Startups", "💼 Entrepreneurship"),
        "🎬 Content Creation" to listOf("📹 YouTube Creator", "🎥 Video Editing", "📸 Photography", "📱 Instagram Creator", "🎵 Reels/Shorts Creator", "🎙 Podcasting", "✍️ Blogging", "🎨 Graphic Design", "🎬 Filmmaking", "🎧 Music Production", "🎤 Live Streaming", "📺 Vlogging"),
        "🎮 Gaming" to listOf("🎮 Mobile Gaming", "🖥 PC Gaming", "🎯 Competitive Gaming", "🎲 Casual Gaming", "🕹 Console Gaming", "♟ Board Games"),
        "📚 Study & Learning" to listOf("📖 Reading Books", "📚 Group Study", "✍️ Competitive Exams", "🌍 Learning Languages", "🧠 Research", "📝 Writing Notes"),
        "🏃 Fitness & Sports" to listOf("🏋️ Gym", "🧘 Yoga", "🏃 Running", "🚴 Cycling", "🏊 Swimming", "⚽ Football", "🏏 Cricket", "🏸 Badminton", "🏀 Basketball", "🎾 Tennis", "🥋 Martial Arts"),
        "🎵 Music & Entertainment" to listOf("🎧 Listening to Music", "🎤 Singing", "🎸 Guitar", "🎹 Piano", "🥁 Drums", "🎻 Violin", "💃 Dancing", "🎭 Acting", "🎬 Movies", "📺 Anime", "📖 Manga"),
        "🌍 Lifestyle" to listOf("✈️ Travelling", "☕ Cafe Hopping", "🍳 Cooking", "🍰 Baking", "🌱 Gardening", "🛍 Shopping", "🐶 Animal Lover", "🌿 Nature Lover"),
        "🎨 Creative Arts" to listOf("🎨 Painting", "✏️ Sketching", "🧵 DIY Crafts", "📷 Photo Editing", "🖌 Digital Art", "🏺 Pottery"),
        "🤝 Social" to listOf("🎉 Partying", "🎲 Board Game Nights", "☕ Hanging Out", "🤝 Networking", "❤️ Volunteering", "🎤 Public Speaking")
    )

    val dealBreakersList = listOf(
        "🚭 No Smokers", "🍺 No Alcohol", "🔊 No Loud Music", "🎉 No Late-Night Parties",
        "🧹 Must Keep Room Clean", "🐶 No Pets", "🚫 No Frequent Guests", "😴 Don't Disturb My Sleep",
        "📚 Need Quiet Study Time", "📹 I Create Content, Need Recording Time", "🎮 No Gaming After Midnight",
        "📞 No Loud Phone Calls", "🚬 No Vaping", "💡 Lights Off Before Sleeping", "🌅 Prefer Early Morning Routine"
    )

    val roomPurposesList = listOf(
        "📚 Studying", "😴 Rest & Sleep", "💻 Coding / Projects", "🎥 Content Creation",
        "🎮 Gaming", "🎨 Creative Work", "🧘 Relaxation", "👥 Socializing"
    )

    if (currentPage == 1) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Interests & Hobbies", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Select multiple interests that describe you best", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(4.dp))

            @OptIn(ExperimentalLayoutApi::class)
            @Composable
            fun CategoryBlock(categoryName: String, items: List<String>) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(categoryName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.secondary)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items.forEach { interest ->
                            val isSel = selectedInterests.contains(interest)
                            FilterChip(
                                selected = isSel,
                                onClick = {
                                    if (isSel) onInterestsChanged(selectedInterests - interest)
                                    else onInterestsChanged(selectedInterests + interest)
                                },
                                label = { Text(interest) }
                            )
                        }
                    }
                }
            }

            categoriesMap.forEach { (catName, items) ->
                CategoryBlock(catName, items)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { currentPage = 2 },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Next: Deal Breakers", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Deal Breakers & Room Purpose", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Help us filter out incompatible matches based on your deal breakers and room usage.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(4.dp))

            // 1. Roommate Deal Breakers Section
            Text("🚫 Roommate Deal Breakers", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.error)
            Text("Select everything you CANNOT accept in a roommate:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                dealBreakersList.forEach { db ->
                    val isSel = selectedDealBreakers.contains(db)
                    FilterChip(
                        selected = isSel,
                        onClick = {
                            if (isSel) onDealBreakersChanged(selectedDealBreakers - db)
                            else onDealBreakersChanged(selectedDealBreakers + db)
                        },
                        label = { Text(db) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()

            // 2. Room Purpose Section
            Text("💡 Room Purpose (Choose up to 2)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
            Text("What do you mainly use your room for?", fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))

            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                roomPurposesList.forEach { rp ->
                    val isSel = selectedRoomPurposes.contains(rp)
                    FilterChip(
                        selected = isSel,
                        onClick = {
                            if (isSel) {
                                onRoomPurposesChanged(selectedRoomPurposes - rp)
                            } else {
                                if (selectedRoomPurposes.size < 2) {
                                    onRoomPurposesChanged(selectedRoomPurposes + rp)
                                }
                            }
                        },
                        label = { Text(rp) },
                        enabled = isSel || selectedRoomPurposes.size < 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { currentPage = 1 },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(52.dp)
                ) {
                    Text("Back to Hobbies")
                }
                Button(
                    onClick = onNext,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(52.dp)
                ) {
                    Text("Continue", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StepBudgetAndBio(
    selectedLanguages: Set<String>, onLanguagesChanged: (Set<String>) -> Unit,
    hometown: String, onHometownChanged: (String) -> Unit,
    isForeign: Boolean, onForeignChanged: (Boolean) -> Unit,
    bio: String, onBioChanged: (String) -> Unit,
    onNext: () -> Unit
) {
    val languagesList = listOf("English", "Hindi", "Punjabi", "Tamil", "Telugu", "Bengali", "Malayalam", "Gujarati")
    var stateExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Background & Bio", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))

        Text("Hometown (Select State)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { stateExpanded = !stateExpanded }, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (hometown == "Choose State" || hometown.isBlank()) "Choose State (India)" else hometown, modifier = Modifier.weight(1f), maxLines = 1)
                    Text("▼")
                }
            }
            DropdownMenu(
                expanded = stateExpanded,
                onDismissRequest = { stateExpanded = false },
                modifier = Modifier.fillMaxWidth().height(260.dp)
            ) {
                indiaStates.forEach { st ->
                    DropdownMenuItem(text = { Text(st) }, onClick = { onHometownChanged(st); stateExpanded = false })
                }
            }
        }

        Text("Languages Spoken", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            languagesList.forEach { lang ->
                val isSel = selectedLanguages.contains(lang)
                FilterChip(
                    selected = isSel,
                    onClick = {
                        if (isSel) onLanguagesChanged(selectedLanguages - lang) else onLanguagesChanged(selectedLanguages + lang)
                    },
                    label = { Text(lang) }
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(checked = isForeign, onCheckedChange = onForeignChanged)
            Spacer(modifier = Modifier.width(8.dp))
            Text("I am an international student open to cultural exploration")
        }

        OutlinedTextField(
            value = bio,
            onValueChange = { if (it.split(" ").size <= 150) onBioChanged(it) },
            label = { Text("About Yourself (Bio - Max 150 words)") },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            maxLines = 4
        )

        val isBudgetBioValid = hometown.isNotBlank() && hometown != "Choose State" &&
                selectedLanguages.isNotEmpty() &&
                bio.isNotBlank() && bio.length >= 10

        Spacer(modifier = Modifier.height(16.dp))
        AnimatedVisibility(
            visible = isBudgetBioValid,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Button(
                onClick = onNext,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StepAICompatibilityCalculation(
    isLoading: Boolean,
    errorMessage: String?,
    onComplete: () -> Unit
) {
    val items = listOf(
        Pair("Lifestyle Habits", 95),
        Pair("Study Environment", 90),
        Pair("Food Preferences", 100),
        Pair("Cleanliness Standard", 85),
        Pair("Room Configuration", 92)
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AI Compatibility Matcher",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "We are calculating your roommate match metrics using Google Gemini AI models...",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items.forEach { opt ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(opt.first, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("${opt.second}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = opt.second / 100f,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                }
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 14.sp, textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onComplete,
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Complete Profile Setup", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
