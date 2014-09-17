package ntut.csie.ezScrum.stapler;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.stapler.Stapler;

public class WebAppMain implements ServletContextListener {
	private static Log log = LogFactory.getLog(WebAppMain.class);
    public void contextInitialized(ServletContextEvent event) {
        Stapler.setRoot(event,EzScrumRoot.ezScrumRoot);
        
        /**
		 * comment by Sam Huang
		 * 系統跑起來便去檢查資料庫連線並且建資料庫與表格，有例外產生直接離開系統。
		 */
		Configuration config = new Configuration();
		MantisService dbService = new MantisService(config);
		if (!dbService.testAndInitDatabase()) {
			System.exit(0);
		}
    }

    public void contextDestroyed(ServletContextEvent event) {
    }
}
 