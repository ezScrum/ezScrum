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
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.support.ProjectComparator;
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

	/**
	 * get all projects use DAO
	 * @return all project list
	 */
	public ArrayList<ProjectObject> getProjects() {
		ProjectMapper projectMapper = new ProjectMapper();
		return projectMapper.getAllProjects();
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

	/**
	 * 比對專案和帳號
	 * 
	 * @param project
	 * @param userSession
	 * @return
	 */
	public boolean isUserExistInProject(ProjectObject project, IUserSession userSession) {
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
	 * @param projectName
	 * @return
	 */
	public boolean isProjectExisted(String projectName) {
		ArrayList<ProjectObject> projects = getProjects();
		for (ProjectObject project : projects) {
			String name = project.getName();
			if (name.equals(projectName)) {
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
		// ezScrum v1.8
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		List<ProjectObject> list = this.getProjects();
		
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
}
