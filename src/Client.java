import core.Session;

public class Client {
    public static void main(String[] args) throws Exception {
        Thread init = new Thread(() -> {
            System.out.println("Initializing DB");
            Session session = Session.createSession();
            session.start();
            session.set("A", 2);
            session.set("B", 3);
            session.set("C", 5);
            session.commit();
            System.out.println("Initialization Completed!");
        }, "init");

        // Initialize
        init.start();
        init.join();

        Thread client1 = new Thread(() -> {
            Session session = Session.createSession();
            System.out.println("Client-1 Starting");
            session.start();
            session.set("A", 3);
            try {
                Thread.sleep(2000); // Client1 doing some task for 2 secs
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            session.commit();
            System.out.println("Client-1 Committed");
        }, "Client-1");

        Thread client2 = new Thread(() -> {
            Session session = Session.createSession();
            System.out.println("Client-2 Starting");
            session.start();
            System.out.println("Step 1: Client-2 Get A = " + session.get("A")); // expecting 2
            session.set("A", 4);
            session.set("C", 6);
            System.out.println("Step 2: Client-2 Get A = " + session.get("A")); // expecting 4
            session.commit();
            System.out.println("Client-2 Committed");
        }, "Client-2");

        Thread client3 = new Thread(() -> {
            Session session = Session.createSession();
            System.out.println("Client-3 Starting");
            session.start();
            System.out.println("Step 3: Client-3 Get A = " + session.get("A")); // expecting 3
            // System.out.println("Client 3: Get count of value 3 = " + session.countKeys(3));
            session.commit();
            System.out.println("Client-2 Committed");
        }, "Client-3");


        client1.start();
        // Client-2 starts session after 1 sec Client-1 starts
        Thread.sleep(1000);
        client2.start();
        client1.join();
        client2.join();

        // Client 3 starts after the all task is done by client 1 and client 2
        client3.start();
    }
}
