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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;

public class ProjectLogic {
	private static Log log = LogFactory.getLog(ProjectLogic.class);
	private String system_Admin = "system_admin";

	public ProjectLogic() {}

	/**
	 * get all projects use DAO
	 * @return all project list
	 */
	public ArrayList<ProjectObject> getProjects() {
		ProjectMapper projectMapper = new ProjectMapper();
		return projectMapper.getAllProjects();
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
}
