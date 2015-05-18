package ntut.csie.ezScrum.web.action.plan;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.UnplannedItemHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowEditUnplannedItemAction extends Action {
	// --------------------------------------------------------- Methods
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		long issueID = Long.parseLong(request.getParameter("issueID"));
		
		UnplannedItemHelper helper=new UnplannedItemHelper(project,session);
		IIssue unplannedItem = helper.getIssue(issueID);
		
		StringBuilder sb = new StringBuilder();
		TranslateSpecialChar adapter= new TranslateSpecialChar();
		
		sb.append("<EditUnplannedItem><UnplannedItem>")
		  .append("<Id>").append(unplannedItem.getIssueID()).append("</Id>")
		  .append("<Link>").append(adapter.TranslateXMLChar(unplannedItem.getIssueLink())).append("</Link>")
		  .append("<Name>").append(adapter.TranslateXMLChar(unplannedItem.getSummary())).append("</Name>")
		  .append("<SprintID>").append(unplannedItem.getSprintID()).append("</SprintID>")
		  .append("<Estimate>").append(unplannedItem.getEstimated()).append("</Estimate>")
		  .append("<Status>").append(unplannedItem.getStatus()).append("</Status>")
		  .append("<ActualHour>").append(unplannedItem.getActualHour()).append("</ActualHour>")
		  .append("<Handler>").append(adapter.TranslateXMLChar(unplannedItem.getAssignto())).append("</Handler>")
		  .append("<Partners>").append(adapter.TranslateXMLChar(unplannedItem.getPartners())).append("</Partners>")
		  .append("<Notes>").append(adapter.TranslateXMLChar(unplannedItem.getNotes())).append("</Notes>")
		  .append("</UnplannedItem></EditUnplannedItem>");
		
		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(sb.toString());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}

