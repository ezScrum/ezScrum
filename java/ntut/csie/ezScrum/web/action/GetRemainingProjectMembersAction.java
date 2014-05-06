package ntut.csie.ezScrum.web.action;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IActor;
import ntut.csie.jcis.account.core.IRole;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetRemainingProjectMembersAction extends Action {
	private static Log log = LogFactory.getLog(GetRemainingProjectMembersAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {
		log.info("Get Remaining Project Members in GetRemainingProjectMembersAction.java");

		// get project member list
		IProject project = (IProject) request.getSession().getAttribute("Project");
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");

//		List<IAccount> accounts = (new ProjectMapper()).getProjectMemberList(userSession, project);
		
//		// get total member list
//		List<IActor> accountList = (new AccountMapper()).getAccountList();
//		StringBuilder result = new StringBuilder();
//		result.append("<Members>");
//		for(IActor total_member_list : accountList){
//			if(! accounts.contains(total_member_list) && !total_member_list.getID().equals("admin")){
//				IAccount member = (IAccount) total_member_list;
//				result.append("<Member>");
//				result.append("<ID>" + member.getID() + "</ID>");
//				result.append("<Name>" + member.getName() + "</Name>");
//				result.append("<Role>" + splitRole(project, member.getRoles()) + "</Role>");
//				result.append("<Enable>" + member.getEnable() + "</Enable>");
//				result.append("</Member>");
//			}
//		}
//		result.append("</Members>");
		
		// ezScrum v1.8
		// get total member list
		List<UserObject> accounts = new ProjectMapper().getProjectMemberListForDb(project.getName());
		List<UserObject> accountList = (new AccountMapper()).getAccountList();
		StringBuilder result = new StringBuilder();
		result.append("<Members>");
//		for (UserObject total_member_list : accountList) {
		for (UserObject member : accountList) {
			if (!accounts.contains(member) && !member.getAccount().equals("admin")) {
//				UserObject member = (UserObject) total_member_list;
				result.append("<Member>");
				result.append("<ID>").append(member.getAccount()).append("</ID>");
				result.append("<Name>").append(member.getName()).append("</Name>");
//				result.append("<Role>").append(splitRole(project, member.getRoles())).append("</Role>");
				result.append("<Role>").append(member.getRoles().get(project.getName()).getScrumRole().getRoleName()).append("</Role>");
				result.append("<Enable>").append(member.getEnable()).append("</Enable>");
				result.append("</Member>");
			}
		}
		result.append("</Members>");

		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(result.toString());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

//	private String splitRole(IProject project, IRole[] roles) {
//		String split_role = "";
//
//		if (roles.length > 0) {
//			for (IRole role : roles) {
//				// 將專案的角色以切字串方式取出
//				String[] token = role.getRoleId().split(project.getName() + "_");
//				if ((token.length == 2) && (token[1].length() > 0)) {
//					// 取得此專案的角色即可
//					split_role = token[1];
//					break;
//				}
//			}
//		}
//
//		return split_role;
//	}
}
