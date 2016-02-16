package model.keys;

import fj.data.Stream;
import husky.api.Table;
import husky.model.source.Column;
import husky.model.source.PrimaryKey;
import husky.model.source.Schema;

import javax.sql.DataSource;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class KeyColumn {
    private final FetchPrimaryKeys fetchPrimaryKeys;
    private final FindSmallestUniqueIndex findSmallestUniqueIndex;

    public KeyColumn(DataSource dataSource) {
        this.fetchPrimaryKeys = new FetchPrimaryKeys(dataSource);
        this.findSmallestUniqueIndex = new FindSmallestUniqueIndex(dataSource);
    }

    public List<String> fetch(Table table, Schema schema) {
        Stream<PrimaryKey> primaryKeys = fetchPrimaryKeys.ofTable(table);

        if (primaryKeys.isNotEmpty()) {
            return newArrayList(primaryKeys.map(primaryKey -> primaryKey.name));
        }

        Stream<Column> smallestUniqueIndex = findSmallestUniqueIndex.of(schema, table);

        if (smallestUniqueIndex.isNotEmpty()) {
            return newArrayList(smallestUniqueIndex.map(column -> column.name));
        }

        throw new RuntimeException("Could not determine unique key columns for " + table.name);
    }
}
