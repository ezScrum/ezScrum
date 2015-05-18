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


public class AjaxGetProductBacklogPermissionAction extends Action {

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
		 * 	<Function name ="ProductBacklog">
		 * 		<AddStory> value </AddStory>
		 * 		<EditStory> value </EditStory>
		 * 		<ImportStory> value </ImportStory>
		 * 		<DeleteStory> value </DeleteStory>"
		 * 		<ShowStoryHistory> value </ShowStoryHistory>"
		 * 		<TagStory>	value </TagStory>
		 * 	</Function>
		 * </Permission>
		 */
		try{
			StringBuilder sb = new StringBuilder();
			sb.append("<Permission>");
			sb.append("<Function name =\"ProductBacklog\">");
			sb.append("<AddStory>"+role.getAccessProductBacklog()+"</AddStory>");
			sb.append("<EditStory>"+role.getAccessProductBacklog()+"</EditStory>");
			sb.append("<ImportStory>"+role.getAccessProductBacklog()+"</ImportStory>");
			sb.append("<DeleteStory>"+role.getAccessProductBacklog()+"</DeleteStory>");
			sb.append("<ShowStoryHistory>"+role.getAccessProductBacklog()+"</ShowStoryHistory>");
			sb.append("<TagStory>"+role.getAccessProductBacklog()+"</TagStory>");
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