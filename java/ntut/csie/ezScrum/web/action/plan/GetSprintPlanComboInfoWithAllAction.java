package ntut.csie.ezScrum.web.action.plan;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

public class GetSprintPlanComboInfoWithAllAction extends PermissionAction {
	private static Log log = LogFactory.getLog(GetSprintPlanComboInfoWithAllAction.class);
	
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
		IProject project = (IProject) SessionManager.getProject(request);
		
		// get parameter
		String CurrentSprintID = request.getParameter("SprintID");
		
		SprintPlanHelper helper = new SprintPlanHelper(project);
		List<ISprintPlanDesc> plans = helper.loadListPlans();
		
		ISprintPlanDesc currentPlan = null;
		
		if (CurrentSprintID == null) {
			currentPlan = helper.loadCurrentPlan();
		} else {
			currentPlan = helper.loadPlan(CurrentSprintID);
		}
		
		SprintPlanUI spui = new SprintPlanUI(plans, currentPlan);
		
		Gson gson = new Gson();
		return new StringBuilder(gson.toJson(spui));
	}
	
	private class SprintPlanUI {
		private List<SprintPlanItem> Sprints = new LinkedList<SprintPlanItem>();
		private SprintPlanItem CurrentSprint = null;
		
		public SprintPlanUI(List<ISprintPlanDesc> descs, ISprintPlanDesc currentSprint) {
			this.CurrentSprint = new SprintPlanItem(currentSprint);
			
			// 多一個 all 的選項
			if (descs != null && descs.size() > 0) { 
				this.Sprints.add(new SprintPlanItem("ALL", "ALL"));
			}
						
			for(int i=descs.size()-1; i>=0; i--){
				Sprints.add(new SprintPlanItem(descs.get(i)));
			}
		}
	}

	private class SprintPlanItem {
		private String Id = "";
		private String Info = "";
		
		public SprintPlanItem(String id, String info) {
			this.Id = id;
			this.Info = info;
		}
		
		public SprintPlanItem(ISprintPlanDesc desc) {
			if (desc != null) {
				this.Id = desc.getID();
				this.Info = "Sprint #" + desc.getID();
			} else {
				this.Id = "0";
				this.Info = "Sprint None";
			}
		}
	}	
}
