package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataInfo.ProjectInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;

public class CreateProject {
	private int mProjectCount = 1;
	public String mProjectName = "TEST_PROJECT_";			// TEST_PROJECT_X
	public String mProjectDisplayName = "TEST_DISPLAYNAME_";// TEST_DISPLAYNAME_X
	public String mProjectMaNager = "Project_Manager_";		// Project_Manager_X
	public String mProjectCommon = "This is Test Project - ";// This is Test Project - X

	private ProjectMapper mProjectMapper;
	
	public CreateProject(int count) {
		mProjectCount = count;
		mProjectMapper = new ProjectMapper();
	}
	
	// ezScrum v1.8
	public ArrayList<ProjectObject> getAllProjects() {
		return mProjectMapper.getAllProjects();
	}

	// ezScrum v1.8
	public void exeCreateForDb() {
		ProjectMapper projectMapper = new ProjectMapper();
		ProjectInfo projectInfo = new ProjectInfo();
		for (int i = 0; i < mProjectCount; i++) {
			projectInfo.name = mProjectName + (i + 1);				// TEST_PROJECT_X
			projectInfo.displayName = mProjectDisplayName + (i + 1);// TEST_DISPLAYNAME_X
			projectInfo.common = mProjectCommon + (i + 1);			// This is Test Project - X
			projectInfo.manager = mProjectMaNager + (i + 1);		// Project_Manager_X
			
			projectMapper.createProject(projectInfo.name, projectInfo);
		}
	}
}
