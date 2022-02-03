package Controller;

import DBAccess.DBAppointments;
import DBAccess.DBCustomer;
import DBAccess.DBShared;
import Model.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/** Appointment Class. */
public class Appointments extends DBAppointments implements Initializable {

    //Table Views
    public TableView<Appointment> apptTable;
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

    //Case Checks
    private static Boolean isModify = false;

    //Appointment Listing
    private static ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private static Appointment selectedAppointment;

    //Dropdown Lists
    final private static ObservableList<Customer> allCustomers = DBCustomer.getAllCustomers();
    final private static ObservableList<Contact> allContacts = DBShared.getAllContacts();
    final private static ObservableList<LocalDateTime> allApptTime = FXCollections.observableArrayList();
    final private static ObservableList<String> types = FXCollections.observableArrayList();

    //Date Functionality
    final private static ZoneId utcZone = ZoneId.of("GMT");
    final private static ZoneId sysZone = ZoneId.systemDefault();
    final private static ZoneId estZone = ZoneId.of("America/New_York");
    final private static FormatStyle format = FormatStyle.SHORT;
    final private static LocalTime dayStart = LocalTime.of(8, 0);
    final private static LocalTime dayEnd = LocalTime.of(22, 1);

    //Form Fields
    public ComboBox<Customer> customerCombo;
    public ComboBox<Contact> contactCombo;
    public ComboBox<LocalDateTime> startCombo;
    public ComboBox<LocalDateTime> endCombo;
    public ComboBox<String> typeCombo;
    public DatePicker startDate;
    public DatePicker endDate;
    public Button formSaveButton;
    public Button backToAddButton;
    public TextField appointmentID;
    public TextField userID;
    public TextField titleInput;
    public TextField descInput;
    public TextField locationInput;

    //Form Labels & Error Images
    public Label formLabel;
    public Label errorLabel;
    public Label modifyLabel;
    public ImageView customerError;
    public ImageView contactError;
    public ImageView titleError;
    public ImageView descError;
    public ImageView locationError;
    public ImageView typeError;
    public ImageView startError;
    public ImageView endError;
    public ImageView modifyError;

    public Button buttonAll;
    public Button buttonWeek;
    public Button buttonMonth;

    private static LocalDateTime startTime;
    private static LocalDateTime endTime;

    public Label alertLabel;

    /** Main initialize class.
     * Pulls all appointments from database and displays in TableView
     * Populates Dropdowns with Customers, Contacts, and Business Hours
     * Uses Lambda expressions to change the view objects in combo boxes
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Helper.appointmentAlert(alertLabel);

        formLabel.setText("Add Appointment");

        //Fill Table View
        allAppointments = getAllAppointments();
        apptTable.setItems(allAppointments);

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

        //Fill Start & End Dropdowns
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), dayStart);
        while(start.isBefore(LocalDateTime.of(LocalDate.now(), dayEnd))) {
            allApptTime.add(start);
            start = start.plusHours(1);
        }

        //Fill Customer, Contact & Type Dropdowns
        customerCombo.setItems(allCustomers);
        contactCombo.setItems(allContacts);

        startCombo.setItems(allApptTime);
        endCombo.setItems(allApptTime);

        types.addAll("Planning Session", "Training Session", "Review Session");
        typeCombo.setItems(types);

        Callback<ListView<LocalDateTime>, ListCell<LocalDateTime>> factory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);

                setText(empty ? "" : (item.atZone(estZone).withZoneSameInstant(sysZone).toLocalDateTime().format(DateTimeFormatter.ofPattern("hh:mm a"))));
            }
        };

        startCombo.setCellFactory(factory);
        startCombo.setButtonCell(factory.call(null));
        endCombo.setCellFactory(factory);
        endCombo.setButtonCell(factory.call(null));
    }

    /** Save Appointment Click.
     * Handles the Appointment form. Switches between Save and Modify if an appointment is being modified
     * */
    public void onActionSave() {
        this.resetFormLabels();
        Boolean isValid = this.validate();

        //Make Sure all fields are full and logical
        if(isValid) {
            Appointment a = new Appointment(
                    0,
                    titleInput.getText(),
                    descInput.getText(),
                    locationInput.getText(),
                    typeCombo.getSelectionModel().getSelectedItem(),
                    Timestamp.valueOf(startTime),
                    Timestamp.valueOf(endTime),
                    contactCombo.getSelectionModel().getSelectedItem().getId(),
                    contactCombo.getSelectionModel().getSelectedItem().getName(),
                    customerCombo.getSelectionModel().getSelectedItem().getId(),
                    customerCombo.getSelectionModel().getSelectedItem().getName(),
                    Integer.parseInt(userID.getText())
            );

            //Are we modifying?
            if(isModify) {
                a.setId(Integer.parseInt(appointmentID.getText()));

                if(modifyAppointment(a) == 1) {
                    int i = 0;
                    errorLabel.getStyleClass().clear();
                    errorLabel.getStyleClass().addAll("success");
                    errorLabel.setText("Appointment Updated!");

                    for(Appointment loopAppointment : allAppointments) {
                        if(loopAppointment.getId() == a.getId()) {
                            allAppointments.set(i, a);
                        }
                        i++;
                    }
                    isModify = false;
                    this.clearForm();
                    backToAddButton.setVisible(false);
                    formSaveButton.setText("Add");
                    formLabel.setText("Add Appointment");
                } else {
                    errorLabel.setText("We've Encountered a Database Error. Please Try Again");
                }

            } else {
                if(addAppointment(a)) {
                    errorLabel.getStyleClass().clear();
                    errorLabel.getStyleClass().addAll("success");
                    errorLabel.setText("Appointment Added!");
                    allAppointments.add(a);
                    this.clearForm();
                } else {
                    errorLabel.setText("We've Encountered a Database Error. Please Try Again");
                }
            }


        }
    }

