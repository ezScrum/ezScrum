package ntut.csie.ezScrum.issue.sql.service.internal;

public class TextParserGeneraterForNote {
	private String mId = "";
	private String mImportance = "";
	private String mEstimation = "";
	private String mValue = "";
	private String mHowToDemo = "";
	private String mNotes = "";
	private String mNoteText = ""; 
	
	public TextParserGeneraterForNote(){
	}
	
	public String generaterNoteText(String imp, String est, String value, String htd, String note){
		String noteText = "<JCIS id=\"20101321132100\">"+
		  				  "<Importance>" + imp + "</Importance>"+
						  "<Estimation>" + est + "</Estimation>"+
						  "<Value>" + value + "</Value>"+
						  "<HowToDemo>" + htd + "</HowToDemo>"+  // htd = HowToDemo
						  "<Notes>" + note + "</Notes>"+
						  "</JCIS>";
		return noteText;
	}
	
	public void parserNoteText(String text){
		this.mNoteText = text;
		this.setImportance(getTagValue("Importance"));
		this.setEstimation(getTagValue("Estimation"));
		this.setValue(getTagValue("Value"));
		this.setHowToDemo(getTagValue("HowToDemo"));
		this.setNotes(getTagValue("Notes"));
	}
	
	// get latest tag's value
	private String getTagValue(String tagName){
		String tagHead = "<"+tagName+">";
		String tagTail = "</"+tagName+">";
		int headIndex = this.mNoteText.lastIndexOf(tagHead) + tagHead.length();
		int tailIndex = this.mNoteText.lastIndexOf(tagTail);
		String tagValue = this.mNoteText.substring(headIndex, tailIndex);
		return tagValue;
	}
	
	public void setId(String textId){
		this.mId = textId;
	}
	
	public String getId(){
		return this.mId;
	}
	
	public void setImportance(String textImportance){
		this.mImportance = textImportance;
	}
	
	public String getImportance(){
		return this.mImportance;
	}

	public void setEstimation(String textEstimation){
		this.mEstimation = textEstimation;
	}
	
	public String getEstimation(){
		return this.mEstimation;
	}
	
	public void setValue(String textValue){
		this.mValue = textValue;
	}
	
	public String getValue(){
		return this.mValue;
	}
	
	public void setHowToDemo(String textHowToDemo){
		this.mHowToDemo = textHowToDemo;
	}
	
	public String getHowToDemo(){
		return this.mHowToDemo;
	}
	
	public void setNotes(String textNotes){
		this.mNotes = textNotes;
	}
	
	public String getNotes(){
		return this.mNotes;
	}
}