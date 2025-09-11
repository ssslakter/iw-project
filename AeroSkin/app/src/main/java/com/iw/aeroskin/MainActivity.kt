package com.iw.aeroskin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.iw.aeroskin.databinding.ActivityMainBinding
import fi.iki.elonen.NanoHTTPD
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    // ViewBinding for easy access to UI elements
    private lateinit var binding: ActivityMainBinding

    // A dedicated thread for camera analysis to avoid blocking the UI
    private lateinit var cameraExecutor: ExecutorService

    // The web server instance
    private var webServer: BrightnessWebServer? = null
    private val serverPort = 8080

    // A thread-safe variable to hold the latest brightness value
    @Volatile
    private var latestBrightness: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the button to toggle the server on and off
        binding.toggleServerButton.setOnClickListener {
            if (webServer?.isAlive == true) {
                stopServer()
            } else {
                // Request camera permission before starting
                requestCameraPermission()
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    // --- Web Server Implementation (using NanoHTTPD) ---
    inner class BrightnessWebServer : NanoHTTPD(serverPort) {
        override fun serve(session: IHTTPSession?): Response {
            // We only respond to requests for "/brightness"
            if (session?.method == Method.GET && session.uri == "/brightness") {
                // Create a simple JSON response
                val json = "{\"brightness\": $latestBrightness}"
                return newFixedLengthResponse(Response.Status.OK, "application/json", json)
            }
            // For any other request, return a 404 Not Found error
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found")
        }
    }

    // --- Server Control Functions ---
    private fun startServer() {
        // Get the device's local IP address to display it
        val ipAddress = getDeviceIpAddress()
        if (ipAddress == null) {
            binding.statusText.text = "Error: Could not get IP Address"
            return
        }

        try {
            webServer = BrightnessWebServer()
            webServer?.start()
            runOnUiThread {
                binding.statusText.text = "Server Running"
                binding.ipAddressText.text = "http://$ipAddress:$serverPort"
                binding.toggleServerButton.text = "Stop Server"
            }
            // Once the server is running, start the camera analysis
            startCamera()
        } catch (e: Exception) {
            e.printStackTrace()
            runOnUiThread {
                binding.statusText.text = "Error starting server"
            }
        }
    }

    private fun stopServer() {
        webServer?.stop()
        // Stop camera analysis when the server stops
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.get().unbindAll()

        runOnUiThread {
            binding.statusText.text = "Server Stopped"
            binding.ipAddressText.text = "IP: N/A"
            binding.toggleServerButton.text = "Start Server"
        }
    }

    // --- CameraX Implementation ---
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Set up the ImageAnalysis use case
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        // This is where we get the brightness value from the analyzer
                        latestBrightness = luma
                        runOnUiThread {
                            // Update the UI with the new brightness value
                            binding.brightnessText.text = String.format("%.2f", luma)
                        }
                    })
                }

            // Select the back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind everything before rebinding
                cameraProvider.unbindAll()
                // Bind the use case to the activity's lifecycle
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, imageAnalyzer
                )
            } catch (exc: Exception) {
                // Handle exceptions
            }

        }, ContextCompat.getMainExecutor(this))
    }

    // --- Brightness Calculation Logic ---
    private class LuminosityAnalyzer(private val listener: (Double) -> Unit) : ImageAnalysis.Analyzer {
        override fun analyze(image: androidx.camera.core.ImageProxy) {
            // The image format from CameraX is YUV. The first plane (Y) is the luminance plane.
            val buffer = image.planes[0].buffer
            val data = ByteArray(buffer.remaining())
            buffer.get(data)

            // Calculate the average luminance (brightness)
            val averageLuma = data.map { it.toInt() and 0xFF }.average()

            listener(averageLuma)

            // CRITICAL: Must close the image to receive the next one
            image.close()
        }
    }

    // --- Utility and Permission Handling ---
    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startServer() // Permission is already granted
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startServer() // Permission was granted
        } else {
            // Permission was denied
        }
    }

    private fun getDeviceIpAddress(): String? {
        // A helper function to find the device's non-loopback IPv4 address
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces().toList()
            for (intf in networkInterfaces) {
                val addrs = intf.inetAddresses.toList()
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return addr.hostAddress
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopServer()
        cameraExecutor.shutdown()
    }
}