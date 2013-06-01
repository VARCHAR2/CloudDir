package elka.clouddir.client;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
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
	private List<AbstractFileInfo> listOfFiles;
	
    /**
     * Creates a WatchService and registers the given directory
     */
	public LocalFileChangedListener(
			BlockingQueue<ClientEvent> clientEventQueue) throws IOException {
		
		this.clientEventQueue = clientEventQueue;
	
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