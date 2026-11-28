package com.softwarica.splitstay.view.household

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.softwarica.splitstay.model.HouseholdDetails
import com.softwarica.splitstay.model.HouseholdMember
import com.softwarica.splitstay.viewmodel.household.HouseholdDetailsUiState
import com.softwarica.splitstay.viewmodel.household.HouseholdDetailsViewModel

// ── Route (stateful wrapper) ───────────────────────────────────────────────────

@Composable
fun HouseholdDetailsRoute(
    householdId: String,
    onBackClick: () -> Unit = {},
    onEditClick: (String) -> Unit = {}
) {
    val viewModel: HouseholdDetailsViewModel = viewModel(
        factory = HouseholdDetailsViewModel.Factory(householdId)
    )

    val uiState by viewModel.uiState.collectAsState()

    // Refresh every time the screen comes back into view (skip initial resume since ViewModel.init loads)
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasResumedOnce by remember { mutableStateOf(false) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (hasResumedOnce) {
                    viewModel.refresh()
                } else {
                    hasResumedOnce = true
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    HouseholdDetailsScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onEditClick = { onEditClick(householdId) },
        onRetryClick = { viewModel.refresh() }
    )
}

// ── Screen (stateless) ─────────────────────────────────────────────────────────

@Composable
fun HouseholdDetailsScreen(
    uiState: HouseholdDetailsUiState,
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onRetryClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .statusBarsPadding()
            .imePadding()
    ) {
        // ── Top bar ─────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = BackArrowIcon,
                    contentDescription = "Back",
                    tint = TitleColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "Household details",
                color = TitleColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )

            // Edit action — visible only to admin
            if (uiState.isAdmin) {
                IconButton(onClick = onEditClick, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = EditIcon,
                        contentDescription = "Edit",
                        tint = PrimaryGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // ── Content ─────────────────────────────────────────────────────────────
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = PrimaryGreen,
                        modifier = Modifier.size(40.dp),
                        strokeWidth = 3.dp
                    )
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.errorMessage,
                            color = SubtitleGray,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onRetryClick,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreen,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Try again",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            uiState.household == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No household found.",
                        color = SubtitleGray,
                        fontSize = 15.sp
                    )
                }
            }

            else -> {
                HouseholdDetailsContent(
                    household = uiState.household,
                    isAdmin = uiState.isAdmin
                )
            }
        }
    }
}

// ── Main content ───────────────────────────────────────────────────────────────

@Composable
private fun HouseholdDetailsContent(
    household: HouseholdDetails,
    isAdmin: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // ── Household info card ─────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .padding(20.dp)
        ) {
            // House icon in soft green circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = PrimaryGreen.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = HomeIcon,
                    contentDescription = "Household",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = household.name,
                color = TitleColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            if (household.address.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = household.address,
                    color = SubtitleGray,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val adminName = household.members
                .firstOrNull {
                    it.userId == household.adminId || it.role.equals("admin", ignoreCase = true)
                }
                ?.name
                ?.takeIf { it.isNotBlank() }

            if (adminName != null) {
                Text(
                    text = "Admin: $adminName",
                    color = TitleColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Member count
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = PersonIcon,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${household.members.size} / 8 members",
                    color = SubtitleGray,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Members section ─────────────────────────────────────────────────
        Text(
            text = "MEMBERS",
            color = PrimaryGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        household.members.forEach { member ->
            val isMemberAdmin = member.role.equals("admin", ignoreCase = true) ||
                member.userId == household.adminId
            MemberRow(member = member, isAdminMember = isMemberAdmin)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (household.members.isEmpty()) {
            Text(
                text = "No members yet.",
                color = SubtitleGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── Member row ─────────────────────────────────────────────────────────────────

@Composable
private fun MemberRow(member: HouseholdMember, isAdminMember: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar circle with initial
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = if (isAdminMember) PrimaryGreen else MemberBadgeBackground,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = member.name.firstOrNull()?.uppercase() ?: "?",
                color = if (isAdminMember) Color.White else MemberBadgeText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = member.name.ifBlank { "Unknown" },
                color = TitleColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Role badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isAdminMember) AdminBadgeBackground else MemberBadgeBackground
                )
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (isAdminMember) "Admin" else "Member",
                color = if (isAdminMember) Color.White else MemberBadgeText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
