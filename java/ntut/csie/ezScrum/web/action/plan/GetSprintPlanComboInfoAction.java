package ntut.csie.ezScrum.web.action.plan;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.core.util.DateUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

public class GetSprintPlanComboInfoAction extends PermissionAction {
	private static Log log = LogFactory.getLog(GetSprintPlanComboInfoAction.class);
	
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

		log.info("Get Sprint Plan Combo Information in GetSprintPlanComboInfoAction");
		
		// get project from session or DB
		ProjectObject project = (ProjectObject) SessionManager.getProjectObject(request);

		// get parameter
		String currentSprintID = request.getParameter("SprintID");
		
		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		List<ISprintPlanDesc> plans = sprintPlanHelper.loadListPlans();
		
		ISprintPlanDesc currentPlan = null;
		
		if (currentSprintID == null) {
			currentPlan = sprintPlanHelper.loadCurrentPlan();
		} else {
			currentPlan = sprintPlanHelper.loadPlan(currentSprintID);
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
			if( descs != null ){
				for (ISprintPlanDesc desc : descs) {
					Sprints.add(new SprintPlanItem(desc));
				}
			}else{
				Sprints.add( new SprintPlanItem( null ) );
			}
		}
	}

	private class SprintPlanItem {
		private String Id = "";
		private String Info = "";
		private String Edit = "";
		public SprintPlanItem(ISprintPlanDesc desc) {
			Date endDate;
			if (desc != null) {
				endDate = DateUtil.dayFilter( desc.getEndDate() );
				this.Id = desc.getID();
				this.Info = "Sprint #" + desc.getID();
				if( isOverSprint( endDate ) ){
					this.Edit = "false" ;
				}else{
					this.Edit = "true" ;
				}
			} else {
				this.Id = "0";
				this.Info = "Sprint None";
				this.Edit = "false";
			}
		}
		public boolean isOverSprint( Date endDate ){
			Date today = new Date();
			if( today.after( endDate ) ){
				return true;
			}else{
				return false;
			}
			
		}
	}	
}
