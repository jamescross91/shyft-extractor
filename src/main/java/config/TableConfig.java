package config;

import api.Table;
import com.typesafe.config.Config;

import java.util.List;

public class TableConfig {
    public final Table table;
    public final Schema schema;
    public final String strategy;
    public final List<String> keyColumns;
    public final Config strategyConfig;
    public final Option<String> customQuery;

    public TableConfig(Table table,
                       Schema schema,
                       String strategy,
                       List<String> keyColumns,
                       Config strategyConfig,
                       Option<String> customQuery) {
        this.table = table;
        this.schema = schema;
        this.strategy = strategy;
        this.keyColumns = keyColumns;
        this.strategyConfig = strategyConfig;
        this.customQuery = customQuery;
    }
}
