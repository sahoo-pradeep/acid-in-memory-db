package core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDatabase implements Database {
    private static final InMemoryDatabase INSTANCE = new InMemoryDatabase();
    private final Map<String, Integer> dataSet;
    private final Map<Integer, Set<String>> reverseDataSet;

    private InMemoryDatabase() {
        dataSet = new ConcurrentHashMap<>();
        reverseDataSet = new ConcurrentHashMap<>();
    }

    public static InMemoryDatabase getInstance() {
        return INSTANCE;
    }

    @Override
    public Integer get(String key) {
        return dataSet.get(key);
    }

    @Override
    public Integer set(String key, Integer value) {
        Integer previousValue = dataSet.put(key, value);
        if (previousValue != null) {
            reverseDataSet.get(previousValue).remove(key);
        }

        if (!reverseDataSet.containsKey(value)) {
            reverseDataSet.put(value, new HashSet<>());
        }
        reverseDataSet.get(value).add(key);
        return previousValue;
    }

    @Override
    public Integer delete(String key) {
        Integer value = dataSet.remove(key);
        if (value != null) {
            reverseDataSet.get(value).remove(key);
        }
        return value;
    }

    @Override
    public Set<String> getKeysWithValue(Integer value) {
        if (reverseDataSet.get(value) == null) {
            return new HashSet<>();
        } else {
            return reverseDataSet.get(value);
        }
    }
}
