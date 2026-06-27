package ir.dbsgraphic.secondbrain.core.data

import ir.dbsgraphic.secondbrain.core.database.dao.HabitCheckinDao
import ir.dbsgraphic.secondbrain.core.database.dao.ItemDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * A snapshot of the last seven days — what came in, what got done, what's owed.
 * The reflective surface that ties the whole system together (§18, §19). All of
 * it is derived from existing data; the review stores nothing of its own.
 */
data class WeeklyReview(
    val captured: Int = 0,
    val triaged: Int = 0,
    val checkins: Int = 0,
    val activeDays: Int = 0,
    val upcomingReminders: Int = 0,
    val overdueReminders: Int = 0,
    val inbox: Int = 0,
    val goalsAchieved: Int = 0,
    val goalsActive: Int = 0,
)

interface ReviewRepository {
    fun observeWeeklyReview(): Flow<WeeklyReview>
}

class ReviewRepositoryImpl @Inject constructor(
    private val itemDao: ItemDao,
    private val habitCheckinDao: HabitCheckinDao,
    private val clock: Clock,
) : ReviewRepository {

    override fun observeWeeklyReview(): Flow<WeeklyReview> = combine(
        itemDao.observeTimeline(),
        itemDao.observeInboxCount(),
        habitCheckinDao.observeAll(),
    ) { items, inbox, checkins ->
        val now = clock.now()
        // The last seven calendar days, inclusive of today.
        val weekStart = DayUtil.startOfDay(now) - 6 * DAY_MS
        val weekAhead = now + 7 * DAY_MS

        val recentCheckins = checkins.filter { it.dayStart >= weekStart }

        WeeklyReview(
            captured = items.count { it.createdAt >= weekStart },
            triaged = items.count { it.status == "triaged" && it.updatedAt >= weekStart },
            checkins = recentCheckins.size,
            activeDays = recentCheckins.map { it.dayStart }.distinct().size,
            // Bind reminderAt to a local val: it's a public property from another
            // module (core:database), so it can't be smart-cast in place.
            upcomingReminders = items.count { val at = it.reminderAt; at != null && at in (now + 1)..weekAhead },
            overdueReminders = items.count { val at = it.reminderAt; at != null && at < now },
            inbox = inbox,
            goalsAchieved = items.count {
                it.type == "goal" && it.updatedAt >= weekStart && GoalCodec.decode(it.details).isAchieved
            },
            goalsActive = items.count {
                it.type == "goal" && !GoalCodec.decode(it.details).isAchieved
            },
        )
    }

    private companion object {
        const val DAY_MS = 24L * 60 * 60 * 1000
    }
}
