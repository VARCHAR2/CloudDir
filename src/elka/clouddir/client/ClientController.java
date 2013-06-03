package elka.clouddir.client;

import java.awt.Event;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystemException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import elka.clouddir.client.clientEvents.ClientEvent;
import elka.clouddir.client.clientEvents.FileChangedOnServerEvent;
import elka.clouddir.client.clientEvents.FileCreatedEvent;
import elka.clouddir.client.clientEvents.FileDeletedEvent;
import elka.clouddir.client.clientEvents.FileDeletedOnServerEvent;
import elka.clouddir.client.clientEvents.FileModifiedEvent;
import elka.clouddir.client.clientEvents.FilePathChangedOnServerEvent;
import elka.clouddir.client.clientEvents.FileRenamedEvent;
import elka.clouddir.client.clientEvents.LoginAcceptedEvent;
import elka.clouddir.client.clientEvents.LoginRejectedEvent;
import elka.clouddir.client.clientEvents.LoginRequestEvent;
import elka.clouddir.client.clientEvents.ServerResponseEvent;
import elka.clouddir.client.exceptions.MetadataNotFound;
import elka.clouddir.server.model.AbstractFileInfo;
import elka.clouddir.server.serverevents.FilePathChangedEvent;
import elka.clouddir.shared.FilesMetadata;
import elka.clouddir.shared.LoginInfo;
import elka.clouddir.shared.Message;
import elka.clouddir.shared.RenameInfo;
import elka.clouddir.shared.protocol.ServerResponse;

/**
 * Controller of the client
 * @author bogdan
 */
public class ClientController {

	private final BlockingQueue<ClientEvent> clientEventQueue;
	
	private LocalFileSystem localFileSystem;
	private ServerCommunicationThread serverCommunicationThread;
	
	private Map<Class<? extends ClientEvent>, Strategy> strategyMap;
	
	public ClientController() {
		
		clientEventQueue = new LinkedBlockingQueue<ClientEvent>();

		try {
			initStrategyMap();
			
			serverCommunicationThread = new ServerCommunicationThread(clientEventQueue);
			serverCommunicationThread.start();

			localFileSystem = new LocalFileSystem(clientEventQueue);
			Thread localFileSystemThread = new Thread(localFileSystem);
			localFileSystemThread.start();
		} catch (IOException e) {
			System.out.println("Server is down");
			System.exit(0);
		}
		
	}

	/**
	 * Initialization of strategy map
	 */
	private void initStrategyMap() {
		
		strategyMap = new HashMap<Class<? extends ClientEvent>, ClientController.Strategy>();
		
		strategyMap.put(LoginRequestEvent.class, new LoginRequestStrategy());
		strategyMap.put(LoginAcceptedEvent.class, new LoginAcceptedStrategy());
		strategyMap.put(LoginRejectedEvent.class, new LoginRejectedStrategy());
		
		strategyMap.put(FileCreatedEvent.class, new FileCreatedStrategy());
		strategyMap.put(FileModifiedEvent.class, new FileModifiedStrategy());
		strategyMap.put(FileRenamedEvent.class, new FileRenamedStrategy());
		strategyMap.put(FileDeletedEvent.class, new FileDeletedStrategy());

        strategyMap.put(ServerResponseEvent.class, new ServerResponseStrategy());
        strategyMap.put(FileChangedOnServerEvent.class, new FileChangedOnServerStrategy());
        strategyMap.put(FileDeletedOnServerEvent.class, new FileDeletedOnServerStrategy());
        strategyMap.put(FilePathChangedOnServerEvent.class, new FilePathChangedOnServerStrategy());
		
	}

