#include "converters.h"
#include "config.h"
#include <Arduino.h>

/**
 * @brief Calculates the required voltage based on the measured brightness.
 *
 * @param brightness The brightness value (0-255).
 * @return The calculated voltage.
 */
float calculateVoltageFromBrightness(int brightness) {
  // Constrain the brightness value to the expected range
  brightness = max(MIN_BRIGHTNESS, min(brightness, MAX_BRIGHTNESS));

  float brightness_range = MAX_BRIGHTNESS - MIN_BRIGHTNESS;
  float voltage_range = MAX_VOLTAGE - MIN_VOLTAGE;

  float brightness_fraction = (float)(brightness - MIN_BRIGHTNESS) / brightness_range;
  float required_voltage = MIN_VOLTAGE + (brightness_fraction * voltage_range);

  return required_voltage;
}

/**
 * @brief Calculates the required servo angle based on the measured brightness.
 *
 * @param brightness The brightness value (0-255).
 * @return The calculated servo angle.
 */
int calculateServoAngleFromBrightness(int brightness) {
  // Constrain the brightness value to the expected range
  brightness = max(MIN_BRIGHTNESS, min(brightness, MAX_BRIGHTNESS));

  // Use the map function to perform a linear conversion
  return map(brightness, MIN_BRIGHTNESS, MAX_BRIGHTNESS, MIN_SERVO_ANGLE, MAX_SERVO_ANGLE);
}