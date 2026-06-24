package ir.dbsgraphic.secondbrain.core.data

import java.util.UUID

/** Time source — abstracted so capture timing is testable. */
fun interface Clock {
    fun now(): Long
}

/** Id source — abstracted so captured ids are testable. */
fun interface IdGenerator {
    fun newId(): String
}

internal val SystemClock = Clock { System.currentTimeMillis() }
internal val UuidGenerator = IdGenerator { UUID.randomUUID().toString() }
