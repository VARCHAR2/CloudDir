package elka.clouddir.server;

import elka.clouddir.server.communication.ClientCommunicationThread;
import elka.clouddir.server.serverevents.ClientConnectEvent;
import elka.clouddir.server.serverevents.ServerEvent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * Klasa obsługująca dołączanie się nowych klientów
 * @author Michał Toporowski
 */
public class ConnectionReceiver implements Runnable {

    private final BlockingQueue<ServerEvent> serverEvents;
    private final ServerSocket serverSocket;

    private boolean running;

    public ConnectionReceiver(BlockingQueue<ServerEvent> serverEvents, ServerSocket serverSocket) {
        this.serverEvents = serverEvents;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        running = true;
        while(running) {
            try {
                Socket clientSocket = serverSocket.accept();
                serverEvents.put(new ClientConnectEvent(clientSocket));
            } catch (IOException e) {
                e.printStackTrace();
                running = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
                running = false;
            }
        }
    }
}
