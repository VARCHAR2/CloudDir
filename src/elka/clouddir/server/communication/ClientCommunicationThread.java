package elka.clouddir.server.communication;

import elka.clouddir.shared.Message;
import elka.clouddir.shared.TransmissionEnd;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Wątek odpowiadający za komunikację z klientem
 * @author Michał Toporowski
 */
public class ClientCommunicationThread extends Thread
{
    ObjectOutputStream  out;
    ObjectInputStream   in;
    boolean             running;

//    static Map<Message, MessageProcesser> processerMap;
//    static {
//        initMap();
//    }

    public ClientCommunicationThread(Socket clientSocket) throws IOException {
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
//                processAdditionalData(message);
                //tu jakoś trzeba dodatkowe informacje pobrać
//                .get(message).process();
                //to po to, aby sprawdzić, że poprawnie zakończono transmisję
                TransmissionEnd transmissionEnd = (TransmissionEnd)in.readObject();
            } catch (Exception e) {
                e.printStackTrace();
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



}
