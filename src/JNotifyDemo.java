import java.awt.Event;
import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyListener;

/**
 *
 * @author Jigar
 */
public class JNotifyDemo {

	BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<String>();
	
    public void sample() throws Exception {

    	new Thread(new OutputThread()).start();
    	
        // path to watch
//        String path = System.getProperty("user.home");
    	String path = new String("testFolder");
        // watch mask, specify events you care about,
        // or JNotify.FILE_ANY for all events.
        int mask = JNotify.FILE_CREATED
                | JNotify.FILE_DELETED
                | JNotify.FILE_MODIFIED
                | JNotify.FILE_RENAMED;

        // watch subtree?
        boolean watchSubtree = true;

        // add actual watch
        int watchID = JNotify.addWatch(path, mask, watchSubtree, new Listener());

        // sleep a little, the application will exit if you
        // don't (watching is asynchronous), depending on your
        // application, this may not be required
        Thread.sleep(1000000);

        // to remove watch the watch
        boolean res = JNotify.removeWatch(watchID);
        if (!res) {
            // invalid watch ID specified.
        }
    }

    class Listener implements JNotifyListener {

        public void fileRenamed(int wd, String rootPath, String oldName,
                String newName) {
            print("renamed " + rootPath + " : " + oldName + " -> " + newName);
        }

        public void fileModified(int wd, String rootPath, String name) {
            File file = new File(rootPath + File.separator + name);
//            System.out.println("file: " + file.toPath());
            if (!file.isDirectory()) {
            	print("modified " + rootPath + " : " + name);
            }
        }

        public void fileDeleted(int wd, String rootPath, String name) {
            print("deleted " + rootPath + " : " + name);
        }

        public void fileCreated(int wd, String rootPath, String name) {
            print("created " + rootPath + " : " + name);
        }

        void print(String msg) {
        	if (!blockingQueue.contains(msg)) {
        		blockingQueue.add(new String(msg));
        	}
//            System.err.println(msg);
        }
    }
    
    class OutputThread implements Runnable {
    	
    	@Override
    	public void run() {
    		while (true) {
				try {
					String string = blockingQueue.take();
					System.out.println(string);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    	
    }
    
    public static void main(String[] args) throws Exception {
        new JNotifyDemo().sample();
    }
}