package ntut.csie.ezScrum.web.support;

import java.util.Iterator;

import org.dom4j.*;

public class ParseXMLString {
	Document doc;
	Element ticket = null;
	public ParseXMLString(String xmlSreing) throws DocumentException{
		StringBuilder sb = new StringBuilder();
		sb.append("<Histories><HistoryList><History");
		sb.append(" id=\"" + "0" + "\"");
		sb.append(" date_modified=\"" + "1" + "\"");
		sb.append(" field_name=\"" + "2" + "\"");
		sb.append(" old_value=\"" + "3" + "\"");
		sb.append(" new_value=\"" + "4" + "\"");
		sb.append(" type=\"" + "5" + "\"/>");
		sb.append("</HistoryList>");	
		sb.append("</Histories>");
//		doc = DocumentHelper.parseText(sb.toString());
//		Element root2 = doc.getRootElement();
//		Iterator tickets2 = null;
//		for (tickets2 = root2.element("HistoryList").elementIterator(); tickets2.hasNext();) {
//		    ticket = (Element) tickets2.next();
//		    System.out.print(ticket.attributeValue("id")+"  ");
//		    System.out.print(ticket.attributeValue("date_modified")+"  ");
//		    System.out.println(ticket.attributeValue("field_name"));
//		    System.out.println(ticket.attributeValue("old_value"));
//		    System.out.println(ticket.attributeValue("new_value"));
//		    System.out.println(ticket.attributeValue("type"));
//		    
//	
//		    
//		   }
		
		doc = DocumentHelper.parseText(xmlSreing.replaceAll("\"\"", "\"\\\""));
		Element root = doc.getRootElement();
		@SuppressWarnings("rawtypes")
		Iterator tickets = root.element("HistoryList").elementIterator();
		ticket = (Element) tickets.next();		
	}

	public String getCharacterDataFromElement(String key) {
		try {
			return ticket.attributeValue(key);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
