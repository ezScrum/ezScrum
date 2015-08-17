package ntut.csie.ezScrum.web.support;


public class TranslateSpecialChar{
	/*
	 * replace: CharSequence target to CharSequence replacement 
	 * replaceAll: String  "regex" to String replacement
	 */
	
	public String TranslateDBChar(String tagName) {
		if (tagName != null) {
			if (tagName.contains("\\")) {
				tagName = tagName.replace("\\", "\\\\");
			}
			
			if (tagName.contains("'")) {
				tagName = tagName.replaceAll("'", "\\\\\'");
			}			
		}
		return tagName;
	}
	
	public String TranslateXMLChar(String tagName) {
		if (tagName != null) {
			if (tagName.contains("&")) {
				tagName = tagName.replaceAll("&", "&amp;");
			}
			
			if (tagName.contains("\"")) {
				tagName = tagName.replaceAll("\"", "&quot;");
			}
			
			if (tagName.contains("'")) {
				tagName = tagName.replaceAll("'", "&apos;");
			}
			
			if (tagName.contains("<")) {
				tagName = tagName.replaceAll("<", "&lt;");
			}
			
			if (tagName.contains(">")) {
				tagName = tagName.replaceAll(">", "&gt;");
			}
		}
		return tagName;
	}
	
	public String TranslateJSONChar(String tagName) {
		if (tagName != null) {

			if (tagName.contains("\\")) {
				tagName = tagName.replace("\\", "\\\\");
			}
			
			if (tagName.contains("\"")) {
				tagName = tagName.replaceAll("\"", "\\\"");
			}
			
			if (tagName.contains("\r")) {
				tagName = tagName.replaceAll("\r", "\\\\r");
			}
			
			if (tagName.contains("\n")) {
				tagName = tagName.replaceAll("\n", "\\\\n");
			}
			
			if (tagName.contains("<")) {
				tagName = tagName.replaceAll("<", "&lt;");
			}
			
			if (tagName.contains(">")) {
				tagName = tagName.replaceAll(">", "&gt;");
			}
		}
		return tagName;
	}
	
	public String HandleNullString(String str) {
		if(str.equals("") || str.equals("0") || str.equals("-1")) {
			return "None";
		}
		
		return str;
	}
}
