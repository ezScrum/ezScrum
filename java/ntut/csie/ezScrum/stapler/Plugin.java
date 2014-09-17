package ntut.csie.ezScrum.stapler;

import java.util.List;

import ntut.csie.ezScrum.pluginLoader.PluginManager;
import ntut.csie.protocal.Action;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public class Plugin {
	/**
	 * todo record project<-->urlName(it must be unique) mapping into db, in order to identify disabled and enable plugin action
	 * */
	public Object getDynamic(String token, StaplerRequest request, StaplerResponse response) {
		PluginManager pluginManager = new PluginManager();
		List<Action> actionList = pluginManager.getActionList();
		for (Action a : actionList) {
			if (a.getUrlName().equals(token)) {
				return a;
			}
		}

		return this;
	}
}
