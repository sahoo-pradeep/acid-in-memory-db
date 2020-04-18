package core;

import java.util.Set;

public interface Database {
    Integer get(String key);

    Integer set(String key, Integer value);

    Integer delete(String key);

    Set<String> getKeysWithValue(Integer value);

    void getLock();

    void releaseLock();
}
