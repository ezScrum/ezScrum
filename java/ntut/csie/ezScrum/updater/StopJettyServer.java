package ntut.csie.ezScrum.updater;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class StopJettyServer {
	public static void stopServer() throws UnknownHostException, IOException  {
		Socket s = new Socket(InetAddress.getByName("127.0.0.1"), 8079);
		OutputStream out = s.getOutputStream();
		out.write(("Restart Server by StopJetty").getBytes());
		out.flush();
		s.close();
	}
}
