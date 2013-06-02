package elka.clouddir.client;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import elka.clouddir.client.clientEvents.ClientEvent;
import elka.clouddir.client.clientEvents.LoginAcceptedEvent;
import elka.clouddir.client.clientEvents.LoginRejectedEvent;
import elka.clouddir.shared.Message;

/**
 * Class for communication with server
 * @author bogdan
 */
public class ServerCommunicationThread extends Thread {
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    
	private Socket clientSocket;
	private static final int PORT = 3333;

    private final BlockingQueue<ClientEvent> clientEventQueue;
    private boolean running = false;

    /**
     * Creating the socket and connecting with server
     * @param clientEventQueue
     * @throws IOException - server is down
     */
    public ServerCommunicationThread(BlockingQueue<ClientEvent> clientEventQueue) throws IOException {
    	
        this.clientEventQueue = clientEventQueue;
        
        clientSocket = new Socket("localhost", PORT);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(clientSocket.getInputStream());
        System.out.println("Connection established");
        
    }

    /**
     * Listening for the incoming messages from server
     */
    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                Message message = (Message)in.readObject();
                ClientEvent event = processMessage(message);
//                TransmissionEnd transmissionEnd = (TransmissionEnd)in.readObject();

                clientEventQueue.add(event);
            } catch (EOFException e) {
                running = false;
                System.out.println("Server disconnected");
            } catch (UnsupportedOperationException | ClassCastException e ) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                running = false;
                e.printStackTrace();
            }

        }

        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


    }

    /**
     * Sending the object to server
     * @param file
     * @throws IOException
     */
    public void sendObject(final Serializable file) throws IOException {
        out.writeObject(file);
        out.flush();
    }

    /**
     * Processing the Message depending on the type
     * @param message
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    private ClientEvent processMessage(final Message message) throws IOException, ClassNotFoundException, InterruptedException {
    	
    	switch (message) {
			case LOGIN_OK:
				return new LoginAcceptedEvent();
			case LOGIN_FAILED:
				return new LoginRejectedEvent();
			default:
				throw new UnsupportedOperationException("Operation not implemented " + message.toString());
    	}
    	
    }

    /**
     * Sending the type of the message
     * @param message
     * @throws IOException
     */
	public void sendMessage(Message message) throws IOException {
		out.writeObject(message);
		out.flush();
	}

}
