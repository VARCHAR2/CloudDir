package elka.clouddir.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import elka.clouddir.server.communication.ClientCommunicationThread;
import elka.clouddir.server.serverevents.ClientConnectEvent;
import elka.clouddir.server.serverevents.FileChangedEvent;
import elka.clouddir.server.serverevents.LoginRequestEvent;
import elka.clouddir.server.serverevents.ServerEvent;
import elka.clouddir.server.serverevents.ServerEventProcessingStrategy;

public class ServerController {

    private static final int PORT = 3333;

    private ClientCommunicationThread threads[];
//    int             size;
    private ServerSocket serverSocket;

    private BlockingQueue<ServerEvent> serverEventQueue;

    private ConnectionReceiver connectionReceiver;

    private Map<Class<? extends ServerEvent>, ServerEventProcessingStrategy> procMap;

    private int MAX_CLIENTS = 10;

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
        threads = new ClientCommunicationThread[MAX_CLIENTS];

        serverEventQueue = new LinkedBlockingQueue<>();

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
    private void connectClient(Socket clientSocket) throws IOException {
            threads[threads.length - 1] = new ClientCommunicationThread(clientSocket, serverEventQueue);
            threads[threads.length - 1].start();
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
