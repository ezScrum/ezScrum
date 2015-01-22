package ntut.csie.ezScrum.issue.sql.service.core;

import junit.framework.TestCase;

public class ConfigurationTest extends TestCase{
	
	private Configuration configuration = null;
	
	public ConfigurationTest(String testMethod) {
		super(testMethod);
	}

	@Override
    protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		super.setUp();
    }

	@Override
    protected void tearDown() throws Exception {
	    super.tearDown();
	    
	    configuration.setTestMode(false);
		configuration.save();
		
		// ============= release ==============
		configuration = null;
    }

	
	public void testStore(){
		// save original value
		String originalServerUrl = configuration.getServerUrl();
		String originalDBAccount = configuration.getDBAccount();
		String originalDBType = configuration.getDBType();
		String originalDBName = configuration.getDBName();
		
		// expected value
		String expectServerUrl = "192.168.0.1";
		String expectDBAccount = "test01";
		String expectDBType= "Normal";
		String expectDBName = "ezscrum_test01";
		
		configuration.setServerUrl(expectServerUrl);
		configuration.setDBAccount(expectDBAccount);
		configuration.setDBType(expectDBType);
		configuration.setDBName(expectDBName);
		configuration.save();
		
		// assertions
		assertEquals(expectServerUrl, configuration.getServerUrl());
		assertEquals(expectDBAccount, configuration.getDBAccount());
		assertEquals(expectDBType, configuration.getDBType());
		assertEquals(expectDBName, configuration.getDBName());
		
		// restore original value
		configuration.setServerUrl(originalServerUrl);
		configuration.setDBAccount(originalDBAccount);
		configuration.setDBType(originalDBType);
		configuration.setDBName(originalDBName);
		configuration.save();
	}
}
