package ntut.csie.ezScrum.web.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.jcis.account.core.IActor;
import ntut.csie.jcis.account.core.IPermission;

public class TranslateUtil {

	/**
	 * 將所有role id轉成字串 以,隔開
	 * 
	 * @param roles
	 * @return
	 */
	static public String getRolesString(HashMap<String, ProjectRole> rolesMap) {
		String roleString = "";
		int roleSize = rolesMap.size();
		int i = 0;
		for (Entry<String, ProjectRole> entry : rolesMap.entrySet()) {
			ScrumRole role = entry.getValue().getScrumRole();
			String id = role.getProjectName() + "_" + role.getRoleName();
			roleString += id;
			if ((i + 1) != roleSize) {
				roleString += ", ";
			}
			i++;
		}
		return roleString;
	}

	static public String getPermissionString(IPermission[] permissions) {
		String permissionString = "";
		for (int i = 0; i < permissions.length; i++) {
			String permissionname = permissions[i].getPermissionName();
			permissionString += permissionname;
			if ((i + 1) != permissions.length) {
				permissionString += ",";
			}
		}
		return permissionString;
	}

	static public String getPermissionStringForView(IPermission[] permissions) {
		String permissionString = getPermissionString(permissions);
		permissionString = permissionString.replaceAll(",", ", ");
		return permissionString;
	}

	/**
	 * 將Actors中的account id以,隔開
	 * 
	 * @param actors
	 * @return
	 */
	static public String getAccountsString(IActor[] actors) {
		String actorString = "";

		for (int i = 0; i < actors.length; i++) {
			IActor actor = actors[i];
			if (actor.getType() == IActor.TYPE_ACCOUNT) {
				actorString += actor.getID() + ",";
			}
		}

		return actorString;
	}

	/**
	 * 將Actors中的group id以,隔開
	 * 
	 * @param actors
	 * @return
	 */
	static public String getGroupsString(IActor[] actors) {
		String actorString = "";

		for (int i = 0; i < actors.length; i++) {
			IActor actor = actors[i];
			if (actor.getType() == IActor.TYPE_GROUP) {
				actorString += actor.getID() + ",";
			}
		}

		return actorString;
	}

	/**
	 * 將Roles String轉成Role String List
	 * 
	 * @return
	 */
	static public List<String> translateRoleString(String roleString) {
		String[] roleArray = roleString.split(",");
		List<String> roleList = new ArrayList<String>();

		for (int i = 0; i < roleArray.length; i++) {
			String tmp = roleArray[i];
			tmp = tmp.trim();
			if (tmp.isEmpty()) continue;
			roleList.add(roleArray[i]);
		}

		return roleList;
	}

}
