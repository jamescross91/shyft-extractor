package model.keys;

import api.Table;
import model.source.Column;
import model.source.PrimaryKey;
import model.source.Schema;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class KeyColumn {
    private final FetchPrimaryKeys fetchPrimaryKeys;
    private final UniqueIndex uniqueIndex;

    public KeyColumn(DataSource dataSource) {
        this.fetchPrimaryKeys = new FetchPrimaryKeys(dataSource);
        this.uniqueIndex = new UniqueIndex(dataSource);
    }

    public List<String> fetch(Table table, Schema schema) {
        Stream<PrimaryKey> primaryKeys = fetchPrimaryKeys.ofTable(table);

        if (primaryKeys.count() > 0) {
            return primaryKeys.map(primaryKey -> primaryKey.name).collect(toList());
        }

        Stream<Column> smallestUniqueIndex = uniqueIndex.findSmallest(schema, table);

        if (smallestUniqueIndex.count() > 0) {
            return primaryKeys.map(column -> column.name).collect(toList());
        }

        throw new RuntimeException("Could not determine unique key columns for " + table.name);
    }
}
