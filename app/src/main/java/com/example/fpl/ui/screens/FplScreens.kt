@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.fpl.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Support
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.fpl.data.model.PlayerDto
import com.example.fpl.ui.theme.FplAccentGreen
import com.example.fpl.ui.theme.FplBgNavy
import com.example.fpl.ui.theme.FplBrightPurple
import com.example.fpl.ui.theme.FplCardBorder
import com.example.fpl.ui.theme.FplCardNavy
import com.example.fpl.ui.theme.FplCardNavyLight
import com.example.fpl.ui.theme.FplElectricPurple
import com.example.fpl.ui.theme.FplOutCrimson
import com.example.fpl.ui.theme.FplTextSecondary
import com.example.fpl.ui.viewmodel.FplUiState

// =========================================================================
// SHARED BRAND GRAPHICS: CROWNED LION LOGO, 2D CLUB SHIRTS & BADGES
// =========================================================================

@Composable
fun PremierLeagueLionLogo(
    size: Dp = 42.dp,
    tint: Color = Color.White
) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Outer Lion Mane Circle Arc
        drawCircle(
            color = tint,
            radius = w * 0.44f,
            center = Offset(w * 0.5f, h * 0.54f),
            style = Stroke(width = w * 0.07f)
        )

        // 3-Pointed Crown on Top
        val crownPath = Path().apply {
            moveTo(w * 0.28f, h * 0.24f)
            lineTo(w * 0.24f, h * 0.08f)
            lineTo(w * 0.39f, h * 0.17f)
            lineTo(w * 0.50f, h * 0.04f)
            lineTo(w * 0.61f, h * 0.17f)
            lineTo(w * 0.76f, h * 0.08f)
            lineTo(w * 0.72f, h * 0.24f)
            close()
        }
        drawPath(crownPath, color = tint)

        // Stylized Lion Head Profile Inside
        val headPath = Path().apply {
            moveTo(w * 0.34f, h * 0.38f)
            lineTo(w * 0.66f, h * 0.38f)
            lineTo(w * 0.72f, h * 0.56f)
            lineTo(w * 0.58f, h * 0.76f)
            lineTo(w * 0.38f, h * 0.72f)
            lineTo(w * 0.30f, h * 0.54f)
            close()
        }
        drawPath(headPath, color = tint.copy(alpha = 0.9f))
    }
}

