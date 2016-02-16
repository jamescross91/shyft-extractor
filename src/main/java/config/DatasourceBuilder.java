package config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.zaxxer.hikari.HikariDataSource;
import datasource.CloseableDatasource;
import datasource.DBCPCloseableDataSource;
import datasource.HikariCloseableDataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;

import java.util.Properties;

@Slf4j
public class DatasourceBuilder {

    @SneakyThrows
    public static CloseableDatasource from(Config config) {

        //Connection pool override
        if (config.hasPath("dbpool")) {
            log.info("Overriding default connection pool with DBCP");
            return dbcp(config);
        }

        int threads = config.getInt("threads");
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setDriverClassName(config.getString("driver"));
        dataSource.setJdbcUrl(config.getString("url"));

        dataSource.setUsername(config.getString("user"));
        dataSource.setPassword(config.getString("pwd"));

        dataSource.setMaximumPoolSize(threads);
        int checkoutTimeout = config.hasPath("checkoutTimeoutSeconds") ? config.getInt("checkoutTimeoutSeconds") * 1000 : 7200 * 1000;//millis
        dataSource.setLoginTimeout(checkoutTimeout);//millis

        int unreturnedConnectionTimeout = config.hasPath("unreturnedConnectionTimeoutSeconds") ? config.getInt("unreturnedConnectionTimeoutSeconds") : 12 * 60 * 60;//seconds
        dataSource.setConnectionTimeout(unreturnedConnectionTimeout);//seconds

        dataSource.setLoginTimeout(60);//seconds
        dataSource.setMaxLifetime(120 * 1000);
        dataSource.setConnectionTimeout(0L);

        if (config.hasPath("testQuery")) {
            dataSource.setConnectionTestQuery(config.getString("testQuery"));
        }

        log.info("Setting row prefetch to " + 15000);
        dataSource.addDataSourceProperty("defaultRowPrefetch", "15000");

        HikariCloseableDataSource ds = new HikariCloseableDataSource(dataSource);
        return ds;
    }

    @SneakyThrows
    private static CloseableDatasource dbcp(Config config) {
        int threads = config.getInt("threads");
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(config.getString("driver"));
        dataSource.setUrl(config.getString("url"));

        dataSource.setUsername(config.getString("user"));
        dataSource.setPassword(config.getString("pwd"));

        dataSource.setInitialSize(threads);

        dataSource.setMinEvictableIdleTimeMillis(120 * 1000);//seconds

        DBCPCloseableDataSource ds = new DBCPCloseableDataSource(dataSource);
        return ds;
    }

    private static Properties properties() {
        Properties properties = new Properties();
        String value = "15000";
        log.info("Setting row prefetch to " + value);
        properties.setProperty("defaultRowPrefetch", value);
        return properties;
    }

    public static CloseableDatasource from(String configFile) {
        Config config = ConfigFactory.load(configFile);
        return DatasourceBuilder.from(config.getConfig("db"));
    }
}
