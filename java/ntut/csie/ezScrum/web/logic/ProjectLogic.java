/*
 * Copyright (C) 2005 Chin-Yun Hsieh <hsieh@csie.ntut.edu.tw> Yu Chin Cheng <yccheng@csie.ntut.edu.tw> Chien-Tsun Chen <ctchen@ctchen.idv.tw> Tsui-Chen She <kay_sher@hotmail.com> Chia-Hao
 * Wu<chwu2004@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307,
 * USA.
 */

package ntut.csie.ezScrum.web.logic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.service.IssueBacklog;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.support.ProjectComparator;
import ntut.csie.jcis.account.core.internal.Account;
import ntut.csie.jcis.core.ISystemPropertyEnum;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProjectLogic {
	private static Log log = LogFactory.getLog(ProjectLogic.class);
	private String system_Admin = "system_admin";

	public ProjectLogic() {}

	/**
	 * 透過Project Mapper取得所有專案
	 * 
	 * @return
	 */
	private List<IProject> getAllProjectList() {
		ProjectMapper projectMapper = new ProjectMapper();
		return projectMapper.getAllProjectList();
	}

	// ezScrum v1.8
	private List<ProjectObject> getAllProjectListForDb() {
		ProjectMapper projectMapper = new ProjectMapper();
		return projectMapper.getProjectListForDb();
	}

	/**
	 * 有排序過的所有專案資訊。
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<IProject> getAllProjects() {
		List<IProject> list = this.getAllProjectList();

		Collections.sort(list, new ProjectComparator(ProjectComparator.COMPARE_TYPE_NAME));

		return list;
	}

	public List<ProjectObject> getAllProjectsForDb() {
		// ezScrum v1.8
		List<ProjectObject> list = this.getAllProjectListForDb();
		return list;
	}

	/**
	 * 比對專案和帳號
	 * 
	 * @param project
	 * @param userSession
	 * @return
	 */
	public boolean isUserExistInProject(IProject project, IUserSession userSession) {
		// String adminAccountID = userSession.getAccount().getID();
		// if( adminAccountID.equalsIgnoreCase("admin")||adminAccountID.equalsIgnoreCase("administrator") ){
		// return true;
		// }
		
//		IPermission permAdmin = (new AccountMapper()).getPermission("system", "admin");
//		if (userSession.getAccount().checkPermission(permAdmin)) {
//			return true;
//		}
//
//		// AccountMapper accountMapper = new AccountMapper();
//		// List<IAccount> projectMemberList = accountMapper.getProjectMemberList(userSession, project);
//		List<IAccount> projectMemberList = (new ProjectMapper()).getProjectMemberList(userSession, project);
//		boolean existedInProject = false;
//		for (IAccount account : projectMemberList) {
//			String accountID = account.getID();
//			if (userSession.getAccount().getAccount().equals(accountID)) {
//				existedInProject = true;
//				break;
//			}
//		}
//		return existedInProject;
		
		// ezScrum v1.8
		AccountObject account = userSession.getAccount();
		ScrumRole scrumRole = new ScrumRoleLogic().getScrumRole(project, account);
		if (scrumRole != null) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 比對專案是否存在
	 * ezScrum v1.8
	 * 
	 * @param projectID
	 * @return
	 */
	public boolean isProjectExisted(String projectID) {
		List<ProjectObject> projects = this.getAllProjectListForDb();
		
		for (ProjectObject project : projects) {
			String PID = project.getName();
			if (PID.equals(projectID)) {
				// System.out.println( PID );
				return true;
			}
		}
		return false;
	}

	/**
	 * 取得特定帳號的權限。
	 * 
	 * @param account
	 * @return
	 */
	public Map<String, Boolean> getProjectPermissionMap(AccountObject account) {
//		Map<String, Boolean> map = new HashMap<String, Boolean>();
//
//		// IAccount account = userSession.getAccount();
//		// IAccountManager manager = AccountFactory.getManager();
//		// IPermission permAdmin = manager.getPermission(this.system_Admin);
//
//		IPermission permAdmin = (new AccountMapper()).getPermission(this.system_Admin);
//		List<ProjectInformation> list = this.getAllProjectListForDb();
//
//		// 如果為系統管理員，那麼將可以瀏覽所有的Project。
//		// 因此所有皆設為True
//		if (account.checkPermission(permAdmin)) {
//			Iterator<ProjectInformation> ir = list.iterator();
//
//			while (ir.hasNext()) {
//				ProjectInformation project = (ProjectInformation) ir.next();
//				map.put(project.getName(), Boolean.TRUE);
//			}
//
//			return map;
//		}
//
//		Iterator<ProjectInformation> ir = list.iterator();
//
//		AccountMapper accountMapper = new AccountMapper();
//
//		while (ir.hasNext()) {
//			IProject project = (IProject) ir.next();
//
//			IPermission permProjectPO = accountMapper.getPermission(project.getName(), ScrumEnum.SCRUMROLE_PRODUCTOWNER);	// productOwner
//			IPermission permProjectSM = accountMapper.getPermission(project.getName(), ScrumEnum.SCRUMROLE_SCRUMMASTER);	// ScrumMaster
//			IPermission permProjectST = accountMapper.getPermission(project.getName(), ScrumEnum.SCRUMROLE_SCRUMTEAM);		// ScrumTeam
//			IPermission permProjectSO = accountMapper.getPermission(project.getName(), ScrumEnum.SCRUMROLE_STAKEHOLDER);	// Stakeholder
//			IPermission permProjectGuest = accountMapper.getPermission(project.getName(), ScrumEnum.SCRUMROLE_GUEST);		// guest
//
//			// check 是否可以讀取專案
//			if (account.checkPermission(permProjectPO)) {
//				map.put(project.getName(), Boolean.TRUE);
//				continue;
//			} else if (account.checkPermission(permProjectSM)) {
//				map.put(project.getName(), Boolean.TRUE);
//				continue;
//			} else if (account.checkPermission(permProjectST)) {
//				map.put(project.getName(), Boolean.TRUE);
//				continue;
//			} else if (account.checkPermission(permProjectSO)) {
//				map.put(project.getName(), Boolean.TRUE);
//				continue;
//			} else if (account.checkPermission(permProjectGuest)) {
//				map.put(project.getName(), Boolean.TRUE);
//				continue;
//			}
//
//			map.put(project.getName(), Boolean.FALSE);
//		}
//
//		return map;
		// ezScrum v1.8
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		List<ProjectObject> list = this.getAllProjectListForDb();
		
		// check 是否可以讀取專案
		Iterator<ProjectObject> ir = list.iterator();

		while (ir.hasNext()) {
			ProjectObject project = (ProjectObject) ir.next();
			ScrumRole scrumRole = new ScrumRoleLogic().getScrumRole(project, account);
			if (scrumRole != null) {
				map.put(project.getName(), Boolean.TRUE);
			} else {
				map.put(project.getName(), Boolean.FALSE);
			}
		}
		return map;
	}

	/**
	 * 回傳過濾可以讓使用者回報 issue 的專案，條件為此專案存在且存在一筆以上的 issue type 為 public
	 * 
	 * @return
	 */
	public IProject[] getAllCustomProjects() {
		List<ProjectObject> CustomProjects = new ArrayList<ProjectObject>();	// 可以讓使用者回報 issue 的專案
		List<ProjectObject> projects = getAllProjectListForDb();

		for (ProjectObject P : projects) {
//			IssueBacklog IB = new IssueBacklog(P, new UserSession(new Account("guest")));
			IssueBacklog IB = new IssueBacklog(P, new UserSession(null));

			if (IB.isReportProject()) {
				CustomProjects.add(P);
			}
		}

		if ((CustomProjects != null) && (CustomProjects.size() > 0)) {
			return CustomProjects.toArray(new IProject[CustomProjects.size()]);
		} else {
			return null;
		}
	}

	/**
	 * ezScrum v1.8 不需要這個 檢查 metadata 有需要初始化的檔案，複製一份到專案設定檔內。
	 */
	public void cloneDefaultFile() {
		String[] checkFilesName = {"ScrumRole.xml"};

		String workspace_matadata = System.getProperty(ISystemPropertyEnum.WORKSPACE_PATH) + File.separator + "_metadata" + File.separator;

		List<IProject> projects = this.getAllProjects();

		for (IProject p : projects) {
			String project_path = p.getFullPath().getPathString();
			for (String filename : checkFilesName) {
				String checkfile = project_path + File.separator + "_metadata" + File.separator + filename;
				String clonefile = workspace_matadata + filename;
				try {
					ProjectMapper projectMapper = new ProjectMapper();
					projectMapper.checkAndClone(checkfile, clonefile);
				} catch (IOException e) {
					log.debug("clonefile path = " + clonefile + " is not exist");
					log.debug("exception " + e.toString());
				}
			}
		}
	}

	//	/** ezScrum v1.8 不需要這個
	//	 * 檢查 role base 裡面是否4個 scrum 角色 + 1個 guest 角色都有
	//	 */
	//	public void check_default_role() {
	//		List<ProjectInformation> projectlist = this.getAllProjectList();
	//		for (ProjectInformation project : projectlist) {
	//			try {
	//				AccountMapper accountMapper = new AccountMapper();
	//				accountMapper.createPermission(project);
	//				accountMapper.createRole(project);
	//			} catch (Exception e) {
	//				e.printStackTrace();
	//			}
	//		}
	//	}

	//	/** ezScrum v1.8 不需要這個
	//	 * 檢查各專案的 scrum role 是否有 guest 此角色
	//	 */
	//	public void check_role_guest() {
	//		// ScrumRoleManager srm = new ScrumRoleManager();
	//		ScrumRoleMapper scrumRoleMapper = new ScrumRoleMapper();
	//		List<ProjectInformation> projectlist = this.getAllProjectList();
	//		for (ProjectInformation p : projectlist) {
	//			ScrumRole guestRole = null;
	//			try {
	//				// guestRole = srm.getPermission(p.getName(), "Guest");
	//				guestRole = scrumRoleMapper.getPermission(p.getName(), "Guest");
	//			} catch (Exception e) {
	//				guestRole = new ScrumRole(p.getName(), "Guest");
	//				guestRole.setAccessKanbanBacklog(false);
	//				guestRole.setAccessKanbanBoard(false);
	//				guestRole.setAccessKanbanReport(false);
	//				guestRole.setAccessManageStatus(false);
	//				guestRole.setAccessProductBacklog(false);
	//				guestRole.setAccessReleasePlan(false);
	//				guestRole.setAccessRetrospective(false);
	//				guestRole.setAccessSprintBacklog(false);
	//				guestRole.setAccessSprintPlan(false);
	//				guestRole.setAccessTaskBoard(false);
	//				guestRole.setAccessUnplannedItem(false);
	//				guestRole.setEditProject(false);
	//				guestRole.setisGuest(true);
	//				guestRole.setReadReport(false);
	//				// srm.update(guestRole);
	//				scrumRoleMapper.update(guestRole);
	//				log.info("Project " + guestRole.getProjectName() + " add a new guest role.");
	//			}
	//		}
	//	}

	// public ProjectInfoForm getProjectInfoForm(IProject project) {
	// //IProjectDescription desc = project.getProjectDesc();
	// ProjectMapper projectMapper = new ProjectMapper();
	// IProjectDescription desc =projectMapper.getProjectInfoForm(project);
	//
	// ProjectInfoForm form = new ProjectInfoForm();
	// String fileSize = desc.getAttachFileSize();
	// if(fileSize==null||fileSize.compareTo("")==0)
	// form.setAttachFileSize("2");
	// else
	// form.setAttachFileSize(desc.getAttachFileSize());
	// form.setName(desc.getName());
	// form.setDisplayName(desc.getDisplayName());
	// form.setComment(desc.getComment());
	// form.setCreateDate(desc.getCreateDate());
	// form.setProjectManager(desc.getProjectManager());
	// form.setOutputPath(desc.getOutput().getPathString());
	// form.setProjectManager(desc.getProjectManager());
	// form.setState(desc.getState());
	// form.setVersion(desc.getVersion());
	//
	// ICVS cvs = desc.getCVS();
	// form.setServerType(cvs.getServerType());
	// form.setCvsConnectionType(cvs.getConnectionType());
	// form.setCvsHost(cvs.getHost());
	// //form.setCvsModuleName(cvs.getModuleName());
	// form.setCvsUserID(cvs.getUserID());
	// form.setCvsRepositoryPath(cvs.getRepositoryPath()+"/"+cvs.getModuleName());
	// form.setCvsPassword(cvs.getPassword());
	// form.setSvnHook(cvs.getSvnHook());
	//
	// log.info("Version:" + form.getVersion());
	//
	// form.setSourcePaths(getSourceStrings(desc.getSrc()));
	//
	// log.info("External Library length:"
	// + desc.getExternalReferences().length);
	//
	// log.info("Proejct Reference length:"
	// + desc.getProjectReferences().length);
	// log.info("Source Path length:" + desc.getProjectReferences().length);
	//
	// return form;
	// }
	//
	// private static String[] getSourceStrings(IPath[] paths) {
	// String[] sourceArray = new String[paths.length];
	//
	// for (int i = 0; i < paths.length; i++) {
	// sourceArray[i] = paths[i].getPathString();
	// }
	//
	// return sourceArray;
	// }
}
