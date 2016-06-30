package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.Date;

public class AddSprintToRelease {
	private int mEachCount = 1;
	private int mReleaseCount = 1;
	private int mProjectCount = 1;
	private CreateProject mCP;
	private CreateRelease mCR;
	private CreateSprint mCS;
	private ArrayList<CreateSprint> createSprints = new ArrayList<CreateSprint>();
	
	public AddSprintToRelease(int count, CreateRelease CR, CreateProject CP) {
		mEachCount = count;
		mReleaseCount = CR.getReleaseCount();
		mProjectCount = CP.getAllProjects().size();
		mCP = CP;
		mCR = CR;
	}
	
	public void exe() {
		for (int i=0 ; i<mProjectCount ; i++) {
			String projectName = mCP.mProjectName + Integer.toString((i+1));
			for (int j=0 ; j<mReleaseCount ; j++) {
				String releaseID = Integer.toString(j+1);
				createSprint(j, mCR.getReleases().get(i).getStartDateString());
				
				for (int k=0 ; k<mEachCount ; k++) {
					System.out.println("專案 " + projectName + ", 第 " + releaseID + " 個 release 加入 sprint-" + ( (j*mEachCount)+(k+1) ) + " 成功");
				}
			}		
		}
	}
	
	// 建立所需的 sprints
	@SuppressWarnings("deprecation")
	private void createSprint(int ReleaseIndex, String StartDate) {
		Date SD = new Date(StartDate);
		mCS = new CreateSprint(mEachCount, ReleaseIndex, SD, mCP);
		mCS.exe();
		// add to list
		createSprints.add(mCS);
	}
	
	public ArrayList<CreateSprint> getCreateSprintsList(){
		return createSprints;
	}
}
