package elka.clouddir.server;

import elka.clouddir.server.communication.ClientCommunicationThread;
import elka.clouddir.server.serverevents.ClientConnectEvent;
import elka.clouddir.server.serverevents.ServerEvent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerController {

    private static final int PORT = 3333;

    private ClientCommunicationThread threads[];
//    int             size;
    private ServerSocket serverSocket;

    private BlockingQueue<ServerEvent> serverEventQueue;

    private ConnectionReceiver connectionReceiver;


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

        serverEventQueue = new LinkedBlockingQueue<>();


        connectionReceiver = new ConnectionReceiver(serverEventQueue, serverSocket);

        serverSocket = new ServerSocket(PORT);
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

                if(event.getClass() == ClientConnectEvent.class) {
                    connectClient(((ClientConnectEvent)event).getClientSocket());
                }



            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
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
            threads[threads.length] = new ClientCommunicationThread(clientSocket);
            threads[threads.length - 1].start();
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
