package core;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InMemoryDatabase implements Database {
    private static final InMemoryDatabase INSTANCE = new InMemoryDatabase();
    private final ConcurrentHashMap<String, Integer> dataSet;
    private final ConcurrentHashMap<Integer, HashSet<String>> reverseDataSet;

    private final Lock lock;

    private InMemoryDatabase() {
        dataSet = new ConcurrentHashMap<>();
        reverseDataSet = new ConcurrentHashMap<>();
        lock = new ReentrantLock();
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
        synchronized (lock) {
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
    }

    @Override
    public Integer delete(String key) {
        synchronized (lock) {
            Integer value = dataSet.remove(key);
            if (value != null) {
                reverseDataSet.get(value).remove(key);
            }
            return value;
        }
    }

    @Override
    public Set<String> getKeysWithValue(Integer value) {
        if (reverseDataSet.get(value) == null) {
            return new HashSet<>();
        } else {
            return reverseDataSet.get(value);
        }
    }

    @Override
    public void getLock() {
        lock.lock();
    }

    @Override
    public void releaseLock() {
        lock.unlock();
    }
}
