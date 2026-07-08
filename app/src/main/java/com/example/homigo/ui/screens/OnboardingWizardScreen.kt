package com.example.homigo.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    "VIT University",
    "SRM University",
    "Indian Institute of Technology, Delhi (IIT Delhi)",
    "Indian Institute of Technology, Bombay (IIT Bombay)",
    "Indian Institute of Technology, Kanpur (IIT Kanpur)",
    "Delhi University (DU)",
    "Manipal Academy of Higher Education",
    "Chandigarh University",
    "Amity University",
    "Bennett University",
    "Galgotias University",
    "KIET Group of Institutions",
    "KIIT University"
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
    var selectedCourse by remember { mutableStateOf("B.Tech") }
    var selectedBranch by remember { mutableStateOf("Computer Science") }
    var selectedYear by remember { mutableStateOf("1st") }
    var selectedSemester by remember { mutableStateOf(1) }
    var sectionName by remember { mutableStateOf("") }
    var rollNumber by remember { mutableStateOf("") }
    var selectedSchool by remember { mutableStateOf(lpuSchools.first()) }

    // Accommodation Setup
    var lookingFor by remember { mutableStateOf("Roommate") }
    var preferredHostel by remember { mutableStateOf("BH-1") }
    var currentHostel by remember { mutableStateOf("BH-1") }
    var currentRoomNumber by remember { mutableStateOf("") }
    var moveInDate by remember { mutableStateOf("") }

    // Lifestyle Preferences
    var foodPref by remember { mutableStateOf("Vegetarian") }
    var sleepSchedule by remember { mutableStateOf("early_bird") }
    var studyHabits by remember { mutableStateOf("Silent") }
    var cleanlinessRating by remember { mutableStateOf(3f) } // slider 1-5
    var smokingPref by remember { mutableStateOf("no") }
    var drinkingPref by remember { mutableStateOf("no") }
    var guestsPref by remember { mutableStateOf("rarely") }
    var petsPref by remember { mutableStateOf("okay") }

    // Interests & Bio details
    var selectedInterests by remember { mutableStateOf(setOf<String>()) }
    var monthlyBudget by remember { mutableStateOf("₹4000 — ₹6000") }
    var selectedLanguages by remember { mutableStateOf(setOf<String>()) }
    var hometownState by remember { mutableStateOf("") }
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
        preferredHostel = selectedHostel
        currentHostel = selectedHostel
    }

    // Keep theme as neutral general blue (male) during steps 1 to 10. Only on step 11 we apply the selected gender's theme!
    val currentThemeGender = if (currentStep < 11) "male" else gender
    val pastelBackgroundColor = if (currentThemeGender == "female") Color(0xFFFFF5F7) else Color(0xFFF2F8FF)
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
                                color = MaterialTheme.colorScheme.primary
                            )
                            Box(modifier = Modifier.size(48.dp)) // Spacer
                        }
                        LinearProgressIndicator(
                            progress = currentStep / 11f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
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
                                6 -> StepAcademicDetails(selectedCollege = selectedCollege, course = selectedCourse, onCourseChanged = { selectedCourse = it }, branch = selectedBranch, onBranchChanged = { selectedBranch = it }, year = selectedYear, onYearChanged = { selectedYear = it }, semester = selectedSemester, onSemesterChanged = { selectedSemester = it }, section = sectionName, onSectionChanged = { sectionName = it }, rollNumber = rollNumber, onRollChanged = { rollNumber = it }, selectedSchool = selectedSchool, onSchoolChanged = { selectedSchool = it }, onNext = { currentStep = 7 })
                                7 -> StepAccommodationSetup(gender = gender, selectedCollege = selectedCollege, lookingFor = lookingFor, onLookingForChanged = { lookingFor = it }, preferredHostel = preferredHostel, onPreferredChanged = { preferredHostel = it }, currentHostel = currentHostel, onCurrentChanged = { currentHostel = it }, roomNumber = currentRoomNumber, onRoomChanged = { currentRoomNumber = it }, moveInDate = moveInDate, onMoveInChanged = { moveInDate = it }, onNext = { currentStep = 8 })
                                8 -> StepLifestylePreferences(foodPref = foodPref, onFoodChanged = { foodPref = it }, sleepSchedule = sleepSchedule, onSleepChanged = { sleepSchedule = it }, studyHabits = studyHabits, onStudyChanged = { studyHabits = it }, cleanliness = cleanlinessRating, onCleanlinessChanged = { cleanlinessRating = it }, smoking = smokingPref, onSmokingChanged = { smokingPref = it }, drinking = drinkingPref, onDrinkingChanged = { drinkingPref = it }, guests = guestsPref, onGuestsChanged = { guestsPref = it }, pets = petsPref, onPetsChanged = { petsPref = it }, cardSelectedColor = cardSelectedColor, onNext = { currentStep = 9 })
                                9 -> StepInterestsSelection(selectedInterests = selectedInterests, onInterestsChanged = { selectedInterests = it }, cardSelectedColor = cardSelectedColor, onNext = { currentStep = 10 })
                                10 -> StepBudgetAndBio(budget = monthlyBudget, onBudgetChanged = { monthlyBudget = it }, selectedLanguages = selectedLanguages, onLanguagesChanged = { selectedLanguages = it }, hometown = hometownState, onHometownChanged = { hometownState = it }, isForeign = foreignNational, onForeignChanged = { foreignNational = it }, bio = userBio, onBioChanged = { userBio = it }, onNext = { currentStep = 11 })
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
                                                    "section" to sectionName,
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
                                                    "sleep_schedule" to sleepSchedule,
                                                    "smoking" to smokingPref,
                                                    "drinking" to drinkingPref,
                                                    "food_preference" to (if (foodPref == "Vegetarian") "veg" else if (foodPref == "Non Vegetarian") "non_veg" else "any"),
                                                    "cleanliness" to (if (cleanlinessRating > 4) "high" else if (cleanlinessRating > 2) "moderate" else "low"),
                                                    "pets" to (if (petsPref == "Love Pets") "yes" else "no"),
                                                    "guests" to (if (guestsPref == "Often") "frequent" else if (guestsPref == "Sometimes") "rare" else "no"),
                                                    "bio" to userBio
                                                )
                                                HomigoRepository.updateProfile(profilePayload)
                                                isLoading = false
                                                onOnboardingComplete()
                                            } catch (e: Exception) {
                                                isLoading = false
                                                errorMessage = e.message ?: "An error occurred during account registration. Please try again."
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
private fun StepGenderSelection(gender: String, onGenderChanged: (String) -> Unit, onNext: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Who are you?", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))
        
        PremiumSelectedCard(
            selected = gender == "male",
            onClick = { onGenderChanged("male") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("👦", fontSize = 32.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Male Student", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        PremiumSelectedCard(
            selected = gender == "female",
            onClick = { onGenderChanged("female") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("👧", fontSize = 32.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Female Student", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
        AnimatedVisibility(
            visible = gender.isNotBlank(),
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
private fun StepCollegeSelection(selectedCollege: String, onCollegeSelected: (String) -> Unit, onNext: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredColleges = remember(searchQuery) {
        if (searchQuery.isBlank()) allColleges else allColleges.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Select College", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search College...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.height(300.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filteredColleges.forEach { college ->
                val isSelected = selectedCollege == college
                PremiumSelectedCard(
                    selected = isSelected,
                    onClick = { onCollegeSelected(college) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "College", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(college, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        AnimatedVisibility(
            visible = selectedCollege.isNotBlank(),
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
private fun StepHostelSelection(selectedCollege: String, gender: String, selectedHostel: String, onHostelSelected: (String) -> Unit, onNext: () -> Unit) {
    val hostels = remember(selectedCollege, gender) {
        if (selectedCollege == "Lovely Professional University") {
            if (gender == "female") (1..9).map { "GH-$it" } else (1..10).map { "BH-$it" }
        } else {
            if (gender == "female") listOf("Kailash Hostel", "Shivalik Hostel", "Himadri Hostel", "Girls Hostel A", "Girls Hostel B")
            else listOf("Aravali Hostel", "Jwalamukhi Hostel", "Karakoram Hostel", "Nilgiri Hostel", "Kumaon Hostel", "Girnar Hostel")
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Select Hostel", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Showing hostels on campus for ${selectedCollege}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(320.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(hostels) { hostel ->
                val isSelected = selectedHostel == hostel
                PremiumSelectedCard(
                    selected = isSelected,
                    onClick = { onHostelSelected(hostel) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(20.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(hostel, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        AnimatedVisibility(
            visible = selectedHostel.isNotBlank(),
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
private fun StepBasicDetails(
    fullName: String, onNameChanged: (String) -> Unit,
    email: String, onEmailChanged: (String) -> Unit,
    phone: String, onPhoneChanged: (String) -> Unit,
    password: String, onPasswordChanged: (String) -> Unit,
    confirmPassword: String, onConfirmChanged: (String) -> Unit,
    onNext: () -> Unit
) {
    var validationError by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Basic Details", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { onNameChanged(it); validationError = null },
            label = { Text("Full Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { onEmailChanged(it); validationError = null },
            label = { Text("Email Address") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { onPhoneChanged(it); validationError = null },
            label = { Text("Phone Number") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { onPasswordChanged(it); validationError = null },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { onConfirmChanged(it); validationError = null },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        if (validationError != null) {
            Text(validationError!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        val isFormValid = fullName.isNotBlank() &&
                email.isNotBlank() && email.contains("@") && email.contains(".") &&
                phone.isNotBlank() && phone.length >= 10 &&
                password.isNotBlank() && password.length >= 6 &&
                confirmPassword == password

        Spacer(modifier = Modifier.height(16.dp))
        AnimatedVisibility(
            visible = isFormValid,
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
private fun StepAcademicDetails(
    selectedCollege: String,
    course: String, onCourseChanged: (String) -> Unit,
    branch: String, onBranchChanged: (String) -> Unit,
    year: String, onYearChanged: (String) -> Unit,
    semester: Int, onSemesterChanged: (Int) -> Unit,
    section: String, onSectionChanged: (String) -> Unit,
    rollNumber: String, onRollChanged: (String) -> Unit,
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

        // Course Dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { courseExpanded = !courseExpanded }, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Course: ${course}")
                    Text("▼")
                }
            }
            DropdownMenu(expanded = courseExpanded, onDismissRequest = { courseExpanded = false }, modifier = Modifier.fillMaxWidth()) {
                coursesList.forEach { c ->
                    DropdownMenuItem(text = { Text(c) }, onClick = { onCourseChanged(c); courseExpanded = false })
                }
            }
        }

        // Branch Dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { branchExpanded = !branchExpanded }, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Branch: ${branch}")
                    Text("▼")
                }
            }
            DropdownMenu(expanded = branchExpanded, onDismissRequest = { branchExpanded = false }, modifier = Modifier.fillMaxWidth()) {
                branchesList.forEach { b ->
                    DropdownMenuItem(text = { Text(b) }, onClick = { onBranchChanged(b); branchExpanded = false })
                }
            }
        }

        // Year and Semester selector side-by-side
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

        OutlinedTextField(
            value = section,
            onValueChange = onSectionChanged,
            label = { Text("Section (e.g. K24AB)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = rollNumber,
            onValueChange = onRollChanged,
            label = { Text("Roll Number (Optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // School dropdown mapping to visual screenshots if college is LPU
        if (selectedCollege == "Lovely Professional University") {
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { schoolExpanded = !schoolExpanded }, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("School: ${selectedSchool}", modifier = Modifier.weight(1f), maxLines = 1)
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

        val isAcademicValid = course.isNotBlank() &&
                branch.isNotBlank() &&
                year.isNotBlank() &&
                section.isNotBlank() &&
                (if (selectedCollege == "Lovely Professional University") selectedSchool.isNotBlank() else true)

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
                    Text("Preferred Hostel: ${preferredHostel}")
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
                    Text("Current Hostel: ${currentHostel}")
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

        OutlinedTextField(
            value = moveInDate,
            onValueChange = onMoveInChanged,
            label = { Text("Expected Move-in Date (e.g. Aug 2026)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

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
    cleanliness: Float, onCleanlinessChanged: (Float) -> Unit,
    smoking: String, onSmokingChanged: (String) -> Unit,
    drinking: String, onDrinkingChanged: (String) -> Unit,
    guests: String, onGuestsChanged: (String) -> Unit,
    pets: String, onPetsChanged: (String) -> Unit,
    cardSelectedColor: Color,
    onNext: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Lifestyle Preferences", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))

        // Food Preference (Big Cards)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Food Preference", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Vegetarian", "Non Vegetarian", "Eggetarian").forEach { opt ->
                    val isSel = foodPref == opt
                    PremiumSelectedCard(
                        selected = isSel,
                        onClick = { onFoodChanged(opt) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(modifier = Modifier.padding(14.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(opt, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }

        // Sleep Habits
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Sleep Habits", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(Pair("early_bird", "Early 🌙"), Pair("night_owl", "Late 🌃")).forEach { opt ->
                    val isSel = sleepSchedule == opt.first
                    PremiumSelectedCard(
                        selected = isSel,
                        onClick = { onSleepChanged(opt.first) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(modifier = Modifier.padding(14.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(opt.second, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Study habits
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Study Environment", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Silent 📚", "Music 🎵").forEach { opt ->
                    val isSel = studyHabits == opt.substringBefore(" ")
                    PremiumSelectedCard(
                        selected = isSel,
                        onClick = { onStudyChanged(opt.substringBefore(" ")) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(modifier = Modifier.padding(14.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(opt, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Cleanliness Slider
        Column {
            Text("Cleanliness rating: ${cleanliness.toInt()}/5", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Slider(
                value = cleanliness,
                onValueChange = onCleanlinessChanged,
                valueRange = 1f..5f,
                steps = 3,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Smoking & Drinking Side-by-Side
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Smoking", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("no", "occasionally", "yes").forEach { opt ->
                        FilterChip(
                            selected = smoking == opt,
                            onClick = { onSmokingChanged(opt) },
                            label = { Text(opt, fontSize = 11.sp) }
                        )
                    }
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Drinking", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("no", "occasionally", "yes").forEach { opt ->
                        FilterChip(
                            selected = drinking == opt,
                            onClick = { onDrinkingChanged(opt) },
                            label = { Text(opt, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // Guests & Pets Side-by-Side
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Guests Frequency", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("rarely", "sometimes", "often").forEach { opt ->
                        FilterChip(
                            selected = guests == opt,
                            onClick = { onGuestsChanged(opt) },
                            label = { Text(opt, fontSize = 11.sp) }
                        )
                    }
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Pets Preference", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("no", "okay", "yes").forEach { opt ->
                        FilterChip(
                            selected = pets == opt,
                            onClick = { onPetsChanged(opt) },
                            label = { Text(if(opt=="yes") "love" else opt, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNext,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StepInterestsSelection(selectedInterests: Set<String>, onInterestsChanged: (Set<String>) -> Unit, cardSelectedColor: Color, onNext: () -> Unit) {
    val interestsList = listOf(
        "🎮 Gaming", "🏋️ Gym", "⚽ Sports", "🎵 Music", 
        "📚 Reading", "🎬 Movies", "✈️ Travelling", "💻 Coding", 
        "📸 Photography", "🎨 Art", "🍳 Cooking", "🎤 Singing"
    )

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Choose Interests", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Select multiple interests that describe you best", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(340.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(interestsList) { interest ->
                val isSelected = selectedInterests.contains(interest)
                PremiumSelectedCard(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) onInterestsChanged(selectedInterests - interest) else onInterestsChanged(selectedInterests + interest)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(18.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(interest, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        AnimatedVisibility(
            visible = selectedInterests.isNotEmpty(),
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
private fun StepBudgetAndBio(
    budget: String, onBudgetChanged: (String) -> Unit,
    selectedLanguages: Set<String>, onLanguagesChanged: (Set<String>) -> Unit,
    hometown: String, onHometownChanged: (String) -> Unit,
    isForeign: Boolean, onForeignChanged: (Boolean) -> Unit,
    bio: String, onBioChanged: (String) -> Unit,
    onNext: () -> Unit
) {
    val languagesList = listOf("English", "Hindi", "Punjabi", "Tamil", "Telugu", "Bengali", "Malayalam", "Gujarati")

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Budget & Background", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))

        Text("Monthly Budget", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("₹2000 — ₹4000", "₹4000 — ₹6000", "₹6000+").forEach { opt ->
                val isSel = budget == opt
                FilterChip(
                    selected = isSel,
                    onClick = { onBudgetChanged(opt) },
                    label = { Text(opt, fontSize = 11.sp) },
                    modifier = Modifier.weight(1f)
                )
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

        OutlinedTextField(
            value = hometown,
            onValueChange = onHometownChanged,
            label = { Text("Hometown (State/Country)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

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

        val isBudgetBioValid = budget.isNotBlank() &&
                selectedLanguages.isNotEmpty() &&
                hometown.isNotBlank() &&
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
