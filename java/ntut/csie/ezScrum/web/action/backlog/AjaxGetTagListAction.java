package ntut.csie.ezScrum.web.action.backlog;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AjaxGetTagListAction extends Action {
	private static Log log = LogFactory.getLog(AjaxGetTagListAction.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info(" Get Tag List in AjaxGetTagListAction. ");
//		String result = "";
//		try
//		{
//			IProject project = (IProject) SessionManager.getProject(request);
//			
//	    	IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
//	    	ProductBacklogHelper pbHelper = new ProductBacklogHelper(project,session);
//			
//			IIssueTag[] tags = pbHelper.getTagList();
//	    	
//			StringBuilder sb = new StringBuilder();
//			sb.append("<TagList><Result>success</Result>");
//	
//			for(int i = 0; i < tags.length; i++)
//			{
//				sb.append("<IssueTag>");
//				sb.append("<Id>" + tags[i].getTagId() + "</Id>");
//				sb.append("<Name>" + new TranslateSpecialChar().TranslateXMLChar(tags[i].getTagName()) + "</Name>");
//				sb.append("</IssueTag>");
//			}
//			sb.append("</TagList>");
//			result = sb.toString();
//		}
//		catch(Exception e)
//		{
//			result = "<TagList><Result>false</Result></TagList>";
//		}
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		String result = (new ProductBacklogHelper(userSession, project)).getTagListResponseText().toString();
		
		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(result);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}