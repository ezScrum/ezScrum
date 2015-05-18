package ntut.csie.ezScrum.web.action.plan;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class SaveReleasePlanAction extends PermissionAction {
	private static Log log = LogFactory.getLog(SaveReleasePlanAction.class);
	
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
		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		TranslateSpecialChar tsc = new TranslateSpecialChar();

		// get parameter info
		String ID = request.getParameter("Id");
		String Name = tsc.TranslateXMLChar(request.getParameter("Name"));
		String StartDate = request.getParameter("StartDate");
		String EndDate = request.getParameter("EndDate");
		String Description = tsc.TranslateXMLChar(request.getParameter("Description"));
		
		if (request.getParameter("action") == null || request.getParameter("action").isEmpty()) {
			return null;
		}			
			
		String Action = request.getParameter("action");
		ReleasePlanHelper rphelper = new ReleasePlanHelper(project);
	
		if (Action.equals("save")) {
			rphelper.editReleasePlan(ID, Name, StartDate, EndDate, Description, "save");
			
			//Add release Plan 後, 自動加入日期範圍內 Sprint 底下的 Story
			rphelper.addReleaseSprintStory(project, session, ID, null, rphelper.getReleasePlan(ID));
			
		} else if (Action.equals("edit")) {
			IReleasePlanDesc reDesc = rphelper.getReleasePlan(ID);
			List<ISprintPlanDesc> oldSprintList =  reDesc.getSprintDescList();//get the original list of sprint
			
			rphelper.editReleasePlan(ID, Name, StartDate, EndDate, Description, "edit");
			
			//Edit Release Plan 後, 重新抓取日期範圍內 Sprint 底下的 Story
			rphelper.addReleaseSprintStory(project, session, ID, oldSprintList, rphelper.getReleasePlan(ID));
		}	
		return new StringBuilder("true");
	}
	
//	//加入 Release 日期範圍內 Sprint 底下的 Story
//	private void addReleaseSprintStory(IProject project, IUserSession session, String ID, List<ISprintPlanDesc> oldSprintList, IReleasePlanDesc reDesc){
//		List<ISprintPlanDesc> newSprintList =  reDesc.getSprintDescList();
//		
//		ProductBacklogHelper pbHelper = new ProductBacklogHelper(project, session);
//		
//		ArrayList<Long> storyList;
//		
//		//For deleting old sprint. Taking original SprintList to compare with new SprintList.
//		if(oldSprintList != null){	
//			storyList = compareReleaseSprint(oldSprintList, newSprintList, project, session);
//			for(long story : storyList) {
//				pbHelper.removeRelease(String.valueOf(story));
//			}
//			storyList.clear();
//		}
//
//		//For adding new sprint. Taking new SprintList to compare with original SprintList.
//		storyList = compareReleaseSprint(newSprintList, oldSprintList, project, session);
//		pbHelper.addRelease(storyList, ID);
//
//	}
//	
//	//SprintList，舊日期的list 與 新日期的list做比對
//	private ArrayList<Long> compareReleaseSprint(List<ISprintPlanDesc> sprintList1, List<ISprintPlanDesc> sprintList2,
//											   IProject project, IUserSession session) {
//		SprintBacklog sprintBacklog;
//		ArrayList<Long> storyList = new ArrayList<Long>();
//		boolean deleteOrAdd = true;
//		
//		if(sprintList2 != null) {
//			for(ISprintPlanDesc list1 : sprintList1) {
//				for(ISprintPlanDesc list2 : sprintList2) {
//					if(list1.getID().equals(list2.getID())) {//Sprint still exists.
//						deleteOrAdd = false;
//						break;
//					}
//				}
//				if(deleteOrAdd == true) {//Finding sprint not existing in list2.
//					sprintBacklog = new SprintBacklog(project, session, Integer.parseInt(list1.getID()));
//					IIssue[] stories = sprintBacklog.getStories();
//					for(IIssue story : stories) {
//						storyList.add(story.getIssueID());
//					}
//				}
//				deleteOrAdd = true;//For next sprint.
//			}
//		}
//		else { // For creating a new sprint
//			for(ISprintPlanDesc list1 : sprintList1) {
//				sprintBacklog = new SprintBacklog(project, session, Integer.parseInt(list1.getID()));
//				IIssue[] stories = sprintBacklog.getStories();
//				for(IIssue story : stories) {
//					storyList.add(story.getIssueID());
//				}
//			}
//		}
//		return storyList;
//	}
}