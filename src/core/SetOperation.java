package core;

import java.util.Objects;

public class SetOperation implements Operation {
    String key;
    Integer value;

    public SetOperation(String key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SetOperation setOperation = (SetOperation) o;
        return Objects.equals(key, setOperation.key) && Objects.equals(value, setOperation.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
