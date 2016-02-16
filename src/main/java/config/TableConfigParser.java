package config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import fj.data.Option;
import husky.api.Table;
import husky.model.keys.DetermineKeyColumns;
import husky.model.source.Schema;

import javax.sql.DataSource;
import java.util.List;

import static com.google.common.collect.Lists.transform;
import static husky.config.Parsing.optionalString;

public class TableConfigParser {

    public static final String Key_Columns = "keyColumns";

    private final DetermineKeyColumns determineKeyColumns;

    public TableConfigParser(DataSource dataSource) {
        determineKeyColumns = new DetermineKeyColumns(dataSource);
    }

    public List<TableConfig> parse(Config config) {
        List<? extends Config> tableList = config.getConfigList("tables");
        List<TableConfig> result = transform(tableList, this::toTableConfig);

        return result;
    }

    private TableConfig toTableConfig(Config config) {
        Table table = new Table(config.getString("table.name"));
        Schema schema = new Schema(config.getString("schema"));
        String strategy = config.getString("strategy");
        List<String> keyColumns = keyColumns(config, table, schema);
        Config strategyConfig = strategyConfig(config);
        Option<String> customQuery = optionalString(config, "query");

        TableConfig tableConfig = new TableConfig(table, schema, strategy, keyColumns, strategyConfig, customQuery);
        return tableConfig;
    }

    private Config strategyConfig(Config config) {
        if (config.hasPath("strategyConfig")) {
            return config.getConfig("strategyConfig");
        }
        return ConfigFactory.empty();
    }

    private List<String> keyColumns(Config config, Table table, Schema schema) {
        if (config.hasPath(Key_Columns)) {
            return config.getStringList(Key_Columns);
        } else {
            return determineKeyColumns.of(table, schema);
        }
    }
}
