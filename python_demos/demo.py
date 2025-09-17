import time
import requests
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation

# --- CONFIGURATION ---
# IMPORTANT: Replace this with the IP address shown on your phone's screen.
PHONE_IP_ADDRESS = "192.168.0.15" 
# --------------------

# --- Do not change these unless you changed them in the app ---
PHONE_PORT = 8080
API_ENDPOINT = f"http://{PHONE_IP_ADDRESS}:{PHONE_PORT}/brightness"

# --- Plotting and Simulation Parameters ---
UPDATE_INTERVAL_SECONDS = 1.0  # How often to request data from the phone (in seconds)
PLOT_WINDOW_SECONDS = 60       # How many seconds of data to show on the plot at once

# Voltage mapping (Luminance from camera is 0-255)
MIN_VOLTAGE = 0.0
MAX_VOLTAGE = 5.0
MIN_BRIGHTNESS = 0    # Darkest possible value from camera
MAX_BRIGHTNESS = 255  # Brightest possible value from camera

# --- Global Data Storage ---
time_points = []
brightness_levels = []
voltage_levels = []

start_time = time.time()

# --- Set up the plot ---
fig, ax1 = plt.subplots(figsize=(14, 7))
ax2 = ax1.twinx()

# Create empty line objects that we will update with data later
line1, = ax1.plot([], [], 'o-', color='tab:orange', label='Phone Camera Brightness (0-255)')
line2, = ax2.plot([], [], 'o-', color='tab:blue', label='Calculated Electrochromic Voltage (V)')

def get_brightness_from_phone():
    """Makes an HTTP request to the phone's web server and returns the brightness."""
    try:
        # Set a timeout to prevent the script from hanging if the network is slow
        response = requests.get(API_ENDPOINT, timeout=1.0)
        
        # Check if the request was successful
        if response.status_code == 200:
            data = response.json()
            return data.get('brightness')
        else:
            print(f"Error: Received status code {response.status_code}")
            return None
    except requests.exceptions.RequestException as e:
        # Handle connection errors, timeouts, etc.
        print(f"Connection Error: {e}")
        return None

def calculate_voltage_from_brightness(brightness):
    """Calculates the required voltage based on the measured brightness."""
    if brightness is None:
        return None
        
    brightness = max(MIN_BRIGHTNESS, min(brightness, MAX_BRIGHTNESS))
    brightness_range = MAX_BRIGHTNESS - MIN_BRIGHTNESS
    voltage_range = MAX_VOLTAGE - MIN_VOLTAGE
    brightness_fraction = (brightness - MIN_BRIGHTNESS) / brightness_range
    required_voltage = MIN_VOLTAGE + (brightness_fraction * voltage_range)
    return required_voltage

def init_plot():
    """Initializes the plot axes and labels."""
    ax1.set_xlabel('Time (s)')
    ax1.set_ylabel('Measured Brightness (Luminance)', color='tab:orange')
    ax2.set_ylabel('Calculated Voltage (V)', color='tab:blue')
    ax1.tick_params(axis='y', labelcolor='tab:orange')
    ax2.tick_params(axis='y', labelcolor='tab:blue')
    
    ax1.set_ylim(MIN_BRIGHTNESS - 10, MAX_BRIGHTNESS + 10)
    ax2.set_ylim(MIN_VOLTAGE - 0.5, MAX_VOLTAGE + 0.5)
    
    ax1.grid(True, linestyle='--', alpha=0.6)
    fig.suptitle('Real-Time Telescope Simulation (Data from Phone Camera)', fontsize=16)
    
    # Combine legends from both axes for a single, clean legend
    lines, labels = ax1.get_legend_handles_labels()
    lines2, labels2 = ax2.get_legend_handles_labels()
    ax2.legend(lines + lines2, labels + labels2, loc='upper left')
    
    return line1, line2

def update_plot(frame):
    """This function is called for each frame of the animation."""
    # 1. Get new data
    brightness = get_brightness_from_phone()
    
    if brightness is not None:
        # 2. Process the data
        voltage = calculate_voltage_from_brightness(brightness)
        current_time = time.time() - start_time

        # 3. Append to our data lists
        time_points.append(current_time)
        brightness_levels.append(brightness)
        voltage_levels.append(voltage)
        
        # 4. Update the plot data
        line1.set_data(time_points, brightness_levels)
        line2.set_data(time_points, voltage_levels)
        
        # 5. Dynamically adjust the x-axis to create a scrolling window effect
        if current_time > PLOT_WINDOW_SECONDS:
            ax1.set_xlim(current_time - PLOT_WINDOW_SECONDS, current_time)
        else:
            ax1.set_xlim(0, PLOT_WINDOW_SECONDS)

    # Return the updated line objects
    return line1, line2

# --- Main execution ---
if __name__ == "__main__":
    print("--- AeroSkin Real-Time Python Client ---")
    print("1. Make sure your phone and this computer are on the SAME Wi-Fi network.")
    print(f"2. Ensure the IP address in the script is set to: {PHONE_IP_ADDRESS}")
    print("3. Start the server in the Android app.")
    print("--------------------------------------------------")
    print("Starting plot... Close the plot window to stop the script.")

    # Create the animation object
    ani = FuncAnimation(fig,
                        update_plot,
                        init_func=init_plot,
                        interval=UPDATE_INTERVAL_SECONDS * 1000, # Interval in milliseconds
                        blit=False,
                        cache_frame_data=False) # Important for continuous data streams

    # Show the plot and start the animation
    plt.show()

    print("Plot window closed. Script finished.")