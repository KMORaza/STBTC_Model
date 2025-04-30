package code.stbtcmodel.simulation;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.ArrayList;
import java.util.List;

public class STBTCController {
    private TextField lambdaField;
    private TextField muField;
    private TextField RField;
    private TextField kField;
    private TextField alphaField;
    private TextField betaField;
    private TextField T0Field;
    private TextField S0Field;
    private TextField dtField;
    private TextField noiseField;
    private Canvas plotCanvas;
    private STBTCModel model;
    private GraphicsContext gc;
    private AnimationTimer timer;
    private boolean isRunning;
    private boolean bifurcationMode;

    public VBox createContent() {
        lambdaField = new TextField("0.1");
        styleTextField(lambdaField);
        muField = new TextField("0.1");
        styleTextField(muField);
        RField = new TextField("1.0");
        styleTextField(RField);
        kField = new TextField("1.0");
        styleTextField(kField);
        alphaField = new TextField("0.2");
        styleTextField(alphaField);
        betaField = new TextField("0.8");
        styleTextField(betaField);
        T0Field = new TextField("1.0");
        styleTextField(T0Field);
        S0Field = new TextField("1.0");
        styleTextField(S0Field);
        dtField = new TextField("0.01");
        styleTextField(dtField);
        noiseField = new TextField("0.0");
        styleTextField(noiseField);
        Button startButton = new Button("Start");
        styleButton(startButton);
        startButton.setOnAction(e -> startSimulation());
        Button stopButton = new Button("Stop");
        styleButton(stopButton);
        stopButton.setOnAction(e -> stopSimulation());
        Button resetButton = new Button("Reset");
        styleButton(resetButton);
        resetButton.setOnAction(e -> resetSimulation());
        Button bifurcationButton = new Button("Bifurcate");
        styleButton(bifurcationButton);
        bifurcationButton.setOnAction(e -> runBifurcationAnalysis());
        plotCanvas = new Canvas(350, 240);
        gc = plotCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, plotCanvas.getWidth(), plotCanvas.getHeight());
        initializeModel();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isRunning && !bifurcationMode) {
                    model.step();
                    plotResults();
                }
            }
        };
        VBox root = new VBox(8);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: black;");
        GridPane paramGrid = new GridPane();
        paramGrid.setHgap(5);
        paramGrid.setVgap(5);
        paramGrid.setAlignment(Pos.CENTER);
        paramGrid.add(createLabeledField("λ (Thermal relax):", lambdaField), 0, 0);
        paramGrid.add(createLabeledField("μ (Freshwater):", muField), 1, 0);
        paramGrid.add(createLabeledField("R (Transport):", RField), 0, 1);
        paramGrid.add(createLabeledField("k (Flow const):", kField), 1, 1);
        paramGrid.add(createLabeledField("α (Thermal exp):", alphaField), 0, 2);
        paramGrid.add(createLabeledField("β (Saline exp):", betaField), 1, 2);
        paramGrid.add(createLabeledField("T₀ (Init Temp):", T0Field), 0, 3);
        paramGrid.add(createLabeledField("S₀ (Init Salinity):", S0Field), 1, 3);
        paramGrid.add(createLabeledField("dt (Time step):", dtField), 0, 4);
        paramGrid.add(createLabeledField("Noise (Amp):", noiseField), 1, 4);
        HBox buttonRow = new HBox(5, startButton, stopButton, resetButton, bifurcationButton);
        buttonRow.setAlignment(Pos.CENTER);
        root.getChildren().addAll(paramGrid, buttonRow, plotCanvas);
        return root;
    }
    private void styleTextField(TextField field) {
        field.setPrefWidth(70);
        field.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 12px; -fx-border-color: #555555; -fx-border-radius: 5;");
    }
    private void styleButton(Button button) {
        button.setPrefWidth(70);
        button.setStyle("-fx-background-color: #444444; -fx-text-fill: white; -fx-font-size: 12px; -fx-border-color: #555555; -fx-border-radius: 5;");
    }
    private HBox createLabeledField(String labelText, TextField field) {
        Label label = new Label(labelText);
        label.setTextFill(Color.WHITE);
        label.setFont(new Font(12));
        HBox hbox = new HBox(5, label, field);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }
    private void initializeModel() {
        try {
            double lambda = Double.parseDouble(lambdaField.getText());
            double mu = Double.parseDouble(muField.getText());
            double R = Double.parseDouble(RField.getText());
            double k = Double.parseDouble(kField.getText());
            double alpha = Double.parseDouble(alphaField.getText());
            double beta = Double.parseDouble(betaField.getText());
            double T0 = Double.parseDouble(T0Field.getText());
            double S0 = Double.parseDouble(S0Field.getText());
            double dt = Double.parseDouble(dtField.getText());
            double noiseAmplitude = Double.parseDouble(noiseField.getText());
            model = new STBTCModel(T0, S0, lambda, mu, R, k, alpha, beta, dt, noiseAmplitude);
        } catch (NumberFormatException e) {
            gc.setFill(Color.RED);
            gc.fillText("Invalid initial parameters", 10, 20);
        }
    }
    private void startSimulation() {
        if (!isRunning && !bifurcationMode) {
            initializeModel();
            isRunning = true;
            timer.start();
        }
    }
    private void stopSimulation() {
        isRunning = false;
        timer.stop();
    }

    private void resetSimulation() {
        stopSimulation();
        bifurcationMode = false;
        initializeModel();
        plotResults();
    }

    private void runBifurcationAnalysis() {
        stopSimulation();
        bifurcationMode = true;
        try {
            double lambda = Double.parseDouble(lambdaField.getText());
            double R = Double.parseDouble(RField.getText());
            double k = Double.parseDouble(kField.getText());
            double alpha = Double.parseDouble(alphaField.getText());
            double beta = Double.parseDouble(betaField.getText());
            double T0 = Double.parseDouble(T0Field.getText());
            double S0 = Double.parseDouble(S0Field.getText());
            double dt = Double.parseDouble(dtField.getText());
            double noiseAmplitude = Double.parseDouble(noiseField.getText());
            List<Double> muValues = new ArrayList<>();
            List<Double> qValues = new ArrayList<>();
            double muMin = 0.01;
            double muMax = 0.5;
            int steps = 50;
            for (double mu = muMin; mu <= muMax; mu += (muMax - muMin) / steps) {
                model = new STBTCModel(T0, S0, lambda, mu, R, k, alpha, beta, dt, noiseAmplitude);
                double[] steadyState = model.runToSteadyState(mu);
                muValues.add(mu);
                qValues.add(steadyState[2]);
            }
            plotBifurcation(muValues, qValues);
        } catch (NumberFormatException e) {
            gc.setFill(Color.RED);
            gc.fillText("Invalid parameters for bifurcation", 10, 20);
        }
    }
    private void plotResults() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, plotCanvas.getWidth(), plotCanvas.getHeight());
        double width = plotCanvas.getWidth();
        double height = plotCanvas.getHeight();
        double maxT = model.getTHistory().stream().mapToDouble(Math::abs).max().orElse(1.0);
        double maxS = model.getSHistory().stream().mapToDouble(Math::abs).max().orElse(1.0);
        double maxQ = model.getQHistory().stream().mapToDouble(Math::abs).max().orElse(1.0);
        double maxY = Math.max(maxT, Math.max(maxS, maxQ)) * 1.2;
        gc.setStroke(Color.WHITE);
        gc.strokeLine(50, height - 50, width - 50, height - 50);
        gc.strokeLine(50, 50, 50, height - 50);
        /// Plot T (red)
        gc.setStroke(Color.RED);
        for (int i = 1; i < model.getTHistory().size(); i++) {
            double x1 = 50 + (i - 1) * (width - 100) / (model.getTHistory().size() - 1);
            double y1 = height - 50 - (model.getTHistory().get(i - 1) / maxY) * (height - 100);
            double x2 = 50 + i * (width - 100) / (model.getTHistory().size() - 1);
            double y2 = height - 50 - (model.getTHistory().get(i) / maxY) * (height - 100);
            gc.strokeLine(x1, y1, x2, y2);
        }
        /// Plot S (blue)
        gc.setStroke(Color.BLUE);
        for (int i = 1; i < model.getSHistory().size(); i++) {
            double x1 = 50 + (i - 1) * (width - 100) / (model.getSHistory().size() - 1);
            double y1 = height - 50 - (model.getSHistory().get(i - 1) / maxY) * (height - 100);
            double x2 = 50 + i * (width - 100) / (model.getSHistory().size() - 1);
            double y2 = height - 50 - (model.getSHistory().get(i) / maxY) * (height - 100);
            gc.strokeLine(x1, y1, x2, y2);
        }
        /// Plot q (green)
        gc.setStroke(Color.GREEN);
        for (int i = 1; i < model.getQHistory().size(); i++) {
            double x1 = 50 + (i - 1) * (width - 100) / (model.getQHistory().size() - 1);
            double x2 = 50 + i * (width - 100) / (model.getQHistory().size() - 1);
            double y1 = height - 50 - (model.getQHistory().get(i - 1) / maxY) * (height - 100);
            double y2 = height - 50 - (model.getQHistory().get(i) / maxY) * (height - 100);
            gc.strokeLine(x1, y1, x2, y2);
        }
        /// Add legend
        gc.setFill(Color.WHITE);
        gc.setFont(new Font(12));
        gc.fillText("T (red), S (blue), q (green)", 60, 60);
    }
    private void plotBifurcation(List<Double> muValues, List<Double> qValues) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, plotCanvas.getWidth(), plotCanvas.getHeight());
        double width = plotCanvas.getWidth();
        double height = plotCanvas.getHeight();
        double maxQ = qValues.stream().mapToDouble(Math::abs).max().orElse(1.0);
        double minQ = qValues.stream().mapToDouble(v -> v).min().orElse(-1.0);
        double maxY = Math.max(maxQ, Math.abs(minQ)) * 1.2;
        double muMin = muValues.get(0);
        double muMax = muValues.get(muValues.size() - 1);
        gc.setStroke(Color.WHITE);
        gc.strokeLine(50, height - 50, width - 50, height - 50); /// μ
        gc.strokeLine(50, 50, 50, height - 50); /// q
        /// Plot q vs. μ
        gc.setStroke(Color.YELLOW);
        for (int i = 1; i < muValues.size(); i++) {
            double x1 = 50 + (muValues.get(i - 1) - muMin) * (width - 100) / (muMax - muMin);
            double y1 = height - 50 - ((qValues.get(i - 1) + maxY) / (2 * maxY)) * (height - 100);
            double x2 = 50 + (muValues.get(i) - muMin) * (width - 100) / (muMax - muMin);
            double y2 = height - 50 - ((qValues.get(i) + maxY) / (2 * maxY)) * (height - 100);
            gc.strokeLine(x1, y1, x2, y2);
        }
        gc.setFill(Color.WHITE);
        gc.setFont(new Font(12));
        gc.fillText("Bifurcation: q vs. μ (yellow)", 60, 60);
    }
}