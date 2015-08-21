package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.ReleaseObject;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
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
		String ID = request.getParameter("Id");
		String Name = translateSpecialChar.TranslateXMLChar(request.getParameter("Name"));
		String StartDate = request.getParameter("StartDate");
		String EndDate = request.getParameter("EndDate");
		String Description = translateSpecialChar.TranslateXMLChar(request.getParameter("Description"));
		
		if (request.getParameter("action") == null || request.getParameter("action").isEmpty()) {
			return null;
		}			
			
		String Action = request.getParameter("action");
		ReleasePlanHelper rphelper = new ReleasePlanHelper(project);
	
		if (Action.equals("save")) {
			rphelper.editReleasePlan(ID, Name, StartDate, EndDate, Description, "save");
			
			//Add release Plan 後, 自動加入日期範圍內 Sprint 底下的 Story
			rphelper.addReleaseSprintStory(project, session, ID, null, rphelper.getReleasePlan(ID));
			
		} else if (Action.equals("edit")) {
			ReleaseObject releasePlanDesc = rphelper.getReleasePlan(ID);
			ArrayList<SprintObject> oldSprintList =  releasePlanDesc.getSprints();//get the original list of sprint
			
			rphelper.editReleasePlan(ID, Name, StartDate, EndDate, Description, "edit");
			
			//Edit Release Plan 後, 重新抓取日期範圍內 Sprint 底下的 Story
			rphelper.addReleaseSprintStory(project, session, ID, oldSprintList, rphelper.getReleasePlan(ID));
		}	
		return new StringBuilder("true");
	}
}