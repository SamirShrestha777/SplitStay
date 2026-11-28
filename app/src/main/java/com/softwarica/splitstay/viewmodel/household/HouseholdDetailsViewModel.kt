package com.softwarica.splitstay.viewmodel.household

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.softwarica.splitstay.repository.HouseholdDetailsError
import com.softwarica.splitstay.repository.HouseholdDetailsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Household Details screen.
 */
class HouseholdDetailsViewModel(
    private val householdId: String,
    private val repository: HouseholdDetailsRepository = HouseholdDetailsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HouseholdDetailsUiState())
    val uiState: StateFlow<HouseholdDetailsUiState> = _uiState.asStateFlow()

    init {
        loadDetails()
    }

    fun refresh() {
        loadDetails()
    }

    private fun loadDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.getHouseholdDetails(householdId).fold(
                onSuccess = { details ->
                    val currentUserId = repository.currentUserId()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            household = details,
                            isAdmin = currentUserId != null && details.adminId == currentUserId,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = friendlyMessage(error)
                        )
                    }
                }
            )
        }
    }

    private fun friendlyMessage(error: Throwable): String {
        return when (error) {
            is HouseholdDetailsError.NotFound -> "Household not found."
            is HouseholdDetailsError.NotAllowed -> "You don't have access to this household."
            is HouseholdDetailsError.Network -> "No internet connection. Check your network and try again."
            else -> "Something went wrong. Try again."
        }
    }

    class Factory(private val householdId: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HouseholdDetailsViewModel(householdId = householdId) as T
        }
    }
}
