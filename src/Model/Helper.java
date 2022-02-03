package Model;

import DBAccess.DBAppointments;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/** Static Helper Methods. */
public abstract class Helper {

    /** Reusable Navigate Method
     Switches between screens
     * @param file FXML file to load and navigate to
     * @param title New stage title
     */
    public static void navigateTo(ActionEvent actionEvent, String file, String title) throws IOException {
        Parent root = FXMLLoader.load(Helper.class.getResource(file));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    /** Appointment Alert
     * Displays a message if the user has an upcoming appointment
     * @param label - JFX Label to display the message in.
     */
    public static void appointmentAlert(Label label) {
        final ZoneId utcZone = ZoneId.of("GMT");

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(15);

        for(Appointment a : DBAppointments.getAllAppointments()) {
            LocalDateTime dbTime = a.getStart().toLocalDateTime();
            dbTime = dbTime.atZone(ZoneId.of("GMT")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

            if(a.getUserID() == User.getId()) {
                if(dbTime.isAfter(start) && (dbTime.isBefore(end))) {
                    label.setText("UPCOMING APPOINTMENT: #" + a.getId() + " at " + dbTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
                    label.getStyleClass().addAll("success");
                }
            }
        }
    }
}
