# My DB
_My DB is an in-memory database which stores Key, Value pair of type <String, Integer>_ 

## Operations
- Set(Key, Value) : Add or update the key, value
- Get(Key): get the value from key
- Delete(Key): delete the key
- Count(Value): Number of keys with Value
- Start: Start the transaction
- Commit: Commit the transaction
- Rollback: Rollback Transaction

## Steps
1. Get the session: ``Session session = Session.createSession();``
2. Start the session: ``session.start();``
3. Do get/set/delete operations: ``session.get("A"); session.set("A", 2); session.delete("A");``
4. Count number of keys with Value ``session.countKeys(3)``
5. Commit/Rollback session: ``session.commit(); session.rollback();``

## Test Sequence
_Test code is written in Client.java_
1. init Thread: initialize the data: A = 2, B = 3, C = 5
2. client1 : set the value of A to 3.
3. client2 : get the value of A (which is 2).
4. client2 : set the value of A to 4.
5. client2 : set the value of C to 6.
6. client2 : get the value of A (which is 4 from Step 4).
7. client2 : commit changes.
8. client1 : commit changes.
9. client3 : get the value of A (which is 3 because Step 8 overrides the value).
