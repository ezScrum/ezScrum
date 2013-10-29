package ntut.csie.ezScrum.test.TestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;
import ntut.csie.ezScrum.web.action.backlog.AddExistedStoryActionTest;
import ntut.csie.ezScrum.web.action.backlog.AjaxAddNewStoryActionTest;
import ntut.csie.ezScrum.web.action.backlog.sprint.AjaxRemoveStoryActionTest;
import ntut.csie.ezScrum.web.action.plan.AddStorytoReleaseActionTest;
import ntut.csie.ezScrum.web.action.plan.AjaxMoveStorySprintActionTest;


public class StoryActionTestSuite {
	public static Test suite() {
	    TestSuite suite= new TestSuite();

	    // AddExistedStoryActionTest
	    suite.addTest(new AddExistedStoryActionTest("testexecute"));
//	    suite.addTest(new AddExistedStoryActionTest("testexecuteNoReleaseParameter"));
//	    suite.addTest(new AddExistedStoryActionTest("testexecuteNoStoryParameter"));
	    suite.addTest(new AddExistedStoryActionTest("testAdd_Story_And_Release_Relation"));
	    
	    // AddStorytoReleaseActionTest
	    // ===================== 此 action 似乎尚未被使用 ===========================
	    suite.addTest(new AddStorytoReleaseActionTest("testexecute"));
	    
	    // AjaxAddNewStoryActionTest
	    suite.addTest(new AjaxAddNewStoryActionTest("testexecute"));
	    
	    // AjaxRemoveStoryActionTest
	    suite.addTest(new AjaxRemoveStoryActionTest("testRemoveStory"));
	    
	    // ShowExistedStoryActionTest
//	    suite.addTest(new ShowExistedStoryActionTest("testexecute"));
//	    suite.addTest(new ShowExistedStoryActionTest("testexecuteSprint"));
//	    suite.addTest(new ShowExistedStoryActionTest("testexecuteWrongParameter1"));
//	    suite.addTest(new ShowExistedStoryActionTest("testexecuteWrongParameter2"));
//	    suite.addTest(new ShowExistedStoryActionTest("testexecuteAssociate1"));
//	    suite.addTest(new ShowExistedStoryActionTest("testexecuteAssociate2"));
//	    suite.addTest(new ShowExistedStoryActionTest("testexecuteAssociate3"));
	    
	    //AjaxMoveStorySprintActionTest
	    suite.addTest(new AjaxMoveStorySprintActionTest("testexecute"));
	    
	    //AjaxAddNewTagActionTestSuite
//	    suite.addTest(new AjaxAddNewTagActionTest("testaddComma"));
//		suite.addTest(new AjaxAddNewTagActionTest("testaddExistTag"));
//		suite.addTest(new AjaxAddNewTagActionTest("testaddNewTag"));
	    
	    return suite;
	}
	
	public static void main(String[] args) {
        junit.textui.TestRunner.run(StoryActionTestSuite.suite());
    }

}
