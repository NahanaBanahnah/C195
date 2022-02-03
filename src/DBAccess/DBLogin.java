package DBAccess;

import Database.DBConnection;
import Model.TransformResult;
import Model.User;
import java.sql.*;

/** Simple Login Class To Check User's Login. */
public class DBLogin {

    /** Basic User Login Check.
     Attempts to query db with user input username & password combo.
     Would be more complex in real world situation.
     @param username User input of username
     @param password User input of password
     * */
    public static User checkUserLogin(String username, String password) {
        try {
            String sql = "SELECT * from users where User_Name = ? AND Password = ?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

           if(rs.next()) {
               TransformResult userRes = s -> new User(s.getInt("User_ID"), s.getString("User_Name"));
               return (User) userRes.transform(rs);

            } else {
               return null;
           }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
