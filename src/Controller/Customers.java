package Controller;

import DBAccess.DBCustomer;
import DBAccess.DBShared;
import Model.Country;
import Model.Customer;
import Model.Division;
import Model.Helper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/** Customer controller class. */
public class Customers extends DBCustomer implements Initializable {

    public TableView<Customer> customerTable;
    public TableColumn<Customer, Integer> idColumn;
    public TableColumn<Customer, String> nameColumn;
    public TableColumn<Customer, String> addressColumn;
    public TableColumn<Customer, String> divisionColumn;
    public TableColumn<Customer, String> countryColumn;
    public TableColumn<Customer, String> postalColumn;
    public TableColumn<Customer, String> phoneColumn;

    public Label errorLabel;
    public Label formLabel;
    public Label modifyLabel;
    public Label alertLabel;
    public ImageView nameError;
    public ImageView addressError;
    public ImageView countryError;
    public ImageView divisionError;
    public ImageView postalError;
    public ImageView phoneError;
    public ImageView modifyError;

    public Button formSaveButton;
    public Button backToAddButton;
    public ComboBox<Country> countryCombo;
    public ComboBox<Division> divisionCombo;
    public TextField idInput;
    public TextField nameInput;
    public TextField addressInput;
    public TextField postalInput;
    public TextField phoneInput;

    private static Boolean isModify = false;
    private static Boolean clearCall = false;
    private static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private static Customer selectedCustomer;
    private static ObservableList<Division> allDivisions = DBShared.getAllDivisions();
    private static ObservableList<Division> currentDivisions = FXCollections.observableArrayList();

    /** Main initialize class.
     * Pulls all customers from database and displays in TableView
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Helper.appointmentAlert(alertLabel);

        allCustomers = getAllCustomers();
        customerTable.setItems(allCustomers);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        divisionColumn.setCellValueFactory(new PropertyValueFactory<>("division"));
        postalColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        //Set the labels and buttons default text
        formSaveButton.setText("Add");
        formLabel.setText("Add Customer");

        //Set the Country Combo box
        ObservableList<Country> countries = DBShared.getAllCountries();
        countryCombo.setItems(countries);

    }

    // ===== BUTTON CLICKS =====

    /** Country Combo Box Change Handler
     * Pulls the records for the divisions matching the selected country and populates division Combo Box
     */
    public void onActionCountryChange() {
        if(!clearCall) {
            Country selectedCountry = countryCombo.getSelectionModel().getSelectedItem();
            this.changeDivision(selectedCountry);
        }

    }

