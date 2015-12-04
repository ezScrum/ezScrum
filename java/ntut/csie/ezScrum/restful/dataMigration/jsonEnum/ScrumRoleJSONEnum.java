package ntut.csie.ezScrum.restful.dataMigration.jsonEnum;

import ntut.csie.ezScrum.web.databaseEnum.RoleEnum;
import ntut.csie.ezScrum.web.databaseEnum.ScrumRoleEnum;

public class ScrumRoleJSONEnum {
	public static final String ROLE = ScrumRoleEnum.ROLE;
    public static final String ACCESS_PRODUCT_BACKLOG = ScrumRoleEnum.ACCESS_PRODUCT_BACKLOG;
    public static final String ACCESS_SPRINT_PLAN = ScrumRoleEnum.ACCESS_SPRINT_PLAN;
    public static final String ACCESS_TASKBOARD = ScrumRoleEnum.ACCESS_TASKBOARD;
    public static final String ACCESS_SPRINT_BACKLOG = ScrumRoleEnum.ACCESS_SPRINT_BACKLOG;
    public static final String ACCESS_RELEASE_PLAN = ScrumRoleEnum.ACCESS_RELEASE_PLAN;
    public static final String ACCESS_RETROSPECTIVE = ScrumRoleEnum.ACCESS_RETROSPECTIVE;
    public static final String ACCESS_UNPLAN = ScrumRoleEnum.ACCESS_UNPLAN;
    public static final String ACCESS_REPORT = ScrumRoleEnum.ACCESS_REPORT;
    public static final String ACCESS_EDIT_PROJECT = ScrumRoleEnum.ACCESS_EDIT_PROJECT;
    public static final String PROJECT_ID = ScrumRoleEnum.PROJECT_ID;
    public static final String PRODUCT_OWNER = RoleEnum.ProductOwner.name();
    public static final String SCRUM_MASTER = RoleEnum.ScrumMaster.name();
    public static final String SCRUM_TEAM = RoleEnum.ScrumTeam.name();
    public static final String STAKEHOLDER = RoleEnum.Stakeholder.name();
    public static final String GUEST = RoleEnum.Guest.name();
}
