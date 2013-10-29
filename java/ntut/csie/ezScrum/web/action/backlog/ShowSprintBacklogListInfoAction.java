package ntut.csie.ezScrum.web.action.backlog;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.SecurityRequestProcessor;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowSprintBacklogListInfoAction extends Action {
	private static Log log = LogFactory.getLog(ShowSprintBacklogListInfoAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Show Sprint Backlog List Information in ShowSprintBacklogListInfo.");
		
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		
		String sprintID = request.getParameter("sprintID");
		
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, userSession, sprintID);
		String reponseText = this.reContructString( sprintBacklogHelper.getSprintBacklogListInfoText() );
		
//		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, userSession);
//		SprintBacklogMapper backlog = sprintBacklogLogic.getSprintBacklogMapper(sprintID);
//		
//		// 取得工作天數
//		int availableDays = sprintBacklogLogic.getAvailableDays(sprintID);
//		
//		String reponseText = "";
//		if (backlog != null) {
//			reponseText = getTreeSprintBacklogStr(backlog, availableDays);
//		}else{
//			return null;
//		}
		
		response.setContentType("text/html; charset=utf-8");
		try {
			response.getWriter().write(reponseText);
			LogFactory.getLog(SecurityRequestProcessor.class).debug("Current Time : " + new Date().toString());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;

//		if (backlog != null) {
//			response.setContentType("text/html; charset=utf-8");
//			try {
//				response.getWriter().write(getTreeSprintBacklogStr(backlog, availableDays));
//				LogFactory.getLog(SecurityRequestProcessor.class).debug("Current Time : " + new Date().toString());
//				response.getWriter().close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		return null;
	}

	
	// 重建 json 字串讓資料可以對應 ext TreeGrid Column 
	private String reContructString(String jsonStr){
		while(jsonStr.contains("\"dateToHourMap\":{")){
			int headIndex = jsonStr.indexOf("\"dateToHourMap\":{");
			int endIndex = jsonStr.indexOf("}", headIndex) + 2;
			String oriSubStr = jsonStr.substring(headIndex, endIndex);
			String changeSubStr = oriSubStr.replace("\"dateToHourMap\":{", "");
			changeSubStr = changeSubStr.replace("}", "");
			if (changeSubStr.equals(","))
				changeSubStr = "";
			
			jsonStr = jsonStr.replace(oriSubStr, changeSubStr);
		}
		
		return jsonStr;
	}
	
//	private String getTreeSprintBacklogStr(SprintBacklogMapper backlog, int availableDays) {
//		List<SprintBacklogTreeStructure> SBtree = new ArrayList<SprintBacklogTreeStructure>();
//		
//		if (backlog.getSprintPlanId() > 0) {
//			List<IIssue> stories = backlog.getStoriesByImp();
//			Map<Long, IIssue[]> map = backlog.getTasksMap();
//			
//			// 取得 Sprint 日期的 Column
//			SprintBacklogHelper sprintHelper = new SprintBacklogHelper();
//			List<SprintBacklogDateColumn> cols = null;
//			if (sprintHelper.GetCurrentDateColumns() == null)
//				cols = sprintHelper.getDateList(backlog.getIterStartDate(), availableDays);
//			else
//				cols = sprintHelper.GetCurrentDateColumns();
//			
//			for (IIssue story : stories) {
//				SprintBacklogTreeStructure tree = new SprintBacklogTreeStructure(story, map.get(Long.valueOf(story.getIssueID())), sprintHelper.GetCurrentDateList());
//				SBtree.add(tree);
//			}
//		} else {
//			// null sprint backlog
//			SprintBacklogTreeStructure tree = new SprintBacklogTreeStructure();
//			SBtree.add(tree);
//		}
//		
//		Gson gson = new Gson();
//		return reContructString(gson.toJson(SBtree));
//	}
}