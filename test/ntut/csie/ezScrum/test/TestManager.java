package ntut.csie.ezScrum.test;

import ntut.csie.ezScrum.dao.AccountDAOTest;
import ntut.csie.ezScrum.dao.HistoryDAOTest;
import ntut.csie.ezScrum.dao.ProjectDAOTest;
import ntut.csie.ezScrum.dao.SerialNumberDAOTest;
import ntut.csie.ezScrum.dao.SprintDAOTest;
import ntut.csie.ezScrum.dao.StoryDAOTest;
import ntut.csie.ezScrum.dao.TagDAOTest;
import ntut.csie.ezScrum.dao.TaskDAOTest;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	// dao
	AccountDAOTest.class, 
	HistoryDAOTest.class,
	ProjectDAOTest.class,
	SerialNumberDAOTest.class,
	SprintDAOTest.class,
	StoryDAOTest.class,
	TagDAOTest.class,
	TaskDAOTest.class
})
public class TestManager {

    @BeforeClass 
    public static void setUpClass() {      
        System.out.println("Master setup");

    }

    @AfterClass public static void tearDownClass() { 
        System.out.println("Master tearDown");
    }

}