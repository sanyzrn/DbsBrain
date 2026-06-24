package ir.dbsgraphic.secondbrain.core.data

import ir.dbsgraphic.secondbrain.core.database.dao.ItemDao
import ir.dbsgraphic.secondbrain.core.database.entity.Item
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val itemDao: ItemDao,
    private val clock: Clock,
    private val idGenerator: IdGenerator,
) : ItemRepository {

    override fun observeInbox(): Flow<List<Item>> = itemDao.observeInbox()

    override fun observeInboxCount(): Flow<Int> = itemDao.observeInboxCount()

    override suspend fun capture(content: String): String {
        val trimmed = content.trim()
        require(trimmed.isNotEmpty()) { "Cannot capture empty content" }

        val now = clock.now()
        val item = Item(
            id = idGenerator.newId(),
            createdAt = now,
            updatedAt = now,
            content = trimmed,
            status = "inbox",
            type = null,
            contentType = "text",
            capturedVia = "quickAdd",
        )
        itemDao.upsert(item)
        return item.id
    }
}
