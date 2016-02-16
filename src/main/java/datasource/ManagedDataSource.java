package datasource;

import javax.sql.DataSource;
import java.sql.SQLException;

public interface ManagedDataSource extends DataSource {
    int getUsedConnections() throws SQLException;

    int getNumConnections() throws SQLException;

    int getConnectionNetworkTimeoutMillis() throws SQLException;


}
