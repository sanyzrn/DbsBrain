package ir.dbsgraphic.secondbrain.feature.goals

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.dbsgraphic.secondbrain.core.data.GoalCodec
import ir.dbsgraphic.secondbrain.core.data.GoalDetails
import ir.dbsgraphic.secondbrain.core.data.GoalRepository
import ir.dbsgraphic.secondbrain.core.database.entity.Item
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class GoalUi(
    val item: Item,
    val details: GoalDetails,
    val dueAt: Long?,
)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
) : ViewModel() {

    val goals: StateFlow<List<GoalUi>> = goalRepository.observeGoals()
        .map { items ->
            items
                .map { GoalUi(item = it, details = GoalCodec.decode(it.details), dueAt = it.reminderAt) }
                // Unfinished first, then by soonest deadline.
                .sortedWith(compareBy({ it.details.isAchieved }, { it.dueAt ?: Long.MAX_VALUE }))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun create(title: String, targetCount: Int, dueAt: Long?) {
        if (title.isBlank()) return
        viewModelScope.launch { runCatching { goalRepository.createGoal(title, targetCount, dueAt) } }
    }

    fun logProgress(id: String) {
        viewModelScope.launch { goalRepository.logProgress(id, 1) }
    }

    fun markAchieved(id: String) {
        viewModelScope.launch { goalRepository.markAchieved(id) }
    }
}
