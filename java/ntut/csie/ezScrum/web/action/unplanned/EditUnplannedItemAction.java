package ntut.csie.ezScrum.web.action.unplanned;

import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataInfo.UnplannedInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.UnplannedObject;
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
		// get parameter info
		long id = Long.parseLong(request.getParameter("issueID"));
		String name = request.getParameter("Name");
		String notes = request.getParameter("Notes");
		String statusString = request.getParameter("Status");
		String sprintName = request.getParameter("SprintID");
		long sprintId = Long.parseLong(sprintName.substring(sprintName.indexOf("#") + 1));
		int estimate = Integer.parseInt(request.getParameter("Estimate"));
		int actual = Integer.parseInt(request.getParameter("ActualHour"));
		String handlerUsername = request.getParameter("Handler");
		String partnersUsername = request.getParameter("Partners");
		String specificTimeString = request.getParameter("SpecificTime");

		Date specificDate;
		try {
			specificDate  = DateUtil.parse(specificTimeString, DateUtil._16DIGIT_DATE_TIME);
		} catch (ParseException e) {
			specificDate = new Date();
		}
		
		// 表格的資料
		UnplannedInfo unplannedInfo = new UnplannedInfo();
		unplannedInfo.id = id;
		unplannedInfo.name = name;
		unplannedInfo.notes = notes;
		unplannedInfo.estimate = estimate;
		unplannedInfo.actual = actual;
		unplannedInfo.statusString = statusString;
		unplannedInfo.projectId = project.getId();
		unplannedInfo.sprintId = sprintId;
		unplannedInfo.specificTime = specificDate.getTime();
		
		UnplannedItemHelper unplannedHelper = new UnplannedItemHelper(project);
		unplannedHelper.updateUnplanned(handlerUsername, partnersUsername, unplannedInfo);
		
		// return result of unplanned item in XML
		UnplannedObject unplanned = unplannedHelper.getUnplanned(id);

		StringBuilder result = new StringBuilder();
		result.append("<EditUnplannedItem><Result>success</Result><UnplannedItem>")
			  .append("<Id>").append(unplanned.getId()).append("</Id>")
			  .append("<Link></Link>")
			  .append("<Name>").append(TranslateSpecialChar.TranslateXMLChar(unplanned.getName())).append("</Name>")
			  .append("<SprintID>").append(unplanned.getSprintId()).append("</SprintID>")
			  .append("<Estimate>").append(unplanned.getEstimate()).append("</Estimate>")
			  .append("<Status>").append(unplanned.getStatusString()).append("</Status>")
			  .append("<ActualHour>").append(unplanned.getActual()).append("</ActualHour>")
			  .append("<Handler>").append(unplanned.getHandlerName()).append("</Handler>")
			  .append("<Partners>").append(unplanned.getPartnersUsername()).append("</Partners>")
			  .append("<Notes>").append(TranslateSpecialChar.TranslateXMLChar(unplanned.getNotes())).append("</Notes>")
			  .append("</UnplannedItem></EditUnplannedItem>");
		result.toString();

		return result;
	}
}
