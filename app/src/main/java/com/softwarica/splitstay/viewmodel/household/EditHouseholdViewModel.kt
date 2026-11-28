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
 * ViewModel for the Edit Household screen.
 */
class EditHouseholdViewModel(
    private val householdId: String,
    private val repository: HouseholdDetailsRepository = HouseholdDetailsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditHouseholdUiState())
    val uiState: StateFlow<EditHouseholdUiState> = _uiState.asStateFlow()

    init {
        loadDetails()
    }

    fun onNameChange(name: String) {
        _uiState.update {
            it.copy(
                name = name,
                nameError = null,
                errorMessage = null,
                hasChanges = name.trim() != it.originalName || it.address.trim() != it.originalAddress,
                isFormValid = validateName(name) == null
            )
        }
    }

    fun onNameBlur() {
        _uiState.update {
            it.copy(nameError = validateName(it.name))
        }
    }

    fun onAddressChange(address: String) {
        _uiState.update {
            it.copy(
                address = address,
                addressError = null,
                errorMessage = null,
                hasChanges = it.name.trim() != it.originalName || address.trim() != it.originalAddress,
                isFormValid = validateName(it.name) == null
            )
        }
    }

    fun retryLoad() {
        loadDetails()
    }

    fun onSaveClick() {
        val state = _uiState.value
        if (state.isSaving || !state.isAdmin) return

        val trimmedName = state.name.trim()
        val trimmedAddress = state.address.trim()

        val nameError = validateName(state.name)
        if (nameError != null) {
            _uiState.update { it.copy(nameError = nameError, isFormValid = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            repository.updateHouseholdDetails(
                householdId = householdId,
                name = trimmedName,
                address = trimmedAddress
            ).fold(
                onSuccess = {
                    _uiState.update { it.copy(isSaving = false, isSaved = true) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = friendlyMessage(error)
                        )
                    }
                }
            )
        }
    }

    private fun validateName(name: String): String? {
        val trimmed = name.trim()
        return when {
            trimmed.isBlank() -> "Name must not be empty"
            trimmed.length < 2 -> "Name must be at least 2 characters"
            trimmed.length > 50 -> "Name must be 50 characters or fewer"
            else -> null
        }
    }

    private fun loadDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadFailed = false, errorMessage = null) }

            val currentUserId = repository.currentUserId()

            repository.getHouseholdDetails(householdId).fold(
                onSuccess = { details ->
                    val isAdmin = currentUserId != null && details.adminId == currentUserId
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadFailed = false,
                            name = details.name,
                            address = details.address,
                            originalName = details.name,
                            originalAddress = details.address,
                            isAdmin = isAdmin,
                            hasChanges = false,
                            isFormValid = validateName(details.name) == null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadFailed = true,
                            errorMessage = friendlyLoadMessage(error)
                        )
                    }
                }
            )
        }
    }

    private fun friendlyLoadMessage(error: Throwable): String {
        return when (error) {
            is HouseholdDetailsError.NotFound -> "Household not found."
            is HouseholdDetailsError.NotAllowed -> "Only the admin can edit this household."
            is HouseholdDetailsError.Network -> "No internet connection. Check your network and try again."
            else -> "Something went wrong. Try again."
        }
    }

    private fun friendlyMessage(error: Throwable): String {
        return when (error) {
            is HouseholdDetailsError.NotFound -> "Household not found."
            is HouseholdDetailsError.NotAllowed -> "Only the admin can edit this household."
            is HouseholdDetailsError.Network -> "Couldn't save. Check your connection and try again."
            else -> "Couldn't save. Check your connection and try again."
        }
    }

    class Factory(private val householdId: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EditHouseholdViewModel(householdId = householdId) as T
        }
    }
}
