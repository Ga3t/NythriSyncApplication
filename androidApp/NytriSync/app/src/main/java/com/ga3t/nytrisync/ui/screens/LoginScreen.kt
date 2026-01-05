package com.ga3t.nytrisync.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.imePadding

@Composable
fun LoginScreen(
    onNavigateToRegistration: () -> Unit,
    onLoggedIn: () -> Unit
) {
    val vm: LoginViewModel = viewModel(factory = LoginViewModel.factory())
    val state = vm.uiState
    var passwordVisible by remember { mutableStateOf(false) }

    val headerGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF81C784), Color(0xFF66BB6A))
    )

    val screenHeight = LocalConfiguration.current.screenHeightDp
    val headerMinHeightDp = maxOf(320, (screenHeight * 0.42f).toInt())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(headerGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Green gradient header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerMinHeightDp.dp),
                contentAlignment = Alignment.Center
            ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "NytriSync",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Login to your Account",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Welcome back, Please enter your details",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xCCFFFFFF),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }


            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp),
                tonalElevation = 2.dp,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .imePadding()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .widthIn(max = 520.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = state.login,
                        onValueChange = vm::onLoginChange,
                        label = { Text("Username") },
                        leadingIcon = { Icon(Icons.Outlined.AlternateEmail, contentDescription = null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = vm::onPasswordChange,
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.fillMaxWidth()
                    )

                    AnimatedVisibility(visible = state.error != null) {
                        Text(
                            text = state.error.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    ElevatedButton(
                        onClick = { vm.submit(onLoggedIn) },
                        enabled = !state.isLoading,
                        shape = MaterialTheme.shapes.large,
                        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "Signing in…",
                                color = Color(0xFF66BB6A)
                            )
                        } else {
                            Text(
                                "Sign in",
                                color = Color(0xFF66BB6A)
                            )
                        }
                    }

                    val link = buildAnnotatedString {
                        append("Don't have an account? ")
                        pushStringAnnotation(tag = "signup", annotation = "signup")
                        withStyle(
                            SpanStyle(
                                color = Color(0xFF66BB6A),
                                fontWeight = FontWeight.Medium,
                                textDecoration = TextDecoration.Underline
                            )
                        ) { append("Sign up") }
                        pop()
                    }
                    ClickableText(
                        text = link,
                        style = MaterialTheme.typography.bodyMedium,
                        onClick = { offset ->
                            link.getStringAnnotations("signup", offset, offset).firstOrNull()?.let {
                                onNavigateToRegistration()
                            }
                        }
                    )
                }
            }
        }
        }
    }
}