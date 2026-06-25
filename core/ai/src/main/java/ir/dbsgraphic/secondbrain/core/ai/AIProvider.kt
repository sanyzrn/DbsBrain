package ir.dbsgraphic.secondbrain.core.ai

/** AI's triage suggestion. Never applied automatically — the user decides (§12). */
data class TriageSuggestion(
    val type: String? = null,        // note|task|idea|doc
    val projectName: String? = null,
    val tags: List<String> = emptyList(),
)

/** AI's reminder suggestion (epoch millis + a short label). Suggestion only. */
data class ReminderSuggestion(
    val whenMillis: Long? = null,
    val label: String? = null,
)

/**
 * The single doorway to AI (engineering spine). Provider-agnostic and pluggable.
 * Every method returns null when AI is off, unconfigured, or the call fails —
 * so the whole app degrades gracefully and stays fully usable without AI
 * (Constitution §12). AI only ever *suggests*.
 */
interface AIProvider {

    /** True only when AI is enabled and configured. Drives whether UI offers it. */
    suspend fun isReady(): Boolean

    /** Transcribe a recorded voice note to text. */
    suspend fun transcribe(audioPath: String): String?

    /** Extract text from an image (OCR). */
    suspend fun ocr(imagePath: String): String?

    /** Suggest a type/project/tags for a captured thought. */
    suspend fun suggestTriage(content: String, projectNames: List<String>): TriageSuggestion?

    /** Suggest a reminder time for a captured thought. */
    suspend fun suggestReminder(content: String): ReminderSuggestion?

    /**
     * Ask-your-brain. [context] is the retrieved snippets (from FTS, never a
     * fixed dump) the answer must be grounded in.
     */
    suspend fun ask(question: String, context: List<String>): String?
}

/** The default: AI does nothing. Safe, offline, always available. */
class NoOpAIProvider : AIProvider {
    override suspend fun isReady(): Boolean = false
    override suspend fun transcribe(audioPath: String): String? = null
    override suspend fun ocr(imagePath: String): String? = null
    override suspend fun suggestTriage(content: String, projectNames: List<String>): TriageSuggestion? = null
    override suspend fun suggestReminder(content: String): ReminderSuggestion? = null
    override suspend fun ask(question: String, context: List<String>): String? = null
}
