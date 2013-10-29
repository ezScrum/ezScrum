package ntut.csie.ezScrum.test.TestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SprintActionTestSuite {
	
	public static Test suite() {
		TestSuite suite= new TestSuite();
		
		// AddSprintToReleaseActionTest
//		suite.addTest(new AddSprintToReleaseActionTest("testexecute"));
//		suite.addTest(new AddSprintToReleaseActionTest("testexecuteWrongParameter1"));
//		suite.addTest(new AddSprintToReleaseActionTest("testexecuteWrongParameter2"));
//		suite.addTest(new AddSprintToReleaseActionTest("testAddSprintAndStoryToRelease"));
		
		// RemoveSprintPlanOfReleasePlanActiontTest
//		suite.addTest(new RemoveSprintPlanOfReleasePlanActiontTest("testexecute"));
//		suite.addTest(new RemoveSprintPlanOfReleasePlanActiontTest("test_Story_Releation_When_Remove_Sprints"));
		
		// ShowExistedSprintActionTest
//		suite.addTest(new ShowExistedSprintActionTest("testexecute"));
//		suite.addTest(new ShowExistedSprintActionTest("testexecuteWrongParameter"));
		
		
		return suite;
	}
	
	public static void main(String[] args) {
        junit.textui.TestRunner.run(SprintActionTestSuite.suite());
    }

}
