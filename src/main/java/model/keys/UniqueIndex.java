package model.keys;

import fj.data.Option;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import model.source.Column;
import model.source.Schema;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

@Slf4j
public class UniqueIndex {
    static final String INDEX_NAME = "INDEX_NAME";
    static final String COLUMN_NAME = "COLUMN_NAME";

    private final DataSource dataSource;

    public UniqueIndex(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @SneakyThrows
    public Stream<Column> findSmallest(final Schema schema, final Table table) {
        Stream<ColumnAndIndex> columnsAndIndices = columnsAndIndicesOf(schema, table);

        ImmutableListMultimap<Index, Column> multimap = toMultimap(columnsAndIndices);

        Stream<Collection<Column>> values = iterableStream(multimap.asMap().values());
        Stream<Collection<Column>> sortedBySize = values.sort(Ord.intOrd.comap(sizeOf()));

        return sortedBySize.toOption().map(columns -> iterableStream(columns)).orSome(Stream.<Column>nil());
    }

    private Stream<ColumnAndIndex> columnsAndIndicesOf(Schema schema, Table table) throws SQLException {
        return columnAndIndexOptions(schema, table).bind(columnAndIndexes -> columnAndIndexes.toStream());
    }

    private Stream<Option<ColumnAndIndex>> columnAndIndexOptions(Schema schema, Table table) throws SQLException {
        log.info("About to fetch column and index metadata for " + table.name);

        try (Connection connection = dataSource.getConnection();
             ResultSet resultSet = connection.getMetaData().getIndexInfo(null, schema.name, table.name, true, true)) {
            return iterableStream(new RowMapperResultSetExtractor<>(toColumnAndIndexOption()).extractData(resultSet));
        }
    }

    private RowMapper<Option<ColumnAndIndex>> toColumnAndIndexOption() {
        return (resultSet, rowNum) -> {
            Option<Index> indexName = Option.fromNull(resultSet.getString(INDEX_NAME)).map(s -> new Index(s));
            Column column = new Column(resultSet.getString(COLUMN_NAME));

            return indexName.map(name -> new ColumnAndIndex(column, name));
        };
    }

    private F<Collection<Column>, Integer> sizeOf() {
        return columns -> columns.size();
    }

    private ImmutableListMultimap<Index, Column> toMultimap(Iterable<ColumnAndIndex> columnsAndIndices) {
        ImmutableListMultimap.Builder<Index, Column> builder = ImmutableListMultimap.builder();
        for (ColumnAndIndex columnAndIndex : columnsAndIndices) {
            builder.put(columnAndIndex.index, columnAndIndex.column);
        }
        return builder.build();
    }

    @RequiredArgsConstructor
    @ToString
    @EqualsAndHashCode
    private static class ColumnAndIndex {
        public final Column column;
        public final Index index;
    }
}
