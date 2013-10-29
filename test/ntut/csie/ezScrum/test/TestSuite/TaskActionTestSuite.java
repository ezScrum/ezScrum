package ntut.csie.ezScrum.test.TestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;
import ntut.csie.ezScrum.web.action.report.CheckOutTaskActionTest;
import ntut.csie.ezScrum.web.action.report.ShowTaskBoardActionTest;

public class TaskActionTestSuite {
	public static Test suite() {
		TestSuite suite= new TestSuite();
		
		// CheckOutTaskActionTest
		suite.addTest(new CheckOutTaskActionTest("testexecute"));
		suite.addTest(new CheckOutTaskActionTest("testWrongParameter1"));
		suite.addTest(new CheckOutTaskActionTest("testWrongParameter2"));
//		suite.addTest(new CheckOutTaskActionTest("testWrongParameter3"));
		
		// ShowCheckOutTaskActionTest
//		suite.addTest(new ShowCheckOutTaskActionTest("testexecute"));
//		suite.addTest(new ShowCheckOutTaskActionTest("testWrongParameter1"));
//		suite.addTest(new ShowCheckOutTaskActionTest("testWrongParameter2"));
		
		// ShowDoneIssueActionTest
//		suite.addTest(new ShowDoneIssueActionTest("testexecute"));
//		suite.addTest(new ShowDoneIssueActionTest("testWrongParameter1"));
//		suite.addTest(new ShowDoneIssueActionTest("testWrongParameter2"));
		
		// ShowEditTaskActionTest
//		suite.addTest(new ShowEditTaskActionTest("testexecute"));
//		suite.addTest(new ShowEditTaskActionTest("testWrongParameter1"));
//		suite.addTest(new ShowEditTaskActionTest("testWrongParameter2"));
//		suite.addTest(new ShowEditTaskActionTest("testWrongParameter3"));
//		suite.addTest(new ShowEditTaskActionTest("testWrongParameter4"));
		
		// ShowTaskBoardActionTest
		suite.addTest(new ShowTaskBoardActionTest("testexecute"));
//		suite.addTest(new ShowTaskBoardActionTest("testWrongParameter1"));
		suite.addTest(new ShowTaskBoardActionTest("testWrongParameter2"));
//		suite.addTest(new ShowTaskBoardActionTest("testWrongParameter3"));
		suite.addTest(new ShowTaskBoardActionTest("testMoveTask1"));
		suite.addTest(new ShowTaskBoardActionTest("testMoveTask2"));
		suite.addTest(new ShowTaskBoardActionTest("testMoveTask3"));
		suite.addTest(new ShowTaskBoardActionTest("testMoveTask4"));
		
		return suite;
	}
	
	public static void main(String[] args) {
        junit.textui.TestRunner.run(TaskActionTestSuite.suite());
    }

}
