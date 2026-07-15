package com.example.homigo.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.example.homigo.ui.theme.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homigo.data.model.*
import com.example.homigo.data.repository.HomigoRepository
import kotlinx.coroutines.launch

private val defaultColleges = listOf(
    College(
        id = "lpu",
        name = "Lovely Professional University",
        city = "Phagwara, Punjab",
        hostels = HostelsConfig(
            male = (1..10).map { Hostel("bh$it", "BH $it") },
            female = (1..9).map { Hostel("gh$it", "GH $it") }
        )
    ),
    College(
        id = "thapar",
        name = "Thapar Institute of Engineering & Technology",
        city = "Patiala, Punjab",
        hostels = HostelsConfig(
            male = listOf(Hostel("hostel_a", "Hostel A"), Hostel("hostel_b", "Hostel B"), Hostel("hostel_c", "Hostel C"), Hostel("hostel_j", "Hostel J"), Hostel("hostel_m", "Hostel M")),
            female = listOf(Hostel("hostel_e", "Hostel E"), Hostel("hostel_g", "Hostel G"), Hostel("hostel_i", "Hostel I"), Hostel("hostel_n", "Hostel N"))
        )
    ),
    College(
        id = "iitd",
        name = "Indian Institute of Technology, Delhi (IITD)",
        city = "Hauz Khas, New Delhi",
        hostels = HostelsConfig(
            male = listOf(Hostel("aravali", "Aravali Hostel"), Hostel("jwalamukhi", "Jwalamukhi Hostel"), Hostel("karakoram", "Karakoram Hostel"), Hostel("nilgiri", "Nilgiri Hostel"), Hostel("kumaon", "Kumaon Hostel"), Hostel("girnar", "Girnar Hostel")),
            female = listOf(Hostel("kailash", "Kailash Hostel"), Hostel("shivalik", "Shivalik Hostel"), Hostel("himadri", "Himadri Hostel"))
        )
    ),
    College(
        id = "du",
        name = "Delhi University (DU)",
        city = "New Delhi, Delhi",
        hostels = HostelsConfig(
            male = listOf(Hostel("gwyer_hall", "Gwyer Hall"), Hostel("jubilee_hall", "Jubilee Hall"), Hostel("vkrv_rao", "VKRV Rao Hostel")),
            female = listOf(Hostel("uhw", "University Hostel for Women"), Hostel("nivedita", "Nivedita House"), Hostel("ughw", "Undergraduate Hostel for Women"))
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileSetupScreen(
    initialCollege: String? = null,
    initialCourse: String? = null,
    onSetupComplete: () -> Unit,
    onLogout: (String) -> Unit = {},
    onShowSnackbar: (String) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val user = HomigoRepository.currentUser.collectAsState().value
    val currentProfile = HomigoRepository.myProfile.collectAsState().value

    val logoutViewModel: com.example.homigo.ui.viewmodel.LogoutViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val logoutState by logoutViewModel.logoutState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        logoutViewModel.navigationEvent.collect { event ->
            when (event) {
                is com.example.homigo.ui.viewmodel.LogoutNavigationEvent.NavigateToWelcome -> {
                    val successMsg = (logoutState as? com.example.homigo.ui.viewmodel.LogoutUiState.Success)?.message 
                        ?: "You've been logged out successfully."
                    showLogoutDialog = false
                    onLogout(successMsg)
                }
            }
        }
    }

    LaunchedEffect(logoutState) {
        if (logoutState is com.example.homigo.ui.viewmodel.LogoutUiState.Error) {
            showLogoutDialog = false
            onShowSnackbar((logoutState as com.example.homigo.ui.viewmodel.LogoutUiState.Error).message)
            logoutViewModel.resetState()
        }
    }

    // Dynamic colleges state
    var collegesList by remember { mutableStateOf<List<College>>(emptyList()) }
    var selectedCollege by remember { mutableStateOf(defaultColleges[0]) }
    var collegeExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            collegesList = HomigoRepository.fetchColleges()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Setup fields
    var college by remember { mutableStateOf(initialCollege ?: "Lovely Professional University") }
    var course by remember { mutableStateOf(initialCourse ?: "B.Tech") }
    
    val isFemale = user?.gender == "female"
    val currentHostels = remember(selectedCollege, isFemale) {
        if (isFemale) {
            selectedCollege.hostels.female.map { it.label }
        } else {
            selectedCollege.hostels.male.map { it.label }
        }
    }
    
    var hostel by remember { mutableStateOf("") }
    
    LaunchedEffect(currentHostels) {
        if (hostel.isEmpty() || !currentHostels.contains(hostel)) {
            hostel = currentHostels.firstOrNull() ?: ""
        }
    }

    var roomPreference by remember { mutableStateOf("2-seater") }
    val roomOptions = listOf("Single", "2-seater", "3-seater", "4-seater")

    var budgetMin by remember { mutableStateOf("2000") }
    var budgetMax by remember { mutableStateOf("6000") }

    var sleepSchedule by remember { mutableStateOf("flexible") }
    val sleepOptions = listOf("early_bird", "night_owl", "flexible")

    var smoking by remember { mutableStateOf("no") }
    var drinking by remember { mutableStateOf("no") }
    
    var foodPreference by remember { mutableStateOf("any") }
    val foodOptions = listOf("veg", "non_veg", "any")

    var cleanliness by remember { mutableStateOf("moderate") }
    val cleanOptions = listOf("high", "moderate", "low")

    var pets by remember { mutableStateOf("no") }
    var guests by remember { mutableStateOf("rare") }
    val guestOptions = listOf("frequent", "rare", "no")

    var bio by remember { mutableStateOf("") }

    // AI Bio Generator state
    var aiBioPrompt by remember { mutableStateOf("") }
    var isGeneratingBio by remember { mutableStateOf(false) }

    // Verification state
    var isVerified by remember { mutableStateOf(false) }
    var isUploadingDoc by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var userDisplayName by remember { mutableStateOf("") }
    var userUsername by remember { mutableStateOf("") }
    var showUsernameSetupDialog by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        user?.let { u ->
            if (userDisplayName.isEmpty()) {
                userDisplayName = u.displayName ?: u.name
            }
            if (userUsername.isEmpty()) {
                userUsername = u.username ?: ""
            }
        }
    }

    // Pre-populate if profile already exists
    LaunchedEffect(currentProfile, collegesList) {
        currentProfile?.let { prof ->
            val listToUse = if (collegesList.isNotEmpty()) collegesList else defaultColleges
            val foundCollege = listToUse.find { it.name.equals(prof.college, ignoreCase = true) }
            if (foundCollege != null) {
                selectedCollege = foundCollege
            }
            college = prof.college
            course = prof.course ?: "B.Tech"
            hostel = prof.hostel.uppercase()
            roomPreference = prof.room_preference
            budgetMin = prof.budget_min.toString()
            budgetMax = prof.budget_max.toString()
            sleepSchedule = prof.sleep_schedule
            smoking = prof.smoking
            drinking = prof.drinking
            foodPreference = prof.food_preference
            cleanliness = prof.cleanliness
            pets = prof.pets
            guests = prof.guests
            bio = prof.bio ?: ""
            isVerified = prof.is_verified == 1
            userDisplayName = prof.displayName ?: user?.displayName ?: user?.name ?: ""
            userUsername = prof.username ?: user?.username ?: ""
        }
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile Setup", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            
            // Instagram-Style Profile Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Profile Photo / Avatar
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            modifier = Modifier.size(90.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isFemale) "👩" else "👨",
                                    fontSize = 44.sp
                                )
                            }
                        }
                        if (isVerified) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .align(Alignment.BottomEnd)
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified Status",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Display Name & Username
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (userDisplayName.isNotBlank()) userDisplayName else (user?.name ?: "Anonymous Student"),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Graphite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (userUsername.isNotBlank()) userUsername else "@username_not_set",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // College & Hostel
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = college,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Graphite.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (hostel.isNotEmpty()) "$hostel Hostel" else "No Hostel Selected",
                            fontSize = 12.sp,
                            color = SecondaryText,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Username Setup Banner if missing
                    if (userUsername.isBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.08f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "⚠️ Username not set. Add one to show up in search.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = { showUsernameSetupDialog = true },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Add", fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    } else {
                        // Display editable username edit button
                        TextButton(onClick = { showUsernameSetupDialog = true }) {
                            Text("Edit Username", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(color = Color.Black.copy(alpha = 0.06f), modifier = Modifier.padding(vertical = 4.dp))

                    // Action buttons: Copy, Share, QR code (disabled)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (userUsername.isNotBlank()) {
                                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("username", userUsername)
                                    clipboard.setPrimaryClip(clip)
                                    onShowSnackbar("Username copied: $userUsername")
                                } else {
                                    onShowSnackbar("Set username first")
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f))
                        ) {
                            Text("Copy", fontSize = 12.sp, color = Graphite)
                        }

                        OutlinedButton(
                            onClick = {
                                if (userUsername.isNotBlank()) {
                                    val cleanUsername = if (userUsername.startsWith("@")) userUsername.substring(1) else userUsername
                                    val shareLink = "https://homigo.app/u/$cleanUsername"
                                    val sendIntent = android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        putExtra(android.content.Intent.EXTRA_TEXT, "Connect with me on Homigo: $shareLink")
                                        type = "text/plain"
                                    }
                                    val shareIntent = android.content.Intent.createChooser(sendIntent, null)
                                    context.startActivity(shareIntent)
                                } else {
                                    onShowSnackbar("Set username first")
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f))
                        ) {
                            Text("Share Link", fontSize = 12.sp, color = Graphite)
                        }

                        OutlinedButton(
                            onClick = {},
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f)),
                            enabled = false
                        ) {
                            Text("QR Code", fontSize = 12.sp, color = Graphite.copy(alpha = 0.5f))
                        }
                    }
                }
            }

            // Personal identity details (Display Name)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Display Name Info", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    OutlinedTextField(
                        value = userDisplayName,
                        onValueChange = { userDisplayName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // College & Hostel Details
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Hostel Location Info", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    
                    // Dynamic College Dropdown Selection
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { collegeExpanded = !collegeExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedCollege.name, modifier = Modifier.weight(1f))
                                Text(if (collegeExpanded) " ▲" else " ▼")
                            }
                        }
                        DropdownMenu(
                            expanded = collegeExpanded,
                            onDismissRequest = { collegeExpanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val listToUse = if (collegesList.isNotEmpty()) collegesList else defaultColleges
                            listToUse.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text("${c.name} (${c.city})") },
                                    onClick = {
                                        selectedCollege = c
                                        college = c.name
                                        collegeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Text("Select Hostel", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    
                    // Horizontal Scrollable Row for Hostels supporting different lengths dynamically
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        currentHostels.forEach { h ->
                            FilterChip(
                                selected = hostel.equals(h, ignoreCase = true),
                                onClick = { hostel = h },
                                label = { Text(h) }
                            )
                        }
                    }

                    Text("Room Share Preference", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        roomOptions.forEach { r ->
                            FilterChip(
                                selected = roomPreference == r,
                                onClick = { roomPreference = r },
                                label = { Text(r) }
                            )
                        }
                    }
                }
            }

            // Budget Info
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Budget Range (per month in ₹)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = budgetMin,
                            onValueChange = { budgetMin = it },
                            label = { Text("Min Budget") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = budgetMax,
                            onValueChange = { budgetMax = it },
                            label = { Text("Max Budget") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Lifestyle Preferences
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Lifestyle Habits", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    Text("Sleep Schedule", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        sleepOptions.forEach { s ->
                            FilterChip(
                                selected = sleepSchedule == s,
                                onClick = { sleepSchedule = s },
                                label = { Text(s.replace("_", " ").capitalize()) }
                            )
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Smoke?", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Row {
                                FilterChip(selected = smoking == "yes", onClick = { smoking = "yes" }, label = { Text("Yes") })
                                Spacer(modifier = Modifier.width(8.dp))
                                FilterChip(selected = smoking == "no", onClick = { smoking = "no" }, label = { Text("No") })
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Drink?", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Row {
                                FilterChip(selected = drinking == "yes", onClick = { drinking = "yes" }, label = { Text("Yes") })
                                Spacer(modifier = Modifier.width(8.dp))
                                FilterChip(selected = drinking == "no", onClick = { drinking = "no" }, label = { Text("No") })
                            }
                        }
                    }

                    Text("Food Preference", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        foodOptions.forEach { f ->
                            FilterChip(
                                selected = foodPreference == f,
                                onClick = { foodPreference = f },
                                label = { Text(f.replace("_", " ").capitalize()) }
                            )
                        }
                    }

                    Text("Cleanliness Standard", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        cleanOptions.forEach { c ->
                            FilterChip(
                                selected = cleanliness == c,
                                onClick = { cleanliness = c },
                                label = { Text(c.capitalize()) }
                            )
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Pets Friendly?", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Row {
                                FilterChip(selected = pets == "yes", onClick = { pets = "yes" }, label = { Text("Yes") })
                                Spacer(modifier = Modifier.width(8.dp))
                                FilterChip(selected = pets == "no", onClick = { pets = "no" }, label = { Text("No") })
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Guests Frequency", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Row {
                                guestOptions.forEach { g ->
                                    FilterChip(selected = guests == g, onClick = { guests = g }, label = { Text(g.capitalize()) })
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                        }
                    }
                }
            }

            // AI Bio Generator & Bio
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Biography & AI Bio Generator", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    OutlinedTextField(
                        value = aiBioPrompt,
                        onValueChange = { aiBioPrompt = it },
                        label = { Text("Enter a brief description for AI Bio (e.g. I am a quiet tech student who reads books)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (aiBioPrompt.isBlank()) return@Button
                            isGeneratingBio = true
                            coroutineScope.launch {
                                try {
                                    val res = HomigoRepository.generateBio(aiBioPrompt)
                                    bio = res.bio
                                    isGeneratingBio = false
                                } catch (e: Exception) {
                                    isGeneratingBio = false
                                    errorMessage = "AI generation failed: " + HomigoRepository.getErrorMessage(e)
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.End),
                        enabled = !isGeneratingBio && aiBioPrompt.isNotBlank()
                    ) {
                        if (isGeneratingBio) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                        } else {
                            Text("Generate AI Bio")
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Roommate Bio") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ID Verification
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Profile Verification", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        "Verify your profile with College ID or Aadhaar to decrease your fake risk score and increase compatibility visibility.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    if (isVerified) {
                        Text("✓ Profile Verified", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    } else {
                        Button(
                            onClick = {
                                isUploadingDoc = true
                                coroutineScope.launch {
                                    try {
                                        // Mock doc verification upload
                                        HomigoRepository.verifyProfile("http://cloudinary.com/proofs/id_proof_${user?.id}.png")
                                        isVerified = true
                                        isUploadingDoc = false
                                        successMessage = "Profile verified successfully!"
                                    } catch (e: Exception) {
                                        isUploadingDoc = false
                                        errorMessage = "Verification failed: " + HomigoRepository.getErrorMessage(e)
                                    }
                                }
                            },
                            enabled = !isUploadingDoc,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isUploadingDoc) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Upload College ID / Aadhaar")
                            }
                        }
                    }
                }
            }

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            }
            if (successMessage != null) {
                Text(text = successMessage!!, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
            }

            // Save Button
            Button(
                onClick = {
                    isLoading = true
                    coroutineScope.launch {
                        try {
                            val map = mapOf(
                                "college" to college,
                                "hostel" to hostel.lowercase(),
                                "room_preference" to roomPreference,
                                "course" to course,
                                "budget_min" to (budgetMin.toIntOrNull() ?: 0),
                                "budget_max" to (budgetMax.toIntOrNull() ?: 100000),
                                "sleep_schedule" to sleepSchedule,
                                "smoking" to smoking,
                                "drinking" to drinking,
                                "food_preference" to foodPreference,
                                "cleanliness" to cleanliness,
                                "pets" to pets,
                                "guests" to guests,
                                "bio" to bio,
                                "displayName" to userDisplayName
                            )
                            HomigoRepository.updateProfile(map)
                            isLoading = false
                            onSetupComplete()
                        } catch (e: Exception) {
                            isLoading = false
                            errorMessage = HomigoRepository.getErrorMessage(e)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(bottom = 20.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Save Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                onClick = { showLogoutDialog = true },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = if (isFemale) Color(0xFFFCE7F3) else Color(0xFFE0F2FE),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Log Out Icon",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Log Out",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF1F2937)
                            )
                            Text(
                                text = "Sign out from your Homigo account securely.",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Chevron",
                        tint = if (isFemale) Color(0xFFF472B6) else Color(0xFF38BDF8),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        if (showLogoutDialog) {
            val isLoggingOut = logoutState is com.example.homigo.ui.viewmodel.LogoutUiState.Loading
            
            AlertDialog(
                onDismissRequest = { if (!isLoggingOut) showLogoutDialog = false },
                title = {
                    Text(
                        text = "Log Out",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Are you sure you want to log out of your account?",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (isLoggingOut) {
                            Spacer(modifier = Modifier.height(16.dp))
                            CircularProgressIndicator(
                                color = if (isFemale) Color(0xFFF472B6) else Color(0xFF38BDF8)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            logoutViewModel.logout()
                        },
                        enabled = !isLoggingOut,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Log Out", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showLogoutDialog = false },
                        enabled = !isLoggingOut,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isFemale) Color(0xFFF472B6) else Color(0xFF38BDF8)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isFemale) Color(0xFFFCE7F3) else Color(0xFFE0F2FE)
                        )
                    ) {
                        Text("Cancel")
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            )
        }

        if (showUsernameSetupDialog) {
            var inputUsername by remember { mutableStateOf("") }
            var availabilityMessage by remember { mutableStateOf("") }
            var isAvailable by remember { mutableStateOf<Boolean?>(null) }
            var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
            var isChecking by remember { mutableStateOf(false) }
            var isSavingUsername by remember { mutableStateOf(false) }

            // Live check availability when typing
            LaunchedEffect(inputUsername) {
                val trimmed = inputUsername.trim()
                if (trimmed.isEmpty()) {
                    availabilityMessage = ""
                    isAvailable = null
                    suggestions = emptyList()
                    return@LaunchedEffect
                }
                
                // Validate format rules
                val clean = if (trimmed.startsWith("@")) trimmed.substring(1) else trimmed
                val regex = "^[a-zA-Z0-9_.]+$".toRegex()
                if (!regex.matches(clean)) {
                    availabilityMessage = "Only letters, numbers, underscores, and dots allowed."
                    isAvailable = false
                    suggestions = emptyList()
                    return@LaunchedEffect
                }
                
                if (clean.length < 1 || clean.length > 30) {
                    availabilityMessage = "Must be between 1 and 30 characters."
                    isAvailable = false
                    suggestions = emptyList()
                    return@LaunchedEffect
                }

                isChecking = true
                try {
                    val checkRes = HomigoRepository.checkUsername(clean)
                    isAvailable = checkRes.available
                    if (checkRes.available) {
                        availabilityMessage = "✓ @$clean is available"
                    } else {
                        availabilityMessage = "✗ Username isn't available."
                        suggestions = checkRes.suggestions
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    isChecking = false
                }
            }

            AlertDialog(
                onDismissRequest = { if (!isSavingUsername) showUsernameSetupDialog = false },
                title = { Text("Choose a Username", fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Every user needs a unique username to be discovered by others on Homigo.",
                            fontSize = 14.sp,
                            color = SecondaryText
                        )

                        OutlinedTextField(
                            value = inputUsername,
                            onValueChange = { inputUsername = it },
                            label = { Text("Username") },
                            placeholder = { Text("e.g. harshit_garg") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = isAvailable == false
                        )

                        if (isChecking) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp).align(Alignment.CenterHorizontally))
                        } else if (availabilityMessage.isNotEmpty()) {
                            Text(
                                text = availabilityMessage,
                                color = if (isAvailable == true) Color(0xFF22C55E) else MaterialTheme.colorScheme.error,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (suggestions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Suggestions:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Graphite)
                            
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                suggestions.forEach { sug ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                            .clickable { inputUsername = sug }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(sug, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            isSavingUsername = true
                            coroutineScope.launch {
                                try {
                                    val clean = if (inputUsername.startsWith("@")) inputUsername.substring(1) else inputUsername
                                    HomigoRepository.updateUsername(clean)
                                    userUsername = "@$clean"
                                    showUsernameSetupDialog = false
                                    onShowSnackbar("Username updated successfully!")
                                } catch (e: Exception) {
                                    onShowSnackbar(e.message ?: "Failed to update username")
                                } finally {
                                    isSavingUsername = false
                                }
                            }
                        },
                        enabled = isAvailable == true && !isSavingUsername
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showUsernameSetupDialog = false },
                        enabled = !isSavingUsername
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
