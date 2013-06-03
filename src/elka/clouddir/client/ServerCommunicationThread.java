package elka.clouddir.client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

import elka.clouddir.client.clientEvents.ClientEvent;
import elka.clouddir.client.clientEvents.FileChangedOnServerEvent;
import elka.clouddir.client.clientEvents.FileDeletedOnServerEvent;
import elka.clouddir.client.clientEvents.FilePathChangedOnServerEvent;
import elka.clouddir.client.clientEvents.LoginAcceptedEvent;
import elka.clouddir.client.clientEvents.LoginRejectedEvent;
import elka.clouddir.client.clientEvents.ServerResponseEvent;
import elka.clouddir.server.model.AbstractFileInfo;
import elka.clouddir.shared.Message;
import elka.clouddir.shared.RenameInfo;
import elka.clouddir.shared.protocol.ServerResponse;

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

                clientEventQueue.add(event);
            }
            catch (EOFException | SocketException e) {
                running = false;
                System.out.println("Server disconnected");
            } catch (UnsupportedOperationException | ClassCastException e ) {
                System.out.println("[Error:] " + e.getMessage());
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
			case FILE_CHANGED: {
                AbstractFileInfo metadata = (AbstractFileInfo) in.readObject();
                byte[] data = (byte[]) in.readObject();
                return new FileChangedOnServerEvent(metadata, data);
            }
            case FILE_DELETED: {
                AbstractFileInfo metadata = (AbstractFileInfo) in.readObject();
                return new FileDeletedOnServerEvent(metadata);
            }
            case FILEPATH_CHANGED: {
                RenameInfo renameInfo = (RenameInfo) in.readObject();
                return new FilePathChangedOnServerEvent(renameInfo);
            }
            case SERVER_RESPONSE:
                ServerResponse response = (ServerResponse) in.readObject();
                return new ServerResponseEvent(response);
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
