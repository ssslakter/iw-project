#pragma once

/**
 * @brief Initializes the servo motor.
 */
void setupServo();

/**
 * @brief Sets a new target angle for the servo to move towards.
 * This does NOT move the servo directly.
 * @param newTarget The desired final angle.
 */
void setTargetAngle(int newTarget);

/**
 * @brief This function should be called repeatedly in the main loop.
 * It calculates the next small step for the servo and moves it, creating a smooth motion.
 */
void updateServoPosition();