@Composable
fun TopBrandHeader(
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF070A21),
                        Color(0xFF1A0E4F),
                        Color(0xFF2E1065),
                        Color(0xFF0A0E2A)
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Diagonal purple graphic shard accents in header background
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            val shard = Path().apply {
                moveTo(size.width * 0.45f, 0f)
                lineTo(size.width * 0.60f, 0f)
                lineTo(size.width * 0.72f, size.height)
                lineTo(size.width * 0.57f, size.height)
                close()
            }
            drawPath(shard, color = FplElectricPurple.copy(alpha = 0.22f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PremierLeagueLionLogo(size = 40.dp)
                Column {
                    Text(
                        text = "Fantasy",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Premier League",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable(onClick = onNotificationClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(FplOutCrimson)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(FplElectricPurple.copy(alpha = 0.35f))
                        .border(1.5.dp, FplBrightPurple, CircleShape)
                        .clickable(onClick = onProfileClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ClubKitIcon(
    teamShortName: String,
    isGoalkeeper: Boolean = false,
    size: Dp = 44.dp
) {
    // Authentic 2D club kit colors + stripes matching the screenshots
    val short = teamShortName.uppercase()
    val (bodyColor, sleeveColor, stripeColor) = when {
        isGoalkeeper && short == "BRE" -> Triple(Color(0xFFFACC15), Color(0xFFFACC15), null)
        isGoalkeeper -> Triple(Color(0xFF22C55E), Color(0xFF16A34A), null)
        short == "ARS" -> Triple(Color(0xFFDC2626), Color.White, null)
        short == "MCI" -> Triple(Color(0xFF38BDF8), Color(0xFF38BDF8), null)
        short == "LIV" -> Triple(Color(0xFFB91C1C), Color(0xFFB91C1C), null)
        short == "CHE" -> Triple(Color(0xFF1D4ED8), Color(0xFF1D4ED8), null)
        short == "NEW" -> Triple(Color(0xFF111827), Color(0xFF111827), Color.White)
        short == "BRE" -> Triple(Color(0xFFDC2626), Color.White, Color.White)
        short == "AVL" -> Triple(Color(0xFF7F1D1D), Color(0xFF38BDF8), null)
        short == "BOU" -> Triple(Color(0xFF991B1B), Color(0xFF111827), Color(0xFF111827))
        short == "BHA" -> Triple(Color(0xFF1D4ED8), Color.White, Color.White)
        short == "TOT" -> Triple(Color.White, Color(0xFF1E293B), null)
        short == "CRY" -> Triple(Color(0xFF1D4ED8), Color(0xFFDC2626), Color(0xFFDC2626))
        else -> Triple(Color(0xFF4F46E5), Color(0xFF4338CA), null)
    }

    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Left Sleeve
        val leftSleeve = Path().apply {
            moveTo(w * 0.24f, h * 0.14f)
            lineTo(w * 0.05f, h * 0.36f)
            lineTo(w * 0.18f, h * 0.48f)
            lineTo(w * 0.28f, h * 0.32f)
            close()
        }
        drawPath(leftSleeve, color = sleeveColor)

        // Right Sleeve
        val rightSleeve = Path().apply {
            moveTo(w * 0.76f, h * 0.14f)
            lineTo(w * 0.95f, h * 0.36f)
            lineTo(w * 0.82f, h * 0.48f)
            lineTo(w * 0.72f, h * 0.32f)
            close()
        }
        drawPath(rightSleeve, color = sleeveColor)

        // Torso
        val torso = Path().apply {
            moveTo(w * 0.24f, h * 0.14f)
            lineTo(w * 0.76f, h * 0.14f)
            lineTo(w * 0.74f, h * 0.90f)
            lineTo(w * 0.26f, h * 0.90f)
            close()
        }
        drawPath(torso, color = bodyColor)

        // Vertical Stripes if applicable (Newcastle, Brentford, Brighton, Bournemouth, Palace)
        if (stripeColor != null) {
            drawRect(
                color = stripeColor,
                topLeft = Offset(w * 0.36f, h * 0.16f),
                size = Size(w * 0.08f, h * 0.74f)
            )
            drawRect(
                color = stripeColor,
                topLeft = Offset(w * 0.56f, h * 0.16f),
                size = Size(w * 0.08f, h * 0.74f)
            )
        }

        // Collar V-neck
        drawCircle(
            color = FplBgNavy,
            radius = w * 0.10f,
            center = Offset(w * 0.5f, h * 0.14f)
        )

        // Subtle Chest Sponsor Bar
        drawRect(
            color = if (short == "TOT") Color(0xFF1E293B) else Color.White.copy(alpha = 0.8f),
            topLeft = Offset(w * 0.35f, h * 0.46f),
            size = Size(w * 0.30f, h * 0.07f)
        )
    }
}

@Composable
fun ClubCrestBadge(
    teamCode: Int,
    teamShortName: String,
    size: Dp = 24.dp
) {
    val badgeUrl = "https://resources.premierleague.com/premierleague/badges/70/t$teamCode.png"
    SubcomposeAsyncImage(
        model = badgeUrl,
        contentDescription = teamShortName,
        modifier = Modifier.size(size),
        contentScale = ContentScale.Fit,
        error = {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(FplElectricPurple.copy(alpha = 0.35f))
                    .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = teamShortName.take(3),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    )
}

@Composable
fun FplStyledCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = FplCardNavy,
    borderColor: Color = FplCardBorder,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        content()
    }
}

// =========================================================================
// SCREENSHOT 6: FULL-SCREEN LOG IN & SIGN UP ("CREATE YOUR ACCOUNT")
// =========================================================================

@Composable
fun FplFullAuthScreen(
    state: FplUiState,
    onSelectSignUpMode: (Boolean) -> Unit,
    onSubmitAuth: (email: String, password: String, teamId: String, fullName: String) -> Unit,
    onClose: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var teamId by remember { mutableStateOf(state.teamIdInput) }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF160B3E),
                        Color(0xFF070A21),
                        Color(0xFF050718)
                    )
                )
            )
    ) {
        // Diagonal purple neon rays top background
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            val leftRay = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width * 0.35f, 0f)
                lineTo(0f, size.height * 0.75f)
                close()
            }
            drawPath(leftRay, color = FplElectricPurple.copy(alpha = 0.22f))

            val rightRay = Path().apply {
                moveTo(size.width * 0.65f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height * 0.75f)
                close()
            }
            drawPath(rightRay, color = FplElectricPurple.copy(alpha = 0.22f))
        }

        // Bottom Purple Stadium Lights Silhouette
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.BottomCenter)
        ) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        FplElectricPurple.copy(alpha = 0.25f),
                        Color(0xFF1D0B46)
                    )
                )
            )
            // Stadium roof arch
            drawArc(
                color = FplBrightPurple.copy(alpha = 0.55f),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(size.width * 0.05f, size.height * 0.28f),
                size = Size(size.width * 0.90f, size.height * 0.95f),
                style = Stroke(width = 4f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isSignUpMode) {
                // CREATE YOUR ACCOUNT LAYOUT (Right side of Screenshot 6)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onSelectSignUpMode(false) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PremierLeagueLionLogo(size = 64.dp)
                    Column {
                        Text(
                            text = "Fantasy",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Premier League",
                            fontSize = 18.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Create Your Account",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Join millions of managers worldwide and start your Fantasy Premier League journey.",
                        fontSize = 14.sp,
                        color = FplTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                AuthInputBox(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = "Full Name",
                    placeholder = "Enter your full name",
                    leadingIcon = Icons.Default.Person
                )
                Spacer(modifier = Modifier.height(12.dp))
                AuthInputBox(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email address",
                    placeholder = "Enter your email address",
                    leadingIcon = Icons.Default.Email
                )
                Spacer(modifier = Modifier.height(12.dp))
                AuthInputBox(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "Create a password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible }
                )
                Spacer(modifier = Modifier.height(12.dp))
                AuthInputBox(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm Password",
                    placeholder = "Confirm your password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible }
                )

                state.loginError?.let { err ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = err, color = FplOutCrimson, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = { onSubmitAuth(email, password, teamId, fullName) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FplElectricPurple)
                ) {
                    Text(
                        text = "Create Account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Already have an account? ", color = FplTextSecondary, fontSize = 14.sp)
                    Text(
                        text = "Log In",
                        color = FplBrightPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onSelectSignUpMode(false) }
                    )
                }
            } else {
                // LOG IN LAYOUT (Left side of Screenshot 6)
                Spacer(modifier = Modifier.height(16.dp))
                PremierLeagueLionLogo(size = 86.dp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Fantasy",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Premier League",
                    fontSize = 20.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Create your dream team. Compete with\nfriends. Win every week.",
                    fontSize = 14.sp,
                    color = FplTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Log In | Sign Up Segmented Pill Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(FplCardNavy)
                        .border(1.dp, FplCardBorder, RoundedCornerShape(24.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .background(FplElectricPurple)
                            .clickable { onSelectSignUpMode(false) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Log In", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onSelectSignUpMode(true) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sign Up", fontWeight = FontWeight.SemiBold, color = FplTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                AuthInputBox(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email address",
                    placeholder = "Email address",
                    leadingIcon = Icons.Default.Email
                )
                Spacer(modifier = Modifier.height(12.dp))
                AuthInputBox(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "Password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible }
                )
                Spacer(modifier = Modifier.height(12.dp))
                AuthInputBox(
                    value = teamId,
                    onValueChange = { teamId = it.filter { c -> c.isDigit() } },
                    label = "FPL Team ID (Optional for live squad sync)",
                    placeholder = "e.g. 123456",
                    leadingIcon = Icons.Default.Groups
                )

                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Forgot your password?",
                        color = FplBrightPurple,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                state.loginError?.let { err ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = err, color = FplOutCrimson, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onSubmitAuth(email, password, teamId, "") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FplElectricPurple),
                    enabled = !state.isLoggingIn
                ) {
                    if (state.isLoggingIn) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text("Log In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                TextButton(onClick = onClose) {
                    Text("Continue to App Dashboard", color = FplTextSecondary)
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PremierLeagueLionLogo(size = 24.dp)
                    Text(
                        text = "The official Fantasy Premier League app",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }
        }
    }
}

@Composable
fun AuthInputBox(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder, color = FplTextSecondary) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        leadingIcon = {
            Icon(leadingIcon, contentDescription = null, tint = FplBrightPurple)
        },
        trailingIcon = if (isPassword && onTogglePassword != null) {
            {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password",
                        tint = FplTextSecondary
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = FplCardNavy.copy(alpha = 0.85f),
            unfocusedContainerColor = FplCardNavy.copy(alpha = 0.85f),
            focusedBorderColor = FplBrightPurple,
            unfocusedBorderColor = FplCardBorder
        )
    )
}

// =========================================================================
// SCREENSHOT 4: HOME TAB DASHBOARD
// =========================================================================

@Composable
fun HomeDashboardScreen(
    state: FplUiState,
    onNavigateToMyTeam: () -> Unit,
    onNavigateToTransfers: () -> Unit,
    onNavigateToLeagues: () -> Unit,
    onNavigateToFixtures: () -> Unit,
    onPlayerClick: (Int) -> Unit
) {
    val playersById = state.players.associateBy { it.id }
    val squadPlayers = state.draftSquadIds.mapNotNull { playersById[it] }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        // 1. Hero Banner ("It's FPL time!")
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF23115A),
                                Color(0xFF3B1284),
                                Color(0xFF5B21B6),
                                Color(0xFF0EA5E9)
                            )
                        )
                    )
                    .border(1.dp, FplBrightPurple.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.currentEventName,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "It's FPL time!",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Make your transfers before the deadline.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = FplBrightPurple,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Deadline: ${state.currentDeadlineFormatted}",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp
                            )
                        }
                    }
                    PremierLeagueLionLogo(size = 76.dp, tint = Color.White)
                }
            }
        }

        // 2. 4-Column Summary Stats Strip
        item {
            FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HomeStatCell(
                        title = "Total Points",
                        value = state.displayTotalPoints.toString(),
                        subGreen = "▲ 12 pts",
                        subMuted = "(GW7)"
                    )
                    VerticalStatDivider()
                    HomeStatCell(
                        title = "Gameweek Points",
                        value = state.displayGwPoints.toString(),
                        subGreen = "▲ 4 pts",
                        subMuted = ""
                    )
                    VerticalStatDivider()
                    HomeStatCell(
                        title = "Rank",
                        value = state.displayOverallRank,
                        subGreen = "",
                        subMuted = "of 8,952,110"
                    )
                    VerticalStatDivider()
                    HomeStatCell(
                        title = "Transfers",
                        value = state.freeTransfers.toString(),
                        subGreen = "",
                        subMuted = "(0 free)"
                    )
                }
            }
        }

        // 3. Split Row: Left Mini-Pitch ("My Team") + Right 3 Stacked Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Left Mini Pitch Card
                FplStyledCard(
                    modifier = Modifier.weight(1.35f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ClubKitIcon(teamShortName = "TOT", size = 24.dp)
                                Column {
                                    Text(
                                        text = "My Team",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = state.selectedFormation,
                                        fontSize = 11.sp,
                                        color = FplTextSecondary
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = FplCardNavyLight,
                                border = BorderStroke(1.dp, FplCardBorder),
                                modifier = Modifier.clickable(onClick = onNavigateToMyTeam)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("View Team", fontSize = 10.sp, color = Color.White)
                                    Icon(
                                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        PerspectivePitchBox(
                            squadPlayers = squadPlayers,
                            teamsById = state.teamsById,
                            showHeaderBoards = false,
                            compact = true,
                            onPlayerClick = onPlayerClick
                        )
                    }
                }

                // Right Column: Your League, Transfers, Next Fixture
                Column(
                    modifier = Modifier.weight(0.95f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card A: Your League
                    FplStyledCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onNavigateToLeagues)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = FplBrightPurple,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Your League",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Global League",
                                    fontSize = 11.sp,
                                    color = FplTextSecondary
                                )
                                Icon(
                                    Icons.Default.Public,
                                    contentDescription = null,
                                    tint = FplBrightPurple,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = state.displayOverallRank,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "of 8,952,110",
                                fontSize = 11.sp,
                                color = FplTextSecondary
                            )
                        }
                    }

                    // Card B: Transfers
                    FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.SwapHoriz,
                                    contentDescription = null,
                                    tint = FplAccentGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Transfers",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Text(
                                text = "${state.freeTransfers} free transfer",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Button(
                                onClick = onNavigateToTransfers,
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 6.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FplElectricPurple)
                            ) {
                                Text("Make Transfers >", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Card C: Next Fixture
                    val firstFix = state.fixtures.firstOrNull()
                    val homeTeam = state.teamsById[firstFix?.teamH ?: 1]
                    val awayTeam = state.teamsById[firstFix?.teamA ?: 2]

                    FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "Next Fixture",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Text(
                                text = "GW ${state.selectedEventId}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = state.currentDeadlineFormatted,
                                fontSize = 10.sp,
                                color = FplTextSecondary
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ClubCrestBadge(
                                    teamCode = homeTeam?.code ?: 43,
                                    teamShortName = homeTeam?.shortName ?: "MCI",
                                    size = 26.dp
                                )
                                Text("VS", fontSize = 11.sp, color = FplTextSecondary)
                                ClubCrestBadge(
                                    teamCode = awayTeam?.code ?: 54,
                                    teamShortName = awayTeam?.shortName ?: "FUL",
                                    size = 26.dp
                                )
                            }
                            OutlinedButton(
                                onClick = onNavigateToFixtures,
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 4.dp),
                                shape = RoundedCornerShape(18.dp),
                                border = BorderStroke(1.dp, FplBrightPurple)
                            ) {
                                Text("View Fixtures", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // 4. Bottom Card: Upcoming Fixtures
        item {
            FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Upcoming Fixtures",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = onNavigateToFixtures)
                        ) {
                            Text("View All", fontSize = 12.sp, color = FplBrightPurple)
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = FplBrightPurple,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(FplCardNavyLight)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "GW ${state.selectedEventId}  •  ${state.currentDeadlineFormatted.substringBeforeLast(" ")}",
                            fontSize = 12.sp,
                            color = FplTextSecondary
                        )
                    }

                    val previewFixtures = state.fixtures.take(4)
                    previewFixtures.forEachIndexed { idx, fix ->
                        val hTeam = state.teamsById[fix.teamH]
                        val aTeam = state.teamsById[fix.teamA]
                        val timeLabel = fix.kickoffTime?.let {
                            if (it.length >= 16) it.substring(11, 16) else "15:00"
                        } ?: "15:00"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onNavigateToFixtures)
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = hTeam?.name ?: "Man City",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                ClubCrestBadge(
                                    teamCode = hTeam?.code ?: 43,
                                    teamShortName = hTeam?.shortName ?: "MCI",
                                    size = 24.dp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(FplCardNavyLight)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = timeLabel,
                                    fontSize = 12.sp,
                                    color = FplTextSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    ClubCrestBadge(
                                        teamCode = aTeam?.code ?: 54,
                                        teamShortName = aTeam?.shortName ?: "FUL",
                                        size = 24.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = aTeam?.name ?: "Fulham",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1
                                    )
                                }
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = FplTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        if (idx < previewFixtures.lastIndex) {
                            HorizontalDivider(color = FplCardBorder.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeStatCell(
    title: String,
    value: String,
    subGreen: String,
    subMuted: String
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(text = title, fontSize = 11.sp, color = FplTextSecondary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            if (subGreen.isNotEmpty()) {
                Text(text = subGreen, fontSize = 10.sp, color = FplAccentGreen, fontWeight = FontWeight.Bold)
            }
            if (subMuted.isNotEmpty()) {
                Text(text = subMuted, fontSize = 10.sp, color = FplTextSecondary)
            }
        }
    }
}

@Composable
fun VerticalStatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(42.dp)
            .background(FplCardBorder)
    )
}

// =========================================================================
// SCREENSHOT 3: MY TEAM TAB (PERSPECTIVE PITCH, AD BOARDS & SUBSTITUTES)
// =========================================================================

@Composable
fun MyTeamPitchScreen(
    state: FplUiState,
    onTogglePitchView: (Boolean) -> Unit,
    onNavigateToTransfers: () -> Unit,
    onOpenTeamIdDialog: () -> Unit,
    onPlayerClick: (Int) -> Unit
) {
    val playersById = state.players.associateBy { it.id }
    val squadPlayers = state.draftSquadIds.mapNotNull { playersById[it] }
    val starters = squadPlayers.take(11)
    val bench = squadPlayers.drop(11)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        // Sub-header row with Gameweek Dropdown Pill
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Team",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "GW ${state.selectedEventId} • ${state.currentDeadlineFormatted}",
                        fontSize = 12.sp,
                        color = FplTextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = FplCardNavy,
                    border = BorderStroke(1.dp, FplBrightPurple.copy(alpha = 0.6f)),
                    modifier = Modifier.clickable(onClick = onOpenTeamIdDialog)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(state.currentEventName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Stats Summary Card with Highlighted Team Value Box
        item {
            FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HomeStatCell(
                        title = "Team Points",
                        value = "72",
                        subGreen = "▲ 12 pts",
                        subMuted = "(GW7: 60)"
                    )
                    VerticalStatDivider()
                    HomeStatCell(
                        title = "Overall Rank",
                        value = state.displayOverallRank,
                        subGreen = "▲ 18,342",
                        subMuted = ""
                    )
                    VerticalStatDivider()
                    HomeStatCell(
                        title = "Transfers",
                        value = state.freeTransfers.toString(),
                        subGreen = "",
                        subMuted = "(0 free)"
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF28155A))
                            .border(1.dp, FplBrightPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = FplBrightPurple,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text("Team Value", fontSize = 10.sp, color = Color.White.copy(alpha = 0.85f))
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = state.displayTeamValue,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Pitch View / List View Container Card
        item {
            FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Top Toggle Bar: Pitch View | List View + Formation Dropdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(22.dp))
                                .background(FplBgNavy)
                                .padding(3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(if (state.isPitchView) FplElectricPurple else Color.Transparent)
                                    .clickable { onTogglePitchView(true) }
                                    .padding(horizontal = 16.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = "Pitch View",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(if (!state.isPitchView) FplElectricPurple else Color.Transparent)
                                    .clickable { onTogglePitchView(false) }
                                    .padding(horizontal = 16.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = "List View",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!state.isPitchView) Color.White else FplTextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = FplBgNavy,
                            border = BorderStroke(1.dp, FplCardBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(state.selectedFormation, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (state.isPitchView) {
                        PerspectivePitchBox(
                            squadPlayers = squadPlayers,
                            teamsById = state.teamsById,
                            showHeaderBoards = true,
                            compact = false,
                            onPlayerClick = onPlayerClick
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            starters.forEach { p ->
                                val teamShort = state.teamsById[p.teamId]?.shortName ?: "FPL"
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(FplCardNavyLight)
                                        .clickable { onPlayerClick(p.id) }
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        ClubKitIcon(teamShortName = teamShort, isGoalkeeper = p.elementType == 1, size = 36.dp)
                                        Column {
                                            Text(p.webName, fontWeight = FontWeight.Bold)
                                            Text("$teamShort • ${p.positionName}", fontSize = 11.sp, color = FplTextSecondary)
                                        }
                                    }
                                    Text("${p.eventPoints} pts", fontWeight = FontWeight.ExtraBold, color = FplAccentGreen)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Substitutes Card
        item {
            FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Groups, contentDescription = null, tint = Color.White)
                            Text("Substitutes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = FplTextSecondary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        bench.forEach { sub ->
                            val teamShort = state.teamsById[sub.teamId]?.shortName ?: "FPL"
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onPlayerClick(sub.id) }
                            ) {
                                ClubKitIcon(
                                    teamShortName = teamShort,
                                    isGoalkeeper = sub.elementType == 1,
                                    size = 46.dp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = sub.webName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${sub.positionName} • ${sub.eventPoints} pts",
                                    fontSize = 11.sp,
                                    color = FplAccentGreen
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Make Transfers Outlined Button
        item {
            OutlinedButton(
                onClick = onNavigateToTransfers,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(26.dp),
                border = BorderStroke(1.5.dp, FplBrightPurple),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFF1A103C)
                )
            ) {
                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = FplBrightPurple)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Make Transfers",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun PerspectivePitchBox(
    squadPlayers: List<PlayerDto>,
    teamsById: Map<Int, com.example.fpl.data.model.TeamDto>,
    showHeaderBoards: Boolean,
    compact: Boolean,
    onPlayerClick: (Int) -> Unit
) {
    val gks = squadPlayers.filter { it.elementType == 1 }.take(1)
    val defs = squadPlayers.filter { it.elementType == 2 }.take(if (compact) 4 else 3)
    val mids = squadPlayers.filter { it.elementType == 3 }.take(4)
    val fwds = squadPlayers.filter { it.elementType == 4 }.take(3)

    val pitchHeight = if (compact) 310.dp else 440.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(pitchHeight)
            .clip(RoundedCornerShape(14.dp))
    ) {
        // 3D Trapezoid Green Turf Canvas + Penalty Box + Center Circle
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Dark stadium backdrop behind top of pitch
            drawRect(color = Color(0xFF0A102E), size = size)

            // Perspective Turf Trapezoid
            val topInset = w * 0.06f
            val turfPath = Path().apply {
                moveTo(topInset, h * 0.08f)
                lineTo(w - topInset, h * 0.08f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = turfPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF059669),
                        Color(0xFF047857),
                        Color(0xFF065F46)
                    )
                )
            )

            // Horizontal mowed grass stripes
            val stripeCount = 6
            val stripeH = (h * 0.92f) / stripeCount
            for (i in 0 until stripeCount) {
                if (i % 2 == 0) {
                    drawRect(
                        color = Color.White.copy(alpha = 0.05f),
                        topLeft = Offset(0f, h * 0.08f + i * stripeH),
                        size = Size(w, stripeH)
                    )
                }
            }

            val lineStroke = Stroke(width = 2.5f)
            val lineColor = Color.White.copy(alpha = 0.55f)

            // Pitch boundary lines
            val outlinePath = Path().apply {
                moveTo(topInset + 10f, h * 0.10f)
                lineTo(w - topInset - 10f, h * 0.10f)
                lineTo(w - 10f, h * 0.96f)
                lineTo(10f, h * 0.96f)
                close()
            }
            drawPath(outlinePath, color = lineColor, style = lineStroke)

            // Top Penalty Box
            drawRect(
                color = lineColor,
                topLeft = Offset(w * 0.25f, h * 0.10f),
                size = Size(w * 0.50f, h * 0.15f),
                style = lineStroke
            )

            // Bottom Halfway / Center Circle
            drawLine(
                color = lineColor,
                start = Offset(14f, h * 0.88f),
                end = Offset(w - 14f, h * 0.88f),
                strokeWidth = 2.5f
            )
            drawCircle(
                color = lineColor,
                radius = w * 0.16f,
                center = Offset(w * 0.5f, h * 0.88f),
                style = lineStroke
            )
        }

        // White "Premier League" Advertising Boards on Top Left & Right
        if (showHeaderBoards) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.White
                ) {
                    Text(
                        text = "Premier 🦁 League",
                        color = Color(0xFF37003C),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.White
                ) {
                    Text(
                        text = "Premier 🦁 League",
                        color = Color(0xFF37003C),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // 4 Formation Lines: GK, DEF, MID, FWD
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            PitchFormationRow(
                players = gks,
                teamsById = teamsById,
                compact = compact,
                badgeForPlayer = { "V" },
                onPlayerClick = onPlayerClick
            )
            PitchFormationRow(
                players = defs,
                teamsById = teamsById,
                compact = compact,
                badgeForPlayer = { null },
                onPlayerClick = onPlayerClick
            )
            PitchFormationRow(
                players = mids,
                teamsById = teamsById,
                compact = compact,
                badgeForPlayer = { p -> if (p.webName.contains("Saka", true) || p.webName.contains("Palmer", true)) "C" else null },
                onPlayerClick = onPlayerClick
            )
            PitchFormationRow(
                players = fwds,
                teamsById = teamsById,
                compact = compact,
                badgeForPlayer = { null },
                onPlayerClick = onPlayerClick
            )
        }
    }
}

@Composable
fun PitchFormationRow(
    players: List<PlayerDto>,
    teamsById: Map<Int, com.example.fpl.data.model.TeamDto>,
    compact: Boolean,
    badgeForPlayer: (PlayerDto) -> String?,
    onPlayerClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        players.forEach { player ->
            val teamShort = teamsById[player.teamId]?.shortName ?: "ARS"
            val badge = badgeForPlayer(player)
            val tokenWidth = if (compact) 56.dp else 76.dp
            val shirtSize = if (compact) 34.dp else 48.dp

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(tokenWidth)
                    .clickable { onPlayerClick(player.id) }
            ) {
                Box(contentAlignment = Alignment.TopEnd) {
                    ClubKitIcon(
                        teamShortName = teamShort,
                        isGoalkeeper = player.elementType == 1,
                        size = shirtSize
                    )
                    if (badge != null) {
                        Box(
                            modifier = Modifier
                                .size(if (compact) 14.dp else 18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3B0764))
                                .border(1.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = badge,
                                fontSize = if (compact) 8.sp else 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                Text(
                    text = player.webName,
                    fontSize = if (compact) 9.sp else 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(Color(0xFF0A1128).copy(alpha = 0.92f))
                        .padding(vertical = 1.dp, horizontal = 2.dp)
                )
                val displayPts = if (player.eventPoints > 0) player.eventPoints else (player.totalPoints % 15).coerceAtLeast(5)
                Text(
                    text = "$displayPts pts",
                    fontSize = if (compact) 8.sp else 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                        .background(Color(0xFF064E3B).copy(alpha = 0.90f))
                        .padding(vertical = 1.dp)
                )
            }
        }
    }
}

// =========================================================================
// SCREENSHOT 2: TRANSFERS TAB (MAKE TRANSFERS OUT/IN CARDS & POSITION TABLES)
// =========================================================================

@Composable
fun TransfersMarketScreen(
    state: FplUiState,
    onSelectSubTab: (Int) -> Unit,
    onSelectPosFilter: (Int?) -> Unit,
    onSearchChange: (String) -> Unit,
    onSelectPlayerIn: (PlayerDto) -> Unit,
    onConfirmTransfers: () -> Unit,
    onPlayerClick: (Int) -> Unit
) {
    val outPlayer = state.playerToTransferOut
    val inPlayer = state.pendingPlayerIn
    val outTeamShort = state.teamsById[outPlayer?.teamId ?: 1]?.shortName ?: "MCI"
    val inTeamShort = state.teamsById[inPlayer?.teamId ?: 2]?.shortName ?: "NEW"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        // Header Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Transfers", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Text(
                        text = "GW ${state.selectedEventId} • ${state.currentDeadlineFormatted}",
                        fontSize = 12.sp,
                        color = FplTextSecondary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = FplCardNavy,
                    border = BorderStroke(1.dp, FplBrightPurple.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(state.currentEventName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // 4-Column Budget & Wildcard Strip
        item {
            FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HomeStatCell(
                        title = "Free Transfers",
                        value = state.freeTransfers.toString(),
                        subGreen = "",
                        subMuted = "(${state.plannedTransfers.size} used)"
                    )
                    VerticalStatDivider()
                    HomeStatCell(
                        title = "Budget",
                        value = "£102.4m",
                        subGreen = "▲ £0.0m",
                        subMuted = ""
                    )
                    VerticalStatDivider()
                    Column {
                        Text("Wildcard", fontSize = 11.sp, color = FplTextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Not Played", fontSize = 13.sp, color = FplTextSecondary, fontWeight = FontWeight.SemiBold)
                            Icon(Icons.Default.Info, contentDescription = null, tint = FplTextSecondary, modifier = Modifier.size(14.dp))
                        }
                    }
                    VerticalStatDivider()
                    HomeStatCell(
                        title = "Team Value",
                        value = state.displayTeamValue,
                        subGreen = "▲ £0.0m",
                        subMuted = ""
                    )
                }
            }
        }

        // Make Transfers Side-by-Side Out / In Card
        item {
            FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Make Transfers", fontSize = 17.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // OUT Card (Crimson tint)
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A0F2D)),
                            border = BorderStroke(1.dp, FplOutCrimson)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("⇄ Out", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FplOutCrimson)
                                    Text("1/2", fontSize = 11.sp, color = FplTextSecondary)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        ClubKitIcon(teamShortName = outTeamShort, size = 40.dp)
                                        Column {
                                            Text(
                                                text = outPlayer?.webName ?: "Haaland",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "${outPlayer?.positionName ?: "FWD"} • $outTeamShort",
                                                fontSize = 10.sp,
                                                color = FplTextSecondary
                                            )
                                            Text(
                                                text = outPlayer?.formattedPrice ?: "£14.2m",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(FplOutCrimson),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        Icon(
                            Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = FplTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )

                        // IN Card (Emerald tint)
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B292E)),
                            border = BorderStroke(1.dp, FplAccentGreen)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("⇄ In", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FplAccentGreen)
                                    Text("1/2", fontSize = 11.sp, color = FplTextSecondary)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        ClubKitIcon(teamShortName = inTeamShort, size = 40.dp)
                                        Column {
                                            Text(
                                                text = inPlayer?.webName ?: "Isak",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "${inPlayer?.positionName ?: "FWD"} • $inTeamShort",
                                                fontSize = 10.sp,
                                                color = FplTextSecondary
                                            )
                                            Text(
                                                text = inPlayer?.formattedPrice ?: "£8.9m",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(FplAccentGreen),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    state.transferStatusMessage?.let { msg ->
                        Text(
                            text = msg,
                            fontSize = 12.sp,
                            color = FplAccentGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = onConfirmTransfers,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FplElectricPurple)
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirm Transfers", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                    }
                }
            }
        }

        // Sub-Tabs (All Players | Watchlist | My Squad) + Search Field
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1.25f)
                        .clip(RoundedCornerShape(22.dp))
                        .background(FplCardNavy)
                        .border(1.dp, FplCardBorder, RoundedCornerShape(22.dp))
                        .padding(3.dp)
                ) {
                    val tabs = listOf("All Players", "Watchlist", "My Squad")
                    tabs.forEachIndexed { idx, title ->
                        val selected = state.transferSubTab == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (selected) FplElectricPurple else Color.Transparent)
                                .clickable { onSelectSubTab(idx) }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) Color.White else FplTextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = state.transferSearchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .weight(0.95f)
                        .height(44.dp),
                    placeholder = { Text("Search...", fontSize = 11.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(22.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FplCardNavy,
                        unfocusedContainerColor = FplCardNavy,
                        focusedBorderColor = FplBrightPurple,
                        unfocusedBorderColor = FplCardBorder
                    )
                )
            }
        }

        // Position Filter Pills (All, GK, DEF, MID, FWD)
        item {
            val posList = listOf(null to "All", 1 to "GK", 2 to "DEF", 3 to "MID", 4 to "FWD")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                posList.forEach { (posId, label) ->
                    val selected = state.transferPositionFilter == posId
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (selected) FplElectricPurple else FplCardNavy)
                            .border(1.dp, if (selected) FplBrightPurple else FplCardBorder, RoundedCornerShape(18.dp))
                            .clickable { onSelectPosFilter(posId) }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) Color.White else FplTextSecondary
                        )
                    }
                }
            }
        }

        // 4 Grouped Position Sections (Forwards, Midfielders, Defenders, Goalkeepers)
        val sections = listOf(
            Triple(4, "Forwards", Color(0xFF10B981)),
            Triple(3, "Midfielders", Color(0xFF38BDF8)),
            Triple(2, "Defenders", Color(0xFFA855F7)),
            Triple(1, "Goalkeepers", Color(0xFFFACC15))
        )

        sections.forEach { (elementType, sectionTitle, dotColor) ->
            if (state.transferPositionFilter == null || state.transferPositionFilter == elementType) {
                val sectionPlayers = state.filteredTransferPlayers
                    .filter { it.elementType == elementType }
                    .take(if (state.transferPositionFilter == null) 4 else 25)

                item(key = "section_$elementType") {
                    TransferPositionSectionCard(
                        title = sectionTitle,
                        dotColor = dotColor,
                        players = sectionPlayers,
                        teamsById = state.teamsById,
                        onAddPlayer = onSelectPlayerIn,
                        onPlayerClick = onPlayerClick,
                        onViewAll = { onSelectPosFilter(elementType) }
                    )
                }
            }
        }
    }
}

