import java.io.*;

/**
 * 
 */

/**
 * @author samuel
 *
 */
public class ServerThread extends Thread {
	BufferedReader socketReader;
	BufferedWriter socketWriter;
	
	public ServerThread(InputStream inputStream, OutputStream outputStream) {
		socketReader = new BufferedReader(new InputStreamReader(inputStream));
		socketWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
	}
	
	public void run(){
		while (true) {
			String s;
			try {
				while ((s = socketReader.readLine()) != null) {
					System.out.println(s);
					socketWriter.write(s, 0, s.length());
					socketWriter.newLine();
					socketWriter.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
