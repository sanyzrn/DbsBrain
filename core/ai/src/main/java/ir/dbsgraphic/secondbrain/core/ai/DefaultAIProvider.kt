package ir.dbsgraphic.secondbrain.core.ai

import ir.dbsgraphic.secondbrain.core.data.AiConfig
import ir.dbsgraphic.secondbrain.core.data.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.intOrNull
import javax.inject.Inject

/**
 * The real provider. Reads the current [AiConfig] on each call, so toggling AI
 * on/off at runtime just works. When AI isn't ready, every method returns null —
 * indistinguishable from the no-op provider (Constitution §12).
 */
class DefaultAIProvider @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val client: OpenAiClient,
) : AIProvider {

    private val json = Json { ignoreUnknownKeys = true }

    private suspend fun config(): AiConfig? =
        settingsRepository.observeAiConfig().first().takeIf { it.isReady }

    override suspend fun isReady(): Boolean = config() != null

    override suspend fun transcribe(audioPath: String): String? {
        val cfg = config() ?: return null
        return client.transcribe(cfg, audioPath)?.ifBlank { null }
    }

    override suspend fun ocr(imagePath: String): String? {
        val cfg = config() ?: return null
        val dataUrl = client.imageToDataUrl(imagePath) ?: return null
        return client.chat(
            config = cfg,
            system = "متن داخل تصویر را دقیق و بدون توضیح اضافه استخراج کن. اگر متنی نیست، خالی برگردان.",
            user = "متن این تصویر را بنویس.",
            imageDataUrl = dataUrl,
        )?.ifBlank { null }
    }

    override suspend fun suggestTriage(content: String, projectNames: List<String>): TriageSuggestion? {
        val cfg = config() ?: return null
        val projects = if (projectNames.isEmpty()) "—" else projectNames.joinToString("، ")
        val raw = client.chat(
            config = cfg,
            system = "تو یک دستیار دسته‌بندی هستی. فقط یک شیء JSON برگردان با کلیدهای " +
                "type (یکی از note|task|idea|doc)، project (نام یکی از پروژه‌های داده‌شده یا null)، " +
                "tags (آرایه‌ای از حداکثر سه برچسب کوتاه فارسی). هیچ توضیحی نده.",
            user = "متن: \"$content\"\nپروژه‌های موجود: $projects",
        ) ?: return null

        return runCatching {
            val obj = extractJsonObject(raw) ?: return null
            val type = obj["type"]?.jsonPrimitive?.contentOrNull?.takeIf {
                it in setOf("note", "task", "idea", "doc")
            }
            val project = obj["project"]?.jsonPrimitive?.contentOrNull
                ?.takeIf { it.isNotBlank() && it != "null" && it in projectNames }
            val tags = obj["tags"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull }
                ?.filter { it.isNotBlank() }
                ?.take(3)
                ?: emptyList()
            TriageSuggestion(type = type, projectName = project, tags = tags)
        }.getOrNull()
    }

    override suspend fun suggestReminder(content: String): ReminderSuggestion? {
        val cfg = config() ?: return null
        val raw = client.chat(
            config = cfg,
            system = "اگر متن نیاز به یادآوری زمان‌دار دارد، فقط JSON برگردان با کلیدهای " +
                "minutesFromNow (عدد صحیح دقیقه از حالا) و label (برچسب کوتاه). " +
                "اگر یادآوری لازم نیست، {\"minutesFromNow\": null} برگردان.",
            user = content,
        ) ?: return null

        return runCatching {
            val obj = extractJsonObject(raw) ?: return null
            val minutes = obj["minutesFromNow"]?.jsonPrimitive?.intOrNull ?: return null
            val label = obj["label"]?.jsonPrimitive?.contentOrNull
            ReminderSuggestion(
                whenMillis = System.currentTimeMillis() + minutes * 60_000L,
                label = label,
            )
        }.getOrNull()
    }

    override suspend fun ask(question: String, context: List<String>): String? {
        val cfg = config() ?: return null
        if (context.isEmpty()) return null
        val grounding = context.mapIndexed { i, c -> "[${i + 1}] $c" }.joinToString("\n")
        return client.chat(
            config = cfg,
            system = "تو دستیار «مغز دوم» کاربر هستی. فقط بر اساس یادداشت‌های زیر پاسخ بده. " +
                "اگر پاسخ در یادداشت‌ها نیست، صادقانه بگو پیدا نشد. کوتاه و فارسی پاسخ بده.",
            user = "یادداشت‌ها:\n$grounding\n\nسؤال: $question",
        )?.ifBlank { null }
    }

    private fun extractJsonObject(text: String): JsonObject? = runCatching {
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start < 0 || end <= start) return null
        json.parseToJsonElement(text.substring(start, end + 1)).jsonObject
    }.getOrNull()
}
