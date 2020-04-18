package core;

import java.util.Objects;

public class DeleteOperation implements Operation {
    private String key;

    public DeleteOperation(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DeleteOperation deleteOperation = (DeleteOperation) o;
        return Objects.equals(key, deleteOperation.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
