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
		// get parameter info
		String name = request.getParameter("Name");
		String sprintName = request.getParameter("SprintID");
		String sprintId = sprintName.substring(sprintName.indexOf("#") + 1);
		String estimate = request.getParameter("Estimate");
		String handlerUsername = request.getParameter("Handler");
		String partnersUsername = request.getParameter("Partners");
		String notes = request.getParameter("Notes");
		String specificTimeString = request.getParameter("SpecificTime");
		
		Date specificDate;
		try {
			specificDate  = DateUtil.parse(specificTimeString, DateUtil._16DIGIT_DATE_TIME);
		} catch (ParseException e) {
			specificDate = new Date();
		}

		// 表格的資料
		UnplannedInfo unplannedInfo = new UnplannedInfo();
		unplannedInfo.name = name;
		unplannedInfo.notes = notes;
		unplannedInfo.estimate = (estimate.equals("") ? 0 : Integer.parseInt(estimate));
		unplannedInfo.projectId = project.getId();
		unplannedInfo.sprintId = Long.parseLong(sprintId);
		unplannedInfo.specificTime = specificDate.getTime();
		
		// Add new unplanned item
		UnplannedItemHelper unplannedHelper = new UnplannedItemHelper(project);
		long id = unplannedHelper.addUnplanned(handlerUsername, partnersUsername, unplannedInfo);

		// return result of new unplanned item in XML
		UnplannedObject unplannedItem = unplannedHelper.getUnplanned(id);
		
		StringBuilder result = new StringBuilder();
		result.append("<AddUnplannedItem><Result>success</Result>")
			  .append("<UnplannedItem>")
			  .append("<Id>").append(unplannedItem.getId()).append("</Id>")
			  .append("<Link></Link>")
			  .append("<Name>").append(TranslateSpecialChar.TranslateXMLChar(unplannedItem.getName())).append("</Name>")
			  .append("<SprintID>").append(unplannedItem.getSprintId()).append("</SprintID>")
			  .append("<Estimate>").append(unplannedItem.getEstimate()).append("</Estimate>")
			  .append("<Status>").append(unplannedItem.getStatusString()).append("</Status>")
			  .append("<ActualHour>").append(unplannedItem.getActual()).append("</ActualHour>")
			  .append("<Handler>").append(unplannedItem.getHandlerName()).append("</Handler>")
			  .append("<Partners>").append(TranslateSpecialChar.TranslateXMLChar(unplannedItem.getPartnersUsername())).append("</Partners>")
			  .append("<Notes>").append(TranslateSpecialChar.TranslateXMLChar(unplannedItem.getNotes())).append("</Notes>")
			  .append("</UnplannedItem>")
			  .append("</AddUnplannedItem>");
		return result;
	}
}
