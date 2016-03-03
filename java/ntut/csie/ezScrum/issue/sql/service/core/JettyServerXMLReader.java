package ntut.csie.ezScrum.issue.sql.service.core;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class JettyServerXMLReader {
	public static String readHostWithPort() {
		String jettyHost = "localhost";
		String jettyPort = "8080";
		try {
			Configuration mConfig = new Configuration();
	        File inputFile = new File(mConfig.getBaseDirPath() + File.separator + "JettyServer.xml");
	        DocumentBuilderFactory dbFactory 
	           = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(inputFile);
	        doc.getDocumentElement().normalize();
	        NodeList callNodes = doc.getElementsByTagName("Call");
	        Element callElement = (Element) callNodes.item(0);
	        NodeList argNodes = callElement.getElementsByTagName("Arg");
	        Element argElement = (Element) argNodes.item(0);
	        NodeList newNodes = argElement.getElementsByTagName("New");
	        Element newElement = (Element) newNodes.item(0);
	        NodeList setNodes = newElement.getElementsByTagName("Set");
	        Element firstSetElement = (Element) setNodes.item(0);
	        NodeList systemPropertyNodesInFirstSetElement = firstSetElement.getElementsByTagName("SystemProperty");
	        Element firstSystemPropertyElement = (Element) systemPropertyNodesInFirstSetElement.item(0);
	        jettyHost = firstSystemPropertyElement.getAttribute("default");
	        Element secondSetElement = (Element) setNodes.item(1);
	        NodeList systemPropertyNodesInSecondSetElement = secondSetElement.getElementsByTagName("SystemProperty");
	        Element secondSystemPropertyElement = (Element) systemPropertyNodesInSecondSetElement.item(0);
	        jettyPort = secondSystemPropertyElement.getAttribute("default");
	     } catch (Exception e) {
	        e.printStackTrace();
	     }
		return jettyHost + ":" + jettyPort;
	}
}
