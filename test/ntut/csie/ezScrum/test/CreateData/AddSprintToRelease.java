package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.resource.core.IProject;

public class AddSprintToRelease {
	private int mEachCount = 1;
	private int mReleaseCount = 1;
	private int mProjectCount = 1;
	private CreateProject mCP;
	private CreateRelease mCR;
	private CreateSprint mCS;
	private ProjectMapper mProjectMapper = new ProjectMapper();
	private ArrayList<CreateSprint> createSprints = new ArrayList<CreateSprint>();
	
	public AddSprintToRelease(int count, CreateRelease CR, CreateProject CP) {
		mEachCount = count;
		mReleaseCount = CR.getReleaseCount();
		mProjectCount = CP.getProjectList().size();
		mCP = CP;
		mCR = CR;
	}
	
	public void exe() throws Exception {
		for (int i=0 ; i<mProjectCount ; i++) {
			String projectName = mCP.mProjectName + Integer.toString((i+1));	// TEST_PROJECT_X
            // IProject project = ResourceFacade.getWorkspace().getRoot().getProject(projectName);
			IProject project = mProjectMapper.getProjectByID(projectName);
			// 此路徑為開發端的   TestData/MyWorkspace/
			
			for (int j=0 ; j<mReleaseCount ; j++) {
				String releaseID = Integer.toString(j+1);
				createSprint(project, j, mCR.getReleaseList().get(i).getStartDate());
				
				for (int k=0 ; k<mEachCount ; k++) {
					System.out.println("專案 " + projectName + ", 第 " + releaseID + " 個 release 加入 sprint-" + ( (j*mEachCount)+(k+1) ) + " 成功");
				}
			}		
		}
	}
	
	// 建立所需的 sprints
	private void createSprint(IProject Project, int ReleaseIndex, String StartDate) {
		Date SD = new Date(StartDate);
        // CreateSprint CS = new CreateSprint(EachCount, ReleaseIndex, SD, Project);
		mCS = new CreateSprint(mEachCount, ReleaseIndex, SD, mCP);
		mCS.exe();
		// add to list
		createSprints.add(mCS);
	}
	
	public ArrayList<CreateSprint> getCreateSprintsList(){
		return createSprints;
	}
	
	// 依據 releaseID 回傳此 release 必須加入的 sprint list
//	private List<String> getSprintList(int ReleaseCount) {
//		List<String> sprintList = new ArrayList<String>();
//		
//		for (int i=0 ; i<EachCount ; i++) {
//			sprintList.add( Integer.toString( (ReleaseCount*EachCount) + i + 1 ) );
//		}	
//		return sprintList;
//	}
}
