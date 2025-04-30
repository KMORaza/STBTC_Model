package code.stbtcmodel.simulation;

import java.util.ArrayList;
import java.util.List;

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
    private double currentTime; // Current simulation time
    private List<Double> tHistory; // Time history
    private List<Double> THistory; // Temperature history
    private List<Double> SHistory; // Salinity history
    private List<Double> qHistory; // Flow rate history
    private final int maxPoints = 1000; // Maximum points to store

    public STBTCModel(double T0, double S0, double lambda, double mu, double R, double k,
                      double alpha, double beta, double dt) {
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
        double q = computeQ();
        double dTdt = lambda * (T0 - T) - R * Math.abs(q) * T;
        double dSdt = mu * (S0 - S) - R * Math.abs(q) * S;
        T += dTdt * dt;
        S += dSdt * dt;
        currentTime += dt;
        tHistory.add(currentTime);
        THistory.add(T);
        SHistory.add(S);
        qHistory.add(q);
        if (tHistory.size() > maxPoints) {
            tHistory.remove(0);
            THistory.remove(0);
            SHistory.remove(0);
            qHistory.remove(0);
        }
    }

    private double computeQ() {
        return k * (alpha * T - beta * S);
    }

    public void reset(double T0, double S0, double lambda, double mu, double R, double k,
                      double alpha, double beta, double dt) {
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