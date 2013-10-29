package ntut.csie.ezScrum.pluginLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.test.CreateData.PluginMockDataHelper;
import ntut.csie.protocal.Action;
import ntut.csie.ui.protocol.UIConfig;

import org.junit.Test;

public class PluginManagerTest extends TestCase{
	
	private final String pluginTestDataPath = "./TestData/PluginData/";
	private final String pluginWorkspacePath = "./WebContent/pluginWorkspace/";
	private final String pluginName = "redminePlugin.war";
	private final String pluginInPluginWorkspacePath = pluginWorkspacePath + pluginName.replace(".war", "") + "/";
	private final String configFileName = "config.conf";
	private PluginManager pluginManager;
	
	@Override
	protected void setUp() throws Exception {
		PluginMockDataHelper.createResourcePropertyFile();
		pluginManager = new PluginManager();
	}

	@Override
	protected void tearDown() throws Exception {
		//	刪除檔案
		PluginMockDataHelper.deleteResourcePropertyFile();
		this.deleteMockFile();
		pluginManager = null;
	}

	/**
	 * 測試新增的plugin.war是否存在於pluginWorkspace
	 */
	@Test
	public void testAddPlugin(){
		String outsidePluginPath = pluginTestDataPath + pluginName;
		pluginManager.addPlugin( outsidePluginPath );
		
		String pluginNamePath = pluginWorkspacePath + pluginName.replace(".war", "");
		File pluginFile = new File( pluginNamePath );
		assertEquals( true, pluginFile.exists() );
	}
	
	@Test
	public void testRemovePlugin(){
		String pluginPath = pluginTestDataPath + pluginName;
		pluginManager.addPlugin( pluginPath );
		
		String insidePluginPath = pluginInPluginWorkspacePath;
		pluginManager.removePlugin( insidePluginPath );
		
		File pluginFile = new File( pluginInPluginWorkspacePath );
		assertEquals( false, pluginFile.exists() );
	}
	
	/**
	 * 測試安裝新的plugin
	 */
	@Test
	public void testGetUIConfigList(){
		List<UIConfig> uiConfigList = new ArrayList<UIConfig>();
		int defaultUIConfigListSize;
		uiConfigList = pluginManager.getUIConfigList();
		defaultUIConfigListSize = uiConfigList.size();
		
		//	未安裝前 config.conf 不存在
		String uiConfigFilePath = this.pluginInPluginWorkspacePath + configFileName;
		File uiConfigFile = new File( uiConfigFilePath );
		assertEquals( false, uiConfigFile.exists() );
		
		//	安裝後 config.conf 存在
		String testPluginPath = this.pluginTestDataPath + this.pluginName;
		pluginManager.addPlugin( testPluginPath );
		assertEquals( true, uiConfigFile.exists() );
		
		pluginManager = new PluginManager();
		uiConfigList = pluginManager.getUIConfigList();
		int correctUIConfigListSize = defaultUIConfigListSize+1;
		assertEquals( correctUIConfigListSize, uiConfigList.size() );
		
		uiConfigList = null;
	}
	
	/**
	 * 測試安裝新的plugin後，Action的UrlName是否正確
	 */
	@Test
	public void testGetActionList(){
		List<Action> actionList = new ArrayList<Action>();
		
		String pluginPath = pluginTestDataPath + pluginName;
		pluginManager.addPlugin( pluginPath );
		pluginManager = new PluginManager();
		actionList = pluginManager.getActionList();
		assertEquals( 1, actionList.size() );	//	目前只安裝redmine
		boolean isActionExisted = false;
		for( Action action: actionList ){
			if( action.getUrlName().equals("redmine") ){
				isActionExisted = true;
				break;
			}
		}
		assertEquals( true, isActionExisted );
		
		actionList = null;
	}
	
	private void deleteMockFile(){
		String folderPath = pluginWorkspacePath + pluginName.replace(".war", "");
		File file = new File( folderPath );
		PluginMockDataHelper.isMockFileExisted( file );
	}
}
