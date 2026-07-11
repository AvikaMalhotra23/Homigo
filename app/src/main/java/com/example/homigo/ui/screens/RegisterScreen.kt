package com.example.homigo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homigo.data.repository.HomigoRepository
import com.example.homigo.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    selectedCollegeName: String,
    onRegisterSuccess: (String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("male") } // "male" or "female"
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    HomigoTheme(gender = gender, completion = 0) {
        FloatingBackground(gender = gender, completion = 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    density = GlassDensity.HIGH
                ) {
                    Text(
                        text = "Create Account",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Text(
                        text = "Campus: $selectedCollegeName",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryText,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 4.dp, bottom = 20.dp)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; errorMessage = null },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Graphite,
                            unfocusedTextColor = Graphite,
                            focusedLabelColor = SecondaryText,
                            unfocusedLabelColor = SecondaryText,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Border
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = course,
                        onValueChange = { course = it; errorMessage = null },
                        label = { Text("Course (e.g. B.Tech, MBA, BCA)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Graphite,
                            unfocusedTextColor = Graphite,
                            focusedLabelColor = SecondaryText,
                            unfocusedLabelColor = SecondaryText,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Border
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = null },
                        label = { Text("College Email ID") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Graphite,
                            unfocusedTextColor = Graphite,
                            focusedLabelColor = SecondaryText,
                            unfocusedLabelColor = SecondaryText,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Border
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = null },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Graphite,
                            unfocusedTextColor = Graphite,
                            focusedLabelColor = SecondaryText,
                            unfocusedLabelColor = SecondaryText,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Border
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gender Selection Column
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Gender",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Graphite
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { gender = "male" },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (gender == "male") MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.05f),
                                    contentColor = if (gender == "male") Color.White else Graphite
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Boy", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { gender = "female" },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (gender == "female") MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.05f),
                                    contentColor = if (gender == "female") Color.White else Graphite
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Girl", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    LiquidGlassButton(
                        text = if (isLoading) "Registering..." else "Register",
                        onClick = {
                            if (name.isBlank() || course.isBlank() || email.isBlank() || password.isBlank()) {
                                errorMessage = "Please fill in all fields"
                                return@LiquidGlassButton
                            }
                            isLoading = true
                            coroutineScope.launch {
                                try {
                                    HomigoRepository.register(
                                        mapOf(
                                            "name" to name,
                                            "email" to email,
                                            "password" to password,
                                            "gender" to gender
                                        )
                                    )
                                    isLoading = false
                                    onRegisterSuccess(selectedCollegeName, course)
                                } catch (e: Exception) {
                                    isLoading = false
                                    errorMessage = HomigoRepository.getErrorMessage(e)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Already have an account? ", fontSize = 13.sp, color = SecondaryText)
                        TextButton(onClick = onNavigateToLogin) {
                            Text("Log In", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
