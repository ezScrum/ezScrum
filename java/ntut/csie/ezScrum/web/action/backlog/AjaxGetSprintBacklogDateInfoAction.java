package ntut.csie.ezScrum.web.action.backlog;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AjaxGetSprintBacklogDateInfoAction extends Action {
	private static Log log = LogFactory.getLog(AjaxGetSprintBacklogDateInfoAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Get Sprint Backlog Date.");
		
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		
		String sprintID = request.getParameter("sprintID");

		String result = (new SprintBacklogHelper(project, userSession, sprintID)).getAjaxGetSprintBacklogDateInfo();
		response.setContentType("text/html; charset=utf-8");
		try {
			response.getWriter().write(result);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
//		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper(sprintID);
////		SprintBacklog backlog = null;		
////		try {
////			// sprint 不存在，回傳最近的一個 sprint 或 空的 sprint
////			if (sprintID == null || sprintID.equals("")) {
////				backlog = new SprintBacklog(project, session);
////			} else {
////				backlog = new SprintBacklog(project, session, Integer.parseInt(sprintID));
////			}
////		} catch (Exception e) {
////			backlog = null;
////			// 已經處理過不必輸出 Exception
////		}
//		
////		SprintPlanHelper spHelper = new SprintPlanHelper(project);
//		String result = "";
//		// 建立 DateColumnStore 的資料
//		if ( (sprintBacklogMapper != null) && (sprintBacklogMapper.getSprintPlanId() > 0) ) {
//			Date StartDate = sprintBacklogMapper.getIterStartDate();
//			// 取得工作天數
//			int availableDays = sprintBacklogLogic.getAvailableDays(sprintID);
////			ISprintPlanDesc desc = spHelper.loadPlan(sprintBacklogMapper.getSprintPlanId());
////			int availableDays = Integer.parseInt(desc.getInterval()) * 5;		// 一個禮拜五天
//			
//			SprintBacklogHelper sprintHelper = new SprintBacklogHelper();
////			List<SprintBacklogDateColumn> cols = sprintHelper.getDateList(StartDate, availableDays);
//			List<SprintBacklogDateColumn> cols = (new SprintBacklogLogic(project, userSession)).calculateSprintBacklogDateList(StartDate, availableDays);
//			
//			Gson gson = new Gson();
//			result = gson.toJson(cols);
//			result = "{\"Dates\":" + result + "}";
//		} else {
//			// default data for null sprint backlog
//			result = "";
//		}
//		
//		response.setContentType("text/html; charset=utf-8");
//		try {
//			response.getWriter().write(result);
//			response.getWriter().close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		return null;
	}
}