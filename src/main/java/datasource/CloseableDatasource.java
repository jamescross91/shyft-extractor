package datasource;

public interface CloseableDatasource extends ManagedDataSource {
    void close();

}
