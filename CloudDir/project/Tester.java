import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Autko a = new Autko(200, 200000, "babe");
		Autko b = null;
		
		try (
			FileOutputStream fileOut = new FileOutputStream("aut.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
				) 
		{
			out.writeObject(a);
			
		} catch (IOException e) {
		}
		
		System.out.println("Zapisano do pliku");
		
		try(
			FileInputStream fileIn = new FileInputStream("aut.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);	
				) 
		{
			b = (Autko)in.readObject();
			System.out.println("odczyt");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(b);
		
	}

}
