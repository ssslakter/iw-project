#include "motor.h"
#include <ESP32Servo.h>
#include "config.h"

Servo microServo;

// State variables for smoothing
int targetAngle = 90; // The angle we want to reach
float currentAngle = 90.0; // The servo's current angle, as a float for precision

void setupServo() {
  microServo.attach(SERVO_GPIO_PIN);
  microServo.write(currentAngle); // Start at the initial position
  Serial.println("Servo motor initialized for smooth motion on GPIO " + String(SERVO_GPIO_PIN));
}

void setTargetAngle(int newTarget) {
  // Constrain the new target to a valid range and update our state
  targetAngle = constrain(newTarget, MIN_SERVO_ANGLE, MAX_SERVO_ANGLE);
}

void updateServoPosition() {
  // Check if the servo is already at the target
  // Use a small tolerance to prevent jittering when very close
  if (abs(targetAngle - currentAngle) > 0.5) {
    // Linear Interpolation (Lerp)
    // Move the current angle a small fraction of the way towards the target
    currentAngle = currentAngle + (targetAngle - currentAngle) * SMOOTHING_FACTOR;

    // Update the physical servo's position
    // The write() function takes an int, so we round the float value
    microServo.write((int)round(currentAngle));
  }
}