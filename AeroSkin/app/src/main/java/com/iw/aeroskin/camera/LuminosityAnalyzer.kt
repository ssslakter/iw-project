package com.iw.aeroskin.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class LuminosityAnalyzer(private val listener: (Double) -> Unit) : ImageAnalysis.Analyzer {
    override fun analyze(image: ImageProxy) {
        val buffer = image.planes[0].buffer
        val data = ByteArray(buffer.remaining())
        buffer.get(data)
        val averageLuma = data.map { it.toInt() and 0xFF }.average()
        listener(averageLuma)
        image.close()
    }
}