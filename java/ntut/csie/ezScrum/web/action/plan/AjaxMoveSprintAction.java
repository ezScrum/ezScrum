package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.control.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxMoveSprintAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxMoveSprintAction.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintPlan();
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		// get project from session or DB
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
	
		// get parameter info
		String oldID = request.getParameter("OldID");
		String newID = request.getParameter("NewID");
		int oldID_int = Integer.parseInt(oldID);
		int newID_int = Integer.parseInt(newID);
		
		//移動iterPlan.xml的資訊
		SprintPlanHelper SPhelper = new SprintPlanHelper(project);
		SPhelper.moveSprintPlan(project, session, oldID_int, newID_int);
		
//		List<ISprintPlanDesc> descs = SPhelper.loadListPlans();
//		SPhelper.moveSprint(oldID_int, newID_int);
//		
//		
//		ProductBacklogHelper pb = new ProductBacklogHelper(project, session);
//		Map<String, List<IIssue>> map = pb.getSprintHashMap();
//		
//		ArrayList<Integer> list = new ArrayList<Integer>();
//		//取出需要修改的sprint ID 
//		if(oldID_int > newID_int){
//			for(int i = newID_int; i<= oldID_int;i++){
//				if(isSprintPlan(descs, i))
//					list.add(i);
//			}
//		}
//		else{
//			for(int i = 0; i<= newID_int - oldID_int;i++){
//				if(isSprintPlan(descs, newID_int-i))
//					list.add(newID_int-i);
//			}
//		}
//
//		//將story的中sprint id做修改
//		if(list.size()!=0){
//			for(int i = 0;i<list.size(); i++){
//				if((i+1)!=list.size()){
//					String sprintID = String.valueOf(list.get(i));
//					String nextSprintID = String.valueOf(list.get(i+1));
//					List<IIssue> stories = map.get(sprintID);
//					if(stories!=null){
//						ArrayList<Long> total = convertTolong(stories);
//						pb.add(total, nextSprintID);
//					}
//				}
//				else{
//					String sprintID = String.valueOf(list.get(i));
//					String nextSprintID = String.valueOf(list.get(0));
//					List<IIssue> stories = map.get(sprintID);
//					if(stories!=null){
//						ArrayList<Long> total = convertTolong(stories);
//						pb.add(total, nextSprintID);
//					}
//				}
//			}
//		}
		
		StringBuilder result = new StringBuilder("true");
		
		return result;
	}
	
//	private ArrayList<Long> convertTolong(List<IIssue> stories){
//		ArrayList<Long> total = new ArrayList<Long>();
//		for(IIssue story:stories){
//			total.add(story.getIssueID());
//		}
//		return total;
//	}
//	
//	private boolean isSprintPlan(List<ISprintPlanDesc> descs, int iteration){
//		String iter = String.valueOf(iteration);
//		for(ISprintPlanDesc desc: descs){
//			if(desc.getID().equals(iter)){
//				return Boolean.TRUE;
//			}
//		}
//		return Boolean.FALSE;
//	}
	
}
