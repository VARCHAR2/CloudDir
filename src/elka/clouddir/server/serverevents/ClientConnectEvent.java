package elka.clouddir.server.serverevents;

import java.net.Socket;

/**
 * Zdarzenie serwera po podłączeniu się nowego klienta
 * @author Michał Toporowski
 */
public class ClientConnectEvent extends ServerEvent {
    private final Socket clientSocket;

    public ClientConnectEvent(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}
