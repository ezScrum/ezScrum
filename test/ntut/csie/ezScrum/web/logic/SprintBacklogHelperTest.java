package ntut.csie.ezScrum.web.logic;

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.web.dataObject.SprintBacklogDateColumn;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;

public class SprintBacklogHelperTest extends TestCase {
	private SprintBacklogLogic sprintBacklogLogic;
	private Configuration configuration = null;
	
	public SprintBacklogHelperTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
//		this.helper = new SprintBacklogHelper();
		this.sprintBacklogLogic = new SprintBacklogLogic();
    }

    protected void tearDown() {
//    	this.helper = null;
    	configuration.setTestMode(false);
		configuration.store();
		
    	this.sprintBacklogLogic = null;
    }
    
    /*-----------------------------------------------------------
	*	測試根據 SprintStartDate 與 AvaliableDays 取得 Sprint Date Column
	-------------------------------------------------------------*/
	public void testGetSprintDateColumn()
	{
		// 日期沒有跳過假日
		List<SprintBacklogDateColumn> dateCols = this.sprintBacklogLogic.calculateSprintBacklogDateList(new Date("2010/07/05"), 5);
		assertEquals("07/05", dateCols.get(0).GetColumnName());
		assertEquals("07/06", dateCols.get(1).GetColumnName());
		assertEquals("07/07", dateCols.get(2).GetColumnName());
		assertEquals("07/08", dateCols.get(3).GetColumnName());
		assertEquals("07/09", dateCols.get(4).GetColumnName());
		
		// 日期跳過假日
		dateCols = this.sprintBacklogLogic.calculateSprintBacklogDateList(new Date("2010/07/06"), 5);
		assertEquals("07/06", dateCols.get(0).GetColumnName());
		assertEquals("07/07", dateCols.get(1).GetColumnName());
		assertEquals("07/08", dateCols.get(2).GetColumnName());
		assertEquals("07/09", dateCols.get(3).GetColumnName());
		assertEquals("07/12", dateCols.get(4).GetColumnName());
		
		// 日期從假日開始
		dateCols = this.sprintBacklogLogic.calculateSprintBacklogDateList(new Date("2010/07/03"), 5);
		assertEquals("07/05", dateCols.get(0).GetColumnName());
		assertEquals("07/06", dateCols.get(1).GetColumnName());
		assertEquals("07/07", dateCols.get(2).GetColumnName());
		assertEquals("07/08", dateCols.get(3).GetColumnName());
		assertEquals("07/09", dateCols.get(4).GetColumnName());
		
		// AvailableDays 等於 0
		dateCols = this.sprintBacklogLogic.calculateSprintBacklogDateList(new Date("2010/07/05"), 0);
		assertEquals(0, dateCols.size());
		
		// 日期 null
		dateCols = this.sprintBacklogLogic.calculateSprintBacklogDateList(null, 5);
		assertEquals(0, dateCols.size());
	}
}
