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
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
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
import coil.request.ImageRequest
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
// REAL PLAYER FACE AVATAR (OFFICIAL PL CDN + FALLBACK) & BRAND GRAPHICS
// =========================================================================

@Composable
fun PlayerPhotoAvatar(
    player: PlayerDto,
    teamShortName: String = "FPL",
    size: Dp = 48.dp,
    borderColor: Color = Color.White.copy(alpha = 0.65f)
) {
    val context = LocalContext.current
    // Extract clean numeric code from player.code or player.photo / player.photoUrl
    val rawCode = when {
        player.code != 0 -> player.code.toString()
        player.photo.isNotBlank() -> player.photo.substringBefore(".")
        else -> player.photoUrl.substringAfterLast("/p").substringBefore(".png")
    }
    val primaryUrl = "https://resources.premierleague.com/premierleague/photos/players/110x140/p$rawCode.png"
    val secondaryUrl = "https://resources.premierleague.com/premierleague25/photos/players/110x140/$rawCode.png"

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFF1A2252))
            .border(1.5.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(primaryUrl)
                .setHeader("User-Agent", "Mozilla/5.0")
                .crossfade(true)
                .build(),
            contentDescription = player.webName,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize(),
            error = {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(secondaryUrl)
                        .setHeader("User-Agent", "Mozilla/5.0")
                        .crossfade(true)
                        .build(),
                    contentDescription = player.webName,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter,
                    modifier = Modifier.fillMaxSize(),
                    error = {
                        ClubKitIcon(
                            teamShortName = teamShortName,
                            isGoalkeeper = player.elementType == 1,
                            size = size * 0.78f
                        )
                    }
                )
            }
        )
    }
}

