package com.iw.aeroskin

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.iw.aeroskin.data.BrightnessRepository
import com.iw.aeroskin.databinding.ActivityMainBinding
import com.iw.aeroskin.permissions.PermissionManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
        observeBrightness()
    }

    private fun setupUI() {
        binding.toggleServerButton.setOnClickListener {
            PermissionManager.checkCameraPermission(this) {
                // Here is the key change!
                // We pass `this` (the Activity, which is a LifecycleOwner)
                // to the ViewModel.
                viewModel.onToggleServerClicked(this)
            }
        }

        binding.manualModeButton.setOnClickListener {
            startActivity(Intent(this, ManualBrightnessActivity::class.java))
        }

        binding.sourceSwitch.setOnCheckedChangeListener { _, isChecked ->
            BrightnessRepository.source = if (isChecked) {
                BrightnessRepository.Source.MANUAL
            } else {
                BrightnessRepository.Source.SENSOR
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isServerRunning.observe(this) { isRunning ->
            binding.toggleServerButton.text = if (isRunning) "Stop Server" else "Start Server"
        }

        viewModel.serverStatus.observe(this) { status ->
            binding.statusText.text = status
        }

        viewModel.ipAddress.observe(this) { ip ->
            binding.ipAddressText.text = ip
        }
    }

    private fun observeBrightness() {
        lifecycleScope.launch {
            BrightnessRepository.brightness.collectLatest { brightness ->
                binding.brightnessText.text = String.format("%.2f", brightness)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.onRequestPermissionsResult(
            requestCode,
            grantResults,
            onPermissionGranted = {
                // Pass the lifecycle owner here as well
                viewModel.onToggleServerClicked(this)
            },
            onPermissionDenied = { /* Handle permission denial */ }
        )
    }

    override fun onResume() {
        super.onResume()
        binding.sourceSwitch.isChecked = (BrightnessRepository.source == BrightnessRepository.Source.MANUAL)
    }
}