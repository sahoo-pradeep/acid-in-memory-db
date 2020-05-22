package core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

/**
 * Here, session can have only 1 transaction.
 * State Flows:
 * START
 * GET/SET/DELETE/COUNT
 * COMMIT/ROLLBACK
 */
public class Session {
    private final Database database;
    private final Queue<Operation> writeOperations;
    private final HashMap<String, Integer> newDataSet;
    private SessionStatus status = SessionStatus.NOT_STARTED;

    private Session() {
        database = InMemoryDatabase.getInstance();
        writeOperations = new LinkedList<>();
        newDataSet = new HashMap<>();
    }

    public static Session createSession() {
        return new Session();
    }

    public Integer get(String key) {
        if (!status.equals(SessionStatus.START)) {
            throw new RuntimeException("Session is not started");
        }

        if (newDataSet.containsKey(key)) {
            return newDataSet.get(key);
        }

        Integer value = database.get(key);

        for (Operation operation : writeOperations) {
            if (operation instanceof SetOperation && ((SetOperation) operation).getKey()
                    .equals(key)) {
                value = ((SetOperation) operation).getValue();
            } else if (operation instanceof DeleteOperation && ((DeleteOperation) operation)
                    .getKey().equals(key)) {
                value = null;
            }
        }

        return value;

    }

    public void set(String key, Integer value) {
        if (!status.equals(SessionStatus.START)) {
            throw new RuntimeException("Session is not started");
        }

        Objects.requireNonNull(key, "Key shouldn't be null");
        Objects.requireNonNull(value, "Value shouldn't be null");

        writeOperations.offer(new SetOperation(key, value));
        newDataSet.put(key, value);
    }

    public void delete(String key) {
        if (!status.equals(SessionStatus.START)) {
            throw new RuntimeException("Session is not started");
        }

        Objects.requireNonNull(key, "Key shouldn't be null");

        if (newDataSet.containsKey(key)) {
            newDataSet.remove(key);
        } else {
            writeOperations.offer(new DeleteOperation(key));
        }
    }

    public Integer countKeys(Integer value) {
        if (!status.equals(SessionStatus.START)) {
            throw new RuntimeException("Session is not started");
        }
        Objects.requireNonNull(value, "Value shouldn't be null");

        Set<String> keys = database.getKeysWithValue(value);

        if (writeOperations.isEmpty()) {
            return keys.size();
        }

        for (Operation operation : writeOperations) {
            if (operation instanceof SetOperation && ((SetOperation) operation).getValue()
                    .equals(value)) {
                keys.add(((SetOperation) operation).getKey());
            } else if (operation instanceof DeleteOperation) {
                keys.remove(((DeleteOperation) operation).getKey());
            }
        }

        return keys.size();
    }

    public void start() {
        if (!status.equals(SessionStatus.NOT_STARTED)) {
            throw new RuntimeException("Session is already started");
        }

        status = SessionStatus.START;
    }

    public void commit() {
        if (!status.equals(SessionStatus.START)) {
            throw new RuntimeException("Session is not started");
        }

        if (writeOperations.isEmpty()) {
            return;
        }


        for (Operation operation : writeOperations) {
            if (operation instanceof SetOperation) {
                database.set(((SetOperation) operation).getKey(),
                        ((SetOperation) operation).getValue());
            } else if (operation instanceof DeleteOperation) {
                database.delete(((DeleteOperation) operation).getKey());
            }
        }

        status = SessionStatus.COMMIT;
    }

    public void rollback() {
        if (!status.equals(SessionStatus.START)) {
            throw new RuntimeException("Session is not started");
        }

        // clear all stored values
        writeOperations.clear();
        newDataSet.clear();

        status = SessionStatus.ROLLBACK;
    }
}
