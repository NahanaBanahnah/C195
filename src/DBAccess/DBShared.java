package DBAccess;

import Database.DBConnection;
import Model.Contact;
import Model.Country;
import Model.Division;
import Model.TransformResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBShared {

    /** Gets all the countries.
     *  Gets all the Countries;  and creates a new Country objects. */
    public static ObservableList<Country> getAllCountries() {

        ObservableList<Country> countryList = FXCollections.observableArrayList();

        try {
            String sql = "SELECT * from countries";

            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            TransformResult countryRes = s -> new Country(s.getInt("Country_ID"), s.getString("Country"));

            while (rs.next()) {
                Country c = (Country) countryRes.transform(rs);
                countryList.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return countryList;

    }

    /** Gets all the divisions.
     *  Gets all the Divisions;  and creates a new Division objects. */
    public static ObservableList<Division> getAllDivisions() {

        ObservableList<Division> divisionList = FXCollections.observableArrayList();

        try {
            String sql = "SELECT * from first_level_divisions";

            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            TransformResult divisionRes = s -> new Division(s.getInt("Division_ID"), s.getString("Division"), s.getInt("Country_ID"));

            while (rs.next()) {
                Division d = (Division) divisionRes.transform(rs);
                divisionList.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return divisionList;

    }

    /** Gets all the Contacts.
     *  Gets all the Contacts; and creates a new Contact objects. */
    public static ObservableList<Contact> getAllContacts() {

        ObservableList<Contact> contactList = FXCollections.observableArrayList();

        try {
            String sql = "SELECT * from contacts";

            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            TransformResult contactRes = s -> new Contact(s.getInt("Contact_ID"), s.getString("Contact_Name"), s.getString("Email"));

            while (rs.next()) {
                Contact c = (Contact) contactRes.transform(rs);
                contactList.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contactList;

    }

}
