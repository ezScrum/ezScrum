package ntut.csie.ezScrum.web.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


public class CheckezScrumUpdate extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
						HttpServletRequest request, HttpServletResponse response) {

		String target = System.getProperty("System_UpdateURL").toString();
		
		URL url = null;
		StringBuilder result = new StringBuilder();
		try {
			url = new URL(target);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			
			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
			    result.append(line);
			}
			rd.close();
			
		} catch (MalformedURLException e) {
			result = new StringBuilder("");
			
			e.printStackTrace();
			System.out.println("class: CheckezScrumUpdate, method: execute, MalformedURLException exception: " + e.toString());				
		} catch (IOException e) {
			result = new StringBuilder("");
			
			e.printStackTrace();
			System.out.println("class: CheckezScrumUpdate, method: execute, IOException exception: " + e.toString());
		}
		
		try {
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().write(result.toString());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("class: CheckezScrumUpdate, method: execute, response IOException exception: " + e.toString());
		}
		
		return null;
	}
}