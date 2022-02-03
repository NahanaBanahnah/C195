package Main;

import Database.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    /** Override of initialize.
     Loads the default Splash Screen.
     * */
    @Override
    public void start(Stage stage) throws Exception {
        DBConnection.startConnection();
        Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        stage.setTitle("Login");
        stage.setScene(new Scene(root, 1800, 800));
        stage.show();

    }

    public static void main(Stage[] args) {

        launch(String.valueOf(args));
    }

    @Override
    public void stop(){
        DBConnection.closeConnection();
    }
}
