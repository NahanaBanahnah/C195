package DBAccess;

import Database.DBConnection;
import Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/** Database Class To Perform All Customer DB Actions. */
public class DBCustomer {

    /** Gets all the customers.
     *  Gets all the customers; along with readable data (Country, Division) and creates a new Customer object. */
    public static ObservableList<Customer> getAllCustomers() {

        ObservableList<Customer> clist = FXCollections.observableArrayList();

        try {
            String sql = "SELECT c.Customer_ID, c.Customer_Name, c.Address, c.Postal_Code, c.Phone, c.Division_ID, c.Create_Date, c.Created_By, c.Last_Update, c.Last_Updated_By, co.Country, fd.Division, co.Country_ID from customers c " +
                    "JOIN first_level_divisions fd on fd.Division_ID = c.Division_ID " +
                    "JOIN countries co on fd.Country_ID = co.Country_ID ORDER BY c.Customer_ID asc";

            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

                TransformResult customerRes = s -> new Customer(
                    s.getInt("Customer_ID"),
                    s.getString("Customer_Name"),
                    s.getString("Address"),
                    s.getString("Postal_Code"),
                    s.getString("Phone"),
                    s.getString("Division"),
                    s.getString("Country"),
                    s.getInt("Division_ID"),
                    s.getInt("Country_ID"));

            while(rs.next()) {
                Customer c = (Customer) customerRes.transform(rs);
                clist.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clist;

    }

    /** Adds a new customer.
     * @param c Customer - Customer object to add
     * */
    public static Boolean addCustomer(Customer c) {
        try {
            String sql = "INSERT INTO Customers VALUES(null, ?, ?, ?, ?, ?,?,?,?,?)";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, c.getName());
            ps.setString(2, c.getAddress());
            ps.setString(3, c.getPostalCode());
            ps.setString(4, c.getPhone());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
            ps.setString(6, User.getUsername());
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
            ps.setString(8, User.getUsername());
            ps.setInt(9, c.getdId());

            ps.execute();

            ResultSet ids = ps.getGeneratedKeys();
            if(ids.next()) {
                c.setId(ids.getInt(1));
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Modify a customer record.
     * @param c Customer Object
     * */
    public static int modifyCustomer(Customer c) {
        try {
            String sql = "UPDATE Customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

            ps.setString(1, c.getName());
            ps.setString(2, c.getAddress());
            ps.setString(3, c.getPostalCode());
            ps.setString(4, c.getPhone());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
            ps.setString(6, User.getUsername());
            ps.setInt(7, c.getdId());
            ps.setInt(8, c.getId());

            int res = ps.executeUpdate();
            return res;

        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        }
    }

    /** Remove Customer and associated Appointment records.
     * @param c Customer Object
     * */
    public static int removeCustomer(Customer c) {
        //remove appointment records
        try {
            String sql = "DELETE FROM appointments WHERE Customer_ID = ?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, c.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //remove the customer record
        try {
            String sql = "DELETE FROM Customers WHERE Customer_ID = ?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

            ps.setInt(1, c.getId());

            int res = ps.executeUpdate();
            return res;

        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        }
    }
}
