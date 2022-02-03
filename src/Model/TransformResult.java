package Model;
import java.sql.ResultSet;
import java.sql.SQLException;

// Interface used to implement lambda expressions on SQL results and built an object from the result set

@FunctionalInterface
public interface TransformResult {
    public Object transform(ResultSet o) throws SQLException;
}
