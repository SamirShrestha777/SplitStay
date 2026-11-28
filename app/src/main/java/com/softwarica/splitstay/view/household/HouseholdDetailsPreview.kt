package com.softwarica.splitstay.view.household

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.softwarica.splitstay.model.HouseholdDetails
import com.softwarica.splitstay.model.HouseholdMember
import com.softwarica.splitstay.viewmodel.household.EditHouseholdUiState
import com.softwarica.splitstay.viewmodel.household.HouseholdDetailsUiState

// ── Household Details previews ─────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetailsLoadingPreview() {
    HouseholdDetailsScreen(
        uiState = HouseholdDetailsUiState(isLoading = true)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetailsDataPreview() {
    HouseholdDetailsScreen(
        uiState = HouseholdDetailsUiState(
            isLoading = false,
            isAdmin = true,
            household = HouseholdDetails(
                id = "1",
                name = "Roommates KTM",
                address = "Dillibazar, Kathmandu",
                adminId = "user1",
                members = listOf(
                    HouseholdMember(id = "m1", userId = "user1", name = "Aayush", role = "admin"),
                    HouseholdMember(id = "m2", userId = "user2", name = "Bikash", role = "member"),
                    HouseholdMember(id = "m3", userId = "user3", name = "Chhavi", role = "member")
                )
            )
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetailsErrorPreview() {
    HouseholdDetailsScreen(
        uiState = HouseholdDetailsUiState(
            isLoading = false,
            errorMessage = "No internet connection. Check your network and try again."
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetailsEmptyPreview() {
    HouseholdDetailsScreen(
        uiState = HouseholdDetailsUiState(
            isLoading = false,
            household = null
        )
    )
}

// ── Delete Household previews ──────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetailsDangerZonePreview() {
    HouseholdDetailsScreen(
        uiState = HouseholdDetailsUiState(
            isLoading = false,
            isAdmin = true,
            household = HouseholdDetails(
                id = "1",
                name = "Roommates KTM",
                address = "Dillibazar, Kathmandu",
                adminId = "user1",
                members = listOf(
                    HouseholdMember(id = "m1", userId = "user1", name = "Aayush", role = "admin"),
                    HouseholdMember(id = "m2", userId = "user2", name = "Bikash", role = "member")
                )
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun DeleteDialogPreview() {
    DeleteHouseholdDialog(
        householdName = "Roommates KTM",
        confirmText = "",
        isDeleting = false,
        deleteError = null,
        onConfirmTextChange = {},
        onConfirmDelete = {},
        onDismiss = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun DeleteDialogMatchedPreview() {
    DeleteHouseholdDialog(
        householdName = "Roommates KTM",
        confirmText = "Roommates KTM",
        isDeleting = false,
        deleteError = null,
        onConfirmTextChange = {},
        onConfirmDelete = {},
        onDismiss = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun DeleteDialogDeletingPreview() {
    DeleteHouseholdDialog(
        householdName = "Roommates KTM",
        confirmText = "Roommates KTM",
        isDeleting = true,
        deleteError = null,
        onConfirmTextChange = {},
        onConfirmDelete = {},
        onDismiss = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun DeleteDialogErrorPreview() {
    DeleteHouseholdDialog(
        householdName = "Roommates KTM",
        confirmText = "Roommates KTM",
        isDeleting = false,
        deleteError = "Couldn't delete. Check your connection and try again.",
        onConfirmTextChange = {},
        onConfirmDelete = {},
        onDismiss = {}
    )
}

// ── Edit Household previews ────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditLoadingPreview() {
    EditHouseholdScreen(
        uiState = EditHouseholdUiState(isLoading = true)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditFormPreview() {
    EditHouseholdScreen(
        uiState = EditHouseholdUiState(
            isLoading = false,
            name = "Roommates KTM",
            address = "Dillibazar, Kathmandu",
            originalName = "Roommates KTM",
            originalAddress = "Dillibazar, Kathmandu",
            isAdmin = true,
            hasChanges = false
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditNotAdminPreview() {
    EditHouseholdScreen(
        uiState = EditHouseholdUiState(
            isLoading = false,
            isAdmin = false
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditValidationErrorPreview() {
    EditHouseholdScreen(
        uiState = EditHouseholdUiState(
            isLoading = false,
            name = "A",
            address = "Kathmandu",
            originalName = "Roommates KTM",
            originalAddress = "Kathmandu",
            isAdmin = true,
            hasChanges = true,
            nameError = "Name must be at least 2 characters"
        )
    )
}
