package ir.dbsgraphic.secondbrain

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dbsgraphic.secondbrain.core.data.ItemRepository
import ir.dbsgraphic.secondbrain.core.data.SettingsRepository
import ir.dbsgraphic.secondbrain.core.data.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppState(
    val loading: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val onboardingComplete: Boolean = false,
)

@HiltViewModel
class AppViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val itemRepository: ItemRepository,
    @ApplicationContext private val appContext: Context,
) : ViewModel() {

    val state: StateFlow<AppState> = combine(
        settingsRepository.observeThemeMode(),
        settingsRepository.observeOnboardingComplete(),
    ) { theme, onboarding ->
        AppState(loading = false, themeMode = theme, onboardingComplete = onboarding)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AppState())

    fun completeOnboarding() {
        viewModelScope.launch { settingsRepository.setOnboardingComplete(true) }
    }

    /**
     * Capture content shared into the app (ACTION_SEND). Returns true if this was
     * a share intent worth acting on, so the caller can confirm to the user.
     */
    fun ingestShareIntent(intent: Intent): Boolean {
        if (intent.action != Intent.ACTION_SEND) return false
        val mime = intent.type ?: return false

        when {
            mime.startsWith("text/") -> {
                val text = intent.getStringExtra(Intent.EXTRA_TEXT)?.trim().orEmpty()
                if (text.isEmpty()) return false
                viewModelScope.launch { itemRepository.captureShared(text, null, "text") }
            }
            mime.startsWith("image/") -> {
                @Suppress("DEPRECATION")
                val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM) ?: return false
                val caption = intent.getStringExtra(Intent.EXTRA_TEXT)?.trim().orEmpty()
                viewModelScope.launch {
                    val path = ShareIngest.copyToBlobs(appContext, uri)
                    if (path != null) itemRepository.captureShared(caption, path, "image")
                }
            }
            else -> return false
        }
        return true
    }
}
