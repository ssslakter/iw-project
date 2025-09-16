package com.iw.aeroskin.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object BrightnessRepository {
    // Enum to define the source of the brightness value
    enum class Source {
        SENSOR,
        MANUAL
    }

    // Holds the current brightness value, exposed as a StateFlow
    private val _brightness = MutableStateFlow(0.0)
    val brightness = _brightness.asStateFlow()

    // The current source of brightness data
    var source: Source = Source.SENSOR

    // Function to update the brightness value
    fun setBrightness(value: Double) {
        _brightness.value = value
    }
}