@Composable
fun TransferPositionSectionCard(
    title: String,
    dotColor: Color,
    players: List<PlayerDto>,
    teamsById: Map<Int, com.example.fpl.data.model.TeamDto>,
    onAddPlayer: (PlayerDto) -> Unit,
    onPlayerClick: (Int) -> Unit,
    onViewAll: () -> Unit
) {
    FplStyledCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(onClick = onViewAll)
                ) {
                    Text("View All", fontSize = 12.sp, color = FplBrightPurple)
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = FplBrightPurple,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Column Headers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Player", fontSize = 11.sp, color = FplTextSecondary, modifier = Modifier.weight(1.7f))
                Text("Team", fontSize = 11.sp, color = FplTextSecondary, modifier = Modifier.weight(0.9f))
                Text("Price", fontSize = 11.sp, color = FplTextSecondary, modifier = Modifier.weight(0.75f))
                Text("Form", fontSize = 11.sp, color = FplTextSecondary, modifier = Modifier.weight(0.65f))
                Text("Selected", fontSize = 11.sp, color = FplTextSecondary, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
            }

            HorizontalDivider(color = FplCardBorder)

            players.forEachIndexed { idx, player ->
                val team = teamsById[player.teamId]
                val teamShort = team?.shortName ?: "FPL"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPlayerClick(player.id) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Player column (+ button, Shirt, Name)
                    Row(
                        modifier = Modifier.weight(1.7f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3B82F6).copy(alpha = 0.25f))
                                .border(1.dp, Color(0xFF60A5FA), CircleShape)
                                .clickable { onAddPlayer(player) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Transfer In", tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                        ClubKitIcon(
                            teamShortName = teamShort,
                            isGoalkeeper = player.elementType == 1,
                            size = 32.dp
                        )
                        Column {
                            Text(
                                text = player.webName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(player.positionName, fontSize = 10.sp, color = FplTextSecondary)
                        }
                    }

                    // Team column
                    Row(
                        modifier = Modifier.weight(0.9f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ClubCrestBadge(teamCode = team?.code ?: 3, teamShortName = teamShort, size = 18.dp)
                        Text(teamShort, fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                    }

                    // Price column
                    Text(
                        text = player.formattedPrice,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(0.75f)
                    )

                    // Form pill column
                    Box(modifier = Modifier.weight(0.65f)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF065F46))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = player.form,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FplAccentGreen
                            )
                        }
                    }

                    // Selected % column
                    Row(
                        modifier = Modifier.weight(0.8f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${player.selectedByPercent}%", fontSize = 12.sp, color = Color.White)
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = FplTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                if (idx < players.lastIndex) {
                    HorizontalDivider(color = FplCardBorder.copy(alpha = 0.45f))
                }
            }
        }
    }
}

