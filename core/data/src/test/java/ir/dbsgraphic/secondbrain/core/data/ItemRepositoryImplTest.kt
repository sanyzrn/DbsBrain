package ir.dbsgraphic.secondbrain.core.data

import ir.dbsgraphic.secondbrain.core.database.dao.ItemDao
import ir.dbsgraphic.secondbrain.core.database.entity.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

private class FakeItemDao : ItemDao {
    val items = MutableStateFlow<List<Item>>(emptyList())

    override suspend fun upsert(item: Item) {
        items.value = items.value.filterNot { it.id == item.id } + item
    }

    override suspend fun update(item: Item) = upsert(item)

    override fun observeInbox(): Flow<List<Item>> =
        items.map { list -> list.filter { it.status == "inbox" }.sortedByDescending { it.createdAt } }

    override fun observeTimeline(): Flow<List<Item>> =
        items.map { list -> list.filter { it.status != "trashed" }.sortedByDescending { it.createdAt } }

    override fun observeById(id: String): Flow<Item?> =
        items.map { list -> list.find { it.id == id } }

    override fun observeInboxCount(): Flow<Int> =
        items.map { list -> list.count { it.status == "inbox" } }
}

class ItemRepositoryImplTest {

    private val dao = FakeItemDao()
    private var counter = 0
    private val repo = ItemRepositoryImpl(
        itemDao = dao,
        clock = { 1_000L },
        idGenerator = { "id-${++counter}" },
    )

    @Test
    fun `capture lands a formless item in the inbox`() = runTest {
        val id = repo.capture("  یک فکر  ")

        val item = dao.observeInbox().first().single()
        assertEquals("id-1", id)
        assertEquals("یک فکر", item.content) // trimmed
        assertEquals("inbox", item.status)
        assertNull(item.type)                 // formless until triage
        assertEquals(1_000L, item.createdAt)
        assertEquals(1_000L, item.updatedAt)
        assertEquals("quickAdd", item.capturedVia)
    }

    @Test
    fun `capture rejects empty content`() = runTest {
        assertThrows(IllegalArgumentException::class.java) {
            kotlinx.coroutines.runBlocking { repo.capture("   ") }
        }
    }

    @Test
    fun `inbox count tracks captures`() = runTest {
        repo.capture("a")
        repo.capture("b")
        assertEquals(2, repo.observeInboxCount().first())
    }
}
