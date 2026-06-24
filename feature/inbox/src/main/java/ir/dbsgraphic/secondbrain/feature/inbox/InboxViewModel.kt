package ir.dbsgraphic.secondbrain.feature.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.dbsgraphic.secondbrain.core.data.ItemRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** One-off feedback the screen consumes (toasts/haptics); never replayed. */
sealed interface InboxEvent {
    data object Captured : InboxEvent
    data class CaptureFailed(val message: String) : InboxEvent
}

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val repository: ItemRepository,
) : ViewModel() {

    private val draft = MutableStateFlow("")
    private val isSaving = MutableStateFlow(false)

    private val content: Flow<InboxContent> = repository.observeInbox()
        .map { items ->
            if (items.isEmpty()) InboxContent.Empty else InboxContent.Items(items)
        }
        .catch { emit(InboxContent.Error("خواندن صندوق ممکن نشد")) }

    val uiState: StateFlow<InboxUiState> =
        combine(content, draft, isSaving) { c, d, s ->
            InboxUiState(content = c, draft = d, isSaving = s)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InboxUiState(),
        )

    private val _events = Channel<InboxEvent>(Channel.BUFFERED)
    val events: Flow<InboxEvent> = _events.receiveAsFlow()

    fun onDraftChange(text: String) {
        draft.value = text
    }

    /** Capture the current draft. Faster than forgetting (Constitution §2). */
    fun capture() {
        val text = draft.value.trim()
        if (text.isEmpty() || isSaving.value) return

        viewModelScope.launch {
            isSaving.value = true
            try {
                repository.capture(text)
                draft.value = ""
                _events.send(InboxEvent.Captured)
            } catch (e: Exception) {
                _events.send(InboxEvent.CaptureFailed("ثبت نشد، دوباره تلاش کن"))
            } finally {
                isSaving.value = false
            }
        }
    }
}
