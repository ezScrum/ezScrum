package ntut.csie.ezScrum.web.action;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

//get task board stages by project id and sprint id, for plugin extension point
public class GetTaskBoardStagesAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {
		
		String result = "";
//		String projectID = request.getParameter("PID");
		ProjectObject project = (ProjectObject) request.getSession().getAttribute("Project");		
		int sprintIDInt  = Integer.parseInt( request.getParameter("sprintID") );
				
		SprintPlanHelper sph = new SprintPlanHelper(project);
//		SprintPlanDescLoader spd = new SprintPlanDescLoader( projectID );
		Map<Integer,String> taskBoardStageMap = sph.loadPlan( sprintIDInt ).getTaskBoardStageMap();	
//		Map<Integer,String> taskBoardStageMap = spd.load( sprintIDInt ).getTaskBoardStageMap();
		
		Gson gson = new Gson();
		result = gson.toJson( taskBoardStageMap.values() );
		
		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(result.toString());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
