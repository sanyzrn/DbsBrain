package ir.dbsgraphic.secondbrain.core.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * The goal payload carried in an Item's [Item.details] JSON. A goal is an
 * ordinary Item (type=goal): its measurable target lives here, its deadline
 * reuses [Item.reminderAt], it can sit in a project ([Item.projectId]), and it
 * connects to supporting items through the same link table everything else uses
 * (§4, §5). [targetCount] of 1 makes a simple binary (done / not-done) goal.
 */
@Serializable
data class GoalDetails(
    val targetCount: Int = 1,
    val doneCount: Int = 0,
) {
    val target: Int get() = targetCount.coerceAtLeast(1)
    val done: Int get() = doneCount.coerceIn(0, target)
    val isAchieved: Boolean get() = done >= target
    val progress: Float get() = done.toFloat() / target
}

/** Reads/writes [GoalDetails] tolerantly so older items never break. */
object GoalCodec {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun decode(details: String?): GoalDetails =
        if (details.isNullOrBlank()) GoalDetails()
        else runCatching { json.decodeFromString(GoalDetails.serializer(), details) }
            .getOrDefault(GoalDetails())

    fun encode(details: GoalDetails): String =
        json.encodeToString(GoalDetails.serializer(), details)
}
