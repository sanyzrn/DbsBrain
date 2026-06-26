package ir.dbsgraphic.secondbrain.core.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GoalTest {

    @Test
    fun `codec round trips details`() {
        val details = GoalDetails(targetCount = 10, doneCount = 3)
        assertEquals(details, GoalCodec.decode(GoalCodec.encode(details)))
    }

    @Test
    fun `decode of blank yields a one-step goal`() {
        val d = GoalCodec.decode(null)
        assertEquals(1, d.target)
        assertEquals(0, d.done)
        assertFalse(d.isAchieved)
    }

    @Test
    fun `binary goal is achieved at one`() {
        assertTrue(GoalDetails(targetCount = 1, doneCount = 1).isAchieved)
        assertFalse(GoalDetails(targetCount = 1, doneCount = 0).isAchieved)
    }

    @Test
    fun `progress is a clamped fraction`() {
        assertEquals(0.5f, GoalDetails(targetCount = 4, doneCount = 2).progress, 0.0001f)
        // done beyond target clamps to 1.0 and reads as achieved
        val over = GoalDetails(targetCount = 4, doneCount = 9)
        assertEquals(1f, over.progress, 0.0001f)
        assertTrue(over.isAchieved)
    }

    @Test
    fun `target is at least one even if stored zero or negative`() {
        assertEquals(1, GoalDetails(targetCount = 0).target)
        assertEquals(1, GoalDetails(targetCount = -5).target)
    }
}
