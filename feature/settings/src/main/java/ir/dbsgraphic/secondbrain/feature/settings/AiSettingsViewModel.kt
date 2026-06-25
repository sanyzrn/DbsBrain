package ir.dbsgraphic.secondbrain.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.dbsgraphic.secondbrain.core.data.AiConfig
import ir.dbsgraphic.secondbrain.core.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val config: StateFlow<AiConfig> = settingsRepository.observeAiConfig()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AiConfig())

    fun setEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setAiEnabled(enabled) }
    }

    fun save(baseUrl: String, apiKey: String, chatModel: String, transcribeModel: String) {
        viewModelScope.launch {
            settingsRepository.setAiConfig(baseUrl, apiKey, chatModel, transcribeModel)
        }
    }
}
