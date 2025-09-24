#include <AccelStepper.h>
#include "motor.h"
#include "config.h"

// Define the AccelStepper interface type for a 4-pin driver
#define MotorInterfaceType AccelStepper::HALF4WIRE
const long STEPS_FOR_60_DEGREES = 1365;
const long offset = 0;

// Create an instance of the AccelStepper class
// Note: The pin order is IN1, IN3, IN2, IN4 for some drivers to ensure
// correct rotational direction. If your motor turns the wrong way,
// try swapping the middle two pins: AccelStepper(MotorInterfaceType, STEPPER_IN1, STEPPER_IN3, STEPPER_IN2, STEPPER_IN4);
AccelStepper stepper = AccelStepper(MotorInterfaceType, STEPPER_IN1, STEPPER_IN3, STEPPER_IN2, STEPPER_IN4);

void setupStepper() {
  Serial.println("Initializing stepper motor...");
  stepper.setMaxSpeed(STEPPER_MAX_SPEED);
  stepper.setAcceleration(STEPPER_ACCELERATION);
  stepper.setCurrentPosition(0); // Start at the 0 position
}

void moveStepperToBrightness(int brightness) {
  // Map the brightness value (0-255) to a stepper position (0 - STEPS_FOR_180_DEGREES)
  long targetPosition = map(brightness, MIN_BRIGHTNESS, MAX_BRIGHTNESS, -STEPS_FOR_60_DEGREES, STEPS_FOR_60_DEGREES);

  // Tell the stepper to move to the new target position
  stepper.moveTo(offset + targetPosition);

  Serial.print("Stepper Target: Brightness ");
  Serial.print(brightness);
  Serial.print(" -> Position ");
  Serial.println(targetPosition);
}

void runStepper() {
  // This is the magic function from AccelStepper.
  // It checks if the motor needs to move and sends step pulses if it does.
  // Calling this continuously in the main loop creates smooth movement
  // without blocking the rest of your code.
  stepper.run();
}