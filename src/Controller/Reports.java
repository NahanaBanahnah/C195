package Controller;

import DBAccess.DBAppointments;
import DBAccess.DBCustomer;
import DBAccess.DBReports;
import DBAccess.DBShared;
import Model.Appointment;
import Model.Contact;
import Model.Customer;
import Model.Helper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;

/** Reports Class. */
public class Reports extends DBReports implements Initializable {
    public ComboBox<LocalDate> monthCombo;
    public ComboBox<String> typeCombo;
    public Label reportOneLabel;
    public Label bottomLabel;
    public Label alertLabel;
    public ComboBox<Contact> contactCombo;
    public ComboBox<Customer> customerCombo;

    //TableView
    public TableView<Appointment> resultTable;
    public TableColumn<Appointment, Integer> idColumn;
    public TableColumn<Appointment, String> customerColumn;
    public TableColumn<Appointment, String> contactColumn;
    public TableColumn<Appointment, String> titleColumn;
    public TableColumn<Appointment, String> descColumn;
    public TableColumn<Appointment, String> locationColumn;
    public TableColumn<Appointment, String> typeColumn;
    public TableColumn<Appointment, String> startColumn;
    public TableColumn<Appointment, String> endColumn;
    public TableColumn<Appointment, Integer> userColumn;

    //Drop Downs
    final private static ObservableList<LocalDate> months = FXCollections.observableArrayList();
    final private static ObservableList<String> types = FXCollections.observableArrayList();

    private static ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

    final private static ZoneId utcZone = ZoneId.of("GMT");
    final private static ZoneId sysZone = ZoneId.systemDefault();
    final private static FormatStyle format = FormatStyle.SHORT;

    /** Main initialize class.
     * Sets tableview columns
     * Populates Dropdowns
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Helper.appointmentAlert(alertLabel);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descColumn.setCellValueFactory(new PropertyValueFactory<>("desc"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startColumn.setCellValueFactory((param) -> new SimpleObjectProperty<>(param.getValue().getStart().toLocalDateTime().atZone(utcZone).withZoneSameInstant(sysZone).toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(format))));
        endColumn.setCellValueFactory((param) -> new SimpleObjectProperty<>(param.getValue().getEnd().toLocalDateTime().atZone(utcZone).withZoneSameInstant(sysZone).toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(format))));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));

        //Build the month Options
        for(int i = 1; i < 13; i++) {
            LocalDate currentMonth = LocalDate.of(2022, i, 1);
            months.add(currentMonth);
        }

        //Set the months
        monthCombo.setItems(months);

        Callback<ListView<LocalDate>, ListCell<LocalDate>> factory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLLL");
                setText(empty ? "" : (formatter.format(item)));
            }
        };

        monthCombo.setCellFactory(factory);
        monthCombo.setButtonCell(factory.call(null));

        //Set the Types
        types.addAll("Planning Session", "Training Session", "Review Session");
        typeCombo.setItems(types);

        //Set Contacts
        ObservableList<Contact> allContacts = DBShared.getAllContacts();
        contactCombo.setItems(allContacts);

        //Set Customers
        ObservableList<Customer> allCustomers = DBCustomer.getAllCustomers();
        customerCombo.setItems(allCustomers);

        //Get All appointments to filter for reports
        allAppointments = DBAppointments.getAllAppointments();
    }

    /** Report One
     * Gets count of appointments of a given month and type
     */
    public void onActionReportOne() {
        if((monthCombo.getSelectionModel().getSelectedItem() != null) && (typeCombo.getSelectionModel().getSelectedItem() != null)) {
            String month = monthCombo.getSelectionModel().getSelectedItem().format(DateTimeFormatter.ofPattern("MM"));
            String type = typeCombo.getSelectionModel().getSelectedItem();

            int appointmentCount = getReportOne(month, type);
            String label;

            if(appointmentCount == 1) {
                label = "Appointment";
            } else {
                label = "Appointments";
            }
            reportOneLabel.setText(appointmentCount + " " + type + " " + label + " In " + monthCombo.getSelectionModel().getSelectedItem().format(DateTimeFormatter.ofPattern("LLLL")) );
        }
    }

    /** Report Two
     * Gets schedule (all appointments) for a given contact
     */
    public void onActionReportTwo() {
        FilteredList<Appointment> filteredContact = new FilteredList<>(allAppointments);
        // lambda to filter views
        filteredContact.setPredicate(row -> row.getContactId() == contactCombo.getSelectionModel().getSelectedItem().getId());
        resultTable.setItems(filteredContact);
        bottomLabel.setText("Appointments for Contact " + contactCombo.getSelectionModel().getSelectedItem().getName());
    }

    /** Report Three
     * Gets schedule (all appointments) for a given customer
     */
    public void onActionReportThree() {
        FilteredList<Appointment> filteredContact = new FilteredList<>(allAppointments);
        // lambda to filter views
        filteredContact.setPredicate(row -> row.getCustomerId() == customerCombo.getSelectionModel().getSelectedItem().getId());
        resultTable.setItems(filteredContact);
        bottomLabel.setText("Appointments for Customer " + customerCombo.getSelectionModel().getSelectedItem().getName());
    }

    /** Button Navigation
     *
     * @param actionEvent
     * @throws IOException
     */
    public void onActionCust(ActionEvent actionEvent) throws IOException {
        Helper.navigateTo(actionEvent, "/View/Customer.fxml", "Customers");
    }

    /** Button Navigation
     *
     * @param actionEvent
     * @throws IOException
     */
    public void onActionApp(ActionEvent actionEvent) throws IOException {
        Helper.navigateTo(actionEvent, "/View/Appointments.fxml", "Appointments");
    }
}
