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
		mNoteText = text;
		setImportance(getTagValue("Importance"));
		setEstimation(getTagValue("Estimation"));
		setValue(getTagValue("Value"));
		setHowToDemo(getTagValue("HowToDemo"));
		setNotes(getTagValue("Notes"));
	}
	
	// get latest tag's value
	private String getTagValue(String tagName){
		String tagHead = "<"+tagName+">";
		String tagTail = "</"+tagName+">";
		int headIndex = mNoteText.lastIndexOf(tagHead) + tagHead.length();
		int tailIndex = mNoteText.lastIndexOf(tagTail);
		String tagValue = mNoteText.substring(headIndex, tailIndex);
		return tagValue;
	}
	
	public void setId(String textId){
		mId = textId;
	}
	
	public String getId(){
		return mId;
	}
	
	public void setImportance(String textImportance){
		mImportance = textImportance;
	}
	
	public String getImportance(){
		return mImportance;
	}

	public void setEstimation(String textEstimation){
		mEstimation = textEstimation;
	}
	
	public String getEstimation(){
		return mEstimation;
	}
	
	public void setValue(String textValue){
		mValue = textValue;
	}
	
	public String getValue(){
		return mValue;
	}
	
	public void setHowToDemo(String textHowToDemo){
		mHowToDemo = textHowToDemo;
	}
	
	public String getHowToDemo(){
		return mHowToDemo;
	}
	
	public void setNotes(String textNotes){
		mNotes = textNotes;
	}
	
	public String getNotes(){
		return mNotes;
	}
}