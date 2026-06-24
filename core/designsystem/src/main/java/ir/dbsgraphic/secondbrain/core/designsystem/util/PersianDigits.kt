package ir.dbsgraphic.secondbrain.core.designsystem.util

private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

/**
 * Fold Latin digits to Persian. Use for human-facing prose (Yekan Bakh).
 * Data shown in the mono "instrument" voice keeps Latin digits deliberately.
 */
fun String.toPersianDigits(): String = buildString(length) {
    for (c in this@toPersianDigits) {
        append(if (c in '0'..'9') persianDigits[c - '0'] else c)
    }
}
