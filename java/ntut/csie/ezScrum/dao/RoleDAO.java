package ntut.csie.ezScrum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.databaseEnum.ProjectEnum;
import ntut.csie.ezScrum.web.databaseEnum.ProjectRoleEnum;
import ntut.csie.ezScrum.web.databaseEnum.RoleEnum;
import ntut.csie.ezScrum.web.databaseEnum.ScrumRoleEnum;
import ntut.csie.ezScrum.web.databaseEnum.SystemEnum;

public class RoleDAO extends AbstractDAO<ProjectRole, ProjectRole>{

	
	private static RoleDAO sInstance = null;

	public static RoleDAO getInstance() {
		if (sInstance == null) {
			sInstance = new RoleDAO();
		}
		return sInstance;
	}
	/**
	 * Create map about user and role in each attend project
	 * 
	 * @param projectId
	 * @param accountId
	 * @param role
	 * @return isCreateSuccess
	 */
	
	
	public boolean createProjectRole(long projectId, long accountId,
			RoleEnum role) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectRoleEnum.TABLE_NAME);
		valueSet.addInsertValue(ProjectRoleEnum.PROJECT_ID, projectId);
		valueSet.addInsertValue(ProjectRoleEnum.ACCOUNT_ID, accountId);
		valueSet.addInsertValue(ProjectRoleEnum.ROLE, String.valueOf(role.ordinal()));
		valueSet.addInsertValue(ProjectRoleEnum.CREATE_TIME, String.valueOf(System.currentTimeMillis()));
		valueSet.addInsertValue(ProjectRoleEnum.UPDATE_TIME, String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getInsertQuery();
		return mControl.executeUpdate(query);
	}

	/**
	 * Get account access mapping each attend project
	 * 
	 * @param accountId
	 * @return account access map
	 */
	
	public HashMap<String, ProjectRole> getProjectRoleMap(long accountId) {
		StringBuilder query = new StringBuilder();
		query.append("select * from ").append(ProjectRoleEnum.TABLE_NAME)
				.append(" as pr").append(" cross join ")
				.append(ProjectEnum.TABLE_NAME).append(" as p on ")
				.append(ProjectRoleEnum.PROJECT_ID).append(" = p.")
				.append(ProjectEnum.ID).append(" cross join ")
				.append(ScrumRoleEnum.TABLE_NAME).append(" as sr on")
				.append(" pr.").append(ProjectRoleEnum.PROJECT_ID)
				.append(" = sr.").append(ScrumRoleEnum.PROJECT_ID)
				.append(" and pr.").append(ProjectRoleEnum.ROLE)
				.append(" = sr.").append(ScrumRoleEnum.ROLE).append(" where ")
				.append(ProjectRoleEnum.ACCOUNT_ID).append(" = ")
				.append(accountId);

		String queryString = query.toString();
		HashMap<String, ProjectRole> map = new HashMap<String, ProjectRole>();
//		ProjectRole systemRole = getSystemRole(accountId);
		ResultSet result = mControl.executeQuery(queryString);

		try {
//			if (systemRole != null) {
//				map.put("system", systemRole);
//			}
			while (result.next()) {
				map.put(result.getString(ProjectEnum.NAME),
						getProjectWithScrumRole(result));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return map;
	}

	/**
	 * Delete account's role in project
	 * 
	 * @param projectId
	 * @param accountId
	 * @param role
	 * @return isDeleteSuccess
	 */
	public boolean deleteProjectRole(long projectId, long accountId, RoleEnum role) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectRoleEnum.TABLE_NAME);
		valueSet.addEqualCondition(ProjectRoleEnum.PROJECT_ID, projectId);
		valueSet.addEqualCondition(ProjectRoleEnum.ACCOUNT_ID, accountId);
		valueSet.addEqualCondition(ProjectRoleEnum.ROLE, String.valueOf(role.ordinal()));
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}

	

	public ScrumRole convertScrumRole(String projectName, String role,
			ResultSet result) throws SQLException {
		ScrumRole scrumRole = new ScrumRole(projectName, role);
		scrumRole.setisGuest(RoleEnum.Guest == RoleEnum.valueOf(role));
		scrumRole.setAccessProductBacklog(result.getBoolean(ScrumRoleEnum.ACCESS_PRODUCT_BACKLOG));
		scrumRole.setAccessReleasePlan(result.getBoolean(ScrumRoleEnum.ACCESS_RELEASE_PLAN));
		scrumRole.setAccessReport(result.getBoolean(ScrumRoleEnum.ACCESS_REPORT));
		scrumRole.setAccessRetrospective(result.getBoolean(ScrumRoleEnum.ACCESS_RETROSPECTIVE));
		scrumRole.setAccessSprintBacklog(result.getBoolean(ScrumRoleEnum.ACCESS_SPRINT_BACKLOG));
		scrumRole.setAccessSprintPlan(result.getBoolean(ScrumRoleEnum.ACCESS_SPRINT_PLAN));
		scrumRole.setAccessUnplanItem(result.getBoolean(ScrumRoleEnum.ACCESS_UNPLAN));
		scrumRole.setAccessTaskBoard(result.getBoolean(ScrumRoleEnum.ACCESS_TASKBOARD));
		scrumRole.setAccessEditProject(result.getBoolean(ScrumRoleEnum.ACCESS_EDIT_PROJECT));
		return scrumRole;
	}

	public ProjectRole getProjectWithScrumRole(ResultSet result) throws SQLException {
		ProjectObject project = ProjectObject.get(result.getLong(ProjectRoleEnum.PROJECT_ID));
		RoleEnum role = RoleEnum.values()[result.getInt(ProjectRoleEnum.ROLE)];
		ScrumRole scrumRole = convertScrumRole(project.getName(), role.name(), result);

		return new ProjectRole(project, scrumRole);
	}
	@Override
	public long create(ProjectRole infoObject) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public ProjectRole get(long id) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean update(ProjectRole dataObject) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean delete(long id) {
		// TODO Auto-generated method stub
		return false;
	}
	/**
	 * Create project system role
	 * 
	 * @param accountId
	 * @return isCreateSuccess
	 */
	public boolean createSystemRole(long accountId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SystemEnum.TABLE_NAME);
		valueSet.addInsertValue(SystemEnum.ACCOUNT_ID, accountId);
		String query = valueSet.getInsertQuery();
		return mControl.executeUpdate(query);
	}

	/**
	 * 藉由 account id 判斷是否取出專案下的管理者帳號
	 * 
	 * @param accountId
	 * @return admin account's project role
	 */
	public ProjectRole getSystemRole(long accountId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SystemEnum.TABLE_NAME);
		valueSet.addEqualCondition(SystemEnum.ACCOUNT_ID, accountId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		ProjectRole projectRole = null;

		try {
			if (result.next()) {
				ProjectObject project = new ProjectObject(0, "system");
				project.setDisplayName("system")
				       .setComment("system")
					   .setManager("admin")
					   .setAttachFileSize(0)
					   .setCreateTime(0);
				ScrumRole scrumRole = new ScrumRole("system", "admin");
				scrumRole.setisAdmin(true);
				projectRole = new ProjectRole(project, scrumRole);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return projectRole;
	}

	/**
	 * Delete account's system role in project
	 * 
	 * @param accountId
	 * @return isDeleteSuccess
	 */
	public boolean deleteSystemRole(long accountId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SystemEnum.TABLE_NAME);
		valueSet.addEqualCondition(SystemEnum.ACCOUNT_ID, accountId);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}
}
