package ntut.csie.ezScrum.test.TestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;
import ntut.csie.ezScrum.web.action.plan.RemoveReleasePlanActionTest;
import ntut.csie.ezScrum.web.action.plan.SaveReleasePlanActionTest;

public class ReleaseActionTestSuite {
	public static Test suite() {
		TestSuite suite= new TestSuite();
		
		// RemoveReleasePlanActionTest
		suite.addTest(new RemoveReleasePlanActionTest("testexecute"));
//		suite.addTest(new RemoveReleasePlanActionTest("testexecuteWrongParameter"));
		
		// SaveReleasePlanActionTest
		suite.addTest(new SaveReleasePlanActionTest("testexecuteSave"));
		suite.addTest(new SaveReleasePlanActionTest("testexecuteEdit"));
//		suite.addTest(new SaveReleasePlanActionTest("testexecuteNullParameter"));
		suite.addTest(new SaveReleasePlanActionTest("testexecuteWrongParameter1"));
		suite.addTest(new SaveReleasePlanActionTest("testexecuteWrongParameter2"));
		
		// ShowEditReleasePlanActionTest
//		suite.addTest(new ShowEditReleasePlanActionTest("testexecute"));
//		suite.addTest(new ShowEditReleasePlanActionTest("testexecuteNullParameter"));
//		suite.addTest(new ShowEditReleasePlanActionTest("testexecuteWrongParameter1"));
//		suite.addTest(new ShowEditReleasePlanActionTest("testexecuteWrongParameter2"));
		
		return suite;
	}
	
	public static void main(String[] args) {
        junit.textui.TestRunner.run(ReleaseActionTestSuite.suite());
    }

}
