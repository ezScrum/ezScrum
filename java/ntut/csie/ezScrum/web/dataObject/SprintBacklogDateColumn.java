package ntut.csie.ezScrum.web.dataObject;

public class SprintBacklogDateColumn {
	private String Id;
	private String Name;
	
	public SprintBacklogDateColumn(String ID, String name) {
		this.Id = ID;
		this.Name = name;
	}
	
	public String GetColumnName(){
		return this.Name;
	}
}
