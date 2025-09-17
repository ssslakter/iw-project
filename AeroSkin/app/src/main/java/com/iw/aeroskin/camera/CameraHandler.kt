package com.iw.aeroskin.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner // <-- Import this
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraHandler(
    // It only needs a Context to get the CameraProvider instance
    private val context: Context,
    private val onBrightnessUpdate: (Double) -> Unit
) {
    private var cameraExecutor: ExecutorService? = null

    // This is the key change: startCamera now requires the LifecycleOwner
    fun startCamera(lifecycleOwner: LifecycleOwner) {
        cameraExecutor = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    // Use the non-null executor
                    it.setAnalyzer(cameraExecutor!!, LuminosityAnalyzer { luma ->
                        onBrightnessUpdate(luma)
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                // It now binds to the lifecycleOwner passed into the method
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalyzer)
            } catch (exc: Exception) {
                // Handle exceptions
                exc.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(context))
    }

    fun stopCamera() {
        cameraExecutor?.shutdown()
        cameraExecutor = null // Set to null to allow for restart
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.get().unbindAll()
    }
}