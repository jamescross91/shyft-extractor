package model.keys;

import api.Table;
import fj.data.Stream;
import model.source.PrimaryKey;
import model.source.Schema;

import javax.sql.DataSource;
import java.util.List;

public class KeyColumn {
    private final FetchPrimaryKeys fetchPrimaryKeys;
    private final UniqueIndex uniqueIndex;

    public KeyColumn(DataSource dataSource) {
        this.fetchPrimaryKeys = new FetchPrimaryKeys(dataSource);
        this.uniqueIndex = new UniqueIndex(dataSource);
    }

    public List<String> fetch(Table table, Schema schema) {
        Stream<PrimaryKey> primaryKeys = fetchPrimaryKeys.ofTable(table);

        if (primaryKeys.isNotEmpty()) {
            return newArrayList(primaryKeys.map(primaryKey -> primaryKey.name));
        }

        Stream<Column> smallestUniqueIndex = uniqueIndex.findSmallest(schema, table);

        if (smallestUniqueIndex.isNotEmpty()) {
            return newArrayList(smallestUniqueIndex.map(column -> column.name));
        }

        throw new RuntimeException("Could not determine unique key columns for " + table.name);
    }
}
