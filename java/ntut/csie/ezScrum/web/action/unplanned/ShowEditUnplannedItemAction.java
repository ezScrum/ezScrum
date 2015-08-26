package ntut.csie.ezScrum.web.action.unplanned;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.UnplannedObject;
import ntut.csie.ezScrum.web.helper.UnplannedItemHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowEditUnplannedItemAction extends Action {
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);
		long unplannedId = Long.parseLong(request.getParameter("issueID"));
		
		// Get unplanned item
		UnplannedItemHelper unplannedHelper = new UnplannedItemHelper(project);
		UnplannedObject unplanned = unplannedHelper.getUnplanned(unplannedId);
		
		// return result of unplanned item in XML
		StringBuilder result = new StringBuilder();
		result.append("<EditUnplannedItem><UnplannedItem>")
		  .append("<Id>").append(unplanned.getId()).append("</Id>")
		  .append("<Link></Link>")
		  .append("<Name>").append(TranslateSpecialChar.TranslateXMLChar(unplanned.getName())).append("</Name>")
		  .append("<SprintID>").append(unplanned.getSprintId()).append("</SprintID>")
		  .append("<Estimate>").append(unplanned.getEstimate()).append("</Estimate>")
		  .append("<Status>").append(unplanned.getStatusString()).append("</Status>")
		  .append("<ActualHour>").append(unplanned.getActual()).append("</ActualHour>")
		  .append("<Handler>").append(unplanned.getHandlerName()).append("</Handler>")
		  .append("<Partners>").append(TranslateSpecialChar.TranslateXMLChar(unplanned.getPartnersUsername())).append("</Partners>")
		  .append("<Notes>").append(TranslateSpecialChar.TranslateXMLChar(unplanned.getNotes())).append("</Notes>")
		  .append("</UnplannedItem></EditUnplannedItem>");
		
		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(result.toString());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}

