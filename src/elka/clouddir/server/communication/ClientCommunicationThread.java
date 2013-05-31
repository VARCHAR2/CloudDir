package elka.clouddir.server.communication;

import elka.clouddir.server.model.AbstractFileInfo;
import elka.clouddir.server.serverevents.FileChangedEvent;
import elka.clouddir.server.serverevents.LoginRequestEvent;
import elka.clouddir.server.serverevents.ServerEvent;
import elka.clouddir.shared.LoginInfo;
import elka.clouddir.shared.Message;
import elka.clouddir.shared.TransmissionEnd;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Wątek odpowiadający za komunikację z klientem
 * @author Michał Toporowski
 */
public class ClientCommunicationThread extends Thread
{
    private final   ObjectOutputStream  out;
    private final   ObjectInputStream   in;

    private final BlockingQueue<ServerEvent> serverEventQueue;

    private         boolean             running;


//    static Map<Message, MessageProcesser> processerMap;
//    static {
//        initMap();
//    }

    public ClientCommunicationThread(Socket clientSocket, BlockingQueue<ServerEvent> serverEventQueue) throws IOException {
        this.serverEventQueue = serverEventQueue;
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    @Override
    public void run()
    {
        running = true;
        while (running) {
            try {
                Message message = (Message)in.readObject();

                //pobranie dodatkowych danych
                ServerEvent event = processMessage(message);

                //to po to, aby sprawdzić, że poprawnie zakończono transmisję
//                TransmissionEnd transmissionEnd = (TransmissionEnd)in.readObject();

                //ok - wyślij do serwera
                serverEventQueue.put(event);
            } catch (Exception e) {
            	running = false;
//                e.printStackTrace();
            	System.out.println("Connection was closed");
            }

        }
    }


    /**
     * Wysyła obiekt do klienta
     * @param object
     * @throws IOException
     */
    public void sendObject(final Serializable object) throws IOException {
        out.writeObject(object);
    }


    private ServerEvent processMessage(final Message message) throws IOException, ClassNotFoundException, InterruptedException {
        switch (message) {
            case LOGIN_REQUEST:
                LoginInfo loginInfo = (LoginInfo) in.readObject();
//                String password = (String) in.readObject();
                System.out.println("LOGIN_REQUEST transmitted");
                return new LoginRequestEvent(loginInfo.getLogin(), loginInfo.getPassword());
            case FILE_CHANGED:
                AbstractFileInfo metadata = (AbstractFileInfo) in.readObject();
                return new FileChangedEvent(metadata);
            default:
                throw new UnsupportedOperationException("Operation not implemented");
        }
    }


}
