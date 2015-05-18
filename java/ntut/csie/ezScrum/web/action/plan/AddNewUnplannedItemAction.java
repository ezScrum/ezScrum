package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.UnplannedItemHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.core.util.DateUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AddNewUnplannedItemAction extends PermissionAction {
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

		TranslateSpecialChar tsc = new TranslateSpecialChar();
		
		// New unplanned item data
		// get parameter info
		String taskName = request.getParameter("Name");
		String SprintName = request.getParameter("SprintID");
		String SprintID = SprintName.substring(SprintName.indexOf("#") + 1);
		String taskEst = request.getParameter("Estimate");
		String handler = request.getParameter("Handler");
		String partners = request.getParameter("Partners");
		String notes = request.getParameter("Notes");
		String specificTime = request.getParameter("SpecificTime");
		
		if (specificTime.isEmpty())
			specificTime = DateUtil.getNow();
		else
			specificTime = specificTime + "-00:00:00";
		
		// Add new unplanned item
		UnplannedItemHelper helper = new UnplannedItemHelper(project, session);
		long id = helper.addUnplannedItem(taskName, taskEst, handler, partners, notes, DateUtil.dayFillter(specificTime, DateUtil._16DIGIT_DATE_TIME), ScrumEnum.UNPLANNEDITEM_ISSUE_TYPE,
		          Long.parseLong(SprintID));

		// return result of new unplanned item in XML
		IIssue unplannedItem = helper.getIssue(id);
		
		StringBuilder result = new StringBuilder("");
		result.append("<AddUnplannedItem><Result>success</Result>")
			  .append("<UnplannedItem>")
			  .append("<Id>").append(unplannedItem.getIssueID()).append("</Id>")
			  .append("<Link>").append(tsc.TranslateXMLChar(unplannedItem.getIssueLink())).append("</Link>")
			  .append("<Name>").append(tsc.TranslateXMLChar(unplannedItem.getSummary())).append("</Name>")
			  .append("<SprintID>").append(unplannedItem.getSprintID()).append("</SprintID>")
			  .append("<Estimate>").append(unplannedItem.getEstimated()).append("</Estimate>")
			  .append("<Status>").append(unplannedItem.getStatus()).append("</Status>")
			  .append("<ActualHour>").append(unplannedItem.getActualHour()).append("</ActualHour>")
			  .append("<Handler>").append(unplannedItem.getAssignto()).append("</Handler>")
			  .append("<Partners>").append(tsc.TranslateXMLChar(unplannedItem.getPartners())).append("</Partners>")
			  .append("<Notes>").append(tsc.TranslateXMLChar(unplannedItem.getNotes())).append("</Notes>")
			  .append("</UnplannedItem>")
			  .append("</AddUnplannedItem>");
		return result;
	}
}
