package com.iw.aeroskin.utils

import java.net.Inet4Address
import java.net.NetworkInterface

object NetworkUtils {
    fun getDeviceIpAddress(): String? {
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
}