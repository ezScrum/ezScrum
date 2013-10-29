package ntut.csie.ezScrum.pluginLoader;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.test.CreateData.PluginMockDataHelper;

import org.junit.Test;

public class PluginWrapperHelperTest extends TestCase{
	private final String pluginTestDataPath = "./TestData/PluginData/";
	private final String pluginWorkspacePath = "./WebContent/pluginWorkspace/";
	private final String pluginName = "redminePlugin.war";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		addPluginToWorkspace();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		this.deletePluginFolder();
	}
	
	@Test
	public void testGeneratePluginWrappers(){
		PluginWrapperHelper pluginWrapperHelper = new PluginWrapperHelper();
		List<PluginWrapper> pluginWrapperList = pluginWrapperHelper.generatePluginWrappers();
		String expectedAction = "plugin.redmine.protocol.RedmineAction";
		String expectedUIConfig = "plugin.redmine.protocol.PluginImp";
		String actualAction = pluginWrapperList.get(0).getMap().get("Action");
		String actualUIConfig = pluginWrapperList.get(0).getMap().get("UIConfig");
		assertEquals( 1, pluginWrapperList.size() );	//	目前只安裝redmine
		assertEquals(expectedAction, actualAction);
		assertEquals(expectedUIConfig, actualUIConfig);
		pluginWrapperHelper = null;
	}
	
	private void addPluginToWorkspace() {
		String outsidePluginPath = pluginTestDataPath + pluginName;
		PluginManager pluginManager = new PluginManager();
		pluginManager.addPlugin( outsidePluginPath );
		pluginManager = null;
	}
	
	private void deletePluginFolder(){
		String folderPath = pluginWorkspacePath + pluginName.replace(".war", "");
		File file = new File( folderPath );
		PluginMockDataHelper.isMockFileExisted( file );
		file = null;
	}

}
