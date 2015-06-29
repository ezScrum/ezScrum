package ntut.csie.ezScrum.web.action.plan;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.UnplannedItemHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.core.util.DateUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class EditUnplannedItemAction extends PermissionAction {

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessUnplannedItem();
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {

		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		// get parameter info
		long id = Long.parseLong(request.getParameter("issueID"));

		TranslateSpecialChar translateSpecialChar = new TranslateSpecialChar();

		String name = request.getParameter("Name");
		String status = request.getParameter("Status");
		String SprintName = request.getParameter("SprintID");
		String sprintID = SprintName.substring(SprintName.indexOf("#") + 1);
		String estimate = request.getParameter("Estimate");
		String handler = request.getParameter("Handler");
		String partners = request.getParameter("Partners");
		String actualHour = request.getParameter("ActualHour");
		String notes = request.getParameter("Notes");
		String specificTime = request.getParameter("SpecificTime");

		if (specificTime.length() == 0) {
			specificTime = DateUtil.format(new Date(), DateUtil._16DIGIT_DATE_TIME_MYSQL);
		}
		
		UnplannedItemHelper helper = new UnplannedItemHelper(project, session);
		helper.modifyUnplannedItemIssue(id, name, handler, status, partners, estimate, actualHour, notes, sprintID,
		        DateUtil.dayFillter(specificTime, DateUtil._16DIGIT_DATE_TIME_MYSQL));

		// return result of unplanned item in XML
		IIssue unplannedItem = helper.getIssue(id);

		StringBuilder result = new StringBuilder();
		result.append("<EditUnplannedItem><Result>success</Result><UnplannedItem>")
			  .append("<Id>").append(unplannedItem.getIssueID()).append("</Id>")
			  .append("<Link>").append(translateSpecialChar.TranslateXMLChar(unplannedItem.getIssueLink())).append("</Link>")
			  .append("<Name>").append(translateSpecialChar.TranslateXMLChar(unplannedItem.getSummary())).append("</Name>")
			  .append("<SprintID>").append(unplannedItem.getSprintID()).append("</SprintID>")
			  .append("<Estimate>").append(unplannedItem.getEstimated()).append("</Estimate>")
			  .append("<Status>").append(unplannedItem.getStatus()).append("</Status>")
			  .append("<ActualHour>").append(unplannedItem.getActualHour()).append("</ActualHour>")
			  .append("<Handler>").append(unplannedItem.getAssignto()).append("</Handler>")
			  .append("<Partners>").append(translateSpecialChar.TranslateXMLChar(unplannedItem.getPartners())).append("</Partners>")
			  .append("<Notes>").append(translateSpecialChar.TranslateXMLChar(unplannedItem.getNotes())).append("</Notes>")
			  .append("</UnplannedItem></EditUnplannedItem>");
		result.toString();

		return result;
	}
}
