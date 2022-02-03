package DBAccess;

import Database.DBConnection;
import Model.Appointment;
import Model.TransformResult;
import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/** Appointment DB Calls. */
public class DBAppointments {

    /** Gets all the appointments.
     *  Gets all the customers; along with contacts and customers
     *  Lambda expression is used to return the Results into an object.
     *  */
    public static ObservableList<Appointment> getAllAppointments() {

        ObservableList<Appointment> appList = FXCollections.observableArrayList();

        try {
            String sql = "SELECT a.Appointment_ID, a.Title, a.Description, a.Location, a.Type, a.Start, a.end, a.Customer_ID, a.Contact_ID, a.User_ID, c.Contact_Name, cu.Customer_Name from appointments a " +
                    "JOIN contacts c ON a.Contact_ID = c.Contact_ID " +
                    "JOIN customers cu ON a.Customer_ID = cu.Customer_ID ORDER BY a.Appointment_ID asc";

            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            TransformResult appointRes = s -> new Appointment(
                    s.getInt("Appointment_ID"),
                    s.getString("Title"),
                    s.getString("Description"),
                    s.getString("Location"),
                    s.getString("Type"),
                    s.getTimestamp("Start"),
                    s.getTimestamp("End"),
                    s.getInt("Contact_ID"),
                    s.getString("Contact_Name"),
                    s.getInt("Customer_ID"),
                    s.getString("Customer_Name"),
                    s.getInt("User_ID"));


            while(rs.next()) {
                Appointment a = (Appointment) appointRes.transform(rs);
                appList.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appList;

    }

    /** Adds a new Appointment.
     * @param a Appointment - Appointment object to add
     * */
    public static Boolean addAppointment(Appointment a) {
        try {
            String sql = "INSERT INTO Appointments VALUES(null, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, a.getTitle());
            ps.setString(2, a.getDesc());
            ps.setString(3, a.getLocation());
            ps.setString(4, a.getType());
            ps.setTimestamp(5, a.getStart());
            ps.setTimestamp(6, a.getEnd());
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
            ps.setString(8, User.getUsername());
            ps.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
            ps.setString(10, User.getUsername());
            ps.setInt(11, a.getCustomerId());
            ps.setInt(12, User.getId());
            ps.setInt(13, a.getContactId());

            ps.execute();

            ResultSet ids = ps.getGeneratedKeys();
            if(ids.next()) {
                a.setId(ids.getInt(1));
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Modify an Appointment record.
     * @param a Appointment Object
     * */
    public static int modifyAppointment(Appointment a) {
        try {
            String sql = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

            ps.setString(1, a.getTitle());
            ps.setString(2, a.getDesc());
            ps.setString(3, a.getLocation());
            ps.setString(4, a.getType());
            ps.setTimestamp(5, a.getStart());
            ps.setTimestamp(6, a.getEnd());
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
            ps.setString(8, User.getUsername());
            ps.setInt(9, a.getCustomerId());
            ps.setInt(10, a.getContactId());
            ps.setInt(11, a.getId());

            int res = ps.executeUpdate();
            return res;

        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        }
    }

    /** Removes Appointment records.
     * @param a Appointment Object
     * */
    public static int removeCustomer(Appointment a) {
        //remove appointment records
        try {
            String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, a.getId());

            int res = ps.executeUpdate();
            return res;

        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        }
    }

}