    /** Save Customer Click.
     * Handles the Customer form. Switches between Save and Modify if a customer is being modified
     * */
    public void onActionSave() {

        this.resetFormLabels();
        Boolean isValid = this.validate();

        if(isValid) {
            Customer c = new Customer(
                    0,
                    nameInput.getText(),
                    addressInput.getText(),
                    postalInput.getText(),
                    phoneInput.getText(),
                    divisionCombo.getSelectionModel().getSelectedItem().getDivision(),
                    countryCombo.getSelectionModel().getSelectedItem().getCountry(),
                    divisionCombo.getSelectionModel().getSelectedItem().getDivisionId(),
                    countryCombo.getSelectionModel().getSelectedItem().getId());

            if(isModify) {
                c.setId(Integer.parseInt(idInput.getText()));

                if(modifyCustomer(c) == 1) {
                    int i = 0;
                    errorLabel.getStyleClass().clear();
                    errorLabel.getStyleClass().addAll("success");
                    errorLabel.setText("User Updated!");

                    for(Customer loopCustomer : allCustomers) {
                        if(loopCustomer.getId() == c.getId()) {
                            allCustomers.set(i, c);
                        }
                        i++;
                    }
                    isModify = false;
                    this.clearForm();
                    backToAddButton.setVisible(false);
                    formSaveButton.setText("Add");
                    formLabel.setText("Add Customer");
                } else {
                    errorLabel.setText("We've Encountered a Database Error. Please Try Again");
                }

            } else {
                if(addCustomer(c)) {
                    errorLabel.getStyleClass().clear();
                    errorLabel.getStyleClass().addAll("success");
                    errorLabel.setText("User Added!");
                    allCustomers.add(c);
                    this.clearForm();
                }
                else {
                    errorLabel.setText("We've Encountered a Database Error. Please Try Again");
                }
            }
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
        formLabel.setText("Add Customer");
        formSaveButton.setText("Add");
        backToAddButton.setVisible(false);
        customerTable.getSelectionModel().clearSelection();
        this.resetFormLabels();
        this.clearForm();
    }

    /** Modify Customer Click.
     * Populates Customer Form with customer Data for modification.
     */
    public void onActionModifyCurrent() {
        this.resetFormLabels();
        isModify = true;
        modifyLabel.getStyleClass().removeAll("error");
        modifyError.setVisible(false);
        selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

        if(selectedCustomer == null) {
            modifyLabel.getStyleClass().addAll("error");
            modifyError.setVisible(true);
        } else {
            backToAddButton.setVisible(true);
            formLabel.setText("Update Customer");
            formSaveButton.setText("Update");

            this.setCustomerCurrent();

        }
    }

    /** Removes Customer.
     * Removes User and Appointments.
     */
    public void onActionRemoveCurrent() {
        selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

        if(selectedCustomer == null) {
            modifyLabel.getStyleClass().addAll("error");
            modifyError.setVisible(true);
        } else {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are You Sure You Wish To Remove This Customer? This Will Also Remove Any Customer Appointment.");
            alert.setTitle("Confirm Delete");
            Optional<ButtonType> result = alert.showAndWait();

            if(result.isPresent() && result.get() == ButtonType.OK) {
                if(removeCustomer(selectedCustomer) == 1) {
                    errorLabel.getStyleClass().clear();
                    errorLabel.getStyleClass().addAll("success");
                    errorLabel.setText("User " + selectedCustomer.getName() + " Removed");
                    allCustomers.remove(selectedCustomer);
                } else {
                    errorLabel.setText("We've Encountered a Database Error. Please Try Again");
                }
            }

        }

    }

    // ===== HELPER METHODS =====

    /** Validates the form.
     * Checks for valid entries and displays errors
     * */
    private Boolean validate() {
        Boolean canSubmit = true;

        if(nameInput.getText().isBlank()) {
            nameError.setVisible(true);
            canSubmit = false;
        }
        if(addressInput.getText().isBlank()) {
            addressError.setVisible(true);
            canSubmit = false;
        }
        if(postalInput.getText().isBlank()) {
            postalError.setVisible(true);
            canSubmit = false;
        }
        if(phoneInput.getText().isBlank()) {
            phoneError.setVisible(true);
            canSubmit = false;
        }
        if(countryCombo.getSelectionModel().getSelectedItem() == null) {
            countryError.setVisible(true);
            canSubmit = false;
        }
        if(divisionCombo.getSelectionModel().getSelectedItem() == null) {
            divisionError.setVisible(true);
            canSubmit = false;
        }

        if(canSubmit) {
            return true;
        } else {
            errorLabel.setText("PLEASE FILL IN ALL FIELDS");
            return false;
        }
    }

    /** Resets Form Error Messages Label.
     * Resets errors and hides images
     * */
    private void resetFormLabels() {
        errorLabel.getStyleClass().clear();
        errorLabel.getStyleClass().addAll("error");
        errorLabel.setText("");

        errorLabel.setText("");
        nameError.setVisible(false);
        addressError.setVisible(false);
        countryError.setVisible(false);
        divisionError.setVisible(false);
        postalError.setVisible(false);
        phoneError.setVisible(false);
    }

    /** Clear Form.
     * Will clear on adding, or reset to current data on modify
     * */
    private void clearForm() {
        clearCall = true;

        if(isModify) {
            setCustomerCurrent();
        } else {
            idInput.setText("");
            nameInput.setText("");
            addressInput.setText("");
            postalInput.setText("");
            phoneInput.setText("");
            countryCombo.getSelectionModel().clearSelection();
            divisionCombo.getSelectionModel().clearSelection();
            countryCombo.setPromptText("Select Country");
            divisionCombo.setPromptText("Please Choose A Country");

            divisionCombo.setDisable(true);
        }

        clearCall = false;
    }

    /** Fills Customer Data.
     * Used to "Reset" on modify and fill selected customer data.
     * */
    private void setCustomerCurrent() {

        //set text filed to customer data
        idInput.setText(String.valueOf(selectedCustomer.getId()));
        nameInput.setText(selectedCustomer.getName());
        addressInput.setText(selectedCustomer.getAddress());
        postalInput.setText(selectedCustomer.getPostalCode());
        phoneInput.setText(selectedCustomer.getPhone());

        //set country combo and rebuild divisions
        for(Country c : countryCombo.getItems()) {
            if(c.getId() == selectedCustomer.getcId()) {
                countryCombo.setValue(c);
                this.changeDivision(c);
                break;
            }
        }

        //set division combo
        for (Division d : divisionCombo.getItems()) {
            if(d.getDivisionId() == selectedCustomer.getdId()) {
                divisionCombo.setValue(d);
                return;
            }
        }
    }

    /** Change Division.
     * Change the division combo based on selected country
     * */
    private void changeDivision(Country selectedCountry) {
        currentDivisions.clear();

        for(Division d : allDivisions) {
            if(d.getCountryId() == selectedCountry.getId()) {
                currentDivisions.add(d);
            }
        }
        divisionCombo.setItems(currentDivisions);
        divisionCombo.setPromptText("Select Division");
        divisionCombo.setDisable(false);
    }


    /** Button Navigation
     *
     * @param actionEvent
     * @throws IOException
     */
    public void onActionApp(ActionEvent actionEvent) throws IOException {
        Helper.navigateTo(actionEvent, "/View/Appointments.fxml", "Appointments");
    }

    /** Button Navigation
     *
     * @param actionEvent
     * @throws IOException
     */
    public void onActionRep(ActionEvent actionEvent) throws IOException {
        Helper.navigateTo(actionEvent, "/View/Reports.fxml", "Reports");
    }
}
