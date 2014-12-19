package ntut.csie.ezScrum.web.dataInfo;

import ntut.csie.ezScrum.iteration.core.ScrumEnum;

public class StoryInfo {
	
	private String stroyID;
	private String name;
	private String importance ;
	private String estimation ;
	private String value ;
	private String howToDemo ;
	private String notes ;
	private String description = "";
	private String sprintID;
	private String tagIDs;
	private String releaseID;
	public StoryInfo(){
	}
	public StoryInfo(String name, String importance, String estimation,
			String value, String howToDemo, String notes, String description,
			String sprintID, String releaseID, String tagIDs) {
		this.setName(name);
		this.setImportance(importance);
		this.setEstimation(estimation);
		this.setValue(value);
		this.setHowToDemo(howToDemo);
		this.setNotes(notes);
		this.setDescription(description);
		this.setSprintID(sprintID);
		this.setTagIDs(tagIDs);
		this.setReleaseID(releaseID);
	}
	
	public StoryInfo(String storyID, String name, String importance, String estimation,
			String value, String howToDemo, String notes, String description,
			String sprintID, String releaseID, String tagIDs) {
		this.setStroyID(storyID);
		this.setName(name);
		this.setImportance(importance);
		this.setEstimation(estimation);
		this.setValue(value);
		this.setHowToDemo(howToDemo);
		this.setNotes(notes);
		this.setDescription(description);
		this.setSprintID(sprintID);
		this.setTagIDs(tagIDs);
		this.setReleaseID(releaseID);
	}
	
	public String getStroyID() {
		return stroyID;
	}
	public void setStroyID(String stroyID) {
		if( stroyID == null || stroyID.equals("") ){
			this.stroyID = "-1";
		}else{
			this.stroyID = stroyID;
		}
	}
	public void setName(String name) {
		if( name == null ){
			this.name = "";
		}else{
			this.name = name;
		}
	}
	public String getName() {
		return name;
	}
	public void setImportance(String importance) {
		if( importance == null || importance.equals("") ){
			this.importance = "0";
		}else{
			this.importance = importance;
		}
	}
	public String getImportance() {
		return importance;
	}
	public void setEstimation(String estimation) {
		if( estimation == null || estimation.equals("") ){
			this.estimation = "0";
		}else{
			this.estimation = estimation;
		}
	}
	public String getEstimation() {
		return estimation;
	}
	public void setValue(String value) {
		if( value == null || value.equals("") ){
			this.value = "0";
		}else{
			this.value = value;
		}
	}
	public String getValue() {
		return value;
	}
	public void setHowToDemo(String howToDemo) {
		if( howToDemo == null ){
			this.howToDemo = "";
		}else{
			this.howToDemo = howToDemo;
		}
	}
	public String getHowToDemo() {
		return howToDemo;
	}
	public void setNotes(String notes) {
		if( notes == null ){
			this.notes = "";
		}else{
			this.notes = notes;
		}
	}
	public String getNotes() {
		return notes;
	}
	public void setDescription(String description) {
		if( description == null ){
			this.description = "";
		}else{
			this.description = description;
		}
	}
	public String getDescription() {
		return description;
	}
	public void setSprintID(String sprintID) {
		if( sprintID == null || sprintID.equals("") ){
//			this.sprintID = "-1";
			this.sprintID = ScrumEnum.DIGITAL_BLANK_VALUE;
		}else{
			this.sprintID = sprintID;
		}
	}
	public void setReleaseID(String releaseID) {
		if( releaseID == null || releaseID.equals("") ){
//			this.releaseID = "-1";
			this.releaseID = ScrumEnum.DIGITAL_BLANK_VALUE;
		}else{
			this.releaseID = releaseID;
		}
	}
	public String getSprintID() {
		return sprintID;
	}

	public void setTagIDs(String tagIDs) {
		if( tagIDs == null || tagIDs.equals("") ){
			this.tagIDs = "";
		}else{
			this.tagIDs = tagIDs;
		}
	}
	/**
	 * @return the tagIDs
	 */
	public String getTagIDs() {
		return tagIDs;
	}
	public String getReleaseID() {
		return this.releaseID;
	}
	
}
