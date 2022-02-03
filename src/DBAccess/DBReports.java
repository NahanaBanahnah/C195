package DBAccess;

import Database.DBConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/** Reports Database Class. */
public class DBReports {

    /** Generate Report One
     * Returns the number of appointments for a given type and month
     * @param month String the month to pull
     * @param type String the type of appointment to pull
     * @return
     */
    public int getReportOne(String month, String type) {
        try {
            String sql = "SELECT Count(*) as count FROM appointments WHERE Type = ? AND (Month(Start) = ?)";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

            ps.setString(1, type);
            ps.setString(2, month);

            ResultSet rs = ps.executeQuery();

            rs.next();
            return rs.getInt("count");

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }
}
