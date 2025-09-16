package com.iw.aeroskin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.iw.aeroskin.camera.CameraHandler
import com.iw.aeroskin.data.BrightnessRepository
import com.iw.aeroskin.databinding.ActivityMainBinding
import com.iw.aeroskin.network.ServerManager
import com.iw.aeroskin.permissions.PermissionManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), ServerManager.ServerStatusListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var serverManager: ServerManager
    private lateinit var cameraHandler: CameraHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the server manager, passing the activity as the listener
        serverManager = ServerManager(this)

        // Initialize the camera handler
        // The lambda updates the BrightnessRepository only if the source is SENSOR
        cameraHandler = CameraHandler(this) { luma ->
            if (BrightnessRepository.source == BrightnessRepository.Source.SENSOR) {
                BrightnessRepository.setBrightness(luma)
            }
        }

        // Set up all UI listeners and initial states
        setupUI()
        // Start observing brightness changes from the repository
        observeBrightness()
    }

    private fun setupUI() {
        // --- Toggle Server Button ---
        binding.toggleServerButton.setOnClickListener {
            if (serverManager.isServerRunning()) {
                stopServerAndCamera()
            } else {
                // Check for camera permission before starting
                PermissionManager.checkCameraPermission(this) {
                    startServerAndCamera()
                }
            }
        }

        // --- Manual Mode Button ---
        binding.manualModeButton.setOnClickListener {
            val intent = Intent(this, ManualBrightnessActivity::class.java)
            startActivity(intent)
        }

        // --- Brightness Source Switch ---
        binding.sourceSwitch.setOnCheckedChangeListener { _, isChecked ->
            BrightnessRepository.source = if (isChecked) {
                BrightnessRepository.Source.MANUAL
            } else {
                BrightnessRepository.Source.SENSOR
            }
        }
    }

    /**
     * Observes the brightness value from the central repository and updates the UI.
     */
    private fun observeBrightness() {
        lifecycleScope.launch {
            BrightnessRepository.brightness.collectLatest { brightness ->
                runOnUiThread {
                    binding.brightnessText.text = String.format("%.2f", brightness)
                }
            }
        }
    }

    private fun startServerAndCamera() {
        serverManager.startServer()
        cameraHandler.startCamera()
    }

    private fun stopServerAndCamera() {
        serverManager.stopServer()
        cameraHandler.stopCamera()
    }

    // --- ServerStatusListener Callbacks ---

    override fun onServerRunning(ipAddress: String) {
        runOnUiThread {
            binding.statusText.text = "Server Running"
            binding.ipAddressText.text = ipAddress
            binding.toggleServerButton.text = "Stop Server"
        }
    }

    override fun onServerStopped() {
        runOnUiThread {
            binding.statusText.text = "Server Stopped"
            binding.ipAddressText.text = "IP: N/A"
            binding.toggleServerButton.text = "Start Server"
        }
    }

    override fun onServerError(message: String) {
        runOnUiThread {
            binding.statusText.text = "Error: $message"
        }
    }

    // --- Permission Handling ---

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.onRequestPermissionsResult(
            requestCode,
            grantResults,
            onPermissionGranted = { startServerAndCamera() },
            onPermissionDenied = {
                // Optionally, show a message to the user that permission is needed
            }
        )
    }

    /**
     * Sync the UI switch state with the repository when the activity is resumed.
     */
    override fun onResume() {
        super.onResume()
        binding.sourceSwitch.isChecked = (BrightnessRepository.source == BrightnessRepository.Source.MANUAL)
    }

    /**
     * Clean up resources when the activity is destroyed.
     */
    override fun onDestroy() {
        super.onDestroy()
        stopServerAndCamera()
    }
}