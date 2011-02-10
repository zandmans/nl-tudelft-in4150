/**
 * Software written for the Distributed Systems Lab course.
 *
 * @author H. Pijper
 * @author P.A.M. Anemaet
 * @author N. Brouwers
 */
package nl.tudelft.in4150.objects;

import java.util.HashMap;
import java.util.Map;

import nl.tudelft.in4150.exception.*;

/**
 * The monitor which activates sockets
 * on demand, when messages are waiting in
 * their queues.
 */
public class SocketMonitor implements Runnable {

    /* Singleton instance */
    private static SocketMonitor instance = null;
    /* System states */
    private boolean paused;
    private boolean running;
    /* Map which assigns clients to ids */
    private Map<String, SynchronizedSocket> clientMap;
    /* Start time */
    public double startTime;
    public double timeFactor;
    public double currentTime;

    public boolean isPaused() {
        return paused;
    }

    /**
     * Create the SocketMonitor. Make sure it starts
     * in a running state, no ids are assigned to
     * sockets and the start time of the simulation is
     * set to the current time.
     */
    private SocketMonitor() {
        /* Reset status indicators */
        paused = false;

        /* Initialize the clientMap */
        clientMap = new HashMap<String, SynchronizedSocket>();

        /* Assign the initial time, so a relative time can be
           * given as meta-info instead of 17247814718724E14 */
        startTime = System.currentTimeMillis();

        /* set the time factor to miliseconds */
        timeFactor = 0.001;

        /* Start a new thread */
        new Thread(this).start();
    }

    /**
     * Returns the singleton instance of SocketMonitor.
     *
     * @return the instance of the SocketMonitor.
     */
    public static SocketMonitor getInstance() {
        if (instance == null) {
            instance = new SocketMonitor();
        }

        return instance;
    }

    /**
     * @return the current time.
     */
    public double getTime() {
        return currentTime;
    }

    /**
     * Pause the system.
     */
    public void pause() {
        if (paused)
            throw new AlreadyPausedException();

        paused = true;
    }

    /**
     * Continue running the system.
     */
    public void play() {
        if (!paused)
            throw new NotPausedException();

        paused = false;
        startTime = System.currentTimeMillis();
    }

    /**
     * Wake up all synchronized sockets, iff
     * they have a message waiting.
     */
    private synchronized void runWakeUp() {
        SynchronizedSocket ss;

        for (Socket s : clientMap.values()) {
            if (!(s instanceof SynchronizedSocket))
                continue;

            ss = (SynchronizedSocket) s;

            if (ss.hasMessagesWaiting()) {
                ss.wakeUp();
            }
        }
    }

    public void run() {
        double tempTime;

        /* Start in running state */
        running = true;

        while (running) {
            /* Sleep in pause state */
            while (paused)
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            try {
                /* Put the current time in a temporary variable,
                     * since we should be using the same value
                     * twice below.
                     */
                tempTime = System.currentTimeMillis();

                /* Assign the current time relative to the seconds spend */
                currentTime += (tempTime - startTime) * timeFactor;

                /* And restore the start time */
                startTime = tempTime;

                /* Go thru all the sockets and wake them up if necessary */
                runWakeUp();

                /* Sleep for some time. */
                /* TODO: sleep forever and wakeup on a getSocket()
                     * request, thats the only possible point where
                     * message queue's can be filled. */
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Assign a socket to the given ID.
     *
     * @param id     should be unique and identifies the socket.
     * @param socket is the actual socket to assign the ID to.
     */
    public synchronized void registerSocket(String id, SynchronizedSocket socket) {
        if (clientMap.containsKey(id))
            throw new AlreadyAssignedIDException();

        clientMap.put(id, socket);
    }

    /**
     * Unregister a socket.
     *
     * @param url should be unique.
     */
    public synchronized void unRegisterSocket(String url) {
        if (!clientMap.containsKey(url))
            throw new IDNotAssignedException();

        clientMap.remove(url);
    }


    /**
     * Halt the SocketMonitor.
     */
	public void shutdown() 
	{
		running = false;
	}
}	
