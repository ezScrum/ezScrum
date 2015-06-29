package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.logic.AccountLogic;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.core.util.DateUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowReleasePlanAction extends Action {
	private static Log log = LogFactory.getLog(ShowReleasePlanAction.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		// get Account, ScrumRole
		AccountObject account = session.getAccount();
//		ScrumRole sr = new ScrumRoleManager().getScrumRole(project, acc);
		ScrumRole sr = new ScrumRoleLogic().getScrumRole(project, account);
//		MantisAccountMapper accountHelper = new MantisAccountMapper(project, session);
//		
//		// 檢查帳號不通過，提示錯誤頁面		    // 檢查此帳號是否允許操作  action 的權限
//		if (!( accountHelper.CheckAccount(request) && sr.getAccessReleasePlan() )) {
//			return mapping.findForward("permissionDenied");
//		}
		
		AccountLogic accountLogic = new AccountLogic();
		
		// 檢查帳號不通過，提示錯誤頁面		    // 檢查此帳號是否允許操作  action 的權限
		if (!( accountLogic.checkAccount(request) && sr.getAccessReleasePlan() )) {
			return mapping.findForward("permissionDenied");
		}
		
		log.info("project=" + project.getName());

		ReleasePlanHelper RPhelper = new ReleasePlanHelper(project);
		
		List<IReleasePlanDesc> releaseDescs = RPhelper.loadReleasePlansList();
		List<IReleasePlanDesc> ListReleaseDescs = new ArrayList<IReleasePlanDesc>();
		
		// ListReleaseDescs 依照 StartDate 排序
		for (IReleasePlanDesc desc : releaseDescs) {
			Date addDate = DateUtil.dayFilter(desc.getStartDate());			// 要新增的 Date
			
			if (ListReleaseDescs.size() > 0) {
				int index = 0;
				for (index=0 ; index<ListReleaseDescs.size() ; index++) {
					IReleasePlanDesc Desc = ListReleaseDescs.get(index);		// 目前要被比對的 relase
					Date cmpDate = DateUtil.dayFilter(Desc.getStartDate());		// 要被比對的 Date
					if ( addDate.compareTo(cmpDate) < 0 ) {
						break;
					}
				}
				ListReleaseDescs.add(index, desc);
			} else {
				ListReleaseDescs.add(desc);
			}
		}
	
		request.setAttribute("ReleasePlan", ListReleaseDescs.toArray(new IReleasePlanDesc[ListReleaseDescs.size()]));
		
		// 用於判斷此 release 是否有 sprint id 的資訊
		HashMap<String, Boolean> hmT = new HashMap<String, Boolean>();
		for (IReleasePlanDesc IRdesc : releaseDescs) {
			if (IRdesc.getSprintDescList() != null) {
				hmT.put(IRdesc.getID(), true);
			}
		}
		request.setAttribute("SprintInfoMap", hmT);
		
		return mapping.findForward("success");
	}
}