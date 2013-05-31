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
import elka.clouddir.server.model.User;
import elka.clouddir.shared.HashGenerator;

public class LocalFileChangedListener implements Runnable {

	private BlockingQueue<ClientEvent> fileSystemBlockingQueue;
	private String folderPath = "testFolder";
	private List<AbstractFileInfo> listOfFiles;
	
	
	public LocalFileChangedListener(
			BlockingQueue<ClientEvent> clientEventQueue) {
		
		this.fileSystemBlockingQueue = clientEventQueue;
		
	}

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

	public List<AbstractFileInfo> getSystemMetadata() throws NoSuchAlgorithmException, IOException {
		
		File folder = new File(folderPath);
		
		listOfFiles = new ArrayList<AbstractFileInfo>();
		listFilesForFolder(folder);
		
		return /*(AbstractFileInfo[]) */listOfFiles;
	}
	
	private void listFilesForFolder(final File folder) throws NoSuchAlgorithmException, IOException {
		if (folder.listFiles().length == 0) {
			listOfFiles.add(generateSharedEmptyFolder(folder));
		}
		else {
		    for (final File fileEntry : folder.listFiles()) {
		        if (fileEntry.isDirectory()) {
		//	        	listOfFiles.add(generateSharedEmptyFolder(fileEntry));
		        	listFilesForFolder(fileEntry);
		        } 
		        else {
		        	listOfFiles.add(generateSharedFileinfo(fileEntry));
		        }
		    }
		}
	}

	private AbstractFileInfo generateSharedEmptyFolder(File folder) {
		return new SharedEmptyFolder(folder.getAbsolutePath(), folder.lastModified(), new User("bogdan", false, null, "10101010"));
	}

	private SharedFile generateSharedFileinfo(final File fileEntry) throws NoSuchAlgorithmException, IOException {
		return new SharedFile(fileEntry.getAbsolutePath(), fileEntry.lastModified(), new User("bogdan", false, null, "10101010"), HashGenerator.sha1(fileEntry), fileEntry.getTotalSpace());
	}
}