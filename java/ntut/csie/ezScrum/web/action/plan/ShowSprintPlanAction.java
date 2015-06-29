package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.logic.AccountLogic;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowSprintPlanAction extends Action {
	// --------------------------------------------------------- Instance
	// Variables
	private static Log log = LogFactory.getLog(ShowSprintPlanAction.class);
	// --------------------------------------------------------- Methods
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		// get Account, ScrumRole
		AccountObject account = session.getAccount();
//		ScrumRole sr = new ScrumRoleManager().getScrumRole(project, acc);
		ScrumRole sr = new ScrumRoleLogic().getScrumRole(project, account);
//		MantisAccountMapper accountHelper = new MantisAccountMapper(project, session);
//		
//		// 檢查帳號不通過，提示錯誤頁面		    // 檢查此帳號是否允許操作  action 的權限
//		if (!( accountHelper.CheckAccount(request) && sr.getAccessSprintPlan() )) {
//			return mapping.findForward("permissionDenied");
//		}
		
		AccountLogic accountLogic = new AccountLogic();
		
		// 檢查帳號不通過，提示錯誤頁面		    // 檢查此帳號是否允許操作  action 的權限
		if (!( accountLogic.checkAccount(request) && sr.getAccessSprintPlan() )) {
			return mapping.findForward("permissionDenied");
		}
		
		log.info("project=" + project.getName());

		SprintPlanHelper SPhelper = new SprintPlanHelper(project);
		List<ISprintPlanDesc> descs = null;
		List<String> totalSprintID = new ArrayList<String>();
		HashMap<String, Boolean> hm = new HashMap<String, Boolean>(); // 用於判斷此 sprint是否有 story
		Map<String, Boolean> permissionMap = new HashMap<String, Boolean>(); // 用來判斷sprint是不是out of sprint

		int currentSprint = SPhelper.getCurrentSprintID();

		if (SPhelper.loadListPlans() != null) {
			if (SPhelper.loadListPlans().size() > 0) {

				descs = SPhelper.loadListPlans();

				Map<String, String> pointMap = new HashMap<String, String>();

				for (ISprintPlanDesc desc : descs) {
					SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, Long.parseLong(desc.getID()));
					SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
					permissionMap.put(desc.getID(), !sprintBacklogLogic.isOutOfSprint());

					hm.put(desc.getID(), true);

					pointMap.put(desc.getID(), " / " + sprintBacklogMapper.getLimitedPoint());
					if (!sprintBacklogLogic.isOutOfSprint() && Integer.parseInt(desc.getID()) != currentSprint)
						totalSprintID.add(desc.getID());
				}
				request.setAttribute("pointMap", pointMap);
			}
		}
		request.setAttribute("totalSprintID", totalSprintID);
		request.setAttribute("permissionMap", permissionMap);
		request.setAttribute("currentSprint", currentSprint);
		request.setAttribute("SprintPlan", descs);
		request.setAttribute("StoryInfoMap", hm);
		
		return mapping.findForward("success");
	}

}
