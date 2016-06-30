package ntut.csie.ezScrum.restful.dataMigration.jsonEnum;

import ntut.csie.ezScrum.web.databaseEnum.ProjectEnum;

public class ProjectJSONEnum {
    public static final String TABLE_NAME = "project";
    public static final String NAME = ProjectEnum.NAME;
    public static final String DISPLAY_NAME = ProjectEnum.DISPLAY_NAME;
    public static final String COMMENT = ProjectEnum.COMMENT;
    public static final String PRODUCT_OWNER = ProjectEnum.PRODUCT_OWNER;
    public static final String ATTATCH_MAX_SIZE = ProjectEnum.ATTATCH_MAX_SIZE;
    public static final String SCRUM_ROLES = "scrum_roles";
    public static final String PROJECT_ROLES = "project_roles";
    public static final String TAGS = "tags";
    public static final String RELEASES = "releases";
    public static final String SPRINTS = "sprints";
    public static final String CREATE_TIME = ProjectEnum.CREATE_TIME;
    public static final String DROPPED_STORIES = "dropped_stories";
	public static final String DROPPED_TASKS = "dropped_tasks";
}
