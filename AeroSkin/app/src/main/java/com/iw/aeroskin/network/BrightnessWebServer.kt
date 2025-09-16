package com.iw.aeroskin.network

import com.iw.aeroskin.data.BrightnessRepository
import fi.iki.elonen.NanoHTTPD

class BrightnessWebServer(port: Int) : NanoHTTPD(port) {

    override fun serve(session: IHTTPSession?): Response {
        if (session?.method == Method.GET && session.uri == "/brightness") {
            val brightnessValue = BrightnessRepository.brightness.value
            val json = "{\"brightness\": $brightnessValue}"
            return newFixedLengthResponse(Response.Status.OK, "application/json", json)
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found")
    }
}