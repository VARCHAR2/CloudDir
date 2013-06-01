package elka.clouddir.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import elka.clouddir.client.clientEvents.ClientEvent;
import elka.clouddir.server.model.AbstractFileInfo;
import elka.clouddir.server.model.SharedEmptyFolder;
import elka.clouddir.server.model.SharedFile;
import elka.clouddir.shared.HashGenerator;

/**
 * That class handles program communication with file system.
 * has it's own thread for listening to the changes
 * @author bogdan
 *
 */
public class LocalFileChangedListener implements Runnable {

	private BlockingQueue<ClientEvent> fileSystemBlockingQueue;
	private String folderPath = "testFolder";
	private List<AbstractFileInfo> listOfFiles;
	
	
	public LocalFileChangedListener(
			BlockingQueue<ClientEvent> clientEventQueue) {
		
		this.fileSystemBlockingQueue = clientEventQueue;
		
	}

	/**
	 * Listening for the file-system changes
	 */
	@Override
	public void run() {
		
		try (WatchService service = FileSystems.getDefault().newWatchService()) {
			Map<WatchKey, Path> keyMap = new HashMap<>();
			Path path = Paths.get(folderPath);
			keyMap.put(path.register(service, 
					StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE,
					StandardWatchEventKinds.ENTRY_MODIFY),
					path);
			
			WatchKey watchKey;
			
			do {
				watchKey = service.take();
				Path eventDir = keyMap.get(watchKey);
				
				for (WatchEvent<?> event : watchKey.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();
					Path eventPath = (Path)event.context();
					System.out.println(eventDir + ": " + kind + ": " + eventPath);
				}
				
			} while (watchKey.reset());
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	/**
	 * Listing file-system metadata
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public List<AbstractFileInfo> getSystemMetadata() throws NoSuchAlgorithmException, IOException {
		
		File folder = new File(folderPath);
		
		listOfFiles = new ArrayList<AbstractFileInfo>();
		listFilesForFolder(folder);
		
		return listOfFiles;
	}
	
	/**
	 * Listing files in the folder
	 * @param folder
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	private void listFilesForFolder(final File folder) throws NoSuchAlgorithmException, IOException {
		if (folder.listFiles().length == 0) {
			listOfFiles.add(generateSharedEmptyFolder(folder));
		}
		else {
		    for (final File fileEntry : folder.listFiles()) {
		        if (fileEntry.isDirectory()) {
		        	listOfFiles.add(generateSharedEmptyFolder(fileEntry));
		        	listFilesForFolder(fileEntry);
		        } 
		        else {
		        	listOfFiles.add(generateSharedFileinfo(fileEntry));
		        }
		    }
		}
	}

	/**
	 * Making up the metadata for the empty folder
	 * @param folder
	 * @return
	 */
	private AbstractFileInfo generateSharedEmptyFolder(File folder) {
		return new SharedEmptyFolder(folder.getAbsolutePath(), folder.lastModified(), "bogdan"); // TODO implement setting username to a file
	}

	/**
	 * Making up the metadata for the existing file
	 * @param fileEntry
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	private SharedFile generateSharedFileinfo(final File fileEntry) throws NoSuchAlgorithmException, IOException {
		return new SharedFile(fileEntry.getAbsolutePath(), fileEntry.lastModified(), "bogdan", 
				HashGenerator.sha1(fileEntry), fileEntry.getTotalSpace()); // TODO implement setting username to a file
	}
}