import java.io.*;
import java.util.regex.*;
public class UnicodeTest {
	public static void main(String[] args) {
		try {
			Reader reader = new FileReader("test.txt");
			StringBuilder sb = new StringBuilder();
			int c;
			while( (c = reader.read()) != -1) sb.append((char) c);
				
			String original = sb.toString();
			String replaced = original.replaceAll("[\\x00-\\x1F&&[^\\n\\t\\r]]+", "");
			System.out.println(original);
			System.out.println(original.length());
			System.out.println(replaced);
			System.out.println(replaced.length());
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}