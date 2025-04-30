module code.stbtcmodel.simulation.stbtcmodel {
    requires javafx.controls;
    requires javafx.fxml;


    opens code.stbtcmodel.simulation to javafx.fxml;
    exports code.stbtcmodel.simulation;
}