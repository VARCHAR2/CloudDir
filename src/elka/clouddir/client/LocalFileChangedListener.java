package elka.clouddir.client;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;
import elka.clouddir.client.clientEvents.ClientEvent;
import elka.clouddir.client.clientEvents.FileChanged;
import elka.clouddir.client.clientEvents.FileCreatedEvent;
import elka.clouddir.client.clientEvents.FileDeletedEvent;
import elka.clouddir.client.clientEvents.FileModifiedEvent;
import elka.clouddir.client.clientEvents.FileRenamedEvent;
import elka.clouddir.client.exceptions.MetadataNotFound;
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

	private BlockingQueue<ClientEvent> clientEventQueue;
	private String folderPath = "testFolder";
	
	private List<AbstractFileInfo> metadataList;
	
    /**
     * Creates a WatchService and registers the given directory
     */
	public LocalFileChangedListener(
			BlockingQueue<ClientEvent> clientEventQueue) throws IOException {
		
		this.clientEventQueue = clientEventQueue;
		metadataList = new ArrayList<AbstractFileInfo>();
	
	}

	/**
	 * Listening for the file-system changes
	 */
	@Override
	public void run() {
		
        // watch mask, specify events you care about,
        // or JNotify.FILE_ANY for all events.
        int mask = JNotify.FILE_CREATED
                | JNotify.FILE_DELETED
                | JNotify.FILE_MODIFIED
                | JNotify.FILE_RENAMED;

        // watch subtree?
        boolean watchSubtree = true;

        // add actual watch
        int watchID;
		try {
			watchID = JNotify.addWatch(folderPath, mask, watchSubtree, new FolderListener());
	        // sleep a little, the application will exit if you
	        // don't (watching is asynchronous), depending on your
	        // application, this may not be required
	        Thread.sleep(1000000);

	        // to remove watch the watch
	        boolean res = JNotify.removeWatch(watchID);
	        if (!res) {
	            // invalid watch ID specified.
	        }
		} catch (JNotifyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * Listener for the file system
	 * @author bogdan
	 *
	 */
	class FolderListener implements JNotifyListener {

        public void fileRenamed(int wd, String rootPath, String oldName,
                String newName) {
        	clientEventQueue.add(new FileRenamedEvent(oldName, newName));
        }

        public void fileModified(int wd, String rootPath, String name) {
        	clientEventQueue.add(new FileModifiedEvent(name));
        }

        public void fileDeleted(int wd, String rootPath, String name) {
        	clientEventQueue.add(new FileDeletedEvent(name));
        }

        public void fileCreated(int wd, String rootPath, String name) {
        	clientEventQueue.add(new FileCreatedEvent(name));
        }

    }

	/**
	 * Listing file-system metadata
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public List<AbstractFileInfo> initSystemMetadata() throws NoSuchAlgorithmException, IOException {
		
		File folder = new File(folderPath);
		
		listFilesForFolder(folder);
		
		return metadataList;
	}
	
	/**
	 * Listing files in the folder
	 * @param folder
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	private void listFilesForFolder(final File folder) throws NoSuchAlgorithmException, IOException {
		if (folder.listFiles().length == 0) {
			metadataList.add(generateSharedEmptyFolder(folder));
		}
		else {
		    for (final File fileEntry : folder.listFiles()) {
		        if (fileEntry.isDirectory()) {
		        	listFilesForFolder(fileEntry);
		        } 
		        else {
		        	metadataList.add(generateSharedFileinfo(fileEntry));
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
		String relativePath = folder.getAbsolutePath().substring((new File(folderPath).getAbsolutePath().length()));
		SharedEmptyFolder folderMetadata = new SharedEmptyFolder(relativePath, folder.lastModified(), "bogdan", null);

		return folderMetadata; // TODO implement setting username to a file
	}

	/**
	 * Making up the metadata for the existing file
	 * @param fileEntry
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	private SharedFile generateSharedFileinfo(final File fileEntry) throws NoSuchAlgorithmException, IOException {
		String relativePath = fileEntry.getAbsolutePath().substring((new File(folderPath).getAbsolutePath().length()));
		SharedFile fileMetadata = new SharedFile(relativePath, fileEntry.lastModified(), "bogdan", 
				HashGenerator.sha1(fileEntry), fileEntry.getTotalSpace(), null);

		return fileMetadata; // TODO implement setting username to a file
	}

	/**
	 * Gets metadata ready in map
	 * @param relativePath
	 * @return
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	/*public AbstractFileInfo getMetadata(String relativePath) throws NoSuchAlgorithmException, IOException {
		for (AbstractFileInfo metadata : metadataList) {
			if (metadata.getRelativePath().equals(relativePath)) {
				return metadata;
			}
		}
		throw new RuntimeException("no metadata, when getting it out");
	}*/
	
	/**
	 * Returns file in array of bytes
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public Serializable getFile(String name) throws IOException {
	    Path path = Paths.get(getRelativeProgramPath(name));
	    return Files.readAllBytes(path);
	}
	
	private String getRelativeProgramPath(String path) {
		return folderPath + File.separator + path;
	}

	public void addMetadata(AbstractFileInfo addedMatadata) {
		metadataList.add(addedMatadata);
	}
	
	public void printMetadata() {
		System.out.println();
		int i = 0;
		for (AbstractFileInfo metadata : metadataList) {
			i++;
			System.out.println(i + "   " + metadata.getRelativePath());
		}
	}

	public boolean isDirectory(String relativePath) {
//		System.out.println(relativePath);
		relativePath = File.separator + relativePath; 
		File file = new File(getRelativeProgramPath(relativePath));
		if (file.exists()) {
			return file.isDirectory();
			
		}
		else {
			for (AbstractFileInfo metadata : metadataList) {
				if (metadata.getRelativePath().equals(relativePath)) {
					if (metadata instanceof SharedEmptyFolder) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public AbstractFileInfo generateMetadataForFile(String filename) throws NoSuchAlgorithmException, IOException {
		return generateSharedFileinfo(new File(filename));
	}
	
	public AbstractFileInfo generateMetadataForFolder(String filename) {
//		System.out.println(filename);
		AbstractFileInfo emptyFolder = generateSharedEmptyFolder(new File(getRelativeProgramPath(filename)));
//		System.out.println(emptyFolder);
		metadataList.add(emptyFolder);
		return emptyFolder;
	}

	public AbstractFileInfo deleteFileMetadata(String relativePath) throws MetadataNotFound {
		for (AbstractFileInfo metadata : metadataList) {
			if (metadata.getRelativePath().equals(File.separator + relativePath)) {
				metadataList.remove(metadata);
				return metadata;
			}
		}
		throw new MetadataNotFound(relativePath);
	}

	public AbstractFileInfo deleteFolderMetadata(String relativePath) throws MetadataNotFound {
//		System.out.println("Main: " + relativePath);
		for (AbstractFileInfo metadata : metadataList) {
//			System.out.println(metadata.getRelativePath());
			if (metadata.getRelativePath().startsWith(File.separator + relativePath)) {
				metadataList.remove(metadata);
//				return metadata;
			}
		}
		throw new MetadataNotFound(relativePath);
	}

	public AbstractFileInfo updateMetadataForFile(String relativePath) throws NoSuchAlgorithmException, IOException, MetadataNotFound {
		System.out.println("Main: " + relativePath);
		relativePath = File.separator + relativePath;
		for (AbstractFileInfo metadata : metadataList) {
//			System.out.println(metadata.getRelativePath());
			if (metadata.getRelativePath().equals(relativePath)) {
				File file = new File(getRelativeProgramPath(relativePath));
//				System.out.println(getRelativeProgramPath(relativePath));
				((SharedFile)metadata).setMd5sum(HashGenerator.sha1(file));
				((SharedFile)metadata).setSize(file.getTotalSpace());
				return metadata;
			}
		}
		throw new MetadataNotFound(relativePath);
	}

	public AbstractFileInfo updateMetadataForFile(String oldRelativePath, String newRelativePath) throws MetadataNotFound {
//		System.out.println("Old: " + oldRelativePath + " New: " + newRelativePath);
		oldRelativePath = File.separator + oldRelativePath;
		newRelativePath = File.separator + newRelativePath;
		for (AbstractFileInfo metadata : metadataList) {
			if (metadata.getRelativePath().equals(oldRelativePath)) {
				metadata.setRelativePath(newRelativePath);
//				System.out.println(metadata);
				return metadata;
			}
			if (metadata.getRelativePath().startsWith(oldRelativePath + File.separator)) {
				metadata.setRelativePath(newRelativePath + metadata.getRelativePath().substring(newRelativePath.length()));
//				System.out.println(metadata);
				return metadata;
			}
		}
		throw new MetadataNotFound(oldRelativePath);	
	}

	
}