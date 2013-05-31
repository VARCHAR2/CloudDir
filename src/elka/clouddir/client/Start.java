package elka.clouddir.client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class Start {

	public static void main(String[] args) {
		BlockingQueue<FileSystemChangedEvent> fileSystemBlockingQueue = new LinkedBlockingQueue<FileSystemChangedEvent>();
		LocalFileChangedListener localFileSystemListener = new LocalFileChangedListener(fileSystemBlockingQueue);
		ClientController clientController = new ClientController(fileSystemBlockingQueue, localFileSystemListener);
		
		Thread localFileSystemThread = new Thread(localFileSystemListener);
		localFileSystemThread.start();
		
	}
	
}
