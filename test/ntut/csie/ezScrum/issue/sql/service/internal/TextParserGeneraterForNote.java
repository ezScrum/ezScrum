package ntut.csie.ezScrum.issue.sql.service.internal;

public class TextParserGeneraterForNote {
	private String id = "";
	private String Importance = "";
	private String Estimation = "";
	private String Value = "";
	private String HowToDemo = "";
	private String Notes = "";
	
	private String noteText = "";
	
//	private String text = "<JCIS id=\"20101130183351\">"+
//							"<Importance>80</Importance>"+
//							"<Estimation>6</Estimation>"+
//							"<Value>50</Value>"+
//							"<HowToDemo>TEST_STORY_DEMO_10</HowToDemo>"+
//							"<Notes>TEST_STORY_NOTE_10</Notes>"+
//							"</JCIS>";
	
//	private String text = "<JCIS id=\"20101202111549\">"+
//						  "<Importance>200</Importance>"+
//						  "<Estimation>21</Estimation>"+
//						  "<Value>300</Value>"+
//						  "<HowToDemo>demo_1</HowToDemo>"+
//						  "<Notes>note_1</Notes>"+
//						  "</JCIS>"+
//						  "<JCIS id=\"20101202111549\">"+
//						  "<Importance>250</Importance>"+
//						  "<Estimation>13</Estimation>"+
//						  "<Value>150</Value>"+
//						  "<HowToDemo>demo_11</HowToDemo>"+
//						  "<Notes>note_11</Notes>"+
//						  "</JCIS>";	 
	
	public TextParserGeneraterForNote(){
		
	}
	
	public String generaterNoteText(String imp, String est, String value, String htd, String note){
		String noteText = "<JCIS id=\"20101321132100\">"+
		  				  "<Importance>" + imp + "</Importance>"+
						  "<Estimation>" + est + "</Estimation>"+
						  "<Value>" + value + "</Value>"+
						  "<HowToDemo>" + htd + "</HowToDemo>"+  //htd = HowToDemo
						  "<Notes>" + note + "</Notes>"+
						  "</JCIS>";
		return noteText;
	}
	
	public void parserNoteText(String text){
		this.noteText = text;
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
		int headIndex = this.noteText.lastIndexOf(tagHead) + tagHead.length();
		int tailIndex = this.noteText.lastIndexOf(tagTail);
		
		String tagValue = this.noteText.substring(headIndex, tailIndex);
//		System.out.println("subString: "+this.noteText.substring(headIndex, tailIndex));

		return tagValue;
	}
	
	public void setId(String textId){
		this.id = textId;
	}
	
	public String getId(){
		return this.id;
	}
	
	public void setImportance(String textImportance){
		this.Importance = textImportance;
	}
	
	public String getImportance(){
		return this.Importance;
	}

	public void setEstimation(String textEstimation){
		this.Estimation = textEstimation;
	}
	
	public String getEstimation(){
		return this.Estimation;
	}
	
	public void setValue(String textValue){
		this.Value = textValue;
	}
	
	public String getValue(){
		return this.Value;
	}
	
	public void setHowToDemo(String textHowToDemo){
		this.HowToDemo = textHowToDemo;
	}
	
	public String getHowToDemo(){
		return this.HowToDemo;
	}
	
	public void setNotes(String textNotes){
		this.Notes = textNotes;
	}
	
	public String getNotes(){
		return this.Notes;
	}
	
}