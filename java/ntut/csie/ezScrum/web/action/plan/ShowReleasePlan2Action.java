package ntut.csie.ezScrum.web.action.plan;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ShowReleasePlan2Action extends PermissionAction {
	private static Log log = LogFactory.getLog(ShowReleasePlan2Action.class);
	
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
		log.info("Show ReleasePlan.");
		
		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);
    	
		ReleasePlanHelper RPhelper = new ReleasePlanHelper(project);
		SprintPlanHelper SPhelper = new SprintPlanHelper(project);
		
		List<IReleasePlanDesc> releaseDescs = RPhelper.loadReleasePlansList();
		List<IReleasePlanDesc> ListReleaseDescs = RPhelper.sortStartDate(releaseDescs);
	
    	StringBuilder result = new StringBuilder(RPhelper.setJSon(ListReleaseDescs, SPhelper));
    	
		return result;
	}	
}