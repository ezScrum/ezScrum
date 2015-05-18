package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;

import ntut.csie.ezScrum.dao.ProjectDAO;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CreateTag {
	private static Log mlog = LogFactory.getLog(CreateTag.class);
	private CreateProject mCP = null;
	private CreateProductBacklog mCPB = null;
	private int mTagCount = 1;
	public String TEST_TAG_NAME = "TEST_TAG_"; // Tag Name

	private ProjectObject mProject;

	private ArrayList<TagObject> mTags = new ArrayList<TagObject>();
	private ProductBacklogMapper mProductBacklogMapper = null;

	public CreateTag(int tagCount, CreateProject CP) {
		mTagCount = tagCount;
		mCP = CP;
	}

	// create tag in project
	public void exe() {
		for (int projectIndex = 0; projectIndex < mCP.getProjectList()
				.size(); projectIndex++) {
			mProject = mCP.getAllProjects().get(projectIndex); // TEST_PROJECT_X
			mProductBacklogMapper = new ProductBacklogMapper(mProject);
			long projectId = ProjectDAO.getInstance().get(mProject.getName()).getId();
			for (int tagIndex = 0; tagIndex < mTagCount; tagIndex++) {
				TagObject tag = new TagObject(TEST_TAG_NAME + Integer.toString(tagIndex + 1), projectId);
				tag.save();
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
			mProject = mCP.getAllProjects().get(projectIndex); // TEST_PROJECT_X
			// m_backlog = new ProductBacklog(m_project, m_userSession);
			mProductBacklogMapper = new ProductBacklogMapper(mProject);
			for (int pbIndex = 0; pbIndex < mCPB.getStories().size(); pbIndex++) {
				for (int tagIndex = 0; tagIndex < mTagCount; tagIndex++) {
					storyId = mCPB.getStories().get(pbIndex).getId();
					// m_backlog.addStoryTag(String.valueOf(storyId),
					// String.valueOf(tagIndex+1));
					mProductBacklogMapper.addTagToStory(storyId, tagIndex + 1);
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
