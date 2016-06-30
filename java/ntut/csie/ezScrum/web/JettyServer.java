package ntut.csie.ezScrum.web;

import java.io.FileInputStream;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.mortbay.jetty.Server;
import org.mortbay.xml.XmlConfiguration;

public class JettyServer {
	private static Server server;
	private static boolean restart = true;
	private static Logger logger;
	
	public static void main(String[] args) throws Exception {
		System.setProperty("ntut.csie.ezScrum.container", "Jetty");
		
		server = new Server();
		logger = Logger.getLogger(JettyServer.class.getName());
		FileHandler handler = new FileHandler("JettyServer.log");
		logger.addHandler(handler);
		
		try {
			while (restart == true) {
				restart = false;
				XmlConfiguration configuration = new XmlConfiguration(
						new FileInputStream("JettyServer.xml"));
				configuration.configure(server);
				
/*
 * comment by liullen
 * 因為socket監聽固定的port，為了讓 jetty能夠執行多個instance，所以把重新啟動的功能註解
 * 
				// 開啟一個localhost的Socket專門接收Stop的訊息
				Thread monitor = new MonitorThread();
				monitor.start();
*/

				server.start();
				server.join();
				
			}
		} catch (Exception e) {
			logger.severe(e.getMessage());
		}
	}
}
