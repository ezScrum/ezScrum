package ntut.csie.ezScrum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.databasEnum.ProjectEnum;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;
import ntut.csie.ezScrum.web.databasEnum.ScrumRoleEnum;

public class ProjectDAO extends AbstractDAO<ProjectObject, ProjectObject> {

	private static ProjectDAO sInstance = null;

	public static ProjectDAO getInstance() {
		if (sInstance == null) {
			sInstance = new ProjectDAO();
		}
		return sInstance;
	}

	@Override
	public long create(ProjectObject project) {
		long createTime = System.currentTimeMillis();

		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectEnum.TABLE_NAME);
		valueSet.addInsertValue(ProjectEnum.NAME, project.getName());
		valueSet.addInsertValue(ProjectEnum.DISPLAY_NAME, project.getDisplayName());
		valueSet.addInsertValue(ProjectEnum.COMMENT, project.getComment());
		valueSet.addInsertValue(ProjectEnum.PRODUCT_OWNER, project.getManager());
		valueSet.addInsertValue(ProjectEnum.ATTATCH_MAX_SIZE, project.getAttachFileSize());
		valueSet.addInsertValue(ProjectEnum.CREATE_TIME, createTime);
		valueSet.addInsertValue(ProjectEnum.UPDATE_TIME, createTime);
		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);

		// create default role
		ScrumRole scrumRole;
		for (RoleEnum role : RoleEnum.values()) {
			scrumRole = new ScrumRole(role);
			createScrumRole(id, role, scrumRole);
		}

		return id;
	}

	public long createScrumRole(long projectId, RoleEnum role, ScrumRole scrumRole) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ScrumRoleEnum.TABLE_NAME);
		valueSet.addInsertValue(ScrumRoleEnum.PROJECT_ID, projectId);
		valueSet.addInsertValue(ScrumRoleEnum.ROLE, String.valueOf(role.ordinal()));
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_PRODUCT_BACKLOG, scrumRole.getAccessProductBacklog() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_RELEASE_PLAN, scrumRole.getAccessReleasePlan() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_REPORT, scrumRole.getReadReport() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_RETROSPECTIVE, scrumRole.getAccessRetrospective() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_SPRINT_BACKLOG, scrumRole.getAccessSprintBacklog() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_SPRINT_PLAN, scrumRole.getAccessSprintPlan() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_UNPLANNED, scrumRole.getAccessUnplannedItem() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_TASKBOARD, scrumRole.getAccessTaskBoard() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_EDIT_PROJECT, scrumRole.getEditProject() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.CREATE_TIME, String.valueOf(System.currentTimeMillis()));
		valueSet.addInsertValue(ScrumRoleEnum.UPDATE_TIME, String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getInsertQuery();
		return mControl.executeInsert(query);
	}

	public ScrumRole getScrumRole(long id, String projectName, RoleEnum role) {
		MySQLQuerySet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ScrumRoleEnum.TABLE_NAME);
		valueSet.addEqualCondition(ScrumRoleEnum.PROJECT_ID, id);
		valueSet.addEqualCondition(ScrumRoleEnum.ROLE, String.valueOf(role.ordinal()));
		String query = valueSet.getSelectQuery();

		ResultSet result = mControl.executeQuery(query);
		try {
			if (result.first()) {
				return convertScrumRole(projectName, role.name(), result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return null;
	}


	public boolean updateScrumRole(long projectId, RoleEnum role, ScrumRole scrumRole) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ScrumRoleEnum.TABLE_NAME);
		valueSet.addEqualCondition(ScrumRoleEnum.PROJECT_ID, projectId);
		valueSet.addEqualCondition(ScrumRoleEnum.ROLE, String.valueOf(role.ordinal()));
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_PRODUCT_BACKLOG, scrumRole.getAccessProductBacklog() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_RELEASE_PLAN, scrumRole.getAccessReleasePlan() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_REPORT, scrumRole.getReadReport() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_RETROSPECTIVE, scrumRole.getAccessRetrospective() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_SPRINT_BACKLOG, scrumRole.getAccessSprintBacklog() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_SPRINT_PLAN, scrumRole.getAccessSprintPlan() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_UNPLANNED, scrumRole.getAccessUnplannedItem() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_TASKBOARD, scrumRole.getAccessTaskBoard() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.ACCESS_EDIT_PROJECT, scrumRole.getEditProject() ? "1" : "0");
		valueSet.addInsertValue(ScrumRoleEnum.UPDATE_TIME, String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getUpdateQuery();
		return mControl.executeUpdate(query);
	}

	private ScrumRole convertScrumRole(String projectName, String role, ResultSet result) {
		ScrumRole scrumRole = new ScrumRole(projectName, role);
		scrumRole.setisGuest(RoleEnum.Guest == RoleEnum.valueOf(role));
		try {
			scrumRole.setAccessProductBacklog(result.getBoolean(ScrumRoleEnum.ACCESS_PRODUCT_BACKLOG));
			scrumRole.setAccessReleasePlan(result.getBoolean(ScrumRoleEnum.ACCESS_RELEASE_PLAN));
			scrumRole.setReadReport(result.getBoolean(ScrumRoleEnum.ACCESS_REPORT));
			scrumRole.setAccessRetrospective(result.getBoolean(ScrumRoleEnum.ACCESS_RETROSPECTIVE));
			scrumRole.setAccessSprintBacklog(result.getBoolean(ScrumRoleEnum.ACCESS_SPRINT_BACKLOG));
			scrumRole.setAccessSprintPlan(result.getBoolean(ScrumRoleEnum.ACCESS_SPRINT_PLAN));
			scrumRole.setAccessUnplannedItem(result.getBoolean(ScrumRoleEnum.ACCESS_UNPLANNED));
			scrumRole.setAccessTaskBoard(result.getBoolean(ScrumRoleEnum.ACCESS_TASKBOARD));
			scrumRole.setEditProject(result.getBoolean(ScrumRoleEnum.ACCESS_EDIT_PROJECT));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return scrumRole;
	}
	
	@Override
	public ProjectObject get(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectEnum.TABLE_NAME);
		valueSet.addEqualCondition(ProjectEnum.ID, id);
		String query = valueSet.getSelectQuery();

		ResultSet result = mControl.executeQuery(query);

		ProjectObject project = null;
		try {
			if (result.next()) {
				project = convertProject(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return project;
	}

	@Override
	public boolean update(ProjectObject project) {
		long currentTime = System.currentTimeMillis();

		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectEnum.TABLE_NAME);
		valueSet.addInsertValue(ProjectEnum.DISPLAY_NAME, project.getDisplayName());
		valueSet.addInsertValue(ProjectEnum.COMMENT, project.getComment());
		valueSet.addInsertValue(ProjectEnum.PRODUCT_OWNER, project.getManager());
		valueSet.addInsertValue(ProjectEnum.ATTATCH_MAX_SIZE, project.getAttachFileSize());
		valueSet.addInsertValue(ProjectEnum.UPDATE_TIME, currentTime);
		valueSet.addEqualCondition(ProjectEnum.ID, project.getId());
		String query = valueSet.getUpdateQuery();

		return mControl.executeUpdate(query);
	}

	@Override
	public boolean delete(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectEnum.TABLE_NAME);
		valueSet.addEqualCondition(ProjectEnum.ID, id);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}

	/**
	 * Get project by name
	 * 
	 * @param name project name
	 * @return ProjectObject
	 */
	public ProjectObject get(String name) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectEnum.TABLE_NAME);
		valueSet.addTextFieldEqualCondition(ProjectEnum.NAME, name);
		String query = valueSet.getSelectQuery();

		ResultSet result = mControl.executeQuery(query);

		ProjectObject project = null;
		try {
			if (result.next()) {
				project = convertProject(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return project;
	}

	public ArrayList<ProjectObject> getAllProjects() {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectEnum.TABLE_NAME);
		String query = valueSet.getSelectQuery();

		ResultSet result = mControl.executeQuery(query);

		ArrayList<ProjectObject> projects = new ArrayList<ProjectObject>();
		try {
			while (result.next()) {
				projects.add(convertProject(result));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			 closeResultSet(result);
		}
		return projects;
	}

	public ProjectObject convertProject(ResultSet result) throws SQLException {
		ProjectObject project = new ProjectObject(result.getLong(ProjectEnum.ID),
		        result.getString(ProjectEnum.NAME));
		project
		        .setDisplayName(result.getString(ProjectEnum.DISPLAY_NAME))
		        .setComment(result.getString(ProjectEnum.COMMENT))
		        .setManager(result.getString(ProjectEnum.PRODUCT_OWNER))
		        .setAttachFileSize(result.getLong(ProjectEnum.ATTATCH_MAX_SIZE))
		        .setCreateTime(result.getLong(ProjectEnum.CREATE_TIME))
		        .setUpdateTime(result.getLong(ProjectEnum.UPDATE_TIME));
		return project;
	}
}
