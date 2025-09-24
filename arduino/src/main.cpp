#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#include <motor.h>

#define SERVICE_UUID "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
#define CHARACTERISTIC_UUID "beb5483e-36e1-4688-b7f5-ea07361b26a8"

int stepsFor90Degrees = 1024;

long targetPosition = 0;

void motor(bool clockwise)
{
  if (clockwise)
  {
    targetPosition = stepper.currentPosition() + stepsFor90Degrees;
  }
  else
  {
    targetPosition = stepper.currentPosition() - stepsFor90Degrees;
  }
  stepper.moveTo(targetPosition);
}

class MyCallbacks : public BLECharacteristicCallbacks
{
  void onWrite(BLECharacteristic *pCharacteristic)
  {
    std::string value = pCharacteristic->getValue();

    if (value.length() > 0)
    {
      Serial.print("Received Value: ");
      Serial.println(value.c_str());

      // --- Handle the new commands ---
      if (value == "u")
      {
        Serial.println("Action: Moving UP");
        motor(true);
      }
      else if (value == "d")
      {
        Serial.println("Action: Moving DOWN");
        motor(false);
      }
      else if (value == "s")
      {
        Serial.println("Action: STOPPING");
        // Add your motor control code for 'stop' here
      }
      else
      {
        // If it's not a known command, assume it's a number
        // Convert string to integer for use
        int number = atoi(value.c_str());
        Serial.print("Action: Received number: ");
        Serial.println(number);
        stepsFor90Degrees = number;
      }
    }
  }
};

void setup()
{
  Serial.begin(115200);
  Serial.println("Starting BLE server for Multi-Control...");

  BLEDevice::init("MyESP32");
  BLEServer *pServer = BLEDevice::createServer();
  BLEService *pService = pServer->createService(SERVICE_UUID);

  BLECharacteristic *pCharacteristic = pService->createCharacteristic(
      CHARACTERISTIC_UUID,
      BLECharacteristic::PROPERTY_WRITE);
  pCharacteristic->setCallbacks(new MyCallbacks());
  pService->start();

  BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
  pAdvertising->addServiceUUID(SERVICE_UUID);
  BLEDevice::startAdvertising();

  Serial.println("Ready to receive commands!");

  setupStepper();
}

void loop()
{
  delay(2000);
}