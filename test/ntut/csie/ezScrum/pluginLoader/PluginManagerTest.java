package ntut.csie.ezScrum.pluginLoader;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.PluginMockDataHelper;
import ntut.csie.protocal.Action;
import ntut.csie.ui.protocol.UIConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PluginManagerTest {
	
	private final String mPluginTestDataPath = "./TestData/PluginData/";
	private final String mPluginWorkspacePath = "./WebContent/pluginWorkspace/";
	private final String mPluginName = "redminePlugin.war";
	private final String mPluginInPluginWorkspacePath = mPluginWorkspacePath + mPluginName.replace(".war", "") + "/";
	private final String mConfigFileName = "config.conf";
	private PluginManager mPluginManager;
	private Configuration mConfig  = null;
	
	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		PluginMockDataHelper.createResourcePropertyFile();
		mPluginManager = new PluginManager();
	}

	@After
	public void tearDown() throws Exception {
		//	刪除檔案
		PluginMockDataHelper.deleteResourcePropertyFile();
		this.deleteMockFile();
		mPluginManager = null;
		mConfig = null;
	}

	/**
	 * 測試新增的plugin.war是否存在於pluginWorkspace
	 */
	@Test
	public void testAddPlugin(){
		String outsidePluginPath = mPluginTestDataPath + mPluginName;
		mPluginManager.addPlugin( outsidePluginPath );
		
		String pluginNamePath = mPluginWorkspacePath + mPluginName.replace(".war", "");
		File pluginFile = new File( pluginNamePath );
		assertEquals( true, pluginFile.exists() );
	}
	
	@Test
	public void testRemovePlugin(){
		String pluginPath = mPluginTestDataPath + mPluginName;
		mPluginManager.addPlugin( pluginPath );
		
		String insidePluginPath = mPluginInPluginWorkspacePath;
		mPluginManager.removePlugin( insidePluginPath );
		
		File pluginFile = new File( mPluginInPluginWorkspacePath );
		assertEquals( false, pluginFile.exists() );
	}
	
	/**
	 * 測試安裝新的plugin
	 */
	@Test
	public void testGetUIConfigList(){
		List<UIConfig> uiConfigList = new ArrayList<UIConfig>();
		int defaultUIConfigListSize;
		uiConfigList = mPluginManager.getUIConfigList();
		defaultUIConfigListSize = uiConfigList.size();
		
		//	未安裝前 config.conf 不存在
		String uiConfigFilePath = this.mPluginInPluginWorkspacePath + mConfigFileName;
		File uiConfigFile = new File( uiConfigFilePath );
		assertEquals( false, uiConfigFile.exists() );
		
		//	安裝後 config.conf 存在
		String testPluginPath = this.mPluginTestDataPath + this.mPluginName;
		mPluginManager.addPlugin( testPluginPath );
		assertEquals( true, uiConfigFile.exists() );
		
		mPluginManager = new PluginManager();
		uiConfigList = mPluginManager.getUIConfigList();
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
		
		String pluginPath = mPluginTestDataPath + mPluginName;
		mPluginManager.addPlugin( pluginPath );
		mPluginManager = new PluginManager();
		actionList = mPluginManager.getActionList();
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
		String folderPath = mPluginWorkspacePath + mPluginName.replace(".war", "");
		File file = new File( folderPath );
		PluginMockDataHelper.isMockFileExisted( file );
	}
}
