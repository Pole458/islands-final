package it.unipr.sowide.actodata.core.acquirer;

import java.util.Scanner;

/**
 * An acquirer that starts a thread and reads lines from {@link System#in}.
 * Used to interact with ActoDatA pipelines.
 */
public class StdInAcquirer extends Acquirer<String> {
    @Override
    public void setupListening(AcquiredDataAcceptor<String> acceptor) {
        Thread thread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                acceptor.submit(scanner.nextLine());
            }
        });
        thread.start();
    }
}
