package ntut.csie.ezScrum.web.action;

import java.io.IOException;
import java.text.ParseException;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import ntut.csie.ezScrum.web.control.FeedbackFileProxy;

public class FeedbackAction  extends Action{

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		FeedbackFileProxy feedbackControl = null;//
		JSONObject feedbackDisplay = new JSONObject();
		try {
			feedbackControl = new FeedbackFileProxy("StartDate.log");
			
			if( request.getMethod().equals("POST") ){
				if(request.getParameter("status")=="false"){feedbackControl.setTimeExpires(false);}
				feedbackControl.writeFromFile();
			}
			
			else if( request.getMethod().equals("GET") ) {
				if(feedbackControl.getCanceled().equals("false"))
				{
					
					feedbackDisplay.put("feedbackDisplay", String.valueOf(feedbackControl.getTimeExpires()));
					response.getWriter().write(feedbackDisplay.toString());
					response.getWriter().close();
				}
				else{
					feedbackDisplay.put("feedbackDisplay","false");
					response.getWriter().write(feedbackDisplay.toString());
				}
			}
			
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( IOException e){
			e.printStackTrace();
		} finally {
			feedbackControl = null;
		}
		
 		return null;
 	}
		
		
}