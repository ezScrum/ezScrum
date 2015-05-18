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

public class CheckReleaseDateAction extends PermissionAction {
	private static Log log = LogFactory.getLog(CheckReleaseDateAction.class);
	
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
		ProjectObject project = (ProjectObject) SessionManager.getProjectObject(request);

		// get parameter info
		String ReleaseID = request.getParameter("Id");
		String StartDate = request.getParameter("StartDate");
		String EndDate = request.getParameter("EndDate");

		//
		if (request.getParameter("action") == null || request.getParameter("action").isEmpty()) {
			return null;
		} 		
		String Action = request.getParameter("action");
		
		ReleasePlanHelper rphelper = new ReleasePlanHelper(project);
		return rphelper.checkReleaseDate(ReleaseID, StartDate, EndDate, Action);
		
//		List<IReleasePlanDesc> rpList = rphelper.loadReleasePlansList();
//		String result = "legal";
//		
//		if (request.getParameter("action") == null || request.getParameter("action").isEmpty()) {
//			return null;
//		} 
//		else{
//			for(IReleasePlanDesc rp : rpList){
//				if(request.getParameter("action").equals("edit") && ID.equals(rp.getID())){//不與自己比較
//					continue;
//				}
//				//check日期的頭尾是否有在各個RP日期範圍內
//				if((StartDate.compareTo(rp.getStartDate()) >= 0 && StartDate.compareTo(rp.getEndDate()) <= 0) ||
//				   (EndDate.compareTo(rp.getStartDate()) >= 0 && EndDate.compareTo(rp.getEndDate()) <= 0 )){
//					result = "illegal";
//					break;
//				}
//			}
//		}
//		
//		return new StringBuilder(result);
	}

}
