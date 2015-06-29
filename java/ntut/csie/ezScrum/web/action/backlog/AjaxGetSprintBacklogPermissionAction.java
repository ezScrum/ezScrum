package ntut.csie.ezScrum.web.action.backlog;

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

public class AjaxGetSprintBacklogPermissionAction extends Action {

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
		 * 	<Function name ="SprintBacklog">
		 * 		<AddStory> value </AddStory>
		 * 		<EditStory> value </EditStory>
		 * 		<DropStory> value </DropStory>
		 * 		<ShowStory> value </ShowStory>
		 * 		<ShowSprintInfo> value </ShowSprintInfo>
		 * 		<ShowPrintableStories> value </ShowPrintableStories>
		 * 		<AddTask> value </AddTask>
		 * 		<EditTask> value </EditTask>
		 * 		<DropTask> value </DropTask>
		 * 		<ShowTask> value </ShowTask>
		 * 	</Function>
		 * </Permission>
		 */
		try{
			StringBuilder sb = new StringBuilder();
			sb.append("<Permission>");
			sb.append("<Function name =\"SprintBacklog\">");
			sb.append("<AddStory>"+role.getAccessSprintBacklog()+"</AddStory>");
			sb.append("<EditStory>"+role.getAccessSprintBacklog()+"</EditStory>");
			sb.append("<DropStory>"+role.getAccessSprintBacklog()+"</DropStory>");
			sb.append("<ShowStory>"+role.getAccessSprintBacklog()+"</ShowStory>");
			sb.append("<ShowSprintInfo>"+role.getAccessSprintBacklog()+"</ShowSprintInfo>");
			sb.append("<ShowPrintableStories>"+role.getAccessSprintBacklog()+"</ShowPrintableStories>");
			sb.append("<AddTask>"+role.getAccessSprintBacklog()+"</AddTask>");
			sb.append("<EditTask>"+role.getAccessSprintBacklog()+"</EditTask>");
			sb.append("<DropTask>"+role.getAccessSprintBacklog()+"</DropTask>");
			sb.append("<ShowTask>"+role.getAccessSprintBacklog()+"</ShowTask>");
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