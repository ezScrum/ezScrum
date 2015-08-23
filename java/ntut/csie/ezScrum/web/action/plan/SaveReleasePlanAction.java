package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataInfo.ReleaseInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class SaveReleasePlanAction extends PermissionAction {
	private static Log log = LogFactory.getLog(SaveReleasePlanAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessReleasePlan();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		TranslateSpecialChar translateSpecialChar = new TranslateSpecialChar();

		// get parameter info
		String releaseIdString = request.getParameter("Id");
		String name = translateSpecialChar.TranslateXMLChar(request.getParameter("Name"));
		String startDate = request.getParameter("StartDate");
		String dueDate = request.getParameter("EndDate");
		String description = translateSpecialChar.TranslateXMLChar(request.getParameter("Description"));
		long releaseId = -1;
		if (releaseIdString != null) {
			releaseId = Long.parseLong(releaseIdString);
		}
		if (request.getParameter("action") == null || request.getParameter("action").isEmpty()) {
			return null;
		}			
			
		String Action = request.getParameter("action");
		ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(project);
	
		ReleaseInfo releaseInfo = new ReleaseInfo();
		releaseInfo.id = releaseId;
		releaseInfo.name = name;
		releaseInfo.startDate = startDate;
		releaseInfo.dueDate = dueDate;
		releaseInfo.description = description;
		
		if (Action.equals("save")) {
			releasePlanHelper.editReleasePlan(releaseInfo);
		} else if (Action.equals("edit")) {
			releasePlanHelper.editReleasePlan(releaseInfo);
		}	
		return new StringBuilder("true");
	}
}