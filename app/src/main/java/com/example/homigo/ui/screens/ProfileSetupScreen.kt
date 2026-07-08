package com.example.homigo.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    initialCollege: String? = null,
    initialCourse: String? = null,
    onSetupComplete: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val user = HomigoRepository.currentUser.collectAsState().value
    val currentProfile = HomigoRepository.myProfile.collectAsState().value

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
        }
    }

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
                                    errorMessage = "AI generation failed: ${e.message}"
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
                                        errorMessage = "Verification failed: ${e.message}"
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
                                "bio" to bio
                            )
                            HomigoRepository.updateProfile(map)
                            isLoading = false
                            onSetupComplete()
                        } catch (e: Exception) {
                            isLoading = false
                            errorMessage = e.message ?: "Failed to save profile"
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
        }
    }
}