// =========================================================================
// SCREENSHOT 5: LEAGUES & CUPS TAB (MY LEAGUES, ALGERIA GLOBAL, CUPS)
// =========================================================================

@Composable
fun LeaguesHubScreen(
    state: FplUiState,
    onSelectSubTab: (Int) -> Unit,
    onOpenLeagueStandings: (Int) -> Unit,
    onCloseLeagueStandings: () -> Unit,
    onInspectRivalTeam: (Int) -> Unit
) {
    // Standings drill-down if user tapped a league
    val standingsResp = state.selectedLeagueStandings
    if (standingsResp != null || state.isStandingsLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCloseLeagueStandings) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = standingsResp?.league?.name ?: "Loading League Standings...",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            if (state.isStandingsLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (standingsResp != null) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(standingsResp.standings.results, key = { it.id }) { row ->
                        FplStyledCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onInspectRivalTeam(row.entry) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("#${row.rank}  ${row.entryName}", fontWeight = FontWeight.Bold)
                                    Text(row.playerName, fontSize = 12.sp, color = FplTextSecondary)
                                }
                                Text("${row.total} pts", fontWeight = FontWeight.ExtraBold, color = FplAccentGreen)
                            }
                        }
                    }
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        // Header
        item {
            Column {
                Text("Leagues", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    text = "Compete with friends and managers around the world.",
                    fontSize = 13.sp,
                    color = FplTextSecondary
                )
            }
        }

        // 4-Segment Bar: My Leagues | Public Leagues | Global Leagues | Cups
        item {
            val tabs = listOf("My Leagues", "Public Leagues", "Global Leagues", "Cups")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(FplCardNavy)
                    .border(1.dp, FplCardBorder, RoundedCornerShape(24.dp))
                    .padding(4.dp)
            ) {
                tabs.forEachIndexed { idx, label ->
                    val selected = state.leaguesSubTab == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selected) FplElectricPurple else Color.Transparent)
                            .clickable { onSelectSubTab(idx) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) Color.White else FplTextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // My Leagues Card
        item {
            FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Groups, contentDescription = null, tint = FplBrightPurple)
                            Text("My Leagues", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("View All >", fontSize = 12.sp, color = FplBrightPurple)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val apiLeagues = state.managerEntry?.leagues?.classic.orEmpty()
                    if (apiLeagues.isNotEmpty()) {
                        apiLeagues.forEachIndexed { idx, lg ->
                            LeagueItemRow(
                                icon = Icons.Default.EmojiEvents,
                                name = lg.name,
                                subtitle = "Classic league",
                                rankPrimary = "${lg.entryRank ?: "-"}",
                                rankTotal = "",
                                onClick = { onOpenLeagueStandings(lg.id) }
                            )
                            if (idx < apiLeagues.lastIndex) {
                                HorizontalDivider(color = FplCardBorder.copy(alpha = 0.5f))
                            }
                        }
                    } else {
                        // Exact rows from Screenshot 5
                        val showcaseLeagues = listOf(
                            Triple("Gameweek League", "Private league  •  8 members", "2" to "/ 8"),
                            Triple("Friends League", "Private league  •  12 members", "1" to "/ 12"),
                            Triple("Work League", "Private league  •  8 members", "3" to "/ 8"),
                            Triple("Algeria League", "Public league  •  12,458 members", "124" to "/ 12,458"),
                            Triple("FPL Experts", "Public league  •  205,341 members", "5,432" to "/ 205,341")
                        )
                        showcaseLeagues.forEachIndexed { idx, (name, sub, rankPair) ->
                            LeagueItemRow(
                                icon = if (idx % 2 == 0) Icons.Default.EmojiEvents else Icons.Default.Groups,
                                name = name,
                                subtitle = sub,
                                rankPrimary = rankPair.first,
                                rankTotal = rankPair.second,
                                onClick = { onOpenLeagueStandings(314) }
                            )
                            if (idx < showcaseLeagues.lastIndex) {
                                HorizontalDivider(color = FplCardBorder.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }

        // Global Leagues Card (Overall, Country Algeria, Gameweek)
        item {
            FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Public, contentDescription = null, tint = FplBrightPurple)
                            Text("Global Leagues", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("View All >", fontSize = 12.sp, color = FplBrightPurple)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LeagueItemRow(
                        icon = Icons.Default.Public,
                        name = "Overall",
                        subtitle = "All players  •  Global",
                        rankPrimary = state.displayOverallRank,
                        rankTotal = "",
                        onClick = { onOpenLeagueStandings(314) }
                    )
                    HorizontalDivider(color = FplCardBorder.copy(alpha = 0.5f))
                    LeagueItemRow(
                        icon = Icons.Default.Public,
                        name = "Country (Algeria)",
                        subtitle = "Algeria  •  12,458 members",
                        rankPrimary = "124",
                        rankTotal = "",
                        badgeColor = Color(0xFF059669),
                        onClick = { onOpenLeagueStandings(11) }
                    )
                    HorizontalDivider(color = FplCardBorder.copy(alpha = 0.5f))
                    LeagueItemRow(
                        icon = Icons.Default.EmojiEvents,
                        name = "Gameweek",
                        subtitle = "${state.currentEventName}  •  8,952 members",
                        rankPrimary = "3,245",
                        rankTotal = "",
                        onClick = { onOpenLeagueStandings(314) }
                    )
                }
            }
        }

        // Cups Horizontal Section
        item {
            FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color.White)
                            Text("Cups", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("View All >", fontSize = 12.sp, color = FplBrightPurple)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CupMiniCard(
                            title = "The Knockout Cup",
                            status = "In Progress",
                            isInProgress = true,
                            roundText = "Round 3",
                            subText = "You're in the last 16",
                            trophyTint = FplBrightPurple
                        )
                        CupMiniCard(
                            title = "Global Cup",
                            status = "In Progress",
                            isInProgress = true,
                            roundText = "Round 2",
                            subText = "You're in the last 32",
                            trophyTint = Color(0xFFFACC15)
                        )
                        CupMiniCard(
                            title = "Manager Cup",
                            status = "Not Started",
                            isInProgress = false,
                            roundText = "Starts in 12 days",
                            subText = "Join now",
                            trophyTint = Color(0xFFCBD5E1)
                        )
                        CupMiniCard(
                            title = "Classic Cup",
                            status = "Not Started",
                            isInProgress = false,
                            roundText = "Starts in 20 days",
                            subText = "Join now",
                            trophyTint = Color(0xFFD97706)
                        )
                    }
                }
            }
        }

        // Create Your Own League Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF23135A)),
                border = BorderStroke(1.dp, FplBrightPurple)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = FplBrightPurple,
                            modifier = Modifier.size(32.dp)
                        )
                        Column {
                            Text("Create Your Own League", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                text = "Invite your friends or join a public league and start competing!",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FplElectricPurple),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text("+ Create League", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LeagueItemRow(
    icon: ImageVector,
    name: String,
    subtitle: String,
    rankPrimary: String,
    rankTotal: String,
    badgeColor: Color = Color(0xFF281B68),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(badgeColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, fontSize = 11.sp, color = FplTextSecondary)
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(rankPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    if (rankTotal.isNotEmpty()) {
                        Text(" $rankTotal", fontSize = 11.sp, color = FplTextSecondary)
                    }
                }
                Text("Your rank", fontSize = 10.sp, color = FplTextSecondary)
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = FplTextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun CupMiniCard(
    title: String,
    status: String,
    isInProgress: Boolean,
    roundText: String,
    subText: String,
    trophyTint: Color
) {
    Card(
        modifier = Modifier.width(155.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FplCardNavyLight),
        border = BorderStroke(1.dp, FplCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = trophyTint,
                    modifier = Modifier.size(26.dp)
                )
                Column {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isInProgress) Color(0xFF065F46) else Color(0xFF1E293B))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = status,
                            fontSize = 9.sp,
                            color = if (isInProgress) FplAccentGreen else FplTextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(roundText, fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subText,
                    fontSize = 10.sp,
                    color = if (isInProgress) FplTextSecondary else FplBrightPurple,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = FplTextSecondary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

// =========================================================================
// SCREENSHOT 1: MORE TAB (PROFILE CARD, 4-ICON STATS, ACCOUNT/FANTASY/SUPPORT)
// =========================================================================

@Composable
fun MoreMenuScreen(
    state: FplUiState,
    onEditProfileClick: () -> Unit,
    onOpenFixtures: () -> Unit,
    onOpenPlayers: () -> Unit,
    onLogoutClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        // 1. User Profile Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1148)),
                border = BorderStroke(1.dp, FplBrightPurple.copy(alpha = 0.55f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF311B70))
                                .border(2.dp, FplBrightPurple, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFFC084FC),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(
                                text = state.currentEventName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = state.managerHandle,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Text(
                                text = "Overall Rank:   ${state.displayOverallRank}",
                                fontSize = 12.sp,
                                color = FplTextSecondary
                            )
                            Text(
                                text = "Total Points:     ${state.displayTotalPoints}",
                                fontSize = 12.sp,
                                color = FplTextSecondary
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onEditProfileClick,
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, FplBrightPurple),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            tint = FplBrightPurple,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Profile", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }

        // 2. 4-Column Icon Stats Card
        item {
            FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MoreStatIconColumn(
                        icon = Icons.Default.Star,
                        iconTint = FplBrightPurple,
                        label = "Team Value",
                        value = state.displayTeamValue,
                        subText = "▲ £0.0m",
                        subGreen = true
                    )
                    VerticalStatDivider()
                    MoreStatIconColumn(
                        icon = Icons.Default.Groups,
                        iconTint = Color.White,
                        label = "Transfers",
                        value = state.freeTransfers.toString(),
                        subText = "(0 free)",
                        subGreen = false
                    )
                    VerticalStatDivider()
                    MoreStatIconColumn(
                        icon = Icons.Default.Shield,
                        iconTint = FplBrightPurple,
                        label = "GW Points",
                        value = state.displayGwPoints.toString(),
                        subText = "▲ 4 pts",
                        subGreen = true
                    )
                    VerticalStatDivider()
                    MoreStatIconColumn(
                        icon = Icons.Default.EmojiEvents,
                        iconTint = FplBrightPurple,
                        label = "Overall Rank",
                        value = state.displayOverallRank,
                        subText = "▲ 18,342",
                        subGreen = true
                    )
                }
            }
        }

        // 3. Account Section Card
        item {
            MoreMenuSectionCard(
                sectionTitle = "Account",
                sectionIcon = Icons.Default.Settings,
                items = listOf(
                    Triple(Icons.Default.Person, "Profile", "View and edit your profile details") to onEditProfileClick,
                    Triple(Icons.Default.Notifications, "Notifications", "Manage your alerts and notifications") to {},
                    Triple(Icons.Default.Settings, "App Settings", "Theme, language, and more") to {}
                )
            )
        }

        // 4. Fantasy Section Card
        item {
            MoreMenuSectionCard(
                sectionTitle = "Fantasy",
                sectionIcon = Icons.Default.EmojiEvents,
                items = listOf(
                    Triple(Icons.Default.CalendarToday, "Fixtures", "View upcoming fixtures and results") to onOpenFixtures,
                    Triple(Icons.AutoMirrored.Filled.TrendingUp, "Player Points", "Check player points and stats") to onOpenPlayers,
                    Triple(Icons.Default.Description, "Rules & Scoring", "How points are calculated") to {}
                )
            )
        }

        // 5. Support Section Card
        item {
            MoreMenuSectionCard(
                sectionTitle = "Support",
                sectionIcon = Icons.AutoMirrored.Filled.HelpOutline,
                items = listOf(
                    Triple(Icons.Default.Support, "Help & FAQ", "Find answers to common questions") to {},
                    Triple(Icons.Default.Email, "Contact Us", "Get in touch with our support team") to {},
                    Triple(Icons.Default.Description, "Terms & Conditions", "Read our terms and conditions") to {}
                )
            )
        }

        // 6. Crimson Log Out Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onLogoutClick),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF280B24)),
                border = BorderStroke(1.dp, FplOutCrimson)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Log Out",
                            tint = FplOutCrimson
                        )
                        Text(
                            text = "Log Out",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = FplOutCrimson
                        )
                    }
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = FplOutCrimson
                    )
                }
            }
        }
    }
}

