package com.softwarica.splitstay.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a member row from the `household_members` table.
 */
@Serializable
data class HouseholdMember(
    val id: String = "",
    @SerialName("household_id") val householdId: String = "",
    @SerialName("user_id") val userId: String = "",
    val role: String = "member",
    val name: String = ""
)
