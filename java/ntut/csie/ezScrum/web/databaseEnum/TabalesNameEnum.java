package ntut.csie.ezScrum.web.databaseEnum;

import java.util.ArrayList;

public class TabalesNameEnum {
	public static ArrayList<String> getAllTablesName() {
		ArrayList<String> tablesName = new ArrayList<>();
		tablesName.add(AccountEnum.TABLE_NAME);
		tablesName.add(AttachFileEnum.TABLE_NAME);
		tablesName.add(HistoryEnum.TABLE_NAME);
		tablesName.add(IssuePartnerRelationEnum.TABLE_NAME);
		tablesName.add(ProjectEnum.TABLE_NAME);
		tablesName.add(ProjectRoleEnum.TABLE_NAME);
		tablesName.add(ReleaseEnum.TABLE_NAME);
		tablesName.add(RetrospectiveEnum.TABLE_NAME);
		tablesName.add(ScrumRoleEnum.TABLE_NAME);
		tablesName.add(SerialNumberEnum.TABLE_NAME);
		tablesName.add(SprintEnum.TABLE_NAME);
		tablesName.add(StoryEnum.TABLE_NAME);
		tablesName.add(StoryTagRelationEnum.TABLE_NAME);
		tablesName.add(SystemEnum.TABLE_NAME);
		tablesName.add(TagEnum.TABLE_NAME);
		tablesName.add(TaskEnum.TABLE_NAME);
		tablesName.add(TokenEnum.TABLE_NAME);
		tablesName.add(UnplanEnum.TABLE_NAME);
		return tablesName;
	}
}