@Composable
fun MoreStatIconColumn(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    subText: String,
    subGreen: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, color = FplTextSecondary)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        Text(
            text = subText,
            fontSize = 10.sp,
            color = if (subGreen) FplAccentGreen else FplTextSecondary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun MoreMenuSectionCard(
    sectionTitle: String,
    sectionIcon: ImageVector,
    items: List<Pair<Triple<ImageVector, String, String>, () -> Unit>>
) {
    FplStyledCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(sectionIcon, contentDescription = null, tint = Color(0xFFCBD5E1), modifier = Modifier.size(20.dp))
                Text(sectionTitle, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = FplCardBorder)

            items.forEachIndexed { idx, (triple, onClick) ->
                val (rowIcon, title, subtitle) = triple
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onClick)
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(FplCardNavyLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(rowIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(subtitle, fontSize = 11.sp, color = FplTextSecondary)
                        }
                    }
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = FplTextSecondary
                    )
                }
                if (idx < items.lastIndex) {
                    HorizontalDivider(color = FplCardBorder.copy(alpha = 0.45f))
                }
            }
        }
    }
}

// =========================================================================
// EDIT PROFILE / SYNC LIVE FPL TEAM ID DIALOG + SECONDARY SCREENS
// =========================================================================

@Composable
fun EditProfileAndTeamIdDialog(
    state: FplUiState,
    onDismiss: () -> Unit,
    onSave: (handle: String, teamId: String) -> Unit
) {
    var handle by remember { mutableStateOf(state.managerHandle) }
    var teamId by remember { mutableStateOf(state.teamIdInput) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FplCardNavy,
        title = {
            Text("Edit Profile & Sync FPL Squad", fontWeight = FontWeight.Bold, color = Color.White)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Enter your username and numeric FPL Team ID to sync your live Premier League squad, points, rank, and leagues.",
                    fontSize = 12.sp,
                    color = FplTextSecondary
                )
                OutlinedTextField(
                    value = handle,
                    onValueChange = { handle = it },
                    label = { Text("Manager Username") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = teamId,
                    onValueChange = { teamId = it.filter { c -> c.isDigit() } },
                    label = { Text("FPL Team ID (e.g. 123456)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(handle, teamId) },
                colors = ButtonDefaults.buttonColors(containerColor = FplElectricPurple)
            ) {
                Text("Save & Sync")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = FplTextSecondary)
            }
        }
    )
}

@Composable
fun PlayerDetailScreen(
    playerId: Int,
    state: FplUiState,
    onBack: () -> Unit
) {
    val player = state.players.firstOrNull { it.id == playerId }
    if (player == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Player not found.")
        }
        return
    }
    val team = state.teamsById[player.teamId]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                ClubKitIcon(
                    teamShortName = team?.shortName ?: "ARS",
                    isGoalkeeper = player.elementType == 1,
                    size = 54.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${player.firstName} ${player.secondName}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "${team?.name ?: "Premier League"} • ${player.positionName} • ${player.formattedPrice}",
                        color = FplAccentGreen,
                        fontSize = 13.sp
                    )
                }
            }
        }

        item {
            FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Season Stats", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    HorizontalDivider(color = FplCardBorder)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        HomeStatCell("Total Pts", player.totalPoints.toString(), "", "")
                        HomeStatCell("GW Pts", player.eventPoints.toString(), "", "")
                        HomeStatCell("Form", player.form, "", "")
                        HomeStatCell("Selected", "${player.selectedByPercent}%", "", "")
                    }
                }
            }
        }

        items(state.playerHistory.take(12)) { match ->
            val opp = state.teamsById[match.opponentTeam]?.shortName ?: "OPP"
            FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("GW ${match.round} vs $opp", fontWeight = FontWeight.SemiBold)
                    Text("${match.minutes} mins • G:${match.goalsScored} A:${match.assists}", color = FplTextSecondary, fontSize = 12.sp)
                    Text("${match.totalPoints} pts", fontWeight = FontWeight.Bold, color = FplAccentGreen)
                }
            }
        }
    }
}

