package ntut.csie.ezScrum.iteration.support;

import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.jcis.core.util.FormCheckUtil;

public class ExcelHandler {
	private String FORMTYPE_NAME = "Name";
	private String FORMTYPE_VALUE = "Value";
	private String FORMTYPE_IMPORTANCE = "Imp";
	private String FORMTYPE_ESTIMATION = "Estimate";
	private String FORMTYPE_HOWTODEMO = "How to demo";
	private String FORMTYPE_NOTES = "Notes";
	private Sheet mSheet;
	private int mColumns;
	private int mRows;
	private long mProjectId;
	private ArrayList<StoryObject> stories;

	public ExcelHandler(long projectId, Sheet sheet) {
		mSheet = sheet;
		mProjectId = projectId;
	}

	public void load() {
		mColumns = mSheet.getColumns();
		mRows = mSheet.getRows();
		
		if (checkTitle()) {
			if (mRows > 1) {
				checkStories();
			} else {
				stories = new ArrayList<StoryObject>();
			}
		}
	}

	public void save(ArrayList<StoryObject> stories) {
		setTitle();
		// index 當做Y軸的坐標，i=0已經被title所使用
		int index = 1;
		for (StoryObject story : stories) {
			try {
				((WritableSheet) mSheet).addCell(new Label(0, index, String.valueOf(story.getId())));
				// tag is a list, so we translate it to a string
				String result = Join(story.getTags(), ",");
				((WritableSheet) mSheet).addCell(new Label(1, index, result));
				((WritableSheet) mSheet).addCell(new Label(2, index, story.getName()));
				((WritableSheet) mSheet).addCell(new Label(3, index, ""));
				((WritableSheet) mSheet).addCell(new Label(4, index, String.valueOf(story.getSprintId())));
				((WritableSheet) mSheet).addCell(new Label(5, index, String.valueOf(story.getValue())));
				((WritableSheet) mSheet).addCell(new Label(6, index, String.valueOf(story.getImportance())));
				((WritableSheet) mSheet).addCell(new Label(7, index, String.valueOf(story.getEstimate())));
				((WritableSheet) mSheet).addCell(new Label(8, index, story.getStatusString()));
				((WritableSheet) mSheet).addCell(new Label(9, index, story.getNotes()));
				((WritableSheet) mSheet).addCell(new Label(10, index, story.getHowToDemo()));
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
			index++;
		}
	}

	private String Join(List<TagObject> tags, String delimiter) {
		if (tags.isEmpty()) {
			return new String("");
		}

		StringBuilder sb = new StringBuilder();

		for (TagObject x : tags) {
			sb.append(x.getName() + delimiter);
		}

		sb.delete(sb.length() - delimiter.length(), sb.length());

		return sb.toString();
	}

	private void setTitle() {
		// 第一排給title使用
		try {
			((WritableSheet) mSheet).addCell(new Label(0, 0, "ID"));
			((WritableSheet) mSheet).addCell(new Label(1, 0, "Tag"));
			((WritableSheet) mSheet).addCell(new Label(2, 0, FORMTYPE_NAME));
			((WritableSheet) mSheet).addCell(new Label(3, 0, "ReleaseID"));
			((WritableSheet) mSheet).addCell(new Label(4, 0, "SprintID"));
			((WritableSheet) mSheet).addCell(new Label(5, 0, FORMTYPE_VALUE));
			((WritableSheet) mSheet).addCell(new Label(6, 0, FORMTYPE_IMPORTANCE));
			((WritableSheet) mSheet).addCell(new Label(7, 0, FORMTYPE_ESTIMATION));
			((WritableSheet) mSheet).addCell(new Label(8, 0, "Status"));
			((WritableSheet) mSheet).addCell(new Label(9, 0, FORMTYPE_NOTES));
			((WritableSheet) mSheet).addCell(new Label(10, 0, FORMTYPE_HOWTODEMO));
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	// 是否找到story的五個欄位，分別為Name, importance, estimation, how_to_demo, notes.
	private boolean checkTitle() {
		if (mSheet.findCell(FORMTYPE_NAME) == null)
			return Boolean.FALSE;
		else if (mSheet.findCell(FORMTYPE_VALUE) == null)
			return Boolean.FALSE;
		else if (mSheet.findCell(FORMTYPE_IMPORTANCE) == null)
			return Boolean.FALSE;
		else if (mSheet.findCell(FORMTYPE_ESTIMATION) == null)
			return Boolean.FALSE;
		else if (mSheet.findCell(FORMTYPE_HOWTODEMO) == null)
			return Boolean.FALSE;
		else if (mSheet.findCell(FORMTYPE_NOTES) == null)
			return Boolean.FALSE;
		else
			return Boolean.TRUE;
	}

	// 判斷xls中的story是否符合格式需求，若否則將stories清空且記錄errorLog。
	private void checkStories() {
		stories = new ArrayList<StoryObject>();
		// 一行一行加入至stories
		for (int i = 1; i < mRows; i++) {
			Cell[] cells = mSheet.getRow(i);
			if (!isContentAllNull(cells)) {
				StoryObject story = new StoryObject(mProjectId);
				for (int j = 0; j < mColumns; j++) {
					Cell titleCell = mSheet.getCell(j, 0);
					String title = titleCell.getContents();
					boolean isTitleEqualToName = title.compareToIgnoreCase(FORMTYPE_NAME) == 0;
					boolean isTitleEqualToValue = title.compareToIgnoreCase(FORMTYPE_VALUE) == 0;
					boolean isTitleEqualToImportance = title.compareToIgnoreCase(FORMTYPE_IMPORTANCE) == 0;
					boolean isTitleEqualToEstimation = title.compareToIgnoreCase(FORMTYPE_ESTIMATION) == 0;
					boolean isTitleEqualToHowToDemo = title.compareToIgnoreCase(FORMTYPE_HOWTODEMO) == 0;
					boolean isTitleEqualToNotes = title.compareToIgnoreCase(FORMTYPE_NOTES) == 0;
					
					if (isTitleEqualToName) {
						String name = cells[j].getContents();
						boolean result = getResult(name, FormCheckUtil.LENGTH128);
						if (!isNull(name) && result) {
							story.setName(name);							
						}
					} else if (isTitleEqualToValue) {
						String value = cells[j].getContents();
						if (!isNull(value)) {
							boolean result = getResult(value, FormCheckUtil.INTEGER);
							if (result) {
								story.setValue(Integer.parseInt(value));
							}
						}
					} else if (isTitleEqualToImportance) {
						String importance = cells[j].getContents();
						if (!isNull(importance)) {
							boolean result = getResult(importance, FormCheckUtil.INTEGER);
							if (result) {
								story.setImportance(Integer.parseInt(importance));
							}
						}
					} else if (isTitleEqualToEstimation) {
						String estimation = cells[j].getContents();
						if (!isNull(estimation)) {
							boolean result = getResult(estimation, FormCheckUtil.DIGITAL);
							if (result) {
								story.setEstimate(Integer.parseInt(estimation));
							}
						}
					} else if (isTitleEqualToHowToDemo) {
						String howToDemo = cells[j].getContents();
						if (!isNull(howToDemo)) {
							story.setHowToDemo(howToDemo);
						}
					} else if (isTitleEqualToNotes) {
						String notes = cells[j].getContents();
						if (notes != null) {
							story.setNotes(notes);
						}
					}
				}
				stories.add(story);
			}
		}
	}

	public ArrayList<StoryObject> getStories() {
		return stories;
	}

	// 判斷value是否符合型態需求
	private boolean getResult(String value, String type) {
		if (type.equals(FormCheckUtil.DIGITAL))
			return FormCheckUtil.isDigital(value);
		else if (type.equals(FormCheckUtil.EXISTED))
			return FormCheckUtil.isExisted(value);
		else if (type.equals(FormCheckUtil.DATE))
			return FormCheckUtil.isDate(value);
		else if (type.equals(FormCheckUtil.INTEGER))
			return FormCheckUtil.isInteger(value);
		else if (type.equals(FormCheckUtil.LENGTH128))
			return FormCheckUtil.isLength128(value);
		return false;
	}

	private boolean isNull(String str) {
		if (str == null || str.length() == 0)
			return Boolean.TRUE;
		else
			return Boolean.FALSE;
	}

	// Cell中的content是否全部為空
	private Boolean isContentAllNull(Cell[] cells) {
		boolean isAllNull = false;
		for (int i = 0; i < cells.length; i++) {
			String contents = cells[i].getContents();
			if (isNull(contents)) {
				isAllNull = true;
			} else {
				isAllNull = false;
			}
		}
		return isAllNull;
	}
}
