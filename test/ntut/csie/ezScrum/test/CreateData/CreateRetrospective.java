package ntut.csie.ezScrum.test.CreateData;

import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IScrumIssue;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.mapper.RetrospectiveMapper;
import ntut.csie.jcis.resource.core.IProject;

public class CreateRetrospective {
	private Configuration configuration = new Configuration();
	
	private int GoodCount = 1;
	private int ImproveCount = 1;
	
	private CreateProject CP = null;
	private CreateSprint CS = null;
	
	private List<IScrumIssue> GoodRetrospectiveList;
	private List<IScrumIssue> ImproveRetrospectiveList;
	
	public String GoodRetrospective = "TEST_RETROSPECTIVE_GOOD_";
	public String ImproveRetrospective = "TEST_RETROSPECTIVE_IMPROVE_";

	
	public CreateRetrospective(int goodCount, int improveCount, CreateProject cp, CreateSprint cs) {
		this.GoodCount = goodCount;
		this.ImproveCount = improveCount;
		
		this.CP = cp;
		this.CS = cs;
	}

	public int getGoodRetrospectiveCount() {
		return this.GoodCount;
	}

	public int getImproveRetrospectiveCount() {
		return this.ImproveCount;
	}	
	
	public List<IScrumIssue> getGoodRetrospectiveList() {		
		return this.GoodRetrospectiveList;
	}
	
	public List<IScrumIssue> getImproveRetrospectiveList() {
		return this.ImproveRetrospectiveList;
	}	
	
	public void exe() {
		IUserSession userSession = configuration.getUserSession();
		int projectCount = this.CP.getProjectList().size();
		int sprintCount = this.CS.getSprintCount();

		for (int i=0 ; i < projectCount; i++) {
			IProject project = this.CP.getProjectList().get(i);			// get Project
			RetrospectiveMapper rm = new RetrospectiveMapper(project, userSession);
			
			for (int j=0 ; j < sprintCount; j++) {
				String sprintID = this.CS.getSprintIDList().get(j);
				// good
				for (int k=0 ; k < this.GoodCount ; k++) {
					String prefix =this.GoodRetrospective + String.valueOf(k+1);
					rm.add(prefix, prefix + "_description", sprintID, ScrumEnum.GOOD_ISSUE_TYPE);
				}
				this.GoodRetrospectiveList = rm.getList(ScrumEnum.GOOD_ISSUE_TYPE);

				// improve
				for (int k=0 ; k < this.ImproveCount ; k++) {
					String prefix =this.ImproveRetrospective + String.valueOf(k+1);
					rm.add(prefix, prefix + "_description", sprintID, ScrumEnum.IMPROVEMENTS_ISSUE_TYPE);
				}		
				this.ImproveRetrospectiveList = rm.getList(ScrumEnum.IMPROVEMENTS_ISSUE_TYPE);

				System.out.println("  " + project.getName() + " create sprint_" + sprintID + " with " + this.GoodCount + " good retrospective success.");			
				System.out.println("  " + project.getName() + " create sprint_" + sprintID + " with " + this.ImproveCount + " improve retrospective success.");				
			}			
		}
				
		System.out.println("Create " + String.valueOf(projectCount) + " project(s)" + String.valueOf(sprintCount) + " sprint(s)" + String.valueOf(this.GoodCount+this.ImproveCount) + " Retrospective Finish!");
	}

	// 有做刪除動作後必須更新
	public void update() {
		IUserSession userSession = configuration.getUserSession();
		int projectCount = this.CP.getProjectList().size();
		int sprintCount = this.CS.getSprintCount();

		for (int i=0 ; i < projectCount; i++) {
			IProject project = this.CP.getProjectList().get(i);			// get Project
			RetrospectiveMapper rm = new RetrospectiveMapper(project, userSession);
			
			for (int j=0 ; j < sprintCount; j++) {
				this.GoodRetrospectiveList = rm.getList(ScrumEnum.GOOD_ISSUE_TYPE);
				this.ImproveRetrospectiveList = rm.getList(ScrumEnum.IMPROVEMENTS_ISSUE_TYPE);
				
				this.GoodCount = this.GoodRetrospectiveList.size();
				this.ImproveCount = this.ImproveRetrospectiveList.size();				
			}			
		}
		
	}
	
}
