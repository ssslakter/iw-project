package com.iw.aeroskin

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity() {

    // UUIDs from your ESP32 code
    private val SERVICE_UUID = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b")
    private val CHARACTERISTIC_UUID = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8")

    // --- UI Elements ---
    private lateinit var statusTextView: TextView
    private lateinit var scanButton: Button
    private lateinit var buttonUp: Button
    private lateinit var buttonDown: Button
    private lateinit var buttonStop: Button
    private lateinit var numberEditText: EditText
    private lateinit var sendNumberButton: Button

    // --- Bluetooth components ---
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private val bleScanner by lazy { bluetoothAdapter.bluetoothLeScanner }
    private var bluetoothGatt: BluetoothGatt? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI Elements
        statusTextView = findViewById(R.id.statusTextView)
        scanButton = findViewById(R.id.scanButton)
        buttonUp = findViewById(R.id.buttonUp)
        buttonDown = findViewById(R.id.buttonDown)
        buttonStop = findViewById(R.id.buttonStop)
        numberEditText = findViewById(R.id.numberEditText)
        sendNumberButton = findViewById(R.id.sendNumberButton)

        // Set button click listeners
        scanButton.setOnClickListener { startScan() }
        buttonUp.setOnClickListener { sendMessage("u") }
        buttonDown.setOnClickListener { sendMessage("d") }
        buttonStop.setOnClickListener { sendMessage("s") }
        sendNumberButton.setOnClickListener {
            val numberText = numberEditText.text.toString()
            if (numberText.isNotEmpty()) {
                sendMessage(numberText)
            } else {
                Toast.makeText(this, "Please enter a number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "Please enable Bluetooth", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startScan() {
        requestPermissions()
        statusTextView.text = "Status: Scanning..."
        scanButton.isEnabled = false

        val scanFilter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(SERVICE_UUID))
            .build()
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        bleScanner.startScan(listOf(scanFilter), scanSettings, scanCallback)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            Log.i("ScanCallback", "Device found: ${device.name} - ${device.address}")
            bleScanner.stopScan(this)
            connectToDevice(device)
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("ScanCallback", "Scan failed with error code: $errorCode")
            statusTextView.text = "Status: Scan Failed"
            scanButton.isEnabled = true
        }
    }

    private fun connectToDevice(device: BluetoothDevice) {
        statusTextView.text = "Status: Connecting..."
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Log.i("GattCallback", "onConnectionStateChange: status=$status, newState=$newState")
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("GattCallback", "Connected to GATT server.")
                runOnUiThread { updateUi(true) } // Update UI on successful connection
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("GattCallback", "Disconnected from GATT server.")
                runOnUiThread { updateUi(false) } // Update UI on disconnection
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.w("GattCallback", "onServicesDiscovered received error: $status")
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("GattCallback", "Characteristic write successful for value: ${characteristic.getStringValue(0)}")
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Sent: ${characteristic.getStringValue(0)}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    // --- Reusable function to send a message ---
    private fun sendMessage(message: String) {
        val gatt = bluetoothGatt ?: return
        val service = gatt.getService(SERVICE_UUID)
        val characteristic = service?.getCharacteristic(CHARACTERISTIC_UUID)

        if (characteristic != null) {
            characteristic.value = message.toByteArray(Charsets.UTF_8)
            characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            val success = gatt.writeCharacteristic(characteristic)
            Log.d("SendMessage", "Write initiated for '$message': $success")
        } else {
            Toast.makeText(this, "Characteristic not found!", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Helper function to update the entire UI based on connection state ---
    private fun updateUi(isConnected: Boolean) {
        if (isConnected) {
            statusTextView.text = "Status: Connected"
            scanButton.isEnabled = false
            buttonUp.isEnabled = true
            buttonDown.isEnabled = true
            buttonStop.isEnabled = true
            numberEditText.isEnabled = true
            sendNumberButton.isEnabled = true
        } else {
            statusTextView.text = "Status: Disconnected"
            scanButton.isEnabled = true
            buttonUp.isEnabled = false
            buttonDown.isEnabled = false
            buttonStop.isEnabled = false
            numberEditText.isEnabled = false
            sendNumberButton.isEnabled = false
        }
    }
    
    // --- Permission Handling (remains the same) ---
    private val PERMISSION_REQUEST_CODE = 101
    private fun requestPermissions() { /* ... same as before ... */ }
}