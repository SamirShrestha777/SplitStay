package com.softwarica.splitstay.repository

/**
 * Sealed class representing possible errors from household detail operations.
 * The ViewModel maps these to user-friendly messages.
 */
sealed class HouseholdDetailsError : Exception() {

    /** The household was not found. */
    data object NotFound : HouseholdDetailsError()

    /** The current user is not allowed to perform this action. */
    data object NotAllowed : HouseholdDetailsError()

    /** Device is offline or the request timed out. */
    data object Network : HouseholdDetailsError()

    /** Any other unexpected error. */
    data object Unknown : HouseholdDetailsError()
}
