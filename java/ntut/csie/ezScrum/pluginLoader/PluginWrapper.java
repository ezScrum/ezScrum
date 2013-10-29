package ntut.csie.ezScrum.pluginLoader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PluginWrapper{
	private String pluginDirPath;
	private Map<String,String> map;
	
	public PluginWrapper(String pluginDirPath, Map<String,String> map){
		this.pluginDirPath = pluginDirPath;
		this.map = map;
	}
	
	public Map<String,String> getMap(){
		return this.map;
	}
	
	public String getPlugnDirPath(){
		return new File(this.pluginDirPath).getAbsolutePath();
	}
	
	public String getPluginName(){
		return new File(this.pluginDirPath).getName();
	}
	
	public Object getPlugin( String protocol ) throws InstantiationException, IllegalAccessException{
		String className = this.map.get( protocol );
		List<URL> urlList = new ArrayList<URL>();
		Class c1 = null;
		try {
			//load all class of plugin
			URL classesUrl = new URL("file:"+this.pluginDirPath+"/WEB-INF/classes/");
			urlList.add( classesUrl );
			
			//load lib of plugin
			File libDir = new File(this.pluginDirPath+"/WEB-INF/lib");
			for( File lib : libDir.listFiles() ){
				URL url = new URL("file:"+lib.getAbsolutePath());
				urlList.add( url );
			}
			 
			URL[] urlArray = (URL[]) urlList.toArray(new URL[0]);
			URLClassLoader urlClassLoader1 = new URLClassLoader(urlArray ,this.getClass().getClassLoader());
			c1 = urlClassLoader1.loadClass( className );
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c1.newInstance();
	}
}
