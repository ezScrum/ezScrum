package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxGetNewReleaseIDAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxGetNewReleaseIDAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessReleasePlan();
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
		ProjectObject project = (ProjectObject) SessionManager.getProjectObject(request);
		
		ReleasePlanHelper helper = new ReleasePlanHelper(project);
		return helper.getNewReleaseId();
		
//		int id = helper.getLastReleasePlanNumber() + 1;	// 依照目前最近ID count 累加
//
//		StringBuilder sb = new StringBuilder();
//		sb.append("<Root><Release>");
//		sb.append("<ID>" + id + "</ID>");
//		sb.append("</Release></Root>");
//		
//		return sb;
	}
}
