#ifndef MOTOR_H
#define MOTOR_H

#include <AccelStepper.h>

extern AccelStepper stepper;
/**
 * @brief Initializes the stepper motor and its settings.
 * Must be called once in the main setup() function.
 */
void setupStepper();

/**
 * @brief Calculates the target stepper position from a brightness value
 * and commands the stepper to move to that position.
 *
 * @param brightness The brightness value (0-255) from the server.
 */
void moveStepperToBrightness(int brightness);

/**
 * @brief Runs the stepper motor's internal processing loop.
 * This function MUST be called as often as possible in the main loop()
 * to ensure smooth, non-blocking motor movement.
 */
void runStepper();

#endif