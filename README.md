### Modell der thermohalinen Stommel-Zwei-Box-Zirkulation, geschrieben in JavaFX (Model of Stommel Two-Box Thermohaline Circulation, written in JavaFX)

* Simulates thermohaline circulation by modeling the interaction between temperature difference (T) and salinity difference (S) between two ocean boxes such as a polar and an equatorial box.
* The circulation strength, represented by the flow rate q, is driven by density differences caused by thermal and saline expansion. The model incorporates stochastic forcing (noise) and uses numerical integration to evolve the system over time.
* The model uses the Runge-Kutta 4th-order (RK4) method for numerical integration.
* Simulation logic:-
  * Defines the core mathematical model and simulation logic.
  * Implements numerical integration and steady-state computation.
  * Plots time series of T (red), S (blue), and q (green) during simulation.
  * Plots steady-state q vs. μ (yellow) to analyze system behavior across a range of μ values (0.01 to 0.5).
  * Updates the simulation and redraws the plot in real-time when running.
  * Allows exploration of how steady-state flow q changes with the freshwater flux parameter μ, revealing potential bifurcations (e.g., transitions between circulation regimes).
  * The model includes Gaussian noise to simulate environmental variability.
 
  ---

  ![](https://github.com/KMORaza/STBTC_Model/blob/main/STBTC%20Model/version-2/src/screen.png)
