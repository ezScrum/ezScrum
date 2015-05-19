package ntut.csie.ezScrum.web.action.plan;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.iteration.iternal.SprintPlanDesc;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.SprintPlanUI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

public class ShowAllSprintForSprintPlanAction extends PermissionAction {
	private static Log log = LogFactory.getLog(ShowAllSprintForSprintPlanAction.class);
	
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
		log.info("Show all sprint for SprintPlan.");
		
		// get project from session or DB
		ProjectObject project = SessionManager.getProjectObject(request);
		
		// get parameter info
		String lastsprint = request.getParameter("lastsprint");
		
		List<ISprintPlanDesc> descs = new LinkedList<ISprintPlanDesc>();
		SprintPlanHelper SPhelper = new SprintPlanHelper(project);
		
		// 只取得最後一筆 sprint
		if (lastsprint != null && Boolean.parseBoolean(lastsprint)) {
			int lastID = SPhelper.getLastSprintId();
			if (lastID > 0) {
				descs.add(SPhelper.loadPlan(lastID));
			} else {
				descs.add(new SprintPlanDesc());	// empty
			}
		} else {
			descs = SPhelper.loadListPlans();
		}
		
		SprintPlanUI spui = new SprintPlanUI(descs);
		
		Gson gson = new Gson();
		return new StringBuilder(gson.toJson(spui));
	}
}
