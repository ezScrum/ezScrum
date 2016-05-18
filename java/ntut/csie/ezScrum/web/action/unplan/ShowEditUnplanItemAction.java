package ntut.csie.ezScrum.web.action.unplan;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.UnplanObject;
import ntut.csie.ezScrum.web.helper.UnplanItemHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

public class ShowEditUnplanItemAction extends Action {
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		// get session info
		ProjectObject project = SessionManager.getProject(request);
		long serialUnplanId = Long.parseLong(request.getParameter("issueID"));
		
		// Get unplan item
		UnplanItemHelper unplanHelper = new UnplanItemHelper(project);
		UnplanObject unplan = unplanHelper.getUnplan(project.getId(), serialUnplanId);
		
		SprintObject sprint = SprintObject.get(unplan.getSprintId());
		// return result of unplan item in XML
		StringBuilder result = new StringBuilder();
		result.append("<EditUnplannedItem><UnplannedItem>")
		  .append("<Id>").append(unplan.getSerialId()).append("</Id>")
		  .append("<Link></Link>")
		  .append("<Name>").append(TranslateSpecialChar.TranslateXMLChar(unplan.getName())).append("</Name>")
		  .append("<SprintID>").append(sprint.getSerialId()).append("</SprintID>")
		  .append("<Estimate>").append(unplan.getEstimate()).append("</Estimate>")
		  .append("<Status>").append(unplan.getStatusString()).append("</Status>")
		  .append("<ActualHour>").append(unplan.getActual()).append("</ActualHour>")
		  .append("<Handler>").append(unplan.getHandlerName()).append("</Handler>")
		  .append("<Partners>").append(TranslateSpecialChar.TranslateXMLChar(unplan.getPartnersUsername())).append("</Partners>")
		  .append("<Notes>").append(TranslateSpecialChar.TranslateXMLChar(unplan.getNotes())).append("</Notes>")
		  .append("</UnplannedItem></EditUnplannedItem>");
		
		PrintWriter printWriter = null;
		try {
			response.setContentType("text/xml; charset=utf-8");
			printWriter = response.getWriter();
			printWriter.write(result.toString());
			printWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		
		return null;
	}
}

