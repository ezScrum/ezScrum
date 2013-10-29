package ntut.csie.ezScrum.web.action.report;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

public class GetIssueStatusByIssueIDAction extends PermissionAction {
	private static Log log = LogFactory.getLog(GetIssueStatusByIssueIDAction.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessTaskBoard();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		String issueStage = null;
		// get session info
		IProject project = (IProject) request.getSession().getAttribute("Project");
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		
//		String projectID = project.getName();//projectID
		
		String sprintID = request.getParameter("sprintID");//sprintID
		int sprintIDInt = Integer.parseInt( sprintID );
		 
		long issueID = Long.parseLong( request.getParameter("issueID") );//issueID
		
		
////		SprintBacklog backlog = null;//get stories and tasks by sprintBacklog
////		try {
////			if (sprintID == null || sprintID.equals("")) {
////				backlog = new SprintBacklog( project, userSession );
////			} else {
////				backlog = new SprintBacklog( project, userSession, sprintIDInt );
////			}
//		SprintBacklogMapper backlog = (new SprintBacklogLogic(project, userSession, sprintID)).getSprintBacklogMapper(sprintID);
//		try{
//			List<IIssue> stories = backlog.getStoriesByImp();//retrieve stories
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, userSession, sprintID);
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		try{
			List<IIssue> stories = sprintBacklogLogic.getStoriesByImp();//retrieve stories
			Map<Long, IIssue[]> taskMap = sprintBacklogMapper.getTasksMap(); //retrieve storyID task map

			SprintPlanHelper sph = new SprintPlanHelper(project);
			Map<Integer,String> taskBoardStageMap = sph.loadPlan( sprintIDInt ).getTaskBoardStageMap();//get taskBoardStageMap to query status name
//			SprintPlanDescLoader spd = new SprintPlanDescLoader( projectID );
//			Map<Integer,String> taskBoardStageMap = spd.load( sprintIDInt ).getTaskBoardStageMap();//get taskBoardStageMap to query status name
			
			//traverse every task in every story to find issueID, not efficient but worked now.
			for( IIssue story : stories ){
				if( story.getIssueID() == issueID ){//every story is a root
					String storyStage = taskBoardStageMap.get( story.getStatusValue() );//story query map to get status name by status id(called value)
					issueStage = storyStage ;
					break;
				}else{ //every task is a story child
					IIssue[] taskArray = taskMap.get( story.getIssueID() );
					for( IIssue task : taskArray ){
						if( task.getIssueID() == issueID ){
							String taskStage = taskBoardStageMap.get( task.getStatusValue() );
							issueStage = taskStage;
							break;
						}
					}
				}
			}
			
			Gson gson = new Gson();

			issueStage = gson.toJson( issueStage );//translate stage string to json
			
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		
		return new StringBuilder( issueStage );// to fit return type;
	}
}
