package elka.clouddir.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import elka.clouddir.client.clientEvents.ClientEvent;
import elka.clouddir.client.clientEvents.FileCreatedEvent;
import elka.clouddir.client.clientEvents.FileDeletedEvent;
import elka.clouddir.client.clientEvents.FileModifiedEvent;
import elka.clouddir.client.clientEvents.FileRenamedEvent;
import elka.clouddir.client.clientEvents.LoginAcceptedEvent;
import elka.clouddir.client.clientEvents.LoginRejectedEvent;
import elka.clouddir.client.clientEvents.LoginRequestEvent;
import elka.clouddir.server.model.AbstractFileInfo;
import elka.clouddir.shared.FilesMetadata;
import elka.clouddir.shared.LoginInfo;
import elka.clouddir.shared.Message;

/**
 * Controller of the client
 * @author bogdan
 */
public class ClientController {

	private final BlockingQueue<ClientEvent> clientEventQueue;
	
	private LocalFileChangedListener localFileSystemListener;
	private ServerCommunicationThread serverCommunicationThread;
	
	private Map<Class<? extends ClientEvent>, Strategy> strategyMap;
	
	public ClientController() {
		
		clientEventQueue = new LinkedBlockingQueue<ClientEvent>();

		try {
			initStrategyMap();
			
			serverCommunicationThread = new ServerCommunicationThread(clientEventQueue);
			serverCommunicationThread.start();

			localFileSystemListener = new LocalFileChangedListener(clientEventQueue);
			Thread localFileSystemThread = new Thread(localFileSystemListener);
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
		
		strategyMap.put(FileCreatedEvent.class, new LoginRejectedStrategy());
		strategyMap.put(FileModifiedEvent.class, new LoginRejectedStrategy());
		strategyMap.put(FileRenamedEvent.class, new LoginRejectedStrategy());
		strategyMap.put(FileDeletedEvent.class, new LoginRejectedStrategy());
		
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
		 
			    System.out.println("Password: ");
			    String password = bufferRead.readLine();
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
				List<AbstractFileInfo> metadataArray = localFileSystemListener.getSystemMetadata();
				FilesMetadata filesMetadata = new FilesMetadata(metadataArray.toArray(new AbstractFileInfo[0]));
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
			System.out.println("Login was rejected");
			clientEventQueue.add(new LoginRequestEvent());
		}
		
	}
	
	class FileCreatedStrategy extends Strategy {
		
		@Override
		void perform(ClientEvent clientEvent) {
			// TODO implement sending info about the created file			
		}
		
	}
	
	class FileModifiedStrategy extends Strategy {
		
		@Override
		void perform(ClientEvent clientEvent) {
			// TODO implement sending info about the modified file			
		}
		
	}
	
	class FileRenamedStrategy extends Strategy {
		
		@Override
		void perform(ClientEvent clientEvent) {
			FileRenamedEvent fileRenamedEvent = (FileRenamedEvent) clientEvent;
			try {
				serverCommunicationThread.sendMessage(Message.FILE_DELETED);
				// TODO implement sending info about the renamed file
//				serverCommunicationThread.sendObject(fileRenamedEvent.getOldName());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
	}

	class FileDeletedStrategy extends Strategy {
		
		@Override
		void perform(ClientEvent clientEvent) {
			FileDeletedEvent fileDeletedEvent = (FileDeletedEvent) clientEvent;
			try {
				serverCommunicationThread.sendMessage(Message.FILE_DELETED);
				serverCommunicationThread.sendObject(fileDeletedEvent.getName());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	public static void main(String[] args) {
		new ClientController().loop();
	}
	
}
