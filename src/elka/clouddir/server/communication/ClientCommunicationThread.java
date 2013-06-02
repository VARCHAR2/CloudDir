package elka.clouddir.server.communication;

import elka.clouddir.server.model.AbstractFileInfo;
import elka.clouddir.server.model.User;
import elka.clouddir.server.serverevents.*;
import elka.clouddir.shared.*;
import elka.clouddir.shared.protocol.ServerResponse;

import java.io.*;
import java.net.Socket;
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

    private User user = null;

    private         boolean             running;

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

                //jeśli zalogowany, lub chce się logować - ślij dalej
                if(message == Message.LOGIN_REQUEST || isLogged()) {
                    //ok - wyślij do serwera
                    serverEventQueue.put(event);
                } else {
                    System.out.println("Message dropped (user not logged in)");
                }
            } catch (EOFException e) {
                running = false;
                System.out.println("Transmission ended, connection closed");
            } catch (ClassCastException | ClassNotFoundException e) {
                System.out.println("Received incompatible object");
            } catch (InterruptedException e) {
                running = false;
                System.out.println("Thread interrupted");
            } catch (IOException e) {
                running = false;
                System.out.println("IO exception");
            }
        }


        try {
            in.close();
            out.close();
            user.setLoggedIn(false);
            user = null;
        } catch (IOException e) {
            e.printStackTrace();
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
        System.out.println("Received message \"" + message.toString() + "\"");


        switch (message) {
            case LOGIN_REQUEST:
                LoginInfo loginInfo = (LoginInfo) in.readObject();
                return new LoginRequestEvent(this, loginInfo);
            case FILE_CHANGED: {
                AbstractFileInfo metadata = (AbstractFileInfo) in.readObject();
                byte[] data = (byte[]) in.readObject();
                return new FileChangedEvent(this, metadata, data);
            }
            case FILE_DELETED: {
                AbstractFileInfo metadata = (AbstractFileInfo) in.readObject();
                return new FileDeletedEvent(this, metadata);
            }
            case FILEPATH_CHANGED: {
                RenameInfo renameInfo = (RenameInfo) in.readObject();
                return new FilePathChangedEvent(this, renameInfo);
            }
            case FULL_METADATA_TRANSFER: {
                FilesMetadata metadata = (FilesMetadata) in.readObject();
                return new FullMetadataTransferEvent(this, metadata);
            }
//            case FILE_TRANSFER:
//                AbstractFileInfo metadata = (AbstractFileInfo) in.readObject();
//                byte[] data = (byte[]) in.readObject();
//                return new FileTransferEvent(this, metadata, data);
            default:
                throw new UnsupportedOperationException("Operation not implemented");
        }
    }


    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public boolean isLogged() {
        return user != null && user.isLoggedIn();
    }
}
