package com.softwarica.splitstay.viewmodel.household

import com.softwarica.splitstay.model.HouseholdDetails

/**
 * UI state for the Household Details screen.
 */
data class HouseholdDetailsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val household: HouseholdDetails? = null,
    val isAdmin: Boolean = false,

    // ── Delete household state ──────────────────────────────────────────────
    val showDeleteDialog: Boolean = false,
    val confirmText: String = "",
    val isDeleting: Boolean = false,
    val deleteError: String? = null,
    val isDeleted: Boolean = false
)
