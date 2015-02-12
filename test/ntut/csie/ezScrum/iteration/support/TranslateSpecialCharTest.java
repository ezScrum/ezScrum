package ntut.csie.ezScrum.iteration.support;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TranslateSpecialCharTest {
	private TranslateSpecialChar mTranslateSpecialChar = null;
	private List<String> mTestStr;
	private List<String> mVerifyStr;
	
	@Before
	public void setUp() throws Exception {
		mTranslateSpecialChar = new TranslateSpecialChar();
		mTestStr = new ArrayList<String>();
		mVerifyStr = new ArrayList<String>();
	}
	
	@After
	public void tearDown() throws Exception {
		mTranslateSpecialChar = null;
		mTestStr.clear();
		mVerifyStr.clear();
	}
	
	@Test
	public void testTranslateDBChar() throws Exception {
		mTestStr.add("\\");
		mTestStr.add("\\'");
		mTestStr.add("'");
		mTestStr.add("'\\");

		mVerifyStr.add("\\\\");
		mVerifyStr.add("\\\\''");
		mVerifyStr.add("''");
		mVerifyStr.add("''\\\\");
		
		for(int i = 0; i < mTestStr.size(); i++) {
			assertEquals(mVerifyStr.get(i), mTranslateSpecialChar.TranslateDBChar(mTestStr.get(i)));
		}
	}
	
	@Test
	public void testTranslateXMLChar() throws Exception {
		mTestStr.add("&");
		mTestStr.add("\"");
		mTestStr.add("<");
		mTestStr.add(">");
		mTestStr.add("<>");

		mVerifyStr.add("&amp;");
		mVerifyStr.add("&quot;");
		mVerifyStr.add("&lt;");
		mVerifyStr.add("&gt;");
		mVerifyStr.add("&lt;&gt;");
		
		for(int i = 0; i < mTestStr.size(); i++) {
			assertEquals(mVerifyStr.get(i), mTranslateSpecialChar.TranslateXMLChar(mTestStr.get(i)));
		}
	}
	
	@Test
	public void testTranslateJSONChar() throws Exception {
		mTestStr.add("\\");
		mTestStr.add("\"");
		mTestStr.add("\r");
		mTestStr.add("\n");
		
		mVerifyStr.add("\\\\");
		mVerifyStr.add("\\\"");
		mVerifyStr.add("\\r");
		mVerifyStr.add("\\n");
		
		for(int i = 0; i < mTestStr.size(); i++) {
			assertEquals(mVerifyStr.get(i), mTranslateSpecialChar.TranslateJSONChar(mTestStr.get(i)));
		}
	}
	
	@Test
	public void testHandleNullString() throws Exception {
		mTestStr.add("");
		mTestStr.add("0");
		mTestStr.add("-1");
		
		mVerifyStr.add("None");
		
		for(int i = 0; i < mTestStr.size(); i++) {
			assertEquals(mVerifyStr.get(0), mTranslateSpecialChar.HandleNullString(mTestStr.get(i)));
		}
	}
	
}
