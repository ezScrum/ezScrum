package ntut.csie.ezScrum.test.TestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;
import ntut.csie.ezScrum.web.action.rbac.AddUserActionTest;
import ntut.csie.ezScrum.web.action.rbac.DeleteAccountActionTest;

public class AccountActionTestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		
		// add user action
		suite.addTest(new AddUserActionTest("testexecute"));
//		suite.addTest(new AddUserActionTest("testexecute_Wrong1"));
//		suite.addTest(new AddUserActionTest("testexecute_Wrong2"));
//		suite.addTest(new AddUserActionTest("testexecute_Wrong3"));
		suite.addTest(new AddUserActionTest("testexecuteAdmin_Add"));
		
		// remove user action
		suite.addTest(new DeleteAccountActionTest("testexecute"));
		suite.addTest(new DeleteAccountActionTest("testexecuteAdmin_Remove1"));
		suite.addTest(new DeleteAccountActionTest("testexecuteAdmin_Remove2"));
		suite.addTest(new DeleteAccountActionTest("testexecuteAdmin_Remove3"));
		
		return suite;
	}	
	
	public static void main(String[] args) {
        junit.textui.TestRunner.run(AccountActionTestSuite.suite());
    }
}
