package com.iw.aeroskin

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.iw.aeroskin.data.BrightnessRepository
import com.iw.aeroskin.databinding.ActivityManualBrightnessBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ManualBrightnessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManualBrightnessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManualBrightnessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSlider()
        observeBrightness()
    }

    private fun setupSlider() {
        binding.brightnessSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val brightnessValue = progress.toDouble()
                    BrightnessRepository.setBrightness(brightnessValue)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun observeBrightness() {
        lifecycleScope.launch {
            BrightnessRepository.brightness.collectLatest { brightness ->
                updateUi(brightness)
            }
        }
    }

    private fun updateUi(brightness: Double) {
        binding.brightnessValueText.text = String.format("Brightness: %.2f", brightness)
        binding.brightnessSeekBar.progress = brightness.toInt()
    }
}