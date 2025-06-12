import pandas as pd
import matplotlib.pyplot as plt

# Load CSV
df = pd.read_csv("results/sweepresult_sinus2.csv")

# Keep only final step for each run
df_final = df[df["Step"] == df["Step"].max()]

# Group by RainfallPrecipitation, compute mean burned ratio
grouped = df_final.groupby("RainfallPrecipitation")["BurnedRatio"].mean().reset_index()

# Plot
plt.figure(figsize=(8,6))
plt.plot(grouped["RainfallPrecipitation"], grouped["BurnedRatio"], marker='o')
plt.xlabel("Rainfall Precipitation")
plt.ylabel("Final Burned Ratio")
plt.title("Phase Transition of Fire vs Rainfall Precipitation")
plt.grid(True)
plt.tight_layout()

# Save plot
plt.savefig("results/phase_transition4_sinus_2.png")
plt.show()
