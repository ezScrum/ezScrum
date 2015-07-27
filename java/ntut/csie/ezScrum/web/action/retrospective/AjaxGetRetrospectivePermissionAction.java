package ntut.csie.ezScrum.web.action.retrospective;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AjaxGetRetrospectivePermissionAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		//取得專案名稱
		IProject project = (IProject) request.getSession().getAttribute(
				"Project");
		String projrctName = project.getName();
		
		Map<String, ScrumRole> scrumRoles = ( Map<String, ScrumRole> )request.getSession().getAttribute("ScrumRoles");
		//取得專案對應的角色權限
		ScrumRole role =  scrumRoles.get(projrctName);
		String result;
		/*
		 * <Permission>
		 * 	<Function name ="Retrospective">
		 * 		<AddRetrospective> value </AddRetrospective>
		 * 		<EditRetrospective> value </EditRetrospective>
		 * 		<DeleteRetrospective> value </DeleteRetrospective>
		 * 	</Function>
		 * </Permission>
		 */
		try{
			StringBuilder sb = new StringBuilder();
			sb.append("<Permission>");
			sb.append("<Function name =\"Restrospective\">");
			sb.append("<AddRetrospective>"+role.getAccessRetrospective()+"</AddRetrospective>");
			sb.append("<EditRetrospective>"+role.getAccessRetrospective()+"</EditRetrospective>");
			sb.append("<DeleteRetrospective>"+role.getAccessRetrospective()+"</DeleteRetrospective>");
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