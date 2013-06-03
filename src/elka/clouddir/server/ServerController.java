package elka.clouddir.server;


import elka.clouddir.server.communication.ClientCommunicationThread;
import elka.clouddir.server.exception.LoginFailedException;
import elka.clouddir.server.model.AbstractFileInfo;
import elka.clouddir.server.model.User;
import elka.clouddir.server.model.UserGroup;
import elka.clouddir.server.serverevents.*;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import elka.clouddir.server.serverevents.ClientConnectEvent;
import elka.clouddir.server.serverevents.FileChangedEvent;
import elka.clouddir.server.serverevents.LoginRequestEvent;
import elka.clouddir.server.serverevents.ServerEvent;
import elka.clouddir.server.serverevents.ServerEventProcessingStrategy;
import elka.clouddir.shared.FileControler;
import elka.clouddir.shared.FilesMetadata;
import elka.clouddir.shared.LoginInfo;
import elka.clouddir.shared.Message;
import elka.clouddir.shared.protocol.ServerResponse;

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

    private FilesMetadata filesMetadata;

//    private Map<AbstractFileInfo, AbstractFileInfo> uncommitedFiles;

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

    public ServerController() throws IOException {

        initProcMap();

        serverEventQueue = new LinkedBlockingQueue<>();

        threads = new LinkedList<>();

//        uncommitedFiles = new HashMap<>();

        filesMetadata = new FilesMetadata(new ArrayList<AbstractFileInfo>());

        connectionReceiver = new ConnectionReceiver(serverEventQueue, serverSocket);

        serverSocket = new ServerSocket(PORT);
        connectionReceiver = new ConnectionReceiver(serverEventQueue, serverSocket);


        new Thread(connectionReceiver).start();
    }


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
                    User logged = logUser(loginRequestEvent.getLoginInfo());
                    System.out.println("Login OK");
                    result = Message.LOGIN_OK;
                    loginRequestEvent.getSenderThread().setUser(logged);
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

                UserGroup ownerGroup = fileChangedEvent.getSenderThread().getUser().getUserGroup();

                AbstractFileInfo changedFile = findFileByName(metadata.getRelativePath());
                if(changedFile != null) {
                    if(changedFile.getLastUploadTime().equals(metadata.getLastUploadTime())) {
                        //OK - updating file
                        //set new metadata
//                        uncommitedFiles.put(metadata, changedFile);
                        //send request
//                        fileChangedEvent.getSenderThread().sendObject(Message.FILE_REQUEST);
//                        fileChangedEvent.getSenderThread().sendObject(metadata);
//                        deletePhysicalFile(changedFile.getServerPath(ownerGroup));
//                        filesMetadata.getFilesMetaList().remove(changedFile);
                        removeFile(ownerGroup, changedFile);

                        addFile(ownerGroup, metadata, fileChangedEvent.getData());

                        fileChangedEvent.getSenderThread().sendObject(Message.SERVER_RESPONSE);
                        fileChangedEvent.getSenderThread().sendObject(new ServerResponse("File update correct: " + metadata.getRelativePath()));
                    } else {
                        //conflict
//                        fileChangedEvent.getSenderThread().sendObject(Message.CONFLICT_DETECTED);
                        metadata.setRelativePath(metadata.getRelativePath() + ".conflicted" + new Date().toString());
                        addFile(ownerGroup, metadata, fileChangedEvent.getData());

                        fileChangedEvent.getSenderThread().sendObject(Message.SERVER_RESPONSE);
                        fileChangedEvent.getSenderThread().sendObject(new ServerResponse("Conflict detected - file saved under a new name: " + metadata.getRelativePath()));
                    }
                } else {
                    //new file
                    addFile(ownerGroup, metadata, fileChangedEvent.getData());
                    fileChangedEvent.getSenderThread().sendObject(Message.SERVER_RESPONSE);
                    fileChangedEvent.getSenderThread().sendObject(new ServerResponse("A new file added: "+ metadata.getRelativePath()));
//                    uncommitedFiles.put(metadata, null);
//                    fileChangedEvent.getSenderThread().sendObject(Message.FILE_REQUEST);
//                    fileChangedEvent.getSenderThread().sendObject(metadata);
                }
                //send further
                propagateMessage(fileChangedEvent.getSenderThread(), Message.FILE_CHANGED, metadata, fileChangedEvent.getData());

            }
        });
        procMap.put(FilePathChangedEvent.class, new ServerEventProcessingStrategy() {
            @Override
            public void process(ServerEvent event) throws Exception {
                FilePathChangedEvent filePathChangedEvent = (FilePathChangedEvent)event;
                AbstractFileInfo meta = findFileByName(filePathChangedEvent.getRenameInfo().getOldPath());
                if(meta != null) {

                    String from = meta.getServerPath(filePathChangedEvent.getSenderThread().getUser().getUserGroup());
                    meta.setRelativePath(filePathChangedEvent.getRenameInfo().getNewPath());
                    String to = meta.getServerPath(filePathChangedEvent.getSenderThread().getUser().getUserGroup());

                    FileControler.moveFile(from, to);

                    filePathChangedEvent.getSenderThread().sendObject(Message.SERVER_RESPONSE);
                    filePathChangedEvent.getSenderThread().sendObject(new ServerResponse("File renamed to: " + meta.getRelativePath()));
                    //send further
                    propagateMessage(filePathChangedEvent.getSenderThread(), Message.FILEPATH_CHANGED, meta);
                } else {
                    System.out.println("Trying to change the path of the non-existent file: " + filePathChangedEvent.getRenameInfo().getOldPath());
                    filePathChangedEvent.getSenderThread().sendObject(Message.INTERNAL_SERVER_ERROR);
                }
            }
        });
        procMap.put(FileDeletedEvent.class, new ServerEventProcessingStrategy() {
            @Override
            public void process(ServerEvent event) throws Exception {
                FileDeletedEvent fileDeletedEvent = (FileDeletedEvent)event;
                AbstractFileInfo meta = findFileByName(fileDeletedEvent.getMetadata().getRelativePath());
                if(meta != null) {
//                    filesMetadata.getFilesMetaList().remove(meta);
//                    deletePhysicalFile(meta.getServerPath(fileDeletedEvent.getSenderThread().getUser().getUserGroup()));
                    removeFile(fileDeletedEvent.getSenderThread().getUser().getUserGroup(), meta);
                    fileDeletedEvent.getSenderThread().sendObject(Message.SERVER_RESPONSE);
                    fileDeletedEvent.getSenderThread().sendObject(new ServerResponse("File deleted: " + meta.getRelativePath()));
                    //send further
                    propagateMessage(fileDeletedEvent.getSenderThread(), Message.FILE_CHANGED, meta);
                } else {
                    fileDeletedEvent.getSenderThread().sendObject(Message.INTERNAL_SERVER_ERROR);
                    System.out.println("Error: deleted file doesn't exist: " + fileDeletedEvent.getMetadata().getRelativePath());
                }
            }
        });
        procMap.put(FullMetadataTransferEvent.class, new ServerEventProcessingStrategy() {
            @Override
            public void process(ServerEvent event) throws Exception {
                //TODO
            }
        });