    /** Modify Appointment Click.
     * Populates Appointment Form with appointment Data for modification.
     */
    public void onActionModifyCurrent() {
        this.resetFormLabels();
        isModify = true;
        modifyLabel.getStyleClass().removeAll("error");
        modifyError.setVisible(false);
        selectedAppointment = apptTable.getSelectionModel().getSelectedItem();

        if(selectedAppointment == null) {
            modifyLabel.getStyleClass().addAll("error");
            modifyError.setVisible(true);
        } else {
            backToAddButton.setVisible(true);
            formLabel.setText("Update Appointment");
            formSaveButton.setText("Update");

            this.setAppointmentCurrent();
        }
    }

    /** Reset Click.
     * Resets to blank on Adding. Resets to current customer data on Modify.
     */
    public void onActionClear() {
        this.clearForm();
    }

    /** Back To Add Form.
     * Resets Modify to switch back to Add view.
     * */
    public void onActionBackToAdd() {
        isModify = false;
        formLabel.setText("Add Appointment");
        formSaveButton.setText("Add");
        backToAddButton.setVisible(false);
        apptTable.getSelectionModel().clearSelection();
        this.resetFormLabels();
        this.clearForm();
    }

    /** Removes Appointment.
     * Removes an Appointments.
     */
    public void onActionRemoveCurrent() {
        selectedAppointment = apptTable.getSelectionModel().getSelectedItem();

        if(selectedAppointment == null) {
            modifyLabel.getStyleClass().addAll("error");
            modifyError.setVisible(true);
        } else {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are You Sure You Wish To Remove This Appointment?");
            alert.setTitle("Confirm Delete");
            Optional<ButtonType> result = alert.showAndWait();

            if(result.isPresent() && result.get() == ButtonType.OK) {
                if(removeCustomer(selectedAppointment) == 1) {
                    errorLabel.getStyleClass().clear();
                    errorLabel.getStyleClass().addAll("success");
                    errorLabel.setText("Appointment " + selectedAppointment.getTitle() + " For " + selectedAppointment.getCustomer() + " Removed");
                    allAppointments.remove(selectedAppointment);
                } else {
                    errorLabel.setText("We've Encountered a Database Error. Please Try Again");
                }
            }

        }

    }

    /** Start Date Pick
     * Sets the End Date value to the picked value. Logic check as appointments cannot run multiple days
     */
    public void onActionDatePickStart() {
        endDate.setValue(startDate.getValue());
    }

    /** End Date Pick
     * Sets the Start Date value to the picked value. Logic check as appointments cannot run multiple days
     */
    public void onActionDatePickEnd() {
        startDate.setValue(endDate.getValue());
    }

    /** View All
     * Shows All Appointments
      */
    public void onActonViewAll() {

        buttonWeek.getStyleClass().clear();
        buttonWeek.getStyleClass().addAll("button", "button-tab");
        buttonMonth.getStyleClass().clear();
        buttonMonth.getStyleClass().addAll("button", "button-tab");
        buttonAll.getStyleClass().clear();
        buttonAll.getStyleClass().addAll("button", "button-tab-on");

        apptTable.setItems(allAppointments);
    }

