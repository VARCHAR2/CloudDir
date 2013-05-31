package elka.clouddir.client;

import elka.clouddir.client.clientEvents.ClientEvent;
import elka.clouddir.server.model.AbstractFileInfo;
import elka.clouddir.server.serverevents.FileChangedEvent;
import elka.clouddir.server.serverevents.LoginRequestEvent;
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
 * @author Bogdan Shkola
 */
public class ServerCommunicationThread extends Thread {
    private final ObjectOutputStream  out;
    private final ObjectInputStream   in;
    
	private Socket clientSocket;
	private static final int PORT = 3333;

    private final BlockingQueue<ClientEvent> clientEventQueue;

//    static Map<Message, MessageProcesser> processerMap;
//    static {
//        initMap();
//    }

    public ServerCommunicationThread(BlockingQueue<ClientEvent> clientEventQueue) throws IOException {
    	
        this.clientEventQueue = clientEventQueue;
        
        clientSocket = new Socket("localhost", PORT);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(clientSocket.getInputStream());
        System.out.println("Connection established");
        
    }

    @Override
    public void run() {
    	try {
    		while (true) {
            
                Message message = (Message)in.readObject();
                ClientEvent event = processMessage(message);
//                TransmissionEnd transmissionEnd = (TransmissionEnd)in.readObject();

                clientEventQueue.add(event);
    		}
    	} catch (Exception e) {
            try {
				in.close();
				out.close();
	            clientSocket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            e.printStackTrace();
        }


    }


    public void sendObject(final Object object) throws IOException {
        out.writeObject(object);
        out.flush();
    }
    
    public void sendFile(final Serializable file) throws IOException {
        out.writeObject(file);
        out.flush();
    }

    private ClientEvent processMessage(final Message message) throws IOException, ClassNotFoundException, InterruptedException {
    	return null;
    }

	public void sendMessage(Message message) throws IOException {
		out.writeObject(message);
		out.flush();
	}

}
