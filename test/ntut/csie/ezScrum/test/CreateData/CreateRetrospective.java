package ntut.csie.ezScrum.test.CreateData;

import java.sql.SQLException;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IScrumIssue;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.RetrospectiveMapper;

public class CreateRetrospective {
	private Configuration mConfig = new Configuration();
	
	private int mGoodCount = 1;
	private int mImproveCount = 1;
	
	private CreateProject mCP = null;
	private CreateSprint mCS = null;
	
	private List<IScrumIssue> mGoodRetrospectives;
	private List<IScrumIssue> mImproveRetrospectives;
	
	public String mGoodRetrospective = "TEST_RETROSPECTIVE_GOOD_";
	public String mImproveRetrospective = "TEST_RETROSPECTIVE_IMPROVE_";

	
	public CreateRetrospective(int goodCount, int improveCount, CreateProject CP, CreateSprint CS) {
		mGoodCount = goodCount;
		mImproveCount = improveCount;
		
		mCP = CP;
		mCS = CS;
	}

	public int getGoodRetrospectiveCount() {
		return mGoodCount;
	}

	public int getImproveRetrospectiveCount() {
		return mImproveCount;
	}	
	
	public List<IScrumIssue> getGoodRetrospectiveList() {		
		return mGoodRetrospectives;
	}
	
	public List<IScrumIssue> getImproveRetrospectiveList() {
		return mImproveRetrospectives;
	}	
	
	public void exe() throws SQLException {
		IUserSession userSession = mConfig.getUserSession();
		int projectCount = mCP.getProjectList().size();
		int sprintCount = mCS.getSprintCount();

		for (int i = 0; i < projectCount; i++) {
			ProjectObject project = mCP.getAllProjects().get(i); // get Project
			RetrospectiveMapper rm = new RetrospectiveMapper(project, userSession);

			for (int j = 0; j < sprintCount; j++) {
				Long sprintID = mCS.getSprintsId().get(j);
				// good
				for (int k = 0; k < mGoodCount; k++) {
					String prefix = mGoodRetrospective + String.valueOf(k + 1);
					rm.add(prefix, prefix + "_description", String.valueOf(sprintID), ScrumEnum.GOOD_ISSUE_TYPE);
				}
				mGoodRetrospectives = rm.getList(ScrumEnum.GOOD_ISSUE_TYPE);

				// improve
				for (int k = 0; k < mImproveCount; k++) {
					String prefix = mImproveRetrospective + String.valueOf(k + 1);
					rm.add(prefix, prefix + "_description", String.valueOf(sprintID), ScrumEnum.IMPROVEMENTS_ISSUE_TYPE);
				}
				mImproveRetrospectives = rm.getList(ScrumEnum.IMPROVEMENTS_ISSUE_TYPE);

				System.out.println("  " + project.getName() + " create sprint_" + sprintID + " with " + mGoodCount + " good retrospective success.");
				System.out.println("  " + project.getName() + " create sprint_" + sprintID + " with " + mImproveCount + " improve retrospective success.");
			}
		}
				
		System.out.println("Create " + String.valueOf(projectCount) + " project(s)" + String.valueOf(sprintCount) + " sprint(s)" + String.valueOf(mGoodCount+mImproveCount) + " Retrospective Finish!");
	}

	// 有做刪除動作後必須更新
	public void update() throws SQLException {
		IUserSession userSession = mConfig.getUserSession();
		int projectCount = mCP.getProjectList().size();
		int sprintCount = mCS.getSprintCount();

		for (int i = 0; i < projectCount; i++) {
			ProjectObject project = mCP.getAllProjects().get(i); // get Project
			RetrospectiveMapper rm = new RetrospectiveMapper(project, userSession);

			for (int j = 0; j < sprintCount; j++) {
				mGoodRetrospectives = rm.getList(ScrumEnum.GOOD_ISSUE_TYPE);
				mImproveRetrospectives = rm.getList(ScrumEnum.IMPROVEMENTS_ISSUE_TYPE);

				mGoodCount = mGoodRetrospectives.size();
				mImproveCount = mImproveRetrospectives.size();
			}
		}

	}

}
