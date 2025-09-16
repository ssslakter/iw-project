import matplotlib
matplotlib.use('QtAgg')

import time
import random
import numpy as np
import matplotlib.pyplot as plt

PHOTO_INTERVAL_SECONDS = 1  # Time between each photo (can be faster for a lively demo)
TOTAL_SIMULATION_TIME_SECONDS = 60 # How long the simulation will run
MIN_VOLTAGE = 0.0  # Minimum voltage (fully transparent)
MAX_VOLTAGE = 5.0  # Maximum voltage (fully opaque)
MIN_BRIGHTNESS = 100  # Arbitrary unit for a dim object
MAX_BRIGHTNESS = 10000 # Arbitrary unit for a bright object

CELESTIAL_TARGETS = [
    "Orion Nebula",
    "Betelgeuse (Red Supergiant)",
    "Andromeda Galaxy",
    "Crab Pulsar",
    "Hubble Deep Field",
    "Sirius (Brightest Star)",
]

def calculate_voltage_from_brightness(brightness):
    """Calculates the required voltage based on the measured brightness."""
    brightness = max(MIN_BRIGHTNESS, min(brightness, MAX_BRIGHTNESS))
    brightness_range = MAX_BRIGHTNESS - MIN_BRIGHTNESS
    voltage_range = MAX_VOLTAGE - MIN_VOLTAGE
    brightness_fraction = (brightness - MIN_BRIGHTNESS) / brightness_range
    required_voltage = MIN_VOLTAGE + (brightness_fraction * voltage_range)
    return required_voltage

def run_and_plot_realtime():
    """
    Runs the telescope simulation and updates a plot in real-time.
    """
    print("--- Starting Real-Time Telescope Simulation ---")
    print("A plot window should appear. The simulation will update it live.")
    print("Close the plot window to end the simulation early.")
    print("-" * 60)

    # --- Step 1: Set up the plot for interactive mode ---
    plt.ion() # Turn on interactive mode
    fig, ax1 = plt.subplots(figsize=(14, 7))

    # Configure the primary axis (Brightness)
    color = 'tab:orange'
    ax1.set_xlabel('Time (s)')
    ax1.set_ylabel('Measured Brightness (units)', color=color)
    ax1.tick_params(axis='y', labelcolor=color)
    ax1.grid(True, linestyle='--', alpha=0.6)
    ax1.set_ylim(MIN_BRIGHTNESS - 500, MAX_BRIGHTNESS + 500) # Pre-set Y limits

    # Configure the secondary axis (Voltage)
    ax2 = ax1.twinx()
    color = 'tab:blue'
    ax2.set_ylabel('Applied Voltage (V)', color=color)
    ax2.tick_params(axis='y', labelcolor=color)
    ax2.set_ylim(MIN_VOLTAGE - 0.5, MAX_VOLTAGE + 0.5)

    # Create empty plot lines that we will update later
    # The comma is important: ax1.plot returns a list of lines.
    line_brightness, = ax1.plot([], [], 'o-', color='tab:orange', label='Object Brightness')
    line_voltage, = ax2.plot([], [], 's--', color='tab:blue', label='Filter Voltage')

    # Add title and legend
    plt.title('Telescope Brightness vs. Electrochromic Voltage (Live)', fontsize=16)
    fig.legend(loc='upper left', bbox_to_anchor=(0.1, 0.9))

    # --- Step 2: Main simulation loop ---
    time_points, brightness_levels, voltage_levels = [], [], []
    start_time = time.time()
    current_time = 0

    while current_time < TOTAL_SIMULATION_TIME_SECONDS:
        # Check if the plot window has been closed by the user
        if not plt.fignum_exists(fig.number):
            print("Plot window closed. Exiting simulation.")
            break
            
        # 1. Simulate observing a new target
        target_object = random.choice(CELESTIAL_TARGETS)
        measured_brightness = random.uniform(MIN_BRIGHTNESS, MAX_BRIGHTNESS)
        voltage_to_apply = calculate_voltage_from_brightness(measured_brightness)
        
        current_time = time.time() - start_time

        # 2. Print status to the console
        print(f"[{current_time:.1f}s] Pointing at: {target_object}")
        print(f"          Brightness: {measured_brightness:.2f} -> Voltage: {voltage_to_apply:.2f}V")

        # 3. Append new data
        time_points.append(current_time)
        brightness_levels.append(measured_brightness)
        voltage_levels.append(voltage_to_apply)

        # 4. Update the plot data
        line_brightness.set_data(time_points, brightness_levels)
        line_voltage.set_data(time_points, voltage_levels)

        # 5. Rescale the X-axis to fit the new data
        ax1.set_xlim(0, max(10, current_time * 1.1)) # Keep some space on the right

        # 6. Redraw the canvas and pause
        # plt.pause() handles both pausing and GUI events (like drawing).
        # This replaces time.sleep().
        plt.pause(PHOTO_INTERVAL_SECONDS)

    print("-" * 60)
    print("--- Simulation Finished ---")
    
    # Turn off interactive mode and show final plot until closed
    plt.ioff()
    plt.show()

if __name__ == "__main__":
    # Before running, ensure you have the necessary Qt bindings. If not:
    # pip install PySide6  (or PyQt6)
    run_and_plot_realtime()