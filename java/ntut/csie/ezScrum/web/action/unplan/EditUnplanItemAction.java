package ntut.csie.ezScrum.web.action.unplan;

import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataInfo.UnplanInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.UnplanObject;
import ntut.csie.ezScrum.web.helper.UnplanItemHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.core.util.DateUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class EditUnplanItemAction extends PermissionAction {

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessUnplanItem();
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
		ProjectObject project = SessionManager.getProject(request);
		// get parameter info
		long serialUnplanId = Long.parseLong(request.getParameter("issueID"));
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
		UnplanInfo unplanInfo = new UnplanInfo();
		unplanInfo.serialId = serialUnplanId;
		unplanInfo.name = name;
		unplanInfo.notes = notes;
		unplanInfo.estimate = estimate;
		unplanInfo.actual = actual;
		unplanInfo.statusString = statusString;
		unplanInfo.projectId = project.getId();
		unplanInfo.sprintId = sprintId;
		unplanInfo.specificTime = specificDate.getTime();
		
		UnplanItemHelper unplanHelper = new UnplanItemHelper(project);
		unplanHelper.updateUnplan(handlerUsername, partnersUsername, unplanInfo);
		
		// return result of unplan item in XML
		UnplanObject unplan = unplanHelper.getUnplan(unplanInfo.projectId, unplanInfo.serialId);

		StringBuilder result = new StringBuilder();
		result.append("<EditUnplannedItem><Result>success</Result><UnplannedItem>")
			  .append("<Id>").append(unplan.getSerialId()).append("</Id>")
			  .append("<Link></Link>")
			  .append("<Name>").append(TranslateSpecialChar.TranslateXMLChar(unplan.getName())).append("</Name>")
			  .append("<SprintID>").append(unplan.getSprintId()).append("</SprintID>")
			  .append("<Estimate>").append(unplan.getEstimate()).append("</Estimate>")
			  .append("<Status>").append(unplan.getStatusString()).append("</Status>")
			  .append("<ActualHour>").append(unplan.getActual()).append("</ActualHour>")
			  .append("<Handler>").append(unplan.getHandlerName()).append("</Handler>")
			  .append("<Partners>").append(unplan.getPartnersUsername()).append("</Partners>")
			  .append("<Notes>").append(TranslateSpecialChar.TranslateXMLChar(unplan.getNotes())).append("</Notes>")
			  .append("</UnplannedItem></EditUnplannedItem>");
		result.toString();

		return result;
	}
}
