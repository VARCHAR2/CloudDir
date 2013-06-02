package elka.clouddir.shared;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class for managing file I/O
 * @author Lukasz Pielaszek
 */
public class FileControler {
	
	
	
	/**
	 * @param path
	 * @param data
	 * Input format doc/case-study/projectX/file.pdf
	 */
	public static void writeFile(String path, byte[] data){
		try {
			File f = new File(path);
			File p = new File(f.getParent());
			if(!p.isDirectory()){
				p.mkdirs();
			}
			FileOutputStream out = new FileOutputStream(f);
			out.write(data);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteFile(String path){
		File f = new File(path);
		if(f.isFile() && f.canWrite()){
			f.delete();
		}
		else{
			System.out.println("File cannot be accessed!");
		}
	}
	
	public static byte[] loadFile(String path){
		File f = new File(path);
		byte[] data = null;
		
		try {
			InputStream in = new FileInputStream(f);
			int size = in.available();
			data = new byte[size];
			
			for(int i = 0; i < size; i++){
				data[i] = (byte) in.read();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
}
