package com.softwarica.splitstay.repository

import com.softwarica.splitstay.model.HouseholdDetails
import com.softwarica.splitstay.model.HouseholdMember
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.UnknownHostException

/**
 * Repository for fetching and updating household details via Supabase Postgrest.
 * Uses the shared [SupabaseClientProvider.client].
 */
class HouseholdDetailsRepository {

    private val client get() = SupabaseClientProvider.client

    /**
     * Row shape returned by the `households` table with an embedded `household_members` join.
     */
    @Serializable
    private data class HouseholdRow(
        val id: String = "",
        val name: String = "",
        val address: String = "",
        @SerialName("admin_id") val adminId: String = "",
        @SerialName("created_at") val createdAt: String = "",
        @SerialName("household_members") val members: List<MemberRow> = emptyList()
    )

    @Serializable
    private data class MemberRow(
        val id: String = "",
        @SerialName("household_id") val householdId: String = "",
        @SerialName("user_id") val userId: String = "",
        val role: String = "member",
        val name: String = ""
    )

    /**
     * Fetches a household and its members.
     */
    suspend fun getHouseholdDetails(householdId: String): Result<HouseholdDetails> =
        withContext(Dispatchers.IO) {
            try {
                val row = client.postgrest["households"]
                    .select(
                        columns = Columns.raw(
                            "id, name, address, admin_id, created_at, household_members(id, household_id, user_id, role, name)"
                        )
                    ) {
                        filter { eq("id", householdId) }
                        limit(1)
                    }
                    .decodeSingleOrNull<HouseholdRow>()
                    ?: return@withContext Result.failure(HouseholdDetailsError.NotFound)

                val details = HouseholdDetails(
                    id = row.id,
                    name = row.name,
                    address = row.address,
                    adminId = row.adminId,
                    createdAt = row.createdAt,
                    members = row.members.map { m ->
                        HouseholdMember(
                            id = m.id,
                            householdId = m.householdId,
                            userId = m.userId,
                            role = m.role,
                            name = m.name
                        )
                    }
                )
                Result.success(details)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(mapError(e))
            }
        }

    /**
     * Updates the household's editable fields. Only the admin should call this;
     * RLS on the server enforces it too.
     */
    suspend fun updateHouseholdDetails(
        householdId: String,
        name: String,
        address: String
    ): Result<HouseholdDetails> = withContext(Dispatchers.IO) {
        try {
            // Verify the current user is the admin
            val currentUserId = client.auth.currentUserOrNull()?.id
                ?: return@withContext Result.failure(HouseholdDetailsError.NotAllowed)

            val updatedRow = client.postgrest["households"].update(
                {
                    set("name", name)
                    set("address", address)
                }
            ) {
                select(columns = Columns.raw("id"))
                filter {
                    eq("id", householdId)
                    eq("admin_id", currentUserId)
                }
            }.decodeSingleOrNull<HouseholdRow>()
                ?: return@withContext Result.failure(HouseholdDetailsError.NotAllowed)

            // Re-fetch to return the updated details including members
            getHouseholdDetails(updatedRow.id)
                .getOrElse { return@withContext Result.failure(it) }
                .let { Result.success(it) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(mapError(e))
        }
    }

    /**
     * Returns the user id of the currently logged-in user, or null.
     */
    fun currentUserId(): String? = client.auth.currentUserOrNull()?.id

    /**
     * Deletes a household (admin only). After deletion, verifies the row is gone
     * so that an RLS silent no-op is detected.
     */
    suspend fun deleteHousehold(householdId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val currentUserId = client.auth.currentUserOrNull()?.id
                    ?: return@withContext Result.failure(HouseholdDetailsError.NotAllowed)

                // Delete the household row (RLS should enforce admin_id = auth.uid())
                client.postgrest["households"].delete {
                    filter {
                        eq("id", householdId)
                        eq("admin_id", currentUserId)
                    }
                }

                // Verify the row was actually deleted (RLS can silently delete 0 rows)
                val stillExists = client.postgrest["households"]
                    .select(columns = Columns.raw("id")) {
                        filter { eq("id", householdId) }
                        limit(1)
                    }
                    .decodeSingleOrNull<IdOnly>()

                if (stillExists != null) {
                    return@withContext Result.failure(HouseholdDetailsError.NotAllowed)
                }

                Result.success(Unit)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(mapError(e))
            }
        }

    /**
     * Minimal row shape used only for existence checks.
     */
    @Serializable
    private data class IdOnly(val id: String = "")

    // ── Error mapping ──────────────────────────────────────────────────────────

    private fun mapError(e: Exception): HouseholdDetailsError {
        return when {
            e is HttpRequestException ||
            e is HttpRequestTimeoutException ||
            e is UnknownHostException ||
            e.cause is UnknownHostException -> HouseholdDetailsError.Network

            e is RestException -> {
                val msg = e.message.orEmpty().lowercase()
                when {
                    "not found" in msg || "0 rows" in msg ||
                    "pgrst116" in msg -> HouseholdDetailsError.NotFound

                    "permission" in msg || "policy" in msg ||
                    "denied" in msg || "403" in msg ||
                    "rls" in msg -> HouseholdDetailsError.NotAllowed

                    else -> HouseholdDetailsError.Unknown
                }
            }

            else -> HouseholdDetailsError.Unknown
        }
    }
}
