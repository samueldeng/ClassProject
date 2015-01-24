import java.net.*;

/**
 * 
 */

/**
 * @author samuel
 * 
 */
public class CalculatorServer {
	public static void main(String[] args) {
		// create socket
		final int port = 34567;
		try{
			ServerSocket serverSocketDaemon = new ServerSocket(port);
			while(true){
				Socket serverSocket = serverSocketDaemon.accept();
				ServerThread serThread = new ServerThread(serverSocket.getInputStream(), serverSocket.getOutputStream());
				serThread.start();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
