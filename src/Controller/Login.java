package Controller;

import DBAccess.DBLogin;
import Model.Helper;
import Model.TranslateText;
import Model.User;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

/** Login Controller Methods. */
public class Login implements Initializable {

    public Label labelUsername;
    public Label labelPassword;
    public Label labelLogin;
    public Label errorLabel;
    public Label regionLabel;
    public Button loginButton;

    public TextField usernameInput;
    public PasswordField passwordInput;


    /** Override of initialize.
     Loads the Login Screen and translates text between English and French.
     * */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorLabel.setVisible(false);
        regionLabel.setText(Locale.getDefault().getLanguage());


            ResourceBundle rb = ResourceBundle.getBundle("ResourceBundles/Nat", Locale.getDefault());

            if(Locale.getDefault().getLanguage().equals("fr") || Locale.getDefault().getLanguage().equals("en")) {
                final String usernameText = "Username";
                final String passwordText = "Password";
                final String loginText = "Login";
                final String error = "Error";

                TranslateText translated = s -> rb.getString(s);

                labelUsername.setText(translated.setText(usernameText));
                labelPassword.setText(translated.setText(passwordText));
                labelLogin.setText(translated.setText(loginText).toUpperCase());
                loginButton.setText(translated.setText(loginText));
                errorLabel.setText(translated.setText(error));

        }
    }

    /** Login Button Click
     Handles clicking the login button and check validity of user login input
     * @param actionEvent event of click
     */
    public void onActionLogin(ActionEvent actionEvent) throws IOException {
        User user = DBLogin.checkUserLogin(usernameInput.getText(), passwordInput.getText());
        FileWriter fw = new FileWriter("login_activity.txt", true);
        PrintWriter pw = new PrintWriter(fw);
        String date = LocalDateTime.now().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if(user != null) {
            String username = User.getUsername();
            int id = User.getId();

            pw.println(date + "\t\tNew Login From: \t\t" + username + "\t\tUser ID: " + id);
            Helper.navigateTo(actionEvent, "/View/Customer.fxml", "Customers");

        } else {
            pw.println(date + "\t\tFailed Login");
            errorLabel.setVisible(true);
        }
        pw.close();
    }
}
