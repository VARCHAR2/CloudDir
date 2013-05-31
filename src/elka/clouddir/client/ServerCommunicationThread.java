package elka.clouddir.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import elka.clouddir.client.clientEvents.ClientEvent;
import elka.clouddir.client.clientEvents.LoginAcceptedEvent;
import elka.clouddir.client.clientEvents.LoginRejectedEvent;
import elka.clouddir.shared.Message;

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

    public void sendObject(final Serializable file) throws IOException {
        out.writeObject(file);
        out.flush();
    }

    private ClientEvent processMessage(final Message message) throws IOException, ClassNotFoundException, InterruptedException {
    	
    	switch (message) {
			case LOGIN_OK:
				return new LoginAcceptedEvent();
			case LOGIN_FAILED:
				return new LoginRejectedEvent();
			default:
				throw new UnsupportedOperationException("Operation not implemented");
    	}
    	
    }

	public void sendMessage(Message message) throws IOException {
		out.writeObject(message);
		out.flush();
	}

}