    /** Week View.
     * Shows appointments for the week
     */
    public void onActionViewWeek() {

        buttonAll.getStyleClass().clear();
        buttonAll.getStyleClass().addAll("button", "button-tab");
        buttonMonth.getStyleClass().clear();
        buttonMonth.getStyleClass().addAll("button", "button-tab");
        buttonWeek.getStyleClass().clear();
        buttonWeek.getStyleClass().addAll("button", "button-tab-on");

        // lambda to filter views
        FilteredList<Appointment> filteredWeek = new FilteredList<>(allAppointments);
        filteredWeek.setPredicate(row -> {

            LocalDate now = LocalDate.now();
            TemporalField fields = WeekFields.of(Locale.getDefault()).dayOfWeek();
            LocalDate weekStart = now.with(fields, 1);
            LocalDate weekEnd = now.with(fields, 7);

            LocalDateTime rowStamp = row.getStart().toLocalDateTime();
            LocalDate rowDate = LocalDate.from(rowStamp);

            return ((rowDate.equals(weekStart) || rowDate.isAfter(weekStart)) && (rowDate.equals(weekEnd) || rowDate.isBefore(weekEnd)));

        });

        apptTable.setItems(filteredWeek);
    }

    /** Month View
     * Shows appointments for the month
     */
    public void onActionViewMonth() {
        buttonAll.getStyleClass().clear();
        buttonAll.getStyleClass().addAll("button", "button-tab");
        buttonWeek.getStyleClass().clear();
        buttonWeek.getStyleClass().addAll("button", "button-tab");
        buttonMonth.getStyleClass().clear();
        buttonMonth.getStyleClass().addAll("button", "button-tab-on");

        // lambda to filter views
        FilteredList<Appointment> filtereMonth = new FilteredList<>(allAppointments);
        filtereMonth.setPredicate(row -> {

            LocalDate now = LocalDate.now();
            LocalDate monthStart = now.withDayOfMonth(1);
            LocalDate monthEnd = now.withDayOfMonth(now.lengthOfMonth());

            LocalDateTime rowStamp = row.getStart().toLocalDateTime();
            LocalDate rowDate = LocalDate.from(rowStamp);

            return ((rowDate.equals(monthStart) || rowDate.isAfter(monthStart)) && (rowDate.equals(monthEnd) || rowDate.isBefore(monthEnd)));

        });

        apptTable.setItems(filtereMonth);

    }

    // ===== HELPER METHODS =====

    /** Validates the form.
     * Checks for valid entries and displays errors
     * */
    private Boolean validate() {
        Boolean canSubmit = true;

        if(titleInput.getText().isBlank()) {
            titleError.setVisible(true);
            canSubmit = false;
        }
        if(descInput.getText().isBlank()) {
            descError.setVisible(true);
            canSubmit = false;
        }
        if(locationInput.getText().isBlank()) {
            locationError.setVisible(true);
            canSubmit = false;
        }
        if(customerCombo.getSelectionModel().getSelectedItem() == null) {
            customerError.setVisible(true);
            canSubmit = false;
        }
        if(contactCombo.getSelectionModel().getSelectedItem() == null) {
            contactError.setVisible(true);
            canSubmit = false;
        }
        if(typeCombo.getSelectionModel().getSelectedItem() == null) {
            typeError.setVisible(true);
            canSubmit = false;
        }
        if(startCombo.getSelectionModel().getSelectedItem() == null) {
            startError.setVisible(true);
            canSubmit = false;
        }
        if(endCombo.getSelectionModel().getSelectedItem() == null) {
            endError.setVisible(true);
            canSubmit = false;
        }
        if(startDate.getValue() == null) {
            startError.setVisible(true);
            canSubmit = false;
        }

        //LOGIC CHECK TIME ... First lets show a blank error if there is one
        if(!canSubmit) {
            errorLabel.setText("PLEASE FILL IN ALL FIELDS");
            return false;
        }

        LocalDateTime start = startCombo.getSelectionModel().getSelectedItem();
        LocalDateTime end = endCombo.getSelectionModel().getSelectedItem();

        if(start.isAfter(end)) {
            errorLabel.setText("START MUST BE BEFORE END");
            return false;
        }

        startTime = LocalDateTime.of(startDate.getValue(), LocalTime.from(startCombo.getSelectionModel().getSelectedItem()));
        startTime = startTime.atZone(estZone).withZoneSameInstant(utcZone).toLocalDateTime();

        endTime = LocalDateTime.of(endDate.getValue(), LocalTime.from(endCombo.getSelectionModel().getSelectedItem()));
        endTime = endTime.atZone(estZone).withZoneSameInstant(utcZone).toLocalDateTime();

        for(Appointment a : allAppointments) {
            if(a.getCustomerId() == customerCombo.getSelectionModel().getSelectedItem().getId()) {

                 if(startTime.plusMinutes(1).isAfter(a.getStart().toLocalDateTime()) && startTime.isBefore(a.getEnd().toLocalDateTime())
                    || endTime.isAfter(a.getStart().toLocalDateTime()) && endTime.isBefore(a.getEnd().toLocalDateTime())
                 ) {
                     errorLabel.setText("APPOINTMENT TIMES CANNOT OVERLAP");
                     return false;
                 }
            }
        }
        return true;
    }

