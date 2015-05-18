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

public class AjaxGetReleasePlanPermissionAction extends Action {

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
		 * 	<Function name ="ReleasePlan">
		 * 		<AddRelease> value </AddRelease>
		 * 		<EditRelease> value </EditRelease>
		 * 		<DeleteRelease> value </DeleteRelease>
		 * 		<DropStory> value </DropStory>"
		 * 		<DropSprint> value </DropSprint>
		 * 		<AddStory> value </AddStory>"
		 * 		<AddSprint>	value </AddSprint>
		 * 		<ShowReleaseBacklog>	value </ShowReleaseBacklog>
		 * 		<ShowPrintableRelease>	value </ShowPrintableRelease>
		 * 	</Function>
		 * </Permission>
		 */
		try{
			StringBuilder sb = new StringBuilder();
			sb.append("<Permission>");
			sb.append("<Function name =\"ReleasePlan\">");
			sb.append("<AddRelease>"+role.getAccessReleasePlan()+"</AddRelease>");
			sb.append("<EditRelease>"+role.getAccessReleasePlan()+"</EditRelease>");
			sb.append("<DeleteRelease>"+role.getAccessReleasePlan()+"</DeleteRelease>");
			sb.append("<DropSprint>"+role.getAccessReleasePlan()+"</DropSprint>");
			sb.append("<DropStory>"+role.getAccessReleasePlan()+"</DropStory>");
			sb.append("<AddStory>"+role.getAccessReleasePlan()+"</AddStory>");
			sb.append("<AddSprint>"+role.getAccessReleasePlan()+"</AddSprint>");
			sb.append("<ShowReleaseBacklog>"+role.getAccessReleasePlan()+"</ShowReleaseBacklog>");
			sb.append("<ShowPrintableRelease>"+"true" +"</ShowPrintableRelease>");
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