package ntut.csie.ezScrum.issue.sql.service.core;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationTest {
	private Configuration mConfig = null;
	
	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
    }

	@After
	public void tearDown() throws Exception {
	    mConfig.setTestMode(false);
		mConfig.save();
		// release resource
		mConfig = null;
    }

	@Test
	public void testStore(){
		// save original value
		String originalServerUrl = mConfig.getServerUrl();
		String originalDBAccount = mConfig.getDBAccount();
		String originalDBType = mConfig.getDBType();
		String originalDBName = mConfig.getDBName();
		
		// expected value
		String expectServerUrl = "192.168.0.1";
		String expectDBAccount = "test01";
		String expectDBType= "Normal";
		String expectDBName = "ezscrum_test01";
		
		mConfig.setServerUrl(expectServerUrl);
		mConfig.setDBAccount(expectDBAccount);
		mConfig.setDBType(expectDBType);
		mConfig.setDBName(expectDBName);
		mConfig.save();
		
		// assertions
		assertEquals(expectServerUrl, mConfig.getServerUrl());
		assertEquals(expectDBAccount, mConfig.getDBAccount());
		assertEquals(expectDBType, mConfig.getDBType());
		assertEquals(expectDBName, mConfig.getDBName());
		
		// restore original value
		mConfig.setServerUrl(originalServerUrl);
		mConfig.setDBAccount(originalDBAccount);
		mConfig.setDBType(originalDBType);
		mConfig.setDBName(originalDBName);
		mConfig.save();
	}
}
