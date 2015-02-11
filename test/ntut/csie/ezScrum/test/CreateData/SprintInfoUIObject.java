package ntut.csie.ezScrum.test.CreateData;

public class SprintInfoUIObject {
	public int mId = 0;
	public String mSprintGoal = "";
	public double mCurrentStoryPoint = 0d;
	public double mCurrentTaskPoint = 0d;
	public String mReleaseID = "Release None";
	public boolean mIsCurrentSprint = false;
	
	public SprintInfoUIObject() {
	}
	
	public SprintInfoUIObject(int id, String goal, double sp, double tp, String rid, boolean current) {
		mId = id;
		mSprintGoal = goal;
		mCurrentStoryPoint = sp;
		mCurrentTaskPoint = tp;
		mReleaseID = "Release #" + rid;
		mIsCurrentSprint = current;
	}
}