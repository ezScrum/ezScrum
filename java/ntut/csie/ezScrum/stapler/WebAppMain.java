package ntut.csie.ezScrum.stapler;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.kohsuke.stapler.Stapler;

import ntut.csie.ezScrum.issue.sql.service.tool.internal.DatabaseChecker;

public class WebAppMain implements ServletContextListener {
    public void contextInitialized(ServletContextEvent event) {
        Stapler.setRoot(event,EzScrumRoot.ezScrumRoot);
        
        /**
		 * comment by Sam Huang
		 * 系統跑起來便去檢查資料庫連線並且建資料庫與表格，有例外產生直接離開系統。
		 */
        DatabaseChecker databaseChecker = new DatabaseChecker();
		if (!databaseChecker.testAndInitDatabase()) {
			System.exit(0);
		}
    }

    public void contextDestroyed(ServletContextEvent event) {
    }
}
 