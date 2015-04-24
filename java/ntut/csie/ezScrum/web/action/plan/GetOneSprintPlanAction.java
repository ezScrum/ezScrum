package ntut.csie.ezScrum.web.action.plan;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.SprintPlanUI;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

public class GetOneSprintPlanAction extends PermissionAction {
//	private static Log log = LogFactory.getLog(GetOneSprintPlanAction.class);
	
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
		
		// get project from session or DB
		ProjectObject project = SessionManager.getProjectObject(request);
		
		// get parameter info
		String lastsprint = request.getParameter("lastsprint");
		String sprintID = request.getParameter("SprintID");
		
		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		List<ISprintPlanDesc> sprintPlanDescList = new LinkedList<ISprintPlanDesc>();
		
//		// 只取得一筆 sprint
//		int SprintID = -1;
//		if (lastsprint != null && Boolean.parseBoolean(lastsprint)) {
//			SprintID = SPhelper.getLastSprintId();
//		} else if (sprintID != null) {
//			SprintID = Integer.parseInt(sprintID);
//		}
//		
//		if (SprintID > 0) {
//			sprintPlanDescList.add(SPhelper.loadPlan(SprintID));
//		} else {
//			sprintPlanDescList.add(new SprintPlanDesc());
//		}
		sprintPlanDescList.add(sprintPlanHelper.getOneSprintInformation(lastsprint, sprintID));
		SprintPlanUI spui = new SprintPlanUI(sprintPlanDescList);
		
		Gson gson = new Gson();
		return new StringBuilder(gson.toJson(spui));
	}
}
