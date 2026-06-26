package ir.dbsgraphic.secondbrain.core.data

import ir.dbsgraphic.secondbrain.core.database.dao.ItemDao
import ir.dbsgraphic.secondbrain.core.database.entity.Item
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Goals as a vertical over the one pipeline (Phase 17). A goal is an Item
 * (type=goal): its measurable target lives in [GoalDetails], its deadline reuses
 * [Item.reminderAt] (notifies for free), and it links to supporting items via the
 * shared connection table — so a goal ties the rest of the system together
 * (§18, §19) without any new storage.
 */
interface GoalRepository {

    /** Goals, newest first. */
    fun observeGoals(): Flow<List<Item>>

    suspend fun createGoal(title: String, targetCount: Int, dueAt: Long? = null): String

    /** Log progress toward a goal (clamped at the target). */
    suspend fun logProgress(id: String, delta: Int = 1)

    /** Mark a goal fully achieved. */
    suspend fun markAchieved(id: String)
}

class GoalRepositoryImpl @Inject constructor(
    private val itemDao: ItemDao,
    private val itemRepository: ItemRepository,
    private val clock: Clock,
    private val idGenerator: IdGenerator,
) : GoalRepository {

    override fun observeGoals(): Flow<List<Item>> = itemDao.observeByType("goal")

    override suspend fun createGoal(title: String, targetCount: Int, dueAt: Long?): String {
        val trimmed = title.trim()
        require(trimmed.isNotEmpty()) { "Goal title cannot be empty" }
        val now = clock.now()
        val details = GoalDetails(targetCount = targetCount.coerceAtLeast(1), doneCount = 0)
        val item = Item(
            id = idGenerator.newId(),
            createdAt = now,
            updatedAt = now,
            content = trimmed,
            type = "goal",
            status = "triaged",
            details = GoalCodec.encode(details),
            capturedVia = "quickAdd",
            reminderAt = dueAt,
        )
        itemDao.upsert(item)
        if (dueAt != null) itemRepository.setReminder(item.id, dueAt)
        return item.id
    }

    override suspend fun logProgress(id: String, delta: Int) {
        val item = itemDao.getById(id) ?: return
        if (item.type != "goal") return
        val details = GoalCodec.decode(item.details)
        val updated = details.copy(
            doneCount = (details.done + delta).coerceIn(0, details.target),
        )
        itemDao.update(item.copy(details = GoalCodec.encode(updated), updatedAt = clock.now()))
    }

    override suspend fun markAchieved(id: String) {
        val item = itemDao.getById(id) ?: return
        if (item.type != "goal") return
        val details = GoalCodec.decode(item.details)
        val updated = details.copy(doneCount = details.target)
        itemDao.update(item.copy(details = GoalCodec.encode(updated), updatedAt = clock.now()))
    }
}
