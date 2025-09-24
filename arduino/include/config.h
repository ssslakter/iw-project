#pragma once

// --- Wi-Fi Configuration ---
#define WIFI_SSID "huawei"
#define WIFI_PASSWORD "yarik54321"

// --- Server Configuration ---
#define PHONE_IP_ADDRESS "192.168.246.44"
#define PHONE_PORT 8080
#define API_ENDPOINT "/brightness"

// --- Control Parameters ---
#define UPDATE_INTERVAL_SECONDS 0.01

// --- Conversion Mapping ---
#define MIN_VOLTAGE 0.0
#define MAX_VOLTAGE 5.0
#define MIN_BRIGHTNESS 0
#define MAX_BRIGHTNESS 1000
#define MIN_SERVO_ANGLE 0
#define MAX_SERVO_ANGLE 180

// --- Stepper Motor Configuration ---
// The four control pins for the stepper motor driver (e.g., ULN2003)
#define STEPPER_IN1 13
#define STEPPER_IN2 27
#define STEPPER_IN3 14
#define STEPPER_IN4 12

// Performance settings for the stepper motor
#define STEPPER_MAX_SPEED 500.0
#define STEPPER_ACCELERATION 500.0

// Steps for a 180-degree rotation. You may need to adjust this value
// based on your specific stepper motor's steps per revolution and gear ratio.
// A common 28BYJ-48 geared stepper has 2048 steps for a full 360-degree
// revolution of the output shaft, so 1024 steps is 180 degrees.
#define STEPS_FOR_180_DEGREES 2048