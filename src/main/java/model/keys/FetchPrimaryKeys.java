package model.keys;

import api.Table;
import fj.data.Stream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import model.source.PrimaryKey;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

@Slf4j
public class FetchPrimaryKeys {
    static final String COLUMN_NAME = "COLUMN_NAME";

    private final DataSource dataSource;

    public FetchPrimaryKeys(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static RowMapper<PrimaryKey> toPrimaryKey() {
        return (resultSet, rowNum) -> new PrimaryKey(resultSet.getString(COLUMN_NAME));
    }

    @SneakyThrows
    public Stream<PrimaryKey> ofTable(final Table table) {
        log.info("About to fetch column metadata for " + table.name);
        try (Connection connection = dataSource.getConnection();
             ResultSet resultSet = connection.getMetaData().getPrimaryKeys(null, null, table.name)) {
            return Stream.iterableStream(new RowMapperResultSetExtractor<>(toPrimaryKey()).extractData(resultSet));
        }
    }
}