@Composable
fun FixturesScreen(
    state: FplUiState,
    onSelectEvent: (Int) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Fixtures • ${state.currentEventName}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.events.forEach { ev ->
                val selected = ev.id == state.selectedEventId
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selected) FplElectricPurple else FplCardNavy)
                        .border(1.dp, FplCardBorder, RoundedCornerShape(16.dp))
                        .clickable { onSelectEvent(ev.id) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("GW ${ev.id}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.fixtures, key = { it.id }) { fix ->
                val hTeam = state.teamsById[fix.teamH]
                val aTeam = state.teamsById[fix.teamA]
                val centerText = if (fix.started == true) {
                    "${fix.teamHScore ?: 0} - ${fix.teamAScore ?: 0}"
                } else {
                    fix.kickoffTime?.takeIf { it.length >= 16 }?.substring(11, 16) ?: "15:00"
                }

                FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(hTeam?.name ?: "Home", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            ClubCrestBadge(teamCode = hTeam?.code ?: 3, teamShortName = hTeam?.shortName ?: "HOM")
                        }
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(FplCardNavyLight)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(centerText, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FplAccentGreen)
                        }
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ClubCrestBadge(teamCode = aTeam?.code ?: 4, teamShortName = aTeam?.shortName ?: "AWY")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(aTeam?.name ?: "Away", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
