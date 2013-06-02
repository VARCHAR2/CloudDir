package elka.clouddir.server;


import elka.clouddir.server.communication.ClientCommunicationThread;
import elka.clouddir.server.exception.LoginFailedException;
import elka.clouddir.server.model.AbstractFileInfo;
import elka.clouddir.server.model.SharedFile;
import elka.clouddir.server.model.User;
import elka.clouddir.server.model.UserGroup;
import elka.clouddir.server.serverevents.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import elka.clouddir.server.communication.ClientCommunicationThread;
import elka.clouddir.server.serverevents.ClientConnectEvent;
import elka.clouddir.server.serverevents.FileChangedEvent;
import elka.clouddir.server.serverevents.LoginRequestEvent;
import elka.clouddir.server.serverevents.ServerEvent;
import elka.clouddir.server.serverevents.ServerEventProcessingStrategy;
import elka.clouddir.shared.LoginInfo;
import elka.clouddir.shared.Message;

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

    private List<AbstractFileInfo> filesList;

    private Map<AbstractFileInfo, AbstractFileInfo> uncommitedFiles;

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
        USERS.add(new User("Michal", false, group, "12345678"));
        USERS.add(new User("Bogdan", false, group, "10101010"));
        USERS.add(new User("Lukasz", false, group, "qwertyuiop"));
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

        uncommitedFiles = new HashMap<>();


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
     * Connect a new thread
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
            public void process(ServerEvent event) throws IOException {
                
            	LoginRequestEvent loginRequestEvent = (LoginRequestEvent) event;
                Message result;

                try {
                    logUser(loginRequestEvent.getLoginInfo());
                    System.out.println("Login OK");
                    result = Message.LOGIN_OK;
                } catch (LoginFailedException e) {
                    System.out.println(e.getMessage());
                    result = Message.LOGIN_FAILED;
                }

                //send back the message
                loginRequestEvent.getSenderThread().sendObject(result);
            }
        });
        procMap.put(FileChangedEvent.class, new ServerEventProcessingStrategy() {
            @Override
            public void process(ServerEvent event) throws IOException {
                FileChangedEvent fileChangedEvent = (FileChangedEvent)event;
                AbstractFileInfo metadata = fileChangedEvent.getMetadata();

                AbstractFileInfo changedFile = findFileByName(metadata);
                if(changedFile != null) {
                    if(changedFile.getLastUploadTime().equals(metadata.getLastUploadTime())) {
                        //OK - updating file
                        //set new metadata
                        uncommitedFiles.put(metadata, changedFile);
                        //send request
                        fileChangedEvent.getSenderThread().sendObject(Message.FILE_REQUEST);
                        fileChangedEvent.getSenderThread().sendObject(metadata);
                    } else {
                        //conflict
                        fileChangedEvent.getSenderThread().sendObject(Message.CONFLICT_DETECTED);
                    }
                } else {
                    //new file
                    metadata.setLastUploadTime(new Date());
                    uncommitedFiles.put(metadata, null);
                    fileChangedEvent.getSenderThread().sendObject(Message.FILE_REQUEST);
                    fileChangedEvent.getSenderThread().sendObject(metadata);
                }
            }
        });
        procMap.put(FilePathChangedEvent.class, new ServerEventProcessingStrategy() {
            @Override
            public void process(ServerEvent event) throws Exception {
//                FileChangedEvent fileChangedEvent = (FileChangedEvent)event;
//                if(fileChangedEvent.getMetadata().getClass() == SharedFile.class) {
//
//                    SharedFile metadata = (SharedFile)fileChangedEvent.getMetadata();
//
//                    AbstractFileInfo changedFile = findFileByMD5(metadata);
//                    if(changedFile != null) {
//                        if(changedFile.getLastUploadTime().equals(metadata.getLastUploadTime())) {
//                            //OK - updating file
//                            //set new metadata
//                            uncommitedFiles.put(metadata, changedFile);
//                            //send request
//                            fileChangedEvent.getSenderThread().sendObject(Message.FILE_REQUEST);
//                            fileChangedEvent.getSenderThread().sendObject(metadata);
//                        } else {
//                            //conflict
//                            fileChangedEvent.getSenderThread().sendObject(Message.CONFLICT_DETECTED);
//                        }
//                    } else {
//                        //new file
//                        System.out.println("Error. FILEPATH_CHANGED send for unexisting file");
//                        fileChangedEvent.getSenderThread().sendObject(Message.INTERNAL_SERVER_ERROR);
//                    }
//                } else {
//                    //sent for SharedEmptyFolder
//                }
            }
        });
        procMap.put(FullMetadataTransferEvent.class, new ServerEventProcessingStrategy() {
            @Override
            public void process(ServerEvent event) throws Exception {
                //TODO
            }
        });
        procMap.put(FileTransferEvent.class, new ServerEventProcessingStrategy() {
            @Override
            public void process(ServerEvent event) throws Exception {
                FileTransferEvent fileTransferEvent = (FileTransferEvent)event;
                AbstractFileInfo newMeta = fileTransferEvent.getMetadata();
                if(uncommitedFiles.containsKey(newMeta)) {
                    //replace file
                    AbstractFileInfo oldMeta = uncommitedFiles.get(newMeta);
                    filesList.remove(oldMeta);
                    filesList.add(newMeta);
                    uncommitedFiles.remove(newMeta);
                    //TODO save physical file data
                } else {
                    //something's wrong
                    fileTransferEvent.getSenderThread().sendObject(Message.INTERNAL_SERVER_ERROR);
                }
            }
        });
    }


    private AbstractFileInfo findFileByName(AbstractFileInfo fileInfo) {
        for(AbstractFileInfo file : filesList) {
            if(file.getRelativePath().equals(fileInfo.getRelativePath())) {
                return file;
            }
        }
        return null;
    }

//    private SharedFile findFileByMD5(SharedFile fileInfo) {
//        for(AbstractFileInfo file : filesList) {
//            if(file.getClass() == SharedFile.class) {
//                if(((SharedFile)file).getMd5sum().equals(fileInfo.getMd5sum())) {
//                    return (SharedFile)file;
//                }
//            }
//        }
//        return null;
//    }


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

    /**
     * próbuje zalogować użytkownika
     * @param loginInfo
     */
    private void logUser(LoginInfo loginInfo) throws LoginFailedException {
        for(User user : users) {
            if(user.getName().equals(loginInfo.getLogin())) {
                if(user.getPassword().equals(loginInfo.getPassword())) {
                    if(user.isLoggedIn()) { //już zalogowany
                        throw new LoginFailedException("User already logged in");
                    } else { //OK
                        user.setLoggedIn(true);
                        return; //OK
                    }
                } else {
                    throw new LoginFailedException("Wrong password");
                }
            }
        }
        throw new LoginFailedException("User not registered in the system");
    }

}
