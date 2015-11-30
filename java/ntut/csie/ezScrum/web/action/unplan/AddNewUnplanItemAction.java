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

public class AddNewUnplanItemAction extends PermissionAction {
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
		UnplanInfo unplanInfo = new UnplanInfo();
		unplanInfo.name = name;
		unplanInfo.notes = notes;
		unplanInfo.estimate = (estimate.equals("") ? 0 : Integer.parseInt(estimate));
		unplanInfo.projectId = project.getId();
		unplanInfo.sprintId = Long.parseLong(sprintId);
		unplanInfo.specificTime = specificDate.getTime();
		
		// Add new unplan item
		UnplanItemHelper unplanHelper = new UnplanItemHelper(project);
		long id = unplanHelper.addUnplan(handlerUsername, partnersUsername, unplanInfo);

		// return result of new unplan item in XML
		UnplanObject unplanItem = unplanHelper.getUnplan(id);
		
		StringBuilder result = new StringBuilder();
		result.append("<AddUnplannedItem><Result>success</Result>")
			  .append("<UnplannedItem>")
			  .append("<Id>").append(unplanItem.getId()).append("</Id>")
			  .append("<Link></Link>")
			  .append("<Name>").append(TranslateSpecialChar.TranslateXMLChar(unplanItem.getName())).append("</Name>")
			  .append("<SprintID>").append(unplanItem.getSprintId()).append("</SprintID>")
			  .append("<Estimate>").append(unplanItem.getEstimate()).append("</Estimate>")
			  .append("<Status>").append(unplanItem.getStatusString()).append("</Status>")
			  .append("<ActualHour>").append(unplanItem.getActual()).append("</ActualHour>")
			  .append("<Handler>").append(unplanItem.getHandlerName()).append("</Handler>")
			  .append("<Partners>").append(TranslateSpecialChar.TranslateXMLChar(unplanItem.getPartnersUsername())).append("</Partners>")
			  .append("<Notes>").append(TranslateSpecialChar.TranslateXMLChar(unplanItem.getNotes())).append("</Notes>")
			  .append("</UnplannedItem>")
			  .append("</AddUnplannedItem>");
		return result;
	}
}
