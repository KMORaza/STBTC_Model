package code.stbtcmodel.simulation;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

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
    private Canvas plotCanvas;
    private STBTCModel model;
    private GraphicsContext gc;
    private AnimationTimer timer;
    private boolean isRunning;

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
        Button startButton = new Button("Start");
        styleButton(startButton);
        startButton.setOnAction(e -> startSimulation());
        Button stopButton = new Button("Stop");
        styleButton(stopButton);
        stopButton.setOnAction(e -> stopSimulation());
        Button resetButton = new Button("Reset");
        styleButton(resetButton);
        resetButton.setOnAction(e -> resetSimulation());
        plotCanvas = new Canvas(350, 300); 
        gc = plotCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, plotCanvas.getWidth(), plotCanvas.getHeight());
        initializeModel();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isRunning) {
                    model.step();
                    plotResults();
                }
            }
        };
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: black;");
        root.getChildren().addAll(
                createLabeledField("λ (Thermal relaxation):", lambdaField),
                createLabeledField("μ (Freshwater flux):", muField),
                createLabeledField("R (Transport coeff):", RField),
                createLabeledField("k (Flow constant):", kField),
                createLabeledField("α (Thermal exp):", alphaField),
                createLabeledField("β (Saline exp):", betaField),
                createLabeledField("T0 (Init Temp):", T0Field),
                createLabeledField("S0 (Init Salinity):", S0Field),
                createLabeledField("dt (Time step):", dtField)
        );
        HBox buttonRow = new HBox(10, startButton, stopButton, resetButton);
        buttonRow.setAlignment(Pos.CENTER);
        root.getChildren().add(buttonRow);
        root.getChildren().add(plotCanvas);
        return root;
    }

    private void styleTextField(TextField field) {
        field.setPrefWidth(100);
        field.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-color: #555555; -fx-border-radius: 5;");
    }

    private void styleButton(Button button) {
        button.setPrefWidth(100);
        button.setStyle("-fx-background-color: #444444; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-color: #555555; -fx-border-radius: 5;");
    }

    private HBox createLabeledField(String labelText, TextField field) {
        Label label = new Label(labelText);
        label.setTextFill(Color.WHITE);
        label.setFont(new Font(14));
        HBox hbox = new HBox(10, label, field);
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
            model = new STBTCModel(T0, S0, lambda, mu, R, k, alpha, beta, dt);
        } catch (NumberFormatException e) {
            gc.setFill(Color.RED);
            gc.fillText("Invalid initial parameters", 10, 20);
        }
    }

    private void startSimulation() {
        if (!isRunning) {
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
        initializeModel();
        plotResults();
    }

    private void plotResults() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, plotCanvas.getWidth(), plotCanvas.getHeight());
        double width = plotCanvas.getWidth();
        double height = plotCanvas.getHeight();
        double maxT = model.getTHistory().stream().mapToDouble(Math::abs).max().orElse(1.0);
        double maxS = model.getSHistory().stream().mapToDouble(Math::abs).max().orElse(1.0);
        double maxQ = model.getQHistory().stream().mapToDouble(Math::abs).min().orElse(1.0);
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
}