package elka.clouddir.shared;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;


public class HashGenerator{
	
	
	/**
	 * @param file
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * Generates sha1 hash of a file and returns it in string of hexes.
	 */
	public static String sha1(final File file) throws NoSuchAlgorithmException, IOException {
	    final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");

	    try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
	      final byte[] buffer = new byte[1024];
	      for (int read = 0; (read = is.read(buffer)) != -1;) {
	        messageDigest.update(buffer, 0, read);
	      }
	    }

	    // Convert the byte to hex format
	    try (Formatter formatter = new Formatter()) {
	      for (final byte b : messageDigest.digest()) {
	        formatter.format("%02x", b);
	      }
	      return formatter.toString();
	    }
	  }
}
