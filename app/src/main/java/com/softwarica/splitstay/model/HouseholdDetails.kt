package com.softwarica.splitstay.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a household row from the `households` table.
 */
@Serializable
data class HouseholdDetails(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    @SerialName("admin_id") val adminId: String = "",
    @SerialName("created_at") val createdAt: String = "",
    val members: List<HouseholdMember> = emptyList()
)
