package datasource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class C3POCloseableDataSource implements CloseableDatasource, ManagedDataSource {
    private final ComboPooledDataSource dataSource;
    private final int connectionNetworkTimeoutMillis;

    public C3POCloseableDataSource(ComboPooledDataSource dataSource, int connectionNetworkTimeoutMillis) {
        this.dataSource = dataSource;
        this.connectionNetworkTimeoutMillis = connectionNetworkTimeoutMillis;
    }

    @Override
    public void close() {
        dataSource.close();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        dataSource.setLogWriter(out);
    }

    @Override
    @Deprecated
    public Connection getConnection(String username, String password) throws SQLException {
        return dataSource.getConnection(username, password);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        dataSource.setLoginTimeout(seconds);
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return dataSource.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return dataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return dataSource.isWrapperFor(iface);
    }

    @Override
    public int getUsedConnections() throws SQLException {
        return dataSource.getNumBusyConnections();
    }

    @Override
    public int getNumConnections() throws SQLException {
        return dataSource.getNumConnections();
    }

    @Override
    public int getConnectionNetworkTimeoutMillis() throws SQLException {
        return connectionNetworkTimeoutMillis;
    }
}
