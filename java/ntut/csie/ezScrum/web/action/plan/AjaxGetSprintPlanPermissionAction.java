package ntut.csie.ezScrum.web.action.plan;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AjaxGetSprintPlanPermissionAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		//取得專案名稱
		ProjectObject project = SessionManager.getProjectObject(request);
		String projrctName = project.getName();
		
		Map<String, ScrumRole> scrumRoles = ( Map<String, ScrumRole> )request.getSession().getAttribute("ScrumRoles");
		//取得專案對應的角色權限
		ScrumRole role =  scrumRoles.get(projrctName);
		String result;
		/*
		 * <Permission>
		 * 	<Function name ="SprintPlan">
		 * 		<AddSprint> value </AddSprint>
		 * 		<EditSprint> value </EditSprint>
		 * 		<DeleteSprint> value </DeleteSprint>
		 * 		<MoveSprint> value </MoveSprint>
		 * 	</Function>
		 * </Permission>
		 */
		try{
			StringBuilder sb = new StringBuilder();
			sb.append("<Permission>");
			sb.append("<Function name =\"SprintPlan\">");
			sb.append("<AddSprint>"+role.getAccessSprintPlan()+"</AddSprint>");
			sb.append("<EditSprint>"+role.getAccessSprintPlan()+"</EditSprint>");
			sb.append("<DeleteSprint>"+role.getAccessSprintPlan()+"</DeleteSprint>");
			sb.append("</Function>");
			sb.append("</Permission>");
			result = sb.toString();
		} catch(Exception e){
			result = "<Permission><Result>false</Result></Permission>";
		}
		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(result);
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}