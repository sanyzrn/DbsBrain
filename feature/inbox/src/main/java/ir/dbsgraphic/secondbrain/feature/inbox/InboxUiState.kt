package ir.dbsgraphic.secondbrain.feature.inbox

import androidx.compose.runtime.Immutable
import ir.dbsgraphic.secondbrain.core.database.entity.Item

/** Explicit, exhaustive UI states for the inbox list (design spine). */
@Immutable
sealed interface InboxContent {
    data object Loading : InboxContent
    data object Empty : InboxContent
    data class Items(val items: List<Item>) : InboxContent
    data class Error(val message: String) : InboxContent
}

/**
 * Full screen state. The list content is one explicit state; the capture draft
 * lives alongside it so the quick-add bar is always ready (Constitution §2).
 */
@Immutable
data class InboxUiState(
    val content: InboxContent = InboxContent.Loading,
    val draft: String = "",
    val isSaving: Boolean = false,
) {
    val canCapture: Boolean get() = draft.isNotBlank() && !isSaving
}
