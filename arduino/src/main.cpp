#include <Arduino.h>

// put function declarations here:
int myFunction(int, int);

// ESP32 Built-in Sensor Data Logger

// The ESP32 has a built-in Hall effect sensor, a temperature sensor, 
// and capacitive touch pins. This program reads data from these sensors 
// and displays it in the serial monitor.

// Define the touch pin to be used
// GPIO4 is a good choice as it is T0
const int touchPin = 4; 

void setup() {
  // Initialize Serial Monitor
  Serial.begin(115200);
  Serial.println("ESP32 Built-in Sensor Data Logger");
}

void loop() {
  // --- Read Hall Effect Sensor ---
  // The hallRead() function returns the value from the internal Hall effect sensor.
  // This value changes in the presence of a magnetic field.
  int hallValue = hallRead();

  // --- Read Internal Temperature Sensor ---
  // The temperatureRead() function returns the internal temperature of the ESP32 chip in Fahrenheit.
  // Note: This is the temperature of the chip itself, not the ambient temperature.
  float tempF = temperatureRead();
  // Convert Fahrenheit to Celsius
  float tempC = (tempF - 32) / 1.8;

  // --- Read Capacitive Touch Sensor ---
  // The touchRead() function returns a value from the specified touch-enabled GPIO pin.
  // The value will be lower when the pin is touched.
  int touchValue = touchRead(touchPin);

  // --- Print Sensor Data to Serial Monitor ---
  Serial.print("Hall Effect: ");
  Serial.print(hallValue);
  
  Serial.print("  |  Internal Temperature: ");
  Serial.print(tempC);
  Serial.print(" °C");
  
  Serial.print("  |  Touch Pin (GPIO");
  Serial.print(touchPin);
  Serial.print("): ");
  Serial.println(touchValue);

  // Wait for a second before the next reading
  delay(1000);
}

// put function definitions here:
int myFunction(int x, int y) {
  return x + y;
}