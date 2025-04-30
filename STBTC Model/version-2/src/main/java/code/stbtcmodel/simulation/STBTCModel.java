package code.stbtcmodel.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class STBTCModel {
    private double T; // Temperature difference
    private double S; // Salinity difference
    private double lambda; // Thermal relaxation rate
    private double mu; // Freshwater flux parameter
    private double R; // Transport coefficient
    private double k; // Flow proportionality constant
    private double alpha; // Thermal expansion coefficient
    private double beta; // Saline expansion coefficient
    private double T0; // Reference temperature difference
    private double S0; // Reference salinity difference
    private double dt; // Time step
    private double noiseAmplitude; // Noise amplitude for stochastic forcing
    private double currentTime; // Current simulation time
    private List<Double> tHistory; // Time history
    private List<Double> THistory; // Temperature history
    private List<Double> SHistory; // Salinity history
    private List<Double> qHistory; // Flow rate history
    private final int maxPoints = 1000; // Maximum points to store
    private final Random random = new Random(42); // Fixed seed for reproducibility

    public STBTCModel(double T0, double S0, double lambda, double mu, double R, double k,
                      double alpha, double beta, double dt, double noiseAmplitude) {
        this.T = T0;
        this.S = S0;
        this.T0 = T0;
        this.S0 = S0;
        this.lambda = lambda;
        this.mu = mu;
        this.R = R;
        this.k = k;
        this.alpha = alpha;
        this.beta = beta;
        this.dt = dt;
        this.noiseAmplitude = noiseAmplitude;
        this.currentTime = 0.0;
        this.tHistory = new ArrayList<>();
        this.THistory = new ArrayList<>();
        this.SHistory = new ArrayList<>();
        this.qHistory = new ArrayList<>();
        tHistory.add(0.0);
        THistory.add(T);
        SHistory.add(S);
        qHistory.add(computeQ());
    }

    public void step() {
        double[] k1 = computeDerivatives(T, S);
        double[] k2 = computeDerivatives(T + dt * k1[0] / 2, S + dt * k1[1] / 2);
        double[] k3 = computeDerivatives(T + dt * k2[0] / 2, S + dt * k2[1] / 2);
        double[] k4 = computeDerivatives(T + dt * k3[0], S + dt * k3[1]);
        T += (dt / 6.0) * (k1[0] + 2 * k2[0] + 2 * k3[0] + k4[0]);
        S += (dt / 6.0) * (k1[1] + 2 * k2[1] + 2 * k3[1] + k4[1]);
        currentTime += dt;
        tHistory.add(currentTime);
        THistory.add(T);
        SHistory.add(S);
        qHistory.add(computeQ());
        if (tHistory.size() > maxPoints) {
            tHistory.remove(0);
            THistory.remove(0);
            SHistory.remove(0);
            qHistory.remove(0);
        }
    }

    private double[] computeDerivatives(double T, double S) {
        double q = computeQ(T, S);
        double dTdt = lambda * (T0 - T) - R * Math.abs(q) * T + noiseAmplitude * random.nextGaussian();
        double dSdt = mu * (S0 - S) - R * Math.abs(q) * S + noiseAmplitude * random.nextGaussian();
        return new double[]{dTdt, dSdt};
    }

    private double computeQ() {
        return computeQ(T, S);
    }

    private double computeQ(double T, double S) {
        return k * (alpha * T - beta * S);
    }

    public void reset(double T0, double S0, double lambda, double mu, double R, double k,
                      double alpha, double beta, double dt, double noiseAmplitude) {
        this.T = T0;
        this.S = S0;
        this.T0 = T0;
        this.S0 = S0;
        this.lambda = lambda;
        this.mu = mu;
        this.R = R;
        this.k = k;
        this.alpha = alpha;
        this.beta = beta;
        this.dt = dt;
        this.noiseAmplitude = noiseAmplitude;
        this.currentTime = 0.0;
        tHistory.clear();
        THistory.clear();
        SHistory.clear();
        qHistory.clear();
        tHistory.add(0.0);
        THistory.add(T);
        SHistory.add(S);
        qHistory.add(computeQ());
    }

    public double[] runToSteadyState(double mu) {
        this.mu = mu;
        double T_prev = T;
        double S_prev = S;
        double epsilon = 1e-6;
        int maxSteps = 100000;
        for (int i = 0; i < maxSteps; i++) {
            step();
            double[] derivatives = computeDerivatives(T, S);
            if (Math.abs(derivatives[0]) < epsilon && Math.abs(derivatives[1]) < epsilon) {
                break;
            }
            T_prev = T;
            S_prev = S;
        }
        return new double[]{T, S, computeQ()};
    }

    public List<Double> getTHistory() {
        return THistory;
    }

    public List<Double> getSHistory() {
        return SHistory;
    }

    public List<Double> getQHistory() {
        return qHistory;
    }

    public List<Double> getTimeHistory() {
        return tHistory;
    }
}