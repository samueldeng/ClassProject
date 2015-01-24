/**
 * 
 */

/**
 * @author samuel
 *
 */
import java.io.*;
import java.net.*;

public class LogSynchronizer extends Thread {
	private final String ServerAdress = "localhost";
	private final int ServerPort = 34567;
	private Socket sock;
	LogRecvThread logRecv;

	public LogSynchronizer() {
		try {
			sock = new Socket(ServerAdress, ServerPort);
			logRecv = new LogRecvThread(sock.getInputStream());
			logRecv.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendLog(String exp, String res) {
		try {
			String equation = exp + "=" + res;
			// BufferedWriter sockWriter = new BufferedWriter(new
			// OutputStreamWriter(sock.getOutputStream()));
			// OutputStreamWriter sockWriter = new
			// OutputStreamWriter(sock.getOutputStream());
			PrintWriter sockWriter = new PrintWriter(sock.getOutputStream());
			sockWriter.println(equation);
			sockWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String recvLog() {
		return logRecv.getExpLog();
	}

	public class LogRecvThread extends Thread {
		private String expLog = "";
		private BufferedReader sockReader;

		public LogRecvThread(InputStream in) {
			sockReader = new BufferedReader(new InputStreamReader(in));
		}

		public void run() {
			String s;
			try {
				while ((s = sockReader.readLine()) != null) {
					expLog += s + "\n";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public String getExpLog() {
			return expLog;
		}
	}
}
