package elka.clouddir.server;

import elka.clouddir.server.communication.ClientCommunicationThread;
import elka.clouddir.server.model.User;
import elka.clouddir.server.model.UserGroup;
import elka.clouddir.server.serverevents.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Kontroler serwera
 * @author Michał Toporowski
 */
public class ServerController {

    private static final int PORT = 3333;

    private List<ClientCommunicationThread> threads;
//    int             size;
    private ServerSocket serverSocket;

    private BlockingQueue<ServerEvent> serverEventQueue;

    private ConnectionReceiver connectionReceiver;

    private Map<Class<? extends ServerEvent>, ServerEventProcessingStrategy> procMap;

    private int MAX_CLIENTS = 10;

    private static List<User> USERS;
    private static List<UserGroup> USER_GROUPS;

    private List<User> users = USERS;
    private List<UserGroup> userGroups = USER_GROUPS;

    static {
        USER_GROUPS = new ArrayList<>();
        UserGroup group = new UserGroup("Ludziska", "folderLudzisk");
        USER_GROUPS.add(group);

        USERS = new ArrayList<>();
        USERS.add(new User("Michał", false, group));
        USERS.add(new User("Богдан", false, group));
        USERS.add(new User("Łukasz", false, group));
    }


    boolean         running;
//
//    ServerController() throws Exception {
//        /* initialize connection */
//        active = true;
//        listen();
//    }
//
//    void listen() throws Exception {
//        while ( active ) {
//            Socket clientSocket = serverSocket.accept();
//            threads[ size++ ] = new ClientThread( clientSocket );
//            threads[ size - 1 ].start();
//        }
//    }

    public ServerController() throws IOException {

        initProcMap();

        serverEventQueue = new LinkedBlockingQueue<>();

        threads = new LinkedList<>();


        connectionReceiver = new ConnectionReceiver(serverEventQueue, serverSocket);

        serverSocket = new ServerSocket(PORT);
        connectionReceiver = new ConnectionReceiver(serverEventQueue, serverSocket);
        
        
        new Thread(connectionReceiver).start();
    }

//
//    void listen() throws IOException {
//
//        running = true;
//        while(running) {
//            Socket clientSocket = serverSocket.accept();
//            threads[threads.length] = new ClientCommunicationThread(clientSocket);
//            threads[threads.length - 1].start();
//        }
//    }

    public void loop() {
        while(true) {
            try {
                ServerEvent event = serverEventQueue.take();

                procMap.get(event.getClass()).process(event);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Podłączenie nowego wątku
     * @param clientSocket
     * @throws IOException
     */
    private void connectClient(Socket clientSocket) throws IOException{
            ClientCommunicationThread newThread = new ClientCommunicationThread(clientSocket, serverEventQueue);
            threads.add(newThread);
            newThread.start();
    }

    private void initProcMap() {
        procMap = new HashMap<>();
        procMap.put(ClientConnectEvent.class, new ServerEventProcessingStrategy() {
            @Override
            public void process(ServerEvent event) throws IOException {
                connectClient(((ClientConnectEvent)event).getClientSocket());
            }
        });
        procMap.put(LoginRequestEvent.class, new ServerEventProcessingStrategy() {
            @Override
            public void process(ServerEvent event) {
                
            	LoginRequestEvent loginRequestEvent = (LoginRequestEvent) event;
            	// TODO make checking out the user's information
//            	System.out.println("Login: " + loginRequestEvent.getUsername() + "\nPassword: " + loginRequestEvent.getPassword());
            }
        });
        procMap.put(FileChangedEvent.class, new ServerEventProcessingStrategy() {
            @Override
            public void process(ServerEvent event) {
                //TODO
            }
        });
    }


    /**
	 * @param args
	 */
	public static void main(String[] args) {
        try {
            new ServerController().loop();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
