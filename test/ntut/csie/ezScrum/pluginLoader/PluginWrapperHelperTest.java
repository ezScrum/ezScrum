package ntut.csie.ezScrum.pluginLoader;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.PluginMockDataHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PluginWrapperHelperTest {
	private final String mPluginTestDataPath = "./TestData/PluginData/";
	private final String mPluginWorkspacePath = "./WebContent/pluginWorkspace/";
	private final String mPluginName = "redminePlugin.war";
	private Configuration mConfig  = null;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		addPluginToWorkspace();
	}

	@After
	public void tearDown() throws Exception {
		deletePluginFolder();
		mConfig = null;
	}

	@Test
	public void testGeneratePluginWrappers() {
		PluginWrapperHelper pluginWrapperHelper = new PluginWrapperHelper();
		List<PluginWrapper> pluginWrapperList = pluginWrapperHelper
				.generatePluginWrappers();
		String expectedAction = "plugin.redmine.protocol.RedmineAction";
		String expectedUIConfig = "plugin.redmine.protocol.PluginImp";
		String actualAction = pluginWrapperList.get(0).getMap().get("Action");
		String actualUIConfig = pluginWrapperList.get(0).getMap()
				.get("UIConfig");
		assertEquals(1, pluginWrapperList.size()); // 目前只安裝redmine
		assertEquals(expectedAction, actualAction);
		assertEquals(expectedUIConfig, actualUIConfig);
		pluginWrapperHelper = null;
	}

	private void addPluginToWorkspace() {
		String outsidePluginPath = mPluginTestDataPath + mPluginName;
		PluginManager pluginManager = new PluginManager();
		pluginManager.addPlugin(outsidePluginPath);
		pluginManager = null;
	}

	private void deletePluginFolder() {
		String folderPath = mPluginWorkspacePath
				+ mPluginName.replace(".war", "");
		File file = new File(folderPath);
		PluginMockDataHelper.isMockFileExisted(file);
		file = null;
	}

}
