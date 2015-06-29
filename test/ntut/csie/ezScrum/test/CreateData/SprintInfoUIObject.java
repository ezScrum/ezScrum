package ntut.csie.ezScrum.test.CreateData;

public class SprintInfoUIObject {
	public int ID = 0;
	public String SprintGoal = "";
	public double CurrentStoryPoint = 0d;
	public double CurrentTaskPoint = 0d;
	public String ReleaseID = "Release None";
	public boolean isCurrentSprint = false;
	
	public SprintInfoUIObject() {
	}
	
	public SprintInfoUIObject(int ID, String goal, double sp, double tp, String rid, boolean current) {
		this.ID = ID;
		this.SprintGoal = goal;
		this.CurrentStoryPoint = sp;
		this.CurrentTaskPoint = tp;
		this.ReleaseID = "Release #" + rid;
		this.isCurrentSprint = current;
	}
}