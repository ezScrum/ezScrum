package ntut.csie.ezScrum.stapler;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.stapler.Stapler;

public class WebAppMain implements ServletContextListener {
	private static Log log = LogFactory.getLog(WebAppMain.class);
    public void contextInitialized(ServletContextEvent event) {
        Stapler.setRoot(event,EzScrumRoot.ezScrumRoot);
    }

    public void contextDestroyed(ServletContextEvent event) {
    }
}