//        procMap.put(FileTransferEvent.class, new ServerEventProcessingStrategy() {
//            @Override
//            public void process(ServerEvent event) throws Exception {
//                FileTransferEvent fileTransferEvent = (FileTransferEvent)event;
//                AbstractFileInfo newMeta = fileTransferEvent.getMetadata();
//                if(uncommitedFiles.containsKey(newMeta)) {
//                    //replace file
//                    AbstractFileInfo oldMeta = uncommitedFiles.get(newMeta);
//                    filesMetadata.getFilesMetaList().remove(oldMeta);
//                    filesMetadata.getFilesMetaList().add(newMeta);
//                    uncommitedFiles.remove(newMeta);
//                    //TODO save physical file data
//                } else {
//                    //something's wrong
//                    fileTransferEvent.getSenderThread().sendObject(Message.INTERNAL_SERVER_ERROR);
//                }
//            }
//        });
    }


    private AbstractFileInfo findFileByName(final String name) {
        for(AbstractFileInfo file : filesMetadata.getFilesMetaList()) {
            if(file.getRelativePath().equals(name)) {
                return file;
            }
        }
        return null;
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

    /**
     * próbuje zalogować użytkownika
     * @param loginInfo
     * @return logged user
     */
    private User logUser(LoginInfo loginInfo) throws LoginFailedException {
        for(User user : users) {
            if(user.getName().equals(loginInfo.getLogin())) {
                if(user.getPassword().equals(loginInfo.getPassword())) {
                    if(user.isLoggedIn()) { //już zalogowany
                        throw new LoginFailedException("User already logged in");
                    } else { //OK
                        user.setLoggedIn(true);
                        return user; //OK
                    }
                } else {
                    throw new LoginFailedException("Wrong password");
                }
            }
        }
        throw new LoginFailedException("User not registered in the system");
    }

    /**
     * Sends the message to other clients
     * @param source
     * @param data
     * @throws IOException
     */
    private void propagateMessage(final ClientCommunicationThread source, final Serializable... data) throws IOException {
        for(ClientCommunicationThread thread : threads) {
            if(thread != source) {
                for(Serializable object : data) {
                    thread.sendObject(object);
                }
            }
        }
    }



    private void addFile(UserGroup ownerGroup, AbstractFileInfo metadata, byte[] data) {
        metadata.setLastUploadTime(new Date());
        filesMetadata.getFilesMetaList().add(metadata);
        FileControler.writeFile(metadata.getServerPath(ownerGroup), data);
    }

    private void removeFile(UserGroup ownerGroup, AbstractFileInfo metadata) {
        filesMetadata.getFilesMetaList().remove(metadata);
        FileControler.deleteFile(metadata.getServerPath(ownerGroup));
    }

//
//    private void savePhysicalFile(String name, byte[] data) {
//        System.out.println("Here, the file should be saved (not implemented yet)");
//    }
//
//    private void deletePhysicalFile(String name) {
//        System.out.println("Here, the file should be saved (not implemented yet)");
//    }

}
