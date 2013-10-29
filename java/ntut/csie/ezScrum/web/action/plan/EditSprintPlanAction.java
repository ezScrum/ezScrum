package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.form.IterationPlanForm;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class EditSprintPlanAction extends PermissionAction {
	private static Log log = LogFactory.getLog(EditSprintPlanAction.class);

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
		
		// get session info
		IProject project = (IProject) request.getSession().getAttribute("Project");

		SprintPlanHelper helper = new SprintPlanHelper(project);
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		
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
		helper.editIterationPlanForm(Sprintform);

		return new StringBuilder("true");
	}
}
