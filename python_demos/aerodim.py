import math

# --- Physical Constants ---
STEFAN_BOLTZMANN = 5.670374e-8  # W/(m^2 K^4)
T_SPACE_K = 3                  # Temperature of deep space in Kelvin

# --- SCENARIO 1: LOW EARTH ORBIT (LEO) SATELLITE ---

def calculate_leo_drag(cross_sectional_area_m2, drag_coefficient, density_kg_m3, velocity_m_s):
    """Calculates aerodynamic drag in LEO using the drag equation."""
    # F_D = 0.5 * rho * V^2 * C_D * A
    return 0.5 * density_kg_m3 * (velocity_m_s**2) * drag_coefficient * cross_sectional_area_m2

def calculate_thermal_radiation(surface_area_m2, emissivity, skin_temp_k):
    """Calculates heat radiated away using the Stefan-Boltzmann Law."""
    # Q_rad = ε * σ * A * (T_skin^4 - T_space^4)
    return emissivity * STEFAN_BOLTZMANN * surface_area_m2 * (skin_temp_k**4 - T_SPACE_K**4)

def model_leo_satellite():
    """Models and compares a satellite with and without AeroCool SkinTech."""
    print("--- LEO SATELLITE ANALYSIS ---")
    print("Analyzing a telescope in a 400km orbit.\n")

    # --- Simulation Parameters ---
    # LEO Environment
    LEO_ALTITUDE_KM = 400
    LEO_DENSITY_KG_M3 = 2.5e-12  # Approximate atmospheric density at 400km
    ORBITAL_VELOCITY_M_S = 7670  # Approx. velocity for a circular orbit at 400km

    # Satellite Properties
    TELESCOPE_BUS_AREA_M2 = 4.0   # Cross-section of the main body
    RADIATOR_PANEL_AREA_M2 = 0.0  # Area of old-style external radiators
    SATELLITE_CD = 2.2            # Typical drag coefficient for a complex shape in rarefied flow

    # Thermal Properties
    INTERNAL_HEAT_W = 1500        # Heat from electronics that must be rejected
    SKIN_TEMP_K = 300             # Target operating temperature (27°C)
    
    # --- Calculation for the Old Way (Traditional Radiators) ---
    print("1. OLD WAY: With Bulky External Radiators")
    total_area_old = TELESCOPE_BUS_AREA_M2 + RADIATOR_PANEL_AREA_M2
    drag_force_old = calculate_leo_drag(total_area_old, SATELLITE_CD, LEO_DENSITY_KG_M3, ORBITAL_VELOCITY_M_S)
    print(f"   - Total Cross-Sectional Area: {total_area_old:.2f} m^2")
    print(f"   - Orbital Drag Force: {drag_force_old:.6f} Newtons")
    
    # --- Calculation for the New Way (AeroCool SkinTech) ---
    print("\n2. NEW WAY: With AeroCool SkinTech")
    # The skin IS the body, so we eliminate the extra radiator panels.
    total_area_new = TELESCOPE_BUS_AREA_M2
    drag_force_new = calculate_leo_drag(total_area_new, SATELLITE_CD, LEO_DENSITY_KG_M3, ORBITAL_VELOCITY_M_S)
    print(f"   - Total Cross-Sectional Area: {total_area_new:.2f} m^2 (No external panels)")
    print(f"   - Orbital Drag Force: {drag_force_new:.6f} Newtons")

    # --- Thermal Control Demonstration ---
    print("\n   - SkinTech Thermal Control:")
    heat_radiated_cool_mode = calculate_thermal_radiation(total_area_new, 0.9, SKIN_TEMP_K) # High emissivity to cool
    heat_radiated_warm_mode = calculate_thermal_radiation(total_area_new, 0.1, SKIN_TEMP_K) # Low emissivity to warm
    print(f"     - Cool Mode (ε=0.9): Can radiate away {heat_radiated_cool_mode:.2f} W of heat.")
    print(f"     - Warm Mode (ε=0.1): Radiates only {heat_radiated_warm_mode:.2f} W, conserving heat.")

    # --- Results and Impact ---
    drag_reduction = ((drag_force_old - drag_force_new) / drag_force_old) * 100
    print("\n--- AERODYNAMIC IMPACT ---")
    print(f"AeroCool SkinTech reduces orbital drag by {drag_reduction:.2f}%.")
    print("This directly translates to massive fuel savings for station-keeping over the satellite's lifespan.\n")


# --- SCENARIO 2: RACING CAR ---

