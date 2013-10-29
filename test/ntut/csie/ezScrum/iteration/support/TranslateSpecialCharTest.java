package ntut.csie.ezScrum.iteration.support;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class TranslateSpecialCharTest extends TestCase{
	private TranslateSpecialChar translateSpecialChar = null;
	private List<String> testStr;
	private List<String> verifyStr;
	
	public TranslateSpecialCharTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		super.setUp();
		this.translateSpecialChar = new TranslateSpecialChar();
		this.testStr = new ArrayList<String>();
		this.verifyStr = new ArrayList<String>();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		this.translateSpecialChar = null;
		this.testStr.clear();
		this.verifyStr.clear();
	}
	
	public void testTranslateDBChar() throws Exception {
		testStr.add("\\");
		testStr.add("\\'");
		testStr.add("'");
		testStr.add("'\\");

		verifyStr.add("\\\\");
		verifyStr.add("\\\\''");
		verifyStr.add("''");
		verifyStr.add("''\\\\");
		
		for(int i = 0; i < testStr.size(); i++) {
			assertEquals(verifyStr.get(i), translateSpecialChar.TranslateDBChar(testStr.get(i)));
		}
	}
	
	public void testTranslateXMLChar() throws Exception {
		testStr.add("&");
		testStr.add("\"");
		testStr.add("<");
		testStr.add(">");
		testStr.add("<>");

		verifyStr.add("&amp;");
		verifyStr.add("&quot;");
		verifyStr.add("&lt;");
		verifyStr.add("&gt;");
		verifyStr.add("&lt;&gt;");
		
		for(int i = 0; i < testStr.size(); i++) {
			assertEquals(verifyStr.get(i), translateSpecialChar.TranslateXMLChar(testStr.get(i)));
		}
	}
	
	public void testTranslateJSONChar() throws Exception {
		testStr.add("\\");
		testStr.add("\"");
		testStr.add("\r");
		testStr.add("\n");
		
		verifyStr.add("\\\\");
		verifyStr.add("\\\"");
		verifyStr.add("\\r");
		verifyStr.add("\\n");
		
		for(int i = 0; i < testStr.size(); i++) {
			assertEquals(verifyStr.get(i), translateSpecialChar.TranslateJSONChar(testStr.get(i)));
		}
	}
	
	public void testHandleNullString() throws Exception {
		testStr.add("");
		testStr.add("0");
		testStr.add("-1");
		
		verifyStr.add("None");
		
		for(int i = 0; i < testStr.size(); i++) {
			assertEquals(verifyStr.get(0), translateSpecialChar.HandleNullString(testStr.get(i)));
		}
	}
	
}
