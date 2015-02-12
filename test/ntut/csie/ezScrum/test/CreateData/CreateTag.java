package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CreateTag {
	private static Log mlog = LogFactory.getLog(CreateTag.class);
	private CreateProject mCP = null;
	private CreateProductBacklog mCPB = null;
	private int mTagCount = 1;
	public String TEST_TAG_NAME = "TEST_TAG_"; // Tag Name

	private IProject mProject;
	private IUserSession mUserSession;
	private Configuration mConfig = new Configuration();

	private ArrayList<TagObject> mTags = new ArrayList<TagObject>();
	// private ProductBacklog m_backlog = null;
	private ProductBacklogMapper mProductBacklogMapper = null;

	public CreateTag(int tagCount, CreateProject CP) {
		mTagCount = tagCount;
		mCP = CP;
	}

	// create tag in project
	public void exe() {
		mUserSession = mConfig.getUserSession();
		for (int projectIndex = 0; projectIndex < mCP.getProjectList()
				.size(); projectIndex++) {
			mProject = mCP.getProjectList().get(projectIndex); // TEST_PROJECT_X
			// m_backlog = new ProductBacklog(m_project, m_userSession);
			mProductBacklogMapper = new ProductBacklogMapper(mProject,
					mUserSession);
			for (int tagIndex = 0; tagIndex < mTagCount; tagIndex++) {
				// m_backlog.addNewTag(TEST_TAG_NAME +
				// Integer.toString(tagIndex+1)); //TEST_TAG_Y
				mProductBacklogMapper.addNewTag(TEST_TAG_NAME
						+ Integer.toString(tagIndex + 1)); // TEST_TAG_Y

				TagObject tag = new TagObject();
				tag.setId(tagIndex + 1);
				tag.setName(TEST_TAG_NAME + Integer.toString(tagIndex + 1));
				mTags.add(tag);
			}
			System.out.println("Project "
					+ mCP.getProjectList().get(projectIndex).getName()
					+ " create " + mTagCount + " Tags success.");
		}
	}

	// attach tag to issue
	public void attachTagToStory(CreateProductBacklog cpb) {
		mCPB = cpb;
		long storyId;
		for (int projectIndex = 0; projectIndex < mCP.getProjectList()
				.size(); projectIndex++) {
			mProject = mCP.getProjectList().get(projectIndex); // TEST_PROJECT_X
			// m_backlog = new ProductBacklog(m_project, m_userSession);
			mProductBacklogMapper = new ProductBacklogMapper(mProject,
					mUserSession);
			for (int pbIndex = 0; pbIndex < mCPB.getIssueList().size(); pbIndex++) {
				for (int tagIndex = 0; tagIndex < mTagCount; tagIndex++) {
					storyId = mCPB.getIssueList().get(pbIndex).getIssueID();
					// m_backlog.addStoryTag(String.valueOf(storyId),
					// String.valueOf(tagIndex+1));
					mProductBacklogMapper.addStoryTag(
							String.valueOf(storyId), tagIndex + 1);
					mlog.info("Project "
							+ mCP.getProjectList().get(projectIndex)
									.getName() + " TEST_STORY_"
							+ String.valueOf(storyId) + " attach "
							+ " TEST_TAG_" + String.valueOf(tagIndex + 1)
							+ " 成功");
				}
			}
		}
	}

	public ArrayList<TagObject> getTagList() {
		return mTags;
	}
}