@Composable
fun PremierLeagueLionLogo(
    size: Dp = 42.dp,
    tint: Color = Color.White
) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        drawCircle(
            color = tint,
            radius = w * 0.44f,
            center = Offset(w * 0.5f, h * 0.54f),
            style = Stroke(width = w * 0.07f)
        )

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
                        contentDescription = "Refresh",
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

        val leftSleeve = Path().apply {
            moveTo(w * 0.24f, h * 0.14f)
            lineTo(w * 0.05f, h * 0.36f)
            lineTo(w * 0.18f, h * 0.48f)
            lineTo(w * 0.28f, h * 0.32f)
            close()
        }
        drawPath(leftSleeve, color = sleeveColor)

        val rightSleeve = Path().apply {
            moveTo(w * 0.76f, h * 0.14f)
            lineTo(w * 0.95f, h * 0.36f)
            lineTo(w * 0.82f, h * 0.48f)
            lineTo(w * 0.72f, h * 0.32f)
            close()
        }
        drawPath(rightSleeve, color = sleeveColor)

        val torso = Path().apply {
            moveTo(w * 0.24f, h * 0.14f)
            lineTo(w * 0.76f, h * 0.14f)
            lineTo(w * 0.74f, h * 0.90f)
            lineTo(w * 0.26f, h * 0.90f)
            close()
        }
        drawPath(torso, color = bodyColor)

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

        drawCircle(
            color = FplBgNavy,
            radius = w * 0.10f,
            center = Offset(w * 0.5f, h * 0.14f)
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
// FULL-SCREEN LOG IN & SIGN UP (EMAIL & PASSWORD ONLY — NO TEAM ID LOGIN)
// =========================================================================

@Composable
fun FplFullAuthScreen(
    state: FplUiState,
    onSelectSignUpMode: (Boolean) -> Unit,
    onLogin: (email: String, password: String) -> Unit,
    onSignUp: (fullName: String, email: String, password: String, confirmPassword: String) -> Unit,
    onClose: (() -> Unit)? = null
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isSignUpMode) {
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
                    PremierLeagueLionLogo(size = 60.dp)
                    Column {
                        Text(
                            text = "Fantasy",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Premier League",
                            fontSize = 17.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Create Your Account",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Sign up with your name, email, and password to start managing your Fantasy Premier League squad.",
                        fontSize = 13.sp,
                        color = FplTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

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
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )
                Spacer(modifier = Modifier.height(12.dp))
                AuthInputBox(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "At least 6 characters",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible },
                    keyboardType = KeyboardType.Password
                )
                Spacer(modifier = Modifier.height(12.dp))
                AuthInputBox(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm Password",
                    placeholder = "Re-enter your password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible },
                    keyboardType = KeyboardType.Password
                )

                state.loginError?.let { err ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = err,
                        color = FplOutCrimson,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = { onSignUp(fullName, email, password, confirmPassword) },
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
                Spacer(modifier = Modifier.height(20.dp))
                PremierLeagueLionLogo(size = 82.dp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Fantasy",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Premier League",
                    fontSize = 19.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Create your dream team. Compete with\nfriends. Win every week.",
                    fontSize = 13.sp,
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
                    placeholder = "Enter your email address",
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )
                Spacer(modifier = Modifier.height(12.dp))
                AuthInputBox(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "Enter your password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible },
                    keyboardType = KeyboardType.Password
                )

                state.loginError?.let { err ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = err,
                        color = FplOutCrimson,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = { onLogin(email, password) },
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

                Spacer(modifier = Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("New to FPL Companion? ", color = FplTextSecondary, fontSize = 13.sp)
                    Text(
                        text = "Create an Account",
                        color = FplBrightPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable { onSelectSignUpMode(true) }
                    )
                }

                if (onClose != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(onClick = onClose) {
                        Text("Back to App", color = FplTextSecondary)
                    }
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
    onTogglePassword: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder, color = FplTextSecondary) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done
        ),
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
// HOME TAB DASHBOARD (FIXED STAT WRAPPING + REAL PLAYER FACES ON PITCH)
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
    val squadPlayers = state.squadPlayers

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        // 1. Hero Banner
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
                    PremierLeagueLionLogo(size = 72.dp, tint = Color.White)
                }
            }
        }

        // 2. 4-Column Summary Stats Strip (Evenly weighted — no wrapping!)
        item {
            FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HomeStatCell(
                        modifier = Modifier.weight(1f),
                        title = "Total Points",
                        value = state.displayTotalPoints.toString(),
                        subGreen = "▲ 12 pts",
                        subMuted = ""
                    )
                    VerticalStatDivider()
                    HomeStatCell(
                        modifier = Modifier.weight(1f),
                        title = "GW Points",
                        value = state.displayGwPoints.toString(),
                        subGreen = "▲ 4 pts",
                        subMuted = ""
                    )
                    VerticalStatDivider()
                    HomeStatCell(
                        modifier = Modifier.weight(1.15f),
                        title = "Overall Rank",
                        value = state.displayOverallRank,
                        subGreen = "",
                        subMuted = "Global"
                    )
                    VerticalStatDivider()
                    HomeStatCell(
                        modifier = Modifier.weight(0.95f),
                        title = "Team Value",
                        value = state.displayTeamValue,
                        subGreen = state.displayBankValue,
                        subMuted = "bank"
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
                FplStyledCard(
                    modifier = Modifier.weight(1.38f)
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
                                ClubKitIcon(teamShortName = "ARS", size = 22.dp)
                                Column {
                                    Text(
                                        text = "My Team",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = state.selectedFormation,
                                        fontSize = 10.sp,
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
                                    Text("Pitch", fontSize = 10.sp, color = Color.White, maxLines = 1)
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
                    FplStyledCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onNavigateToLeagues)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = FplBrightPurple,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Your League",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = "Global League",
                                fontSize = 10.sp,
                                color = FplTextSecondary,
                                maxLines = 1
                            )
                            Text(
                                text = state.displayOverallRank,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "of 8,952,110",
                                fontSize = 10.sp,
                                color = FplTextSecondary
                            )
                        }
                    }

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
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Transfers",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = "${state.freeTransfersRemaining} free • ${state.displayBankValue} bank",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                maxLines = 1
                            )
                            Button(
                                onClick = onNavigateToTransfers,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FplElectricPurple)
                            ) {
                                Text(
                                    text = "Make Transfers >",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }

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
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Next Fixture",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
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
                                    size = 24.dp
                                )
                                Text("VS", fontSize = 10.sp, color = FplTextSecondary)
                                ClubCrestBadge(
                                    teamCode = awayTeam?.code ?: 54,
                                    teamShortName = awayTeam?.shortName ?: "FUL",
                                    size = 24.dp
                                )
                            }
                            OutlinedButton(
                                onClick = onNavigateToFixtures,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(32.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, FplBrightPurple)
                            ) {
                                Text("View Fixtures", fontSize = 10.sp, color = Color.White, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }

        // 4. Upcoming Fixtures
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
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                ClubCrestBadge(
                                    teamCode = hTeam?.code ?: 43,
                                    teamShortName = hTeam?.shortName ?: "MCI",
                                    size = 22.dp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(FplCardNavyLight)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = timeLabel,
                                    fontSize = 11.sp,
                                    color = FplTextSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ClubCrestBadge(
                                    teamCode = aTeam?.code ?: 54,
                                    teamShortName = aTeam?.shortName ?: "FUL",
                                    size = 22.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = aTeam?.name ?: "Fulham",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
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
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subGreen: String,
    subMuted: String
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 10.sp,
            color = FplTextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (subGreen.isNotEmpty()) {
                Text(
                    text = subGreen,
                    fontSize = 9.sp,
                    color = FplAccentGreen,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
            if (subMuted.isNotEmpty()) {
                Text(
                    text = subMuted,
                    fontSize = 9.sp,
                    color = FplTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun VerticalStatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(38.dp)
            .background(FplCardBorder)
    )
}

// =========================================================================
// MY TEAM TAB (REAL PLAYER FACES ON PITCH & BENCH + FIXED TEAM VALUE TEXT)
// =========================================================================

@Composable
fun MyTeamPitchScreen(
    state: FplUiState,
    onTogglePitchView: (Boolean) -> Unit,
    onNavigateToTransfers: () -> Unit,
    onPlayerClick: (Int) -> Unit
) {
    val squadPlayers = state.squadPlayers
    val starters = squadPlayers.take(11)
    val bench = squadPlayers.drop(11)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        // Sub-header row
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

        // Fixed 4-Column Stats Summary Card (No text wrapping on Team Value!)
        item {
            FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HomeStatCell(
                        modifier = Modifier.weight(0.9f),
                        title = "GW Points",
                        value = state.displayGwPoints.toString(),
                        subGreen = "▲ 12 pts",
                        subMuted = ""
                    )
                    VerticalStatDivider()
                    HomeStatCell(
                        modifier = Modifier.weight(1.15f),
                        title = "Overall Rank",
                        value = state.displayOverallRank,
                        subGreen = "▲ 18.3k",
                        subMuted = ""
                    )
                    VerticalStatDivider()
                    HomeStatCell(
                        modifier = Modifier.weight(0.85f),
                        title = "Transfers",
                        value = state.freeTransfersRemaining.toString(),
                        subGreen = "",
                        subMuted = "(${state.plannedTransfers.size} used)"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .weight(1.1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF28155A))
                            .border(1.dp, FplBrightPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = FplBrightPurple,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Team Value",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = state.displayTeamValue,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                maxLines = 1
                            )
                            Text(
                                text = "Bank: ${state.displayBankValue}",
                                fontSize = 9.sp,
                                color = FplAccentGreen,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
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
                                val displayPts = if (p.eventPoints > 0) p.eventPoints else (p.totalPoints % 9).coerceAtLeast(2)
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
                                        PlayerPhotoAvatar(
                                            player = p,
                                            teamShortName = teamShort,
                                            size = 42.dp
                                        )
                                        Column {
                                            Text(p.webName, fontWeight = FontWeight.Bold)
                                            Text("$teamShort • ${p.positionName} • ${p.formattedPrice}", fontSize = 11.sp, color = FplTextSecondary)
                                        }
                                    }
                                    Text("$displayPts pts", fontWeight = FontWeight.ExtraBold, color = FplAccentGreen)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Substitutes Card (with Real Player Faces!)
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
                            val subPts = if (sub.eventPoints > 0) sub.eventPoints else (sub.totalPoints % 6).coerceAtLeast(1)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onPlayerClick(sub.id) }
                            ) {
                                PlayerPhotoAvatar(
                                    player = sub,
                                    teamShortName = teamShort,
                                    size = 50.dp
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = sub.webName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${sub.positionName} • $subPts pts",
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
    val starters = squadPlayers.take(11)
    val gks = starters.filter { it.elementType == 1 }.ifEmpty { squadPlayers.filter { it.elementType == 1 }.take(1) }
    val defs = starters.filter { it.elementType == 2 }.ifEmpty { squadPlayers.filter { it.elementType == 2 }.take(3) }
    val mids = starters.filter { it.elementType == 3 }.ifEmpty { squadPlayers.filter { it.elementType == 3 }.take(4) }
    val fwds = starters.filter { it.elementType == 4 }.ifEmpty { squadPlayers.filter { it.elementType == 4 }.take(3) }

    val pitchHeight = if (compact) 340.dp else 460.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(pitchHeight)
            .clip(RoundedCornerShape(14.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawRect(color = Color(0xFF0A102E), size = size)

            val topInset = w * 0.05f
            val turfPath = Path().apply {
                moveTo(topInset, h * 0.06f)
                lineTo(w - topInset, h * 0.06f)
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

            val stripeCount = 6
            val stripeH = (h * 0.94f) / stripeCount
            for (i in 0 until stripeCount) {
                if (i % 2 == 0) {
                    drawRect(
                        color = Color.White.copy(alpha = 0.05f),
                        topLeft = Offset(0f, h * 0.06f + i * stripeH),
                        size = Size(w, stripeH)
                    )
                }
            }

            val lineStroke = Stroke(width = 2.5f)
            val lineColor = Color.White.copy(alpha = 0.55f)

            val outlinePath = Path().apply {
                moveTo(topInset + 8f, h * 0.08f)
                lineTo(w - topInset - 8f, h * 0.08f)
                lineTo(w - 8f, h * 0.97f)
                lineTo(8f, h * 0.97f)
                close()
            }
            drawPath(outlinePath, color = lineColor, style = lineStroke)

            drawRect(
                color = lineColor,
                topLeft = Offset(w * 0.25f, h * 0.08f),
                size = Size(w * 0.50f, h * 0.14f),
                style = lineStroke
            )

            drawLine(
                color = lineColor,
                start = Offset(12f, h * 0.89f),
                end = Offset(w - 12f, h * 0.89f),
                strokeWidth = 2.5f
            )
            drawCircle(
                color = lineColor,
                radius = w * 0.15f,
                center = Offset(w * 0.5f, h * 0.89f),
                style = lineStroke
            )
        }

        if (showHeaderBoards) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.White
                ) {
                    Text(
                        text = "Premier League",
                        color = Color(0xFF37003C),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.White
                ) {
                    Text(
                        text = "Premier League",
                        color = Color(0xFF37003C),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

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
                badgeForPlayer = { p -> if (p.id == mids.firstOrNull()?.id) "C" else null },
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
            val tokenWidth = if (compact) 54.dp else 76.dp
            val faceSize = if (compact) 32.dp else 50.dp

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(tokenWidth)
                    .clickable { onPlayerClick(player.id) }
            ) {
                Box(contentAlignment = Alignment.TopEnd) {
                    PlayerPhotoAvatar(
                        player = player,
                        teamShortName = teamShort,
                        size = faceSize,
                        borderColor = if (badge == "C") FplAccentGreen else Color.White.copy(alpha = 0.85f)
                    )
                    if (badge != null) {
                        Box(
                            modifier = Modifier
                                .size(if (compact) 13.dp else 18.dp)
                                .clip(CircleShape)
                                .background(if (badge == "C") FplElectricPurple else Color(0xFF3B0764))
                                .border(1.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = badge,
                                fontSize = if (compact) 7.sp else 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = player.webName,
                    fontSize = if (compact) 8.sp else 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(Color(0xFF0A1128).copy(alpha = 0.94f))
                        .padding(vertical = 1.dp, horizontal = 2.dp)
                )
                val displayPts = if (player.eventPoints > 0) player.eventPoints else (player.totalPoints % 9).coerceAtLeast(2)
                Text(
                    text = "$displayPts pts",
                    fontSize = if (compact) 7.sp else 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                        .background(Color(0xFF064E3B).copy(alpha = 0.94f))
                        .padding(vertical = 1.dp)
                )
            }
        }
    }
}

// =========================================================================
// INTERACTIVE TRANSFERS TAB (SQUAD SELECTOR, REAL FACES, LIVE BUDGET)
// =========================================================================

@Composable
fun TransfersMarketScreen(
    state: FplUiState,
    onSelectSubTab: (Int) -> Unit,
    onSelectPosFilter: (Int?) -> Unit,
    onSearchChange: (String) -> Unit,
    onSelectPlayerOut: (PlayerDto?) -> Unit,
    onSelectPlayerIn: (PlayerDto) -> Unit,
    onToggleWatchlist: (Int) -> Unit,
    onConfirmTransfers: () -> Unit,
    onResetTransfers: () -> Unit,
    onPlayerClick: (Int) -> Unit
) {
    val outPlayer = state.playerToTransferOut
    val inPlayer = state.pendingPlayerIn
    val outTeamShort = state.teamsById[outPlayer?.teamId ?: 1]?.shortName ?: "MCI"
    val inTeamShort = state.teamsById[inPlayer?.teamId ?: 2]?.shortName ?: "NEW"
    val squadPlayers = state.squadPlayers

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
                if (state.plannedTransfers.isNotEmpty()) {
                    OutlinedButton(
                        onClick = onResetTransfers,
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, FplBrightPurple),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset (${state.plannedTransfers.size})", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }

        // Fixed 4-Column Budget & Team Value Strip (No text wrapping!)
        item {
            FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HomeStatCell(
                        modifier = Modifier.weight(1f),
                        title = "Free Transfers",
                        value = state.freeTransfersRemaining.toString(),
                        subGreen = "",
                        subMuted = "(${state.plannedTransfers.size} made)"
                    )
                    VerticalStatDivider()
                    HomeStatCell(
                        modifier = Modifier.weight(1f),
                        title = "In Bank",
                        value = state.displayBankValue,
                        subGreen = if (state.remainingBankTenths >= 0) "Available" else "Over!",
                        subMuted = ""
                    )
                    VerticalStatDivider()
                    HomeStatCell(
                        modifier = Modifier.weight(1f),
                        title = "Wildcard",
                        value = "Active",
                        subGreen = "Ready",
                        subMuted = ""
                    )
                    VerticalStatDivider()
                    HomeStatCell(
                        modifier = Modifier.weight(1f),
                        title = "Team Value",
                        value = state.displayTeamValue,
                        subGreen = "15 players",
                        subMuted = ""
                    )
                }
            }
        }

        // Interactive Make Transfers Card
        item {
            FplStyledCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Make Transfers", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Step 1: Tap squad player OUT • Step 2: Tap replacement IN",
                            fontSize = 10.sp,
                            color = FplTextSecondary
                        )
                    }

                    // Interactive Horizontal Strip of User's 15 Squad Players
                    Text(
                        text = "Your Current Squad (Tap any player to transfer OUT):",
                        fontSize = 11.sp,
                        color = FplBrightPurple,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        squadPlayers.forEach { sp ->
                            val isSelectedOut = outPlayer?.id == sp.id
                            val spTeamShort = state.teamsById[sp.teamId]?.shortName ?: "FPL"
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(66.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelectedOut) Color(0xFF3B0D2C) else FplCardNavyLight
                                    )
                                    .border(
                                        width = if (isSelectedOut) 1.5.dp else 1.dp,
                                        color = if (isSelectedOut) FplOutCrimson else FplCardBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onSelectPlayerOut(sp) }
                                    .padding(vertical = 6.dp, horizontal = 4.dp)
                            ) {
                                PlayerPhotoAvatar(
                                    player = sp,
                                    teamShortName = spTeamShort,
                                    size = 38.dp,
                                    borderColor = if (isSelectedOut) FplOutCrimson else Color.White.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = sp.webName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${sp.positionName} • ${sp.formattedPrice}",
                                    fontSize = 9.sp,
                                    color = if (isSelectedOut) FplOutCrimson else FplTextSecondary,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Side-by-Side Out / In Cards (Clean layout with Real Faces)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // OUT Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A0F2D)),
                            border = BorderStroke(1.dp, FplOutCrimson)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("⇄ Transfer Out", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FplOutCrimson)
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(FplOutCrimson),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (outPlayer != null) {
                                        PlayerPhotoAvatar(
                                            player = outPlayer,
                                            teamShortName = outTeamShort,
                                            size = 42.dp,
                                            borderColor = FplOutCrimson
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = outPlayer?.webName ?: "Tap Squad",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${outPlayer?.positionName ?: "-"} • $outTeamShort",
                                            fontSize = 10.sp,
                                            color = FplTextSecondary,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = outPlayer?.formattedPrice ?: "-",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        Icon(
                            Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = FplTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )

                        // IN Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B292E)),
                            border = BorderStroke(1.dp, FplAccentGreen)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("⇄ Transfer In", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FplAccentGreen)
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(FplAccentGreen),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(13.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (inPlayer != null) {
                                        PlayerPhotoAvatar(
                                            player = inPlayer,
                                            teamShortName = inTeamShort,
                                            size = 42.dp,
                                            borderColor = FplAccentGreen
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = inPlayer?.webName ?: "Tap Market",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${inPlayer?.positionName ?: "-"} • $inTeamShort",
                                            fontSize = 10.sp,
                                            color = FplTextSecondary,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = inPlayer?.formattedPrice ?: "-",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
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

        // Sub-Tabs (All Players | Watchlist | My Squad) + Fixed Search Pill (No vertical clipping!)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(FplCardNavy)
                        .border(1.dp, FplCardBorder, RoundedCornerShape(22.dp))
                        .padding(3.dp)
                ) {
                    val tabs = listOf(
                        "All Players",
                        "Watchlist (${state.watchlistIds.size})",
                        "My Squad (15)"
                    )
                    tabs.forEachIndexed { idx, title ->
                        val selected = state.transferSubTab == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (selected) FplElectricPurple else Color.Transparent)
                                .clickable { onSelectSubTab(idx) }
                                .padding(vertical = 8.dp),
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

                // Clean custom search bar so placeholder text is never cut off
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(FplCardNavy)
                        .border(1.dp, FplCardBorder, RoundedCornerShape(22.dp))
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = FplTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (state.transferSearchQuery.isEmpty()) {
                            Text(
                                text = "Search player name...",
                                color = FplTextSecondary,
                                fontSize = 13.sp
                            )
                        }
                        BasicTextField(
                            value = state.transferSearchQuery,
                            onValueChange = onSearchChange,
                            singleLine = true,
                            textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                            cursorBrush = SolidColor(FplBrightPurple),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (state.transferSearchQuery.isNotEmpty()) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = FplTextSecondary,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { onSearchChange("") }
                        )
                    }
                }
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

        // 4 Grouped Position Sections
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
                    .take(if (state.transferPositionFilter == null) 6 else 35)

                item(key = "section_$elementType") {
                    TransferPositionSectionCard(
                        title = sectionTitle,
                        dotColor = dotColor,
                        players = sectionPlayers,
                        draftSquadIds = state.draftSquadIds,
                        watchlistIds = state.watchlistIds,
                        teamsById = state.teamsById,
                        onSelectPlayer = onSelectPlayerIn,
                        onToggleWatchlist = onToggleWatchlist,
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
    draftSquadIds: List<Int>,
    watchlistIds: Set<Int>,
    teamsById: Map<Int, com.example.fpl.data.model.TeamDto>,
    onSelectPlayer: (PlayerDto) -> Unit,
    onToggleWatchlist: (Int) -> Unit,
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tap to Pick", fontSize = 11.sp, color = FplTextSecondary, modifier = Modifier.weight(1.9f))
                Text("Team", fontSize = 11.sp, color = FplTextSecondary, modifier = Modifier.weight(0.75f))
                Text("Price", fontSize = 11.sp, color = FplTextSecondary, modifier = Modifier.weight(0.7f))
                Text("Pts", fontSize = 11.sp, color = FplTextSecondary, modifier = Modifier.weight(0.6f))
                Text("Info", fontSize = 11.sp, color = FplTextSecondary, modifier = Modifier.weight(0.55f), textAlign = TextAlign.End)
            }

            HorizontalDivider(color = FplCardBorder)

            players.forEachIndexed { idx, player ->
                val team = teamsById[player.teamId]
                val teamShort = team?.shortName ?: "FPL"
                val isInSquad = draftSquadIds.contains(player.id)
                val isWatched = watchlistIds.contains(player.id)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectPlayer(player) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Player column (+ or - button, Real Face Photo, Name)
                    Row(
                        modifier = Modifier.weight(1.9f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isInSquad) FplOutCrimson.copy(alpha = 0.25f)
                                    else FplAccentGreen.copy(alpha = 0.25f)
                                )
                                .border(
                                    1.dp,
                                    if (isInSquad) FplOutCrimson else FplAccentGreen,
                                    CircleShape
                                )
                                .clickable { onSelectPlayer(player) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isInSquad) Icons.Default.Remove else Icons.Default.Add,
                                contentDescription = "Select",
                                tint = if (isInSquad) FplOutCrimson else FplAccentGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        PlayerPhotoAvatar(
                            player = player,
                            teamShortName = teamShort,
                            size = 34.dp
                        )
                        Column {
                            Text(
                                text = player.webName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isInSquad) "IN SQUAD" else player.positionName,
                                fontSize = 9.sp,
                                color = if (isInSquad) FplAccentGreen else FplTextSecondary
                            )
                        }
                    }

                    // Team column
                    Row(
                        modifier = Modifier.weight(0.75f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ClubCrestBadge(teamCode = team?.code ?: 3, teamShortName = teamShort, size = 16.dp)
                        Text(teamShort, fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                    }

                    // Price column
                    Text(
                        text = player.formattedPrice,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(0.7f)
                    )

                    // Points pill column
                    Box(modifier = Modifier.weight(0.6f)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF065F46))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${player.totalPoints}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FplAccentGreen
                            )
                        }
                    }

                    // Watchlist Star + Profile Info Button
                    Row(
                        modifier = Modifier.weight(0.55f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isWatched) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Watchlist",
                            tint = if (isWatched) Color(0xFFFACC15) else FplTextSecondary,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { onToggleWatchlist(player.id) }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Stats",
                            tint = FplBrightPurple,
                            modifier = Modifier
                                .size(17.dp)
                                .clickable { onPlayerClick(player.id) }
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
// LEAGUES & CUPS TAB
// =========================================================================

@Composable
fun LeaguesHubScreen(
    state: FplUiState,
    onSelectSubTab: (Int) -> Unit,
    onOpenLeagueStandings: (Int) -> Unit,
    onCloseLeagueStandings: () -> Unit,
    onInspectRivalTeam: (Int) -> Unit
) {
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
// MORE TAB & PROFILE DIALOG (NO TEAM ID INPUT)
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
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF311B70))
                                .border(2.dp, FplBrightPurple, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFFC084FC),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(
                                text = state.managerHandle,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = state.loggedInEmail,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Rank: ${state.displayOverallRank} • ${state.displayTotalPoints} pts",
                                fontSize = 11.sp,
                                color = FplTextSecondary,
                                maxLines = 1
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
                        Text("Edit", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }

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
                        subText = "Bank ${state.displayBankValue}",
                        subGreen = true
                    )
                    VerticalStatDivider()
                    MoreStatIconColumn(
                        icon = Icons.Default.Groups,
                        iconTint = Color.White,
                        label = "Transfers",
                        value = state.freeTransfersRemaining.toString(),
                        subText = "(${state.plannedTransfers.size} made)",
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
                        subText = "▲ 18.3k",
                        subGreen = true
                    )
                }
            }
        }

        item {
            MoreMenuSectionCard(
                sectionTitle = "Account",
                sectionIcon = Icons.Default.Settings,
                items = listOf(
                    Triple(Icons.Default.Person, "Profile", "Edit your manager display name") to onEditProfileClick,
                    Triple(Icons.Default.Notifications, "Notifications", "Manage deadline and price alerts") to {},
                    Triple(Icons.Default.Settings, "App Settings", "Theme, language, and preferences") to {}
                )
            )
        }

        item {
            MoreMenuSectionCard(
                sectionTitle = "Fantasy",
                sectionIcon = Icons.Default.EmojiEvents,
                items = listOf(
                    Triple(Icons.Default.CalendarToday, "Fixtures", "View upcoming fixtures and results") to onOpenFixtures,
                    Triple(Icons.AutoMirrored.Filled.TrendingUp, "Player Market", "Browse players and make transfers") to onOpenPlayers,
                    Triple(Icons.Default.Description, "Rules & Scoring", "How FPL points are calculated") to {}
                )
            )
        }

        item {
            MoreMenuSectionCard(
                sectionTitle = "Support",
                sectionIcon = Icons.AutoMirrored.Filled.HelpOutline,
                items = listOf(
                    Triple(Icons.Default.Support, "Help & FAQ", "Find answers to common questions") to {},
                    Triple(Icons.Default.Email, "Contact Us", "Get in touch with our support team") to {}
                )
            )
        }

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
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, color = FplTextSecondary, maxLines = 1)
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, maxLines = 1)
        Text(
            text = subText,
            fontSize = 9.sp,
            color = if (subGreen) FplAccentGreen else FplTextSecondary,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
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

@Composable
fun EditProfileDialog(
    state: FplUiState,
    onDismiss: () -> Unit,
    onSave: (handle: String) -> Unit
) {
    var handle by remember { mutableStateOf(state.managerHandle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FplCardNavy,
        title = {
            Text("Edit Manager Profile", fontWeight = FontWeight.Bold, color = Color.White)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Signed in as ${state.loggedInEmail}",
                    fontSize = 12.sp,
                    color = FplAccentGreen
                )
                OutlinedTextField(
                    value = handle,
                    onValueChange = { handle = it },
                    label = { Text("Manager Display Handle") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(handle) },
                colors = ButtonDefaults.buttonColors(containerColor = FplElectricPurple)
            ) {
                Text("Save")
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
    val teamShort = team?.shortName ?: "ARS"

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
                PlayerPhotoAvatar(
                    player = player,
                    teamShortName = teamShort,
                    size = 64.dp,
                    borderColor = FplAccentGreen
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
                        HomeStatCell(Modifier.weight(1f), "Total Pts", player.totalPoints.toString(), "", "")
                        HomeStatCell(Modifier.weight(1f), "GW Pts", player.eventPoints.toString(), "", "")
                        HomeStatCell(Modifier.weight(1f), "Form", player.form, "", "")
                        HomeStatCell(Modifier.weight(1f), "Selected", "${player.selectedByPercent}%", "", "")
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
                            Text(
                                text = hTeam?.name ?: "Home",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
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
                            Text(
                                text = aTeam?.name ?: "Away",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
