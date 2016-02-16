package api;

import com.github.davidmoten.rx.jdbc.Database;
import com.typesafe.config.Config;
import config.DatasourceBuilder;
import config.TableConfig;
import datasource.CloseableDatasource;
import fj.data.Option;
import org.redisson.Redisson;

import java.util.List;
import java.util.TimeZone;

public class ExtractorClient {
    private final Redisson redisson;
    private final List<TableConfig> tableConfigs;
    private final Database database;
    private final Option<String> callbackName;
    private final CloseableDatasource dataSource;
    private final DatabaseName databaseName;
    private final TimeZone timeZone;
    private final SqlFlavour sqlFlavour;

    public ExtractorClient(Config config) {
        this.dataSource = DatasourceBuilder.from(config.getConfig("db"));
        this.databaseName = new DatabaseName(config.getString("database.name"));
        ParseTableConfig parseTableConfig = new ParseTableConfig(dataSource);
        this.sqlFlavour = detectSqlFlavour(config.getConfig("db"));
        this.tableConfigs = parseTableConfig.parse(config);
        this.callbackName = optionalString(config, "strategy.callbackName");
        this.redisson = createRedisso(config);
        this.timeZone = TimeZoneConfig.timeZone(config);
        this.database = new Database(new DataSourceConnectionProvider(dataSource), () -> Schedulers.io());
    }


}
