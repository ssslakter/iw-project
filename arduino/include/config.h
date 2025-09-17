#pragma once

// --- Wi-Fi Configuration ---
#define WIFI_SSID "wifi-name"
#define WIFI_PASSWORD "pswd"

// --- Server Configuration ---
// IMPORTANT: Replace this with the IP address shown on your phone's screen.
#define PHONE_IP_ADDRESS "192.168.6.23"
#define PHONE_PORT 8080
#define API_ENDPOINT "/brightness"


#define SERVO_GPIO_PIN 13
#define SMOOTHING_FACTOR 0.5 // Tune this value (0.0 to 1.0). Lower is smoother.

// --- Control Parameters ---
#define HTTP_UPDATE_INTERVAL_MS 200 // How often to fetch data from the server (in milliseconds)
#define SERVO_UPDATE_INTERVAL_MS 20 // How often to update the servo's physical position (50Hz)


// --- Conversion Mapping ---
// Voltage mapping (Luminance from camera is 0-255)
#define MIN_VOLTAGE 0.0
#define MAX_VOLTAGE 5.0
#define MIN_BRIGHTNESS 0
#define MAX_BRIGHTNESS 255

// Servo mapping (adjust if you are using a servo)
#define MIN_SERVO_ANGLE 0
#define MAX_SERVO_ANGLE 180