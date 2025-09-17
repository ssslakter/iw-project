package com.iw.aeroskin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LifecycleOwner // <-- Import this
import com.iw.aeroskin.camera.CameraHandler
import com.iw.aeroskin.data.BrightnessRepository
import com.iw.aeroskin.network.ServerManager

class MainViewModel(application: Application) : AndroidViewModel(application), ServerManager.ServerStatusListener {

    private val _serverStatus = MutableLiveData<String>("Server Stopped")
    val serverStatus: LiveData<String> = _serverStatus

    private val _ipAddress = MutableLiveData<String>("IP: N/A")
    val ipAddress: LiveData<String> = _ipAddress

    private val _isServerRunning = MutableLiveData<Boolean>(false)
    val isServerRunning: LiveData<Boolean> = _isServerRunning

    private val serverManager: ServerManager = ServerManager(this)
    private val cameraHandler: CameraHandler

    init {
        // Initialize CameraHandler with the application context
        cameraHandler = CameraHandler(application.applicationContext) { luma ->
             if (BrightnessRepository.source == BrightnessRepository.Source.SENSOR) {
                BrightnessRepository.setBrightness(luma)
            }
        }
    }

    // This method now accepts the LifecycleOwner
    fun onToggleServerClicked(lifecycleOwner: LifecycleOwner) {
        if (_isServerRunning.value == true) {
            stopServerAndCamera()
        } else {
            startServerAndCamera(lifecycleOwner)
        }
    }

    // Pass the lifecycleOwner down to the cameraHandler
    private fun startServerAndCamera(lifecycleOwner: LifecycleOwner) {
        serverManager.startServer()
        cameraHandler.startCamera(lifecycleOwner)
    }

    private fun stopServerAndCamera() {
        serverManager.stopServer()
        cameraHandler.stopCamera()
    }

    // --- ServerStatusListener Callbacks (no changes here) ---
    override fun onServerRunning(ip: String) {
        _serverStatus.postValue("Server Running")
        _ipAddress.postValue(ip)
        _isServerRunning.postValue(true)
    }

    override fun onServerStopped() {
        _serverStatus.postValue("Server Stopped")
        _ipAddress.postValue("IP: N/A")
        _isServerRunning.postValue(false)
    }

    override fun onServerError(message: String) {
        _serverStatus.postValue("Error: $message")
        _isServerRunning.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        stopServerAndCamera()
    }
}