package com.iw.aeroskin.network
import com.iw.aeroskin.utils.NetworkUtils

class ServerManager(private val listener: ServerStatusListener) {
    private var webServer: BrightnessWebServer? = null
    private val serverPort = 8080

    fun startServer() {
        val ipAddress = NetworkUtils.getDeviceIpAddress()
        if (ipAddress == null) {
            listener.onServerError("Could not get IP Address")
            return
        }

        try {
            webServer = BrightnessWebServer(serverPort)
            webServer?.start()
            val fullAddress = "http://$ipAddress:$serverPort"
            listener.onServerRunning(fullAddress)
        } catch (e: Exception) {
            e.printStackTrace()
            listener.onServerError("Error starting server")
        }
    }

    fun stopServer() {
        webServer?.stop()
        listener.onServerStopped()
    }

    fun isServerRunning(): Boolean {
        return webServer?.isAlive == true
    }

    interface ServerStatusListener {
        fun onServerRunning(ipAddress: String)
        fun onServerStopped()
        fun onServerError(message: String)
    }
}