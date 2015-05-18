package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.form.IterationPlanForm;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class SaveSprintPlanAction extends PermissionAction {
//	private static Log log = LogFactory.getLog(SaveSprintPlanAction.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintPlan();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		// get project from session or DB
		ProjectObject project = (ProjectObject) SessionManager.getProjectObject(request);
		
		// get parameter info
		String isCreate = request.getParameter("isCreate");

		TranslateSpecialChar tsc = new TranslateSpecialChar();

		// get parameter info
		IterationPlanForm Sprintform = new IterationPlanForm();
		Sprintform.setID(request.getParameter("Id"));
		Sprintform.setGoal(tsc.TranslateXMLChar(request.getParameter("Goal")));
		Sprintform.setIterStartDate(request.getParameter("StartDate"));
		Sprintform.setIterIterval(request.getParameter("Interval"));
		Sprintform.setIterMemberNumber(request.getParameter("Members"));
		Sprintform.setAvailableDays(request.getParameter("AvaliableDays"));
		Sprintform.setFocusFactor(request.getParameter("FocusFactor"));
		Sprintform.setNotes(tsc.TranslateXMLChar(request.getParameter("DailyScrum")));
		Sprintform.setDemoDate(request.getParameter("DemoDate"));
		Sprintform.setDemoPlace(tsc.TranslateXMLChar(request.getParameter("DemoPlace")));
		
		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		
		if ( (isCreate != null) && Boolean.parseBoolean(isCreate) ) {
			// new sprint
			sprintPlanHelper.saveIterationPlanForm(Sprintform);
		} else {
			// edit sprint
			sprintPlanHelper.editIterationPlanForm(Sprintform);			
		}

		return new StringBuilder("true");
	}
}
