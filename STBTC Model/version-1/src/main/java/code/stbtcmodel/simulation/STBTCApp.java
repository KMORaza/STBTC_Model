package code.stbtcmodel.simulation;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class STBTCApp extends Application {
    @Override
    public void start(Stage stage) {
        STBTCController controller = new STBTCController();
        Scene scene = new Scene(controller.createContent(), 400, 700); 
        stage.setTitle("STBTC Two-Box Thermohaline Circulation Model");
        stage.setScene(scene);
        stage.setResizable(false); 
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}