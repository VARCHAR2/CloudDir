package elka.clouddir.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import elka.clouddir.server.serverevents.ClientConnectEvent;
import elka.clouddir.server.serverevents.ServerEvent;

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
            	System.out.println("Trying to accept");
                Socket clientSocket = serverSocket.accept();
                System.out.println(serverSocket.getInetAddress().getHostAddress());
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
