package ntut.csie.ezScrum.web.action.retrospective;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetAddSprintTaskInfoAction extends Action {
	private static Log log = LogFactory.getLog(GetAddSprintTaskInfoAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Get Add Sprint Task Information in GetAddSprintTaskInfoAction");
		
		IProject project = (IProject) request.getSession().getAttribute("Project");	
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		//所有 Sprint 封裝成 XML 給 Ext(ComboBox) 使用
		StringBuilder sb = new StringBuilder();

		String sprintID = request.getParameter("sprintId");

		if ( (sprintID == null) || (sprintID.length() <= 0 ) || (sprintID.equals("")) || (sprintID.equals("0")) || (sprintID.equals("-1")) ) {
			// default data for empty sprint backlog information
			sb.append("<Handlers><Partner></Partner><Handler></Handler></Handlers>");
		}
		
//		SprintBacklogMapper backlog = null;
//		try {
//			backlog = new SprintBacklogMapper(project, userSession, Integer.parseInt(sprintID));
//		} catch (Exception e) {
//			backlog = null;
//			System.out.println("class: GetAddSprintTaskInfoAction, method: execute, exception: " + e.toString());
//		}
		
		SprintBacklogMapper backlog = (new SprintBacklogLogic(project, userSession, sprintID)).getSprintBacklogMapper();
	
		if ( (backlog != null) && (backlog.getSprintId() > 0) ) {
//			MantisAccountMapper helper = new MantisAccountMapper(project, session);
//			List<String> actorList = helper.getScrumWorkerList();
			List<String> actorList = (new ProjectMapper()).getProjectScrumWorkerList(userSession, project);
			String defaultActor="";
			if(actorList!=null){
				for(int i=0;i<actorList.size();i++){
					//預設角色會有一個為null
					if(i>1){
						defaultActor +="; ";
					}
					defaultActor += actorList.get(i);
				}
			}
			
			sb.append("<Handlers><Partner><Name>" + defaultActor + "</Name></Partner>");
			for (String handler : actorList)
			{
				sb.append("<Handler>");
				sb.append("<Name>" + handler + "</Name>");
				sb.append("</Handler>");
			}
			sb.append("</Handlers>");
		}
		
		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(sb.toString());
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}