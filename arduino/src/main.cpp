#include <Arduino.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>

#include "config.h"
#include "wifi_manager.h"
#include "converters.h"
#include "motor.h"

// --- Global Variables ---
HTTPClient http;
unsigned long lastHttpUpdateTime = 0;
unsigned long lastServoUpdateTime = 0;

void setup() {
  Serial.begin(115200);
  Serial.println("\n--- ESP32 Smooth Motion Client ---");

  connectToWiFi();
  setupServo();
}


void handleHttpRequests() {
  String apiUrl = "http://" + String(PHONE_IP_ADDRESS) + ":" + String(PHONE_PORT) + String(API_ENDPOINT);

  http.begin(apiUrl);
  // Set a short timeout to prevent the loop from blocking for too long
  http.setTimeout(HTTP_UPDATE_INTERVAL_MS - 50);
  int httpCode = http.GET();

  if (httpCode == HTTP_CODE_OK) {
    String payload = http.getString();
    StaticJsonDocument<128> doc;
    DeserializationError error = deserializeJson(doc, payload);

    if (!error && doc.containsKey("brightness")) {
      int brightness = doc["brightness"];
      int newServoAngle = calculateServoAngleFromBrightness(brightness);

      // --- Log the new target ---
      Serial.print("New Data: Brightness=");
      Serial.print(brightness);
      Serial.print(", Target Angle=");
      Serial.println(newServoAngle);

      // --- SET THE NEW TARGET (does not move the servo directly) ---
      setTargetAngle(newServoAngle);

    } else {
      Serial.println("Error parsing JSON.");
    }
  } else {
    // Don't print error on timeout, it's expected sometimes
    if (httpCode > 0) {
      Serial.printf("HTTP Error. Code: %d\n", httpCode);
    }
  }
  http.end();
}

void loop() {
  unsigned long currentTime = millis();

  // --- Task 1: Fetch data from the server (less frequent) ---
  if (currentTime - lastHttpUpdateTime >= HTTP_UPDATE_INTERVAL_MS) {
    lastHttpUpdateTime = currentTime;
    handleHttpRequests();
  }

  // --- Task 2: Update the servo's physical position (very frequent) ---
  if (currentTime - lastServoUpdateTime >= SERVO_UPDATE_INTERVAL_MS) {
    lastServoUpdateTime = currentTime;
    updateServoPosition();
  }
}