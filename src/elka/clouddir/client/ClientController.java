package elka.clouddir.client;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import elka.clouddir.server.model.AbstractFileInfo;

public class ClientController {

	/**
	 * @param args
	 */
	
	private final BlockingQueue<FileSystemChangedEvent> fileSystemBlockingQueue;
	private LocalFileChangedListener localFileSystemListener;
	private static Path dir = Paths.get("testFolder");
	
	public ClientController(BlockingQueue<FileSystemChangedEvent> fileSystemBlockingQueue, LocalFileChangedListener localFileChangedListener) {
		
		this.fileSystemBlockingQueue = fileSystemBlockingQueue;
		this.localFileSystemListener = localFileChangedListener;
		
		System.out.println("Abstract signing in");

		try {
			List<AbstractFileInfo> metadataArray = localFileSystemListener.getSystemMetadata();
			for (AbstractFileInfo abstractFileInfo : metadataArray) {
				System.out.println(abstractFileInfo.toString());
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
