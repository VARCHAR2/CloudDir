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
import elka.clouddir.client.clientEvents.LoginAcceptedEvent;
import elka.clouddir.client.clientEvents.LoginRejectedEvent;
import elka.clouddir.client.clientEvents.LoginRequestEvent;
import elka.clouddir.server.model.AbstractFileInfo;
import elka.clouddir.shared.FilesMetadata;
import elka.clouddir.shared.LoginInfo;
import elka.clouddir.shared.Message;

/**
 * Kontroler klienta
 */
public class ClientController {

	/**
	 * @param args
	 */
	
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

	private void initStrategyMap() {
		strategyMap = new HashMap<Class<? extends ClientEvent>, ClientController.Strategy>();
		
		strategyMap.put(LoginRequestEvent.class, new LoginRequestStrategy());
		strategyMap.put(LoginAcceptedEvent.class, new LoginAcceptedStrategy());
		strategyMap.put(LoginRejectedEvent.class, new LoginRejectedStrategy());
		
	}

	public void loop() {

		clientEventQueue.add(new LoginRequestEvent());
		
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
	
	abstract class Strategy {
		
		abstract void perform(ClientEvent clientEvent);
		
	}
	
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
	
	class LoginRejectedStrategy extends Strategy {
		
		@Override
		void perform(ClientEvent clientEvent) {
			System.out.println("Login was rejected");
			clientEventQueue.add(new LoginRequestEvent());
		}
		
	}
	
	public static void main(String[] args) {
		new ClientController().loop();
	}
	
}
