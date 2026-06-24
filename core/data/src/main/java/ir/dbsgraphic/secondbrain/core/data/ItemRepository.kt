package ir.dbsgraphic.secondbrain.core.data

import ir.dbsgraphic.secondbrain.core.database.entity.Item
import kotlinx.coroutines.flow.Flow

/**
 * The single doorway to Items for the whole app (Constitution §4: stored once,
 * all modules read one source). Feature modules never touch Room directly.
 */
interface ItemRepository {

    /** The Inbox stream — formless items, newest first. */
    fun observeInbox(): Flow<List<Item>>

    /** Live count of inbox items, for badges/headers. */
    fun observeInboxCount(): Flow<Int>

    /**
     * Capture a raw thought. No decisions are made here — the item lands in the
     * Inbox formless (`status = inbox`, `type = null`) so capture stays faster
     * than forgetting (Constitution §2, §3). Returns the new item's id.
     */
    suspend fun capture(content: String): String
}
