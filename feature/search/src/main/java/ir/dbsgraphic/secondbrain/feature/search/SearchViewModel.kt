package ir.dbsgraphic.secondbrain.feature.search

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.dbsgraphic.secondbrain.core.ai.AIProvider
import ir.dbsgraphic.secondbrain.core.data.ItemRepository
import ir.dbsgraphic.secondbrain.core.data.SettingsRepository
import ir.dbsgraphic.secondbrain.core.database.entity.Item
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class AskState(
    val question: String = "",
    val loading: Boolean = false,
    val answer: String? = null,
    val sources: List<Item> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: ItemRepository,
    private val aiProvider: AIProvider,
    settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val results: StateFlow<List<Item>> = _query
        .debounce(120)
        .flatMapLatest { q -> repository.search(q) }
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Whether the "ask your brain" mode is offered (AI enabled + configured). */
    val aiReady: StateFlow<Boolean> = settingsRepository.observeAiConfig()
        .map { it.isReady }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val _ask = MutableStateFlow(AskState())
    val ask: StateFlow<AskState> = _ask.asStateFlow()

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun onAskQuestionChange(value: String) {
        _ask.value = _ask.value.copy(question = value)
    }

    /** Ask-your-brain: retrieve with FTS, then ground the answer in the hits. */
    fun runAsk() {
        val question = _ask.value.question.trim()
        if (question.isEmpty() || _ask.value.loading) return
        viewModelScope.launch {
            _ask.value = _ask.value.copy(loading = true, answer = null, sources = emptyList())
            val sources = runCatching { repository.search(question).first().take(8) }.getOrDefault(emptyList())
            val answer = aiProvider.ask(question, sources.map { it.content })
            _ask.value = _ask.value.copy(
                loading = false,
                answer = answer ?: "پاسخی پیدا نشد.",
                sources = sources,
            )
        }
    }
}
