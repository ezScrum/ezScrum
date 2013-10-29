package ntut.csie.ezScrum.test.TestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;
import ntut.csie.ezScrum.web.action.backlog.product.ProductBacklogHelperTest;
import ntut.csie.ezScrum.web.control.AccountHelperTest;
import ntut.csie.ezScrum.web.control.ProjectHelperTest;
import ntut.csie.ezScrum.web.control.ReleasePlanHelperTest;
import ntut.csie.ezScrum.web.control.SprintPlanHelperTest;
import ntut.csie.ezScrum.web.control.TaskBoardTest;

public class HelperTestSuite {
	public static Test suite() {
		TestSuite suite= new TestSuite();
		
		// ProductBacklogHelperTest
		suite.addTest(new ProductBacklogHelperTest("testAddNewTag"));
		suite.addTest(new ProductBacklogHelperTest("testIsTagExist"));
		suite.addTest(new ProductBacklogHelperTest("testGetTagByName"));
		suite.addTest(new ProductBacklogHelperTest("testDeleteTag"));
		suite.addTest(new ProductBacklogHelperTest("testGetTagList"));
		suite.addTest(new ProductBacklogHelperTest("testUpdateTag"));
		suite.addTest(new ProductBacklogHelperTest("testAddStoryTag"));
		suite.addTest(new ProductBacklogHelperTest("testRemoveStoryTag"));
		suite.addTest(new ProductBacklogHelperTest("testTagScenario1"));
		suite.addTest(new ProductBacklogHelperTest("testTagScenario2"));
		
		suite.addTest(new ProductBacklogHelperTest("testgetAddableStories1"));
		suite.addTest(new ProductBacklogHelperTest("testgetAddableStories2"));
		suite.addTest(new ProductBacklogHelperTest("testgetAddableStories3"));
		suite.addTest(new ProductBacklogHelperTest("testgetAddableStories4"));
		suite.addTest(new ProductBacklogHelperTest("testgetAddableStories5"));
		
		// ReleasePlanHelperTest
		suite.addTest(new ReleasePlanHelperTest("testloadReleasePlans"));
		suite.addTest(new ReleasePlanHelperTest("testloadReleasePlansList"));
		suite.addTest(new ReleasePlanHelperTest("testgetLastReleasePlanNumber"));
		suite.addTest(new ReleasePlanHelperTest("testdeleteReleasePlan"));
		suite.addTest(new ReleasePlanHelperTest("testeditReleasePlan"));
//		suite.addTest(new ReleasePlanHelperTest("testaddReleaseSprints"));
//		suite.addTest(new ReleasePlanHelperTest("testdeleteSpritnOfRelease"));
//		suite.addTest(new ReleasePlanHelperTest("testgetReleasePlan"));
//		suite.addTest(new ReleasePlanHelperTest("testgetReleaseID"));
		
		// SprintPlanHelperTest
		suite.addTest(new SprintPlanHelperTest("testFocusFactorAndAvailableDays"));
		suite.addTest(new SprintPlanHelperTest("testdeleteIterationPlan"));
		suite.addTest(new SprintPlanHelperTest("testgetProjectStartDate"));
		suite.addTest(new SprintPlanHelperTest("testgetProjectEndDate"));
		suite.addTest(new SprintPlanHelperTest("testgetLastSprintId"));
		suite.addTest(new SprintPlanHelperTest("testgetLastSprintPlanNumber"));
		suite.addTest(new SprintPlanHelperTest("testgetSprintIDbyDate"));
		suite.addTest(new SprintPlanHelperTest("testmoveSprint"));
		
		// TaskBoardTest
		suite.addTest(new TaskBoardTest("testgetStrories1"));
		suite.addTest(new TaskBoardTest("testgetStrories2"));
		suite.addTest(new TaskBoardTest("testgetStrories3"));
		
		// ProjectHelperTest
		suite.addTest(new ProjectHelperTest("testgetAllCustomProjects"));
		suite.addTest(new ProjectHelperTest("testgetAllCustomProjectsWrongParameter"));
		suite.addTest(new ProjectHelperTest("testgetAllCustomProjectsWrongParameter2"));
		suite.addTest(new ProjectHelperTest("testgetProject"));
		suite.addTest(new ProjectHelperTest("testgetProjectWrongParameter"));
		
		// AccountHelperTest
		suite.addTest(new AccountHelperTest("testgetScrumWorkerList"));
		
		return suite;
	}
	
	public static void main(String[] args) {
        junit.textui.TestRunner.run(HelperTestSuite.suite());
    }

}
