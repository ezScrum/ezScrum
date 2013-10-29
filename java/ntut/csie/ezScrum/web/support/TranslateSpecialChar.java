package ntut.csie.ezScrum.web.support;


public class TranslateSpecialChar{
	/*
	 * replace: CharSequence target to CharSequence replacement 
	 * replaceAll: String  "regex" to String replacement
	 */
	
	public String TranslateDBChar(String tagname)
	{
		if (tagname != null) {
			if (tagname.contains("\\")) {
				tagname = tagname.replace("\\", "\\\\");
			}
			
			if (tagname.contains("'")) {
				tagname = tagname.replaceAll("'", "\\\\'");
			}			
		}
		
		return tagname;
	}
	
	public String TranslateXMLChar(String tagname)
	{
		if (tagname != null) {
			if (tagname.contains("&")) {
				tagname = tagname.replaceAll("&", "&amp;");
			}
			
			if (tagname.contains("\"")) {
				tagname = tagname.replaceAll("\"", "&quot;");
			}
			
			if (tagname.contains("'")) {
				tagname = tagname.replaceAll("'", "&apos;");
			}
			
			if (tagname.contains("<")) {
				tagname = tagname.replaceAll("<", "&lt;");
			}
			
			if (tagname.contains(">")) {
				tagname = tagname.replaceAll(">", "&gt;");
			}
		}
		
		return tagname;
	}
	
	public String TranslateJSONChar(String tagname)
	{
		if (tagname != null) {

			if (tagname.contains("\\")) {
				tagname = tagname.replace("\\", "\\\\");
			}
			
			if (tagname.contains("\"")) {
				tagname = tagname.replaceAll("\"", "\\\\\"");
			}
			
			if (tagname.contains("\r")) {
				tagname = tagname.replaceAll("\r", "\\\\r");
			}
			
			if (tagname.contains("\n")) {
				tagname = tagname.replaceAll("\n", "\\\\n");
			}
			
			if (tagname.contains("<")) {
				tagname = tagname.replaceAll("<", "&lt;");
			}
			
			if (tagname.contains(">")) {
				tagname = tagname.replaceAll(">", "&gt;");
			}
		}
		
		return tagname;
	}
	
	public String HandleNullString(String str) {
		if(str.equals("") || str.equals("0") || str.equals("-1")) {
			return "None";
		}
		
		return str;
	}
}
