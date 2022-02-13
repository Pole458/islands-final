package it.unipr.sowide.islands.content;


import it.unipr.sowide.actodes.interaction.Request;
import it.unipr.sowide.util.annotations.Singleton;

/**
 * A message sent by the master to the controller that requests to start
 * the simulation.
 *
 */
@Singleton
public class StartIslandsLoop implements Request {
    private StartIslandsLoop() { }

    public static final StartIslandsLoop INSTANCE = new StartIslandsLoop();
}