	/**
	 * Main loop of the controller
	 * Take event from the queue and handle it
	 */
	public void loop() {

		clientEventQueue.add(new LoginRequestEvent());
//		clientEventQueue.add(new LoginAcceptedEvent()); // TODO change back when server will accept the log in
		
		while (true) {
			try {
				ClientEvent clientEvent = clientEventQueue.take();
				strategyMap.get(clientEvent.getClass()).perform(clientEvent);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * Common strategy for client
	 * @author ������
	 *
	 */
	abstract class Strategy {
		
		abstract void perform(ClientEvent clientEvent);
		
	}
	
	/**
	 * Strategy after executing the program - trying to log in
	 * @author bogdan
	 *
	 */
	class LoginRequestStrategy extends Strategy {
		
		@Override
		void perform(ClientEvent clientEvent) {
			try{
				System.out.print("Login: ");
			    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			    String login = bufferRead.readLine();
//			    login = "Bogdan";
		 
			    System.out.print("Password: ");
			    String password = bufferRead.readLine();
//			    password = "10101010";
			    serverCommunicationThread.sendMessage(Message.LOGIN_REQUEST);
			    serverCommunicationThread.sendObject(new LoginInfo(login, password));
//			    clientEventQueue.add(new LoginAcceptedEvent()); 
//			    serverCommunicationThread.sendMessage(Message.FULL_METADATA_TRANSFER);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
		}

	}
	
	/**
	 * Strategy after log in accepted by server.
	 * Collecting metadata and sending it to server
	 * @author bogdan
	 *
	 */
	class LoginAcceptedStrategy extends Strategy {
		
		@Override
		void perform(ClientEvent clientEvent) {
			try {
				List<AbstractFileInfo> metadataArray = localFileSystem.initSystemMetadata();
				FilesMetadata filesMetadata = new FilesMetadata(metadataArray);
                System.out.println("[System:] Login OK");
				serverCommunicationThread.sendMessage(Message.FULL_METADATA_TRANSFER);
				serverCommunicationThread.sendObject(filesMetadata);
//				for (AbstractFileInfo abstractFileInfo : abstractFileInfos) {
//					System.out.println(abstractFileInfo.toString());
//				}
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Strategy after unsuccessful log in. 
	 * Going back to Log in strategy
	 * @author bogdan
	 *
	 */
	class LoginRejectedStrategy extends Strategy {
		
		@Override
		void perform(ClientEvent clientEvent) {
			System.out.println("[System:] Login was rejected");
			clientEventQueue.add(new LoginRequestEvent());
		}
		
	}
	
	class FileCreatedStrategy extends Strategy {
		
		@Override
		void perform(ClientEvent clientEvent) {
			FileCreatedEvent fileCreatedEvent = (FileCreatedEvent) clientEvent;
			System.out.println("[System:] " + fileCreatedEvent.getName() + " is created");
			try {
				
				if (!localFileSystem.isDirectory(fileCreatedEvent.getName())) {
					serverCommunicationThread.sendMessage(Message.FILE_CHANGED);
					AbstractFileInfo fileInfo = localFileSystem.generateMetadataForFile(fileCreatedEvent.getName());
					localFileSystem.deleteUpperEmptyFolders(fileCreatedEvent.getName());
					serverCommunicationThread.sendObject(fileInfo);
					serverCommunicationThread.sendObject(localFileSystem.getFile(fileCreatedEvent.getName()));
				}
				else {
					localFileSystem.generateMetadataForFolder(fileCreatedEvent.getName());
				}
//				localFileSystemListener.printMetadata();
			} catch (FileSystemException e) {
				try {
					serverCommunicationThread.sendObject(new byte[0]);
				} catch (IOException e1) {
					System.out.println("Something wrong with connection");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			localFileSystem.saveMetadata();
		}
		
	}
	
	class FileModifiedStrategy extends Strategy {
		
		@Override
		void perform(ClientEvent clientEvent) {
			FileModifiedEvent fileModifiedEvent = (FileModifiedEvent) clientEvent;
			System.out.println("[System:] " + fileModifiedEvent.getName() + " is modified");
			try {
				
				if (!localFileSystem.isDirectory(fileModifiedEvent.getName())) {
					serverCommunicationThread.sendMessage(Message.FILE_CHANGED);
					AbstractFileInfo fileInfo = localFileSystem.updateMetadataForFile(fileModifiedEvent.getName());
					serverCommunicationThread.sendObject(fileInfo);
					serverCommunicationThread.sendObject(localFileSystem.getFile(fileModifiedEvent.getName()));
				}
				
//				localFileSystemListener.printMetadata();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
			} catch (MetadataNotFound e) {
			}
			localFileSystem.saveMetadata();
		}
		
	}
	
	class FileRenamedStrategy extends Strategy {
		
		@Override
		void perform(ClientEvent clientEvent) {
			FileRenamedEvent fileRenamedEvent = (FileRenamedEvent) clientEvent;
			System.out.println("[System:] " + fileRenamedEvent.getOldName() + " -> " + fileRenamedEvent.getNewName());
			try {
				
				if (!localFileSystem.isDirectory(fileRenamedEvent.getNewName())) {
					serverCommunicationThread.sendMessage(Message.FILEPATH_CHANGED);
					localFileSystem.updateMetadataForFile(fileRenamedEvent.getOldName(), 
							fileRenamedEvent.getNewName());
					serverCommunicationThread.sendObject(new RenameInfo("/" + fileRenamedEvent.getOldName(), "/" + fileRenamedEvent.getNewName()));
//					serverCommunicationThread.sendObject(localFileSystemListener.getFile(fileDeletedEvent.getName()));
				}
				else {
					serverCommunicationThread.sendMessage(Message.FILEPATH_CHANGED);
					localFileSystem.updateMetadataForFolder(fileRenamedEvent.getOldName(), 
							fileRenamedEvent.getNewName());
					serverCommunicationThread.sendObject(new RenameInfo(fileRenamedEvent.getOldName(), fileRenamedEvent.getNewName()));
					System.out.println(fileRenamedEvent.getOldName());
					System.out.println(fileRenamedEvent.getNewName());
				}
//				localFileSystemListener.printMetadata();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MetadataNotFound e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			localFileSystem.saveMetadata();
		}
		
	}

	class FileDeletedStrategy extends Strategy {
		
		@Override
		void perform(ClientEvent clientEvent) {
			FileDeletedEvent fileDeletedEvent = (FileDeletedEvent) clientEvent;
			System.out.println("[System:] " + fileDeletedEvent.getName() + " is deleted");
			try {
				
				if (!localFileSystem.isDirectory(fileDeletedEvent.getName())) {
					serverCommunicationThread.sendMessage(Message.FILE_DELETED);
					AbstractFileInfo fileInfo = localFileSystem.deleteFileMetadata(fileDeletedEvent.getName());
					serverCommunicationThread.sendObject(fileInfo);
				}
				else {
					localFileSystem.deleteFolderMetadata(fileDeletedEvent.getName());
				}
//				localFileSystemListener.printMetadata();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MetadataNotFound e) {
				System.err.println(e.getMessage());
			}		
			localFileSystem.saveMetadata();
		}
		
	}

    class ServerResponseStrategy extends Strategy {

        @Override
        void perform(ClientEvent clientEvent) {
            System.out.println("[Server response:] " +
                    ((ServerResponseEvent)clientEvent).getServerResponse().getMessage());
        }
    }
    
    class FileChangedOnServerStrategy extends Strategy {

        @Override
        void perform(ClientEvent clientEvent) {
        	FileChangedOnServerEvent fileChangedOnServerEvent = (FileChangedOnServerEvent) clientEvent;
        	AbstractFileInfo metadata = fileChangedOnServerEvent.getMetadata();

        	AbstractFileInfo changedFile = localFileSystem.findFileByName(metadata.getRelativePath());
    		if(changedFile != null) {
				localFileSystem.removeFile(changedFile);
    			localFileSystem.addFile(metadata, fileChangedOnServerEvent.getData());
    		}
    		else {
    			localFileSystem.addFile(metadata, fileChangedOnServerEvent.getData());
    		}
    		localFileSystem.saveMetadata();
        }
    }
    
    class FileDeletedOnServerStrategy extends Strategy {

        @Override
        void perform(ClientEvent clientEvent) {
        	FileDeletedOnServerEvent fileDeletedOnServerEvent = (FileDeletedOnServerEvent) clientEvent;
        	AbstractFileInfo metadata = fileDeletedOnServerEvent.getMetadata();

        	AbstractFileInfo changedFile = localFileSystem.findFileByName(metadata.getRelativePath());
    		if(changedFile != null) {
				localFileSystem.removeFile(changedFile);
    		}
    		else {
    			System.out.println("[System:] Deleted file doesn't exist");
    		}
    		localFileSystem.saveMetadata();
        }
    }
    
    class FilePathChangedOnServerStrategy extends Strategy {

        @Override
        void perform(ClientEvent clientEvent) {
        	FilePathChangedOnServerEvent pathChangedOnServerEvent = (FilePathChangedOnServerEvent) clientEvent;
            AbstractFileInfo meta = localFileSystem.findFileByName(pathChangedOnServerEvent.getRenameInfo().getOldPath());
            if(meta != null) {
                meta.setRelativePath(pathChangedOnServerEvent.getRenameInfo().getNewPath());
                //TODO rename the physical file
            } else {
                System.out.println("[System:] Trying to change the path of the non-existent file");
            }
            localFileSystem.saveMetadata();
        }
    }

	public static void main(String[] args) {
		new ClientController().loop();
	}
	
}
