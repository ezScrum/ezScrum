package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.resource.core.IProject;
import ntut.csie.jcis.resource.core.ResourceFacade;

public class AddSprintToRelease {
	private int EachCount = 1;
	private int ReleaseCount = 1;
	private int ProjectCount = 1;
	private CreateProject CP;
	private CreateRelease CR;
	private ProjectMapper projectMapper = new ProjectMapper();
	
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	
	public AddSprintToRelease(int count, CreateRelease cr, CreateProject cp) {
		this.EachCount = count;
		this.ReleaseCount = cr.getReleaseCount();
		this.ProjectCount = cp.getProjectList().size();
		this.CP = cp;
		this.CR = cr;
	}
	
	public void exe() throws Exception {
		
		for (int i=0 ; i<this.ProjectCount ; i++) {
			String projectName = this.CP.PJ_NAME + Integer.toString((i+1));	// TEST_PROJECT_X
//			IProject project = ResourceFacade.getWorkspace().getRoot().getProject(projectName);
			IProject project = this.projectMapper.getProjectByID(projectName);
			// 此路徑為開發端的   TestData/MyWorkspace/
			
			for (int j=0 ; j<this.ReleaseCount ; j++) {
				String releaseID = Integer.toString(j+1);
				createSprint(project, j, this.CR.getReleaseList().get(i).getStartDate());
				
				for (int k=0 ; k<this.EachCount ; k++) {
					System.out.println("專案 " + projectName + ", 第 " + releaseID + " 個 release 加入 sprint-" + ( (j*this.EachCount)+(k+1) ) + " 成功");
				}
			}		
		}
	}
	
	// 建立所需的 sprints
	private void createSprint(IProject Project, int ReleaseIndex, String StartDate) {
		Date SD = new Date(StartDate);
//		CreateSprint CS = new CreateSprint(this.EachCount, ReleaseIndex, SD, Project);
		CreateSprint CS = new CreateSprint(this.EachCount, ReleaseIndex, SD, this.CP);
		CS.exe();
	}
	
	// 依據 releaseID 回傳此 release 必須加入的 sprint list
//	private List<String> getSprintList(int ReleaseCount) {
//		List<String> sprintList = new ArrayList<String>();
//		
//		for (int i=0 ; i<this.EachCount ; i++) {
//			sprintList.add( Integer.toString( (ReleaseCount*this.EachCount) + i + 1 ) );
//		}	
//		return sprintList;
//	}
}