    /** Fills Appointment Data.
     * Used to "Reset" on modify and fill selected appointment data.
     * */
    private void setAppointmentCurrent() {

        //set text filed to appointment data
        appointmentID.setText(String.valueOf(selectedAppointment.getId()));
        titleInput.setText(selectedAppointment.getTitle());
        descInput.setText(selectedAppointment.getDesc());
        locationInput.setText(selectedAppointment.getLocation());

        //set customer combo box
        for(Customer customer : customerCombo.getItems()) {
            if(customer.getId() == selectedAppointment.getCustomerId()) {
                customerCombo.setValue(customer);
                break;
            }
        }

        //set contact combo box
        for (Contact contact : contactCombo.getItems()) {
            if(contact.getId() == selectedAppointment.getContactId()) {
                contactCombo.setValue(contact);
                break;
            }
        }

        //set type combo box
        for (Object type : typeCombo.getItems()) {
            if(type.equals(selectedAppointment.getType())) {
                typeCombo.setValue(type.toString());
                break;
            }
        }

        // ===== CONVERT TO EST HERE ... CONVERSION TO LOCAL TIME IS DONE IN THE COMBO BOX
        LocalDateTime dbStartTime = selectedAppointment.getStart().toLocalDateTime();
        dbStartTime = dbStartTime.atZone(utcZone).withZoneSameInstant(estZone).toLocalDateTime();
        LocalTime dbStartHour = LocalTime.from(dbStartTime);
        LocalDate dbStartDay = LocalDate.from(dbStartTime);

        LocalDateTime dbEndTime = selectedAppointment.getEnd().toLocalDateTime();
        dbEndTime = dbEndTime.atZone(utcZone).withZoneSameInstant(estZone).toLocalDateTime();
        LocalTime dbEndHour = LocalTime.from(dbEndTime);
        LocalDate dbEndDay = LocalDate.from(dbEndTime);

        //set start time
        for (LocalDateTime obj : startCombo.getItems()) {
            LocalTime listStart = LocalTime.from(obj);
            if(listStart.equals(dbStartHour)) {
                startCombo.setValue(obj);
                break;
            }
        }

        //set end time
        for (LocalDateTime obj : endCombo.getItems()) {
            LocalTime listEnd = LocalTime.from(obj);
            if(listEnd.equals(dbEndHour)) {
                endCombo.setValue(obj);
                break;
            }
        }

        startDate.setValue(dbStartDay);
        endDate.setValue(dbEndDay);

    }

    /** Resets Form Error Messages Label.
     * Resets errors and hides images
     * */
    private void resetFormLabels() {
        errorLabel.getStyleClass().clear();
        errorLabel.getStyleClass().addAll("error");
        errorLabel.setText("");

        customerError.setVisible(false);
        contactError.setVisible(false);
        titleError.setVisible(false);
        descError.setVisible(false);
        locationError.setVisible(false);
        typeError.setVisible(false);
        startError.setVisible(false);
        endError.setVisible(false);
    }

    /** Clear Form.
     * Will clear on adding, or reset to current data on modify
     * */
    private void clearForm() {

        if(isModify) {
            this.setAppointmentCurrent();
        } else {
            customerCombo.getSelectionModel().clearSelection();
            contactCombo.getSelectionModel().clearSelection();
            appointmentID.setText("");
            titleInput.setText("");
            descInput.setText("");
            locationInput.setText("");
            typeCombo.getSelectionModel().clearSelection();
            startCombo.getSelectionModel().clearSelection();
            endCombo.getSelectionModel().clearSelection();
            startDate.setValue(null);
            endDate.setValue(null);
        }

    }

    /** Button Navigation
     *
     * @param actionEvent button click
     * @throws IOException file error
     */
    public void onActionCust(ActionEvent actionEvent) throws IOException {
        Helper.navigateTo(actionEvent, "/View/Customer.fxml", "Customers");
    }

    /** Button Navigation
     *
     * @param actionEvent button click
     * @throws IOException file error
     */
    public void onActionRep(ActionEvent actionEvent) throws IOException {
        Helper.navigateTo(actionEvent, "/View/Reports.fxml", "Reports");
    }

}
