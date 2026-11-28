package com.softwarica.splitstay.viewmodel.household

/**
 * UI state for the Edit Household screen.
 */
data class EditHouseholdUiState(
    val isLoading: Boolean = true,
    val loadFailed: Boolean = false,
    val name: String = "",
    val address: String = "",
    val originalName: String = "",
    val originalAddress: String = "",
    val nameError: String? = null,
    val addressError: String? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false,
    val isAdmin: Boolean = true,
    val hasChanges: Boolean = false,
    val isFormValid: Boolean = false
)
