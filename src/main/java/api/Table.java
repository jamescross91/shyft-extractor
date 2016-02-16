package api;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Table {
    public final String name;

    public Table(String name) {
        this.name = name;
    }
}