def calculate_top_speed(power_watts, drag_coefficient, frontal_area_m2, air_density_kg_m3):
    """Calculates the theoretical top speed where engine power equals drag power."""
    # P = Fd * V = (0.5 * rho * V^2 * Cd * A) * V
    # P = 0.5 * rho * Cd * A * V^3
    # V = (2 * P / (rho * Cd * A))^(1/3)
    # This formula finds the speed where all engine power is used to fight drag.
    v_cubed = (2 * power_watts) / (air_density_kg_m3 * drag_coefficient * frontal_area_m2)
    return v_cubed**(1/3) # returns speed in m/s

def calculate_required_convective_h(heat_to_dissipate_w, surface_area_m2, skin_temp_c, air_temp_c):
    """Calculates the required convective heat transfer coefficient 'h'."""
    # Q = h * A * (T_skin - T_air)  =>  h = Q / (A * delta_T)
    delta_t = skin_temp_c - air_temp_c
    if delta_t <= 0:
        return float('inf') # Avoid division by zero
    return heat_to_dissipate_w / (surface_area_m2 * delta_t)

def model_racing_car():
    """Models and compares a race car with and without AeroCool SkinTech."""
    print("--- RACING CAR ANALYSIS ---")
    print("Analyzing a 900 HP race car.\n")

    # --- Simulation Parameters ---
    # Environment
    AIR_DENSITY_KG_M3 = 1.225 # Sea level air density
    AMBIENT_TEMP_C = 25

    # Car Properties
    ENGINE_HP = 900
    ENGINE_POWER_WATTS = ENGINE_HP * 745.7
    THERMAL_EFFICIENCY = 0.35 # 35% of energy becomes motion, 65% becomes waste heat
    FRONTAL_AREA_M2 = 1.8
    SKINTECH_AREA_M2 = 5.0 # Total surface area of bodywork used for cooling
    COOLANT_TEMP_C = 95 # Target temp of the skin

    # --- Calculation for the Old Way (Traditional Radiator & Vents) ---
    print("1. OLD WAY: With Radiator Vents and Ducts")
    cd_old = 0.45 # Higher drag due to vents, grilles, and internal cooling drag
    top_speed_old_ms = calculate_top_speed(ENGINE_POWER_WATTS, cd_old, FRONTAL_AREA_M2, AIR_DENSITY_KG_M3)
    top_speed_old_kmh = top_speed_old_ms * 3.6
    print(f"   - Drag Coefficient (Cd): {cd_old} (High due to cooling drag)")
    print(f"   - Theoretical Top Speed: {top_speed_old_kmh:.2f} km/h")

    # --- Calculation for the New Way (AeroCool SkinTech) ---
    print("\n2. NEW WAY: With AeroCool SkinTech (Smooth Body)")
    cd_new = 0.30 # Lower drag from a clean, smooth body shape
    top_speed_new_ms = calculate_top_speed(ENGINE_POWER_WATTS, cd_new, FRONTAL_AREA_M2, AIR_DENSITY_KG_M3)
    top_speed_new_kmh = top_speed_new_ms * 3.6
    print(f"   - Drag Coefficient (Cd): {cd_new} (Aerodynamically clean)")
    print(f"   - Theoretical Top Speed: {top_speed_new_kmh:.2f} km/h")
    
    # --- Thermal Engineering Requirement ---
    heat_to_dissipate = ENGINE_POWER_WATTS * (1 - THERMAL_EFFICIENCY)
    required_h = calculate_required_convective_h(heat_to_dissipate, SKINTECH_AREA_M2, COOLANT_TEMP_C, AMBIENT_TEMP_C)
    print("\n   - SkinTech Thermal Requirement:")
    print(f"     - Engine Waste Heat to Dissipate: {heat_to_dissipate / 1000:.2f} kW")
    print(f"     - Required Convective Coefficient (h): {required_h:.2f} W/(m^2·K)")
    print("       (This value must be achieved by airflow at race speeds)")


    # --- Results and Impact ---
    speed_increase = top_speed_new_kmh - top_speed_old_kmh
    print("\n--- AERODYNAMIC IMPACT ---")
    print(f"AeroCool SkinTech allows for a top speed increase of {speed_increase:.2f} km/h.")
    print("The same engine power results in much higher performance by drastically reducing drag.")


# --- Main Execution ---
if __name__ == "__main__":
    model_leo_satellite()
    print("\n" + "="*50 + "\n")
    model_racing_car()