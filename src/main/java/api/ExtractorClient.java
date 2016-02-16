package api;

        import com.github.davidmoten.rx.jdbc.Database;
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

    public ExtractorClient(Redisson redisson, List<TableConfig> tableConfigs, Database database, Option<String> callbackName, CloseableDatasource dataSource, DatabaseName databaseName, TimeZone timeZone, SqlFlavour sqlFlavour) {
        this.redisson = redisson;
        this.tableConfigs = tableConfigs;
        this.database = database;
        this.callbackName = callbackName;
        this.dataSource = dataSource;
        this.databaseName = databaseName;
        this.timeZone = timeZone;
        this.sqlFlavour = sqlFlavour;
    }
}
