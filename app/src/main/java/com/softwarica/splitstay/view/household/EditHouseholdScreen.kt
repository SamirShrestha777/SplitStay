package com.softwarica.splitstay.view.household

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.softwarica.splitstay.viewmodel.household.EditHouseholdUiState
import com.softwarica.splitstay.viewmodel.household.EditHouseholdViewModel

// ── Route (stateful wrapper) ───────────────────────────────────────────────────

@Composable
fun EditHouseholdRoute(
    householdId: String,
    onBackClick: () -> Unit = {},
    onSaved: () -> Unit = {}
) {
    val viewModel: EditHouseholdViewModel = viewModel(
        factory = EditHouseholdViewModel.Factory(householdId)
    )

    val uiState by viewModel.uiState.collectAsState()

    // Navigate back when saved
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onSaved()
        }
    }

    EditHouseholdScreen(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onNameBlur = viewModel::onNameBlur,
        onAddressChange = viewModel::onAddressChange,
        onSaveClick = viewModel::onSaveClick,
        onRetryClick = viewModel::retryLoad,
        onBackClick = onBackClick
    )
}

// ── Screen (stateless) ─────────────────────────────────────────────────────────

@Composable
fun EditHouseholdScreen(
    uiState: EditHouseholdUiState,
    onNameChange: (String) -> Unit = {},
    onNameBlur: () -> Unit = {},
    onAddressChange: (String) -> Unit = {},
    onSaveClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    var nameWasFocused by remember { mutableStateOf(false) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = CardBackground,
        unfocusedContainerColor = CardBackground,
        errorContainerColor = CardBackground,
        focusedBorderColor = PrimaryGreen,
        unfocusedBorderColor = InputBorder,
        errorBorderColor = ErrorRed,
        focusedTextColor = TitleColor,
        unfocusedTextColor = TitleColor
    )

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
                text = "Edit household",
                color = TitleColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
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

            uiState.loadFailed -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.errorMessage ?: "Something went wrong. Try again.",
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

            !uiState.isAdmin -> {
                // Non-admin gating
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Only the admin can edit this household",
                            color = SubtitleGray,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onBackClick,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreen,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Go back",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Name field ──────────────────────────────────────────────
                    Text(text = "Household name", fontSize = 12.sp, color = SubtitleGray)
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = onNameChange,
                        placeholder = {
                            Text("e.g. Roommates Nepal", fontSize = 14.sp, color = SubtitleGray)
                        },
                        textStyle = TextStyle(fontSize = 14.sp),
                        isError = uiState.nameError != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    nameWasFocused = true
                                } else if (nameWasFocused) {
                                    onNameBlur()
                                }
                            },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        colors = fieldColors
                    )
                    if (uiState.nameError != null) {
                        Text(
                            text = uiState.nameError,
                            color = ErrorRed,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Address field ───────────────────────────────────────────
                    Text(text = "Address", fontSize = 12.sp, color = SubtitleGray)
                    OutlinedTextField(
                        value = uiState.address,
                        onValueChange = onAddressChange,
                        placeholder = {
                            Text("e.g. Kathmandu, Nepal", fontSize = 14.sp, color = SubtitleGray)
                        },
                        textStyle = TextStyle(fontSize = 14.sp),
                        isError = uiState.addressError != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        colors = fieldColors
                    )
                    if (uiState.addressError != null) {
                        Text(
                            text = uiState.addressError,
                            color = ErrorRed,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // ── General error ──────────────────────────────────────────
                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = uiState.errorMessage,
                            color = ErrorRed,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // ── Save button ────────────────────────────────────────────
                    val canSave = uiState.hasChanges &&
                        uiState.isFormValid &&
                        uiState.nameError == null &&
                        !uiState.isSaving

                    Button(
                        onClick = onSaveClick,
                        enabled = canSave,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen,
                            contentColor = Color.White,
                            disabledContainerColor = PrimaryGreen.copy(alpha = 0.5f),
                            disabledContentColor = Color.White.copy(alpha = 0.7f)
                        )
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Save changes",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Cancel button ──────────────────────────────────────────
                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen)
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}
