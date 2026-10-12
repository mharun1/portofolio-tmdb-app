package com.harun.tmdbapp.ui.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class DisplayUtilsTest {

    @Test
    fun `toDisplayRuntime returns dash when runtime is null or zero`() {
        assertEquals("-", null.toDisplayRuntime())
        assertEquals("-", 0.toDisplayRuntime())
    }

    @Test
    fun `toDisplayRuntime formats runtime in hours and minutes properly`() {
        assertEquals("45min", 45.toDisplayRuntime())
        assertEquals("2h", 120.toDisplayRuntime())
        assertEquals("2h 28min", 148.toDisplayRuntime())
    }

    @Test
    fun `toDisplayDate returns Unknown when date is null empty or blank`() {
        assertEquals("Unknown", null.toDisplayDate())
        assertEquals("Unknown", "".toDisplayDate())
        assertEquals("Unknown", "   ".toDisplayDate())
    }

    @Test
    fun `toDisplayDate formats valid date correctly`() {
        assertEquals("2024 05 15", "2024-05-15".toDisplayDate())
    }

    @Test
    fun `toDisplayDate returns Unknown on invalid date format`() {
        assertEquals("Unknown", "not-a-date".toDisplayDate())
    }
}
