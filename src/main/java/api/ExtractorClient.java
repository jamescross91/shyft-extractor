package api;

import config.TableConfig;
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
}
