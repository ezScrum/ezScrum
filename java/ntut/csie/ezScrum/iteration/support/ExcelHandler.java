package ntut.csie.ezScrum.iteration.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueTag;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.core.util.FormCheckUtil;

import org.jdom.Element;

public class ExcelHandler {
	private String FORMTYPE_NAME = "Name";
	private String FORMTYPE_VALUE = "Value";
	private String FORMTYPE_IMPORTANCE = "Imp";
	private String FORMTYPE_ESTIMATION = "Estimate";
	private String FORMTYPE_HOWTODEMO = "How to test";
	private String FORMTYPE_NOTES = "Notes";
	private Sheet sheet;
	private int columns;
	private int rows;
	private List<IIssue> stories;

	public ExcelHandler(Sheet sheet) {
		this.sheet = sheet;
	}

	public void load() {
		this.columns = sheet.getColumns();
		this.rows = sheet.getRows();
		if (!checkTitle()) {
		} else {
			if (rows > 1)
				checkStories();
			else
				stories = new ArrayList<IIssue>();
		}
	}

	public void save(IStory[] stories) {
		setTitle();
		// index 當做Y軸的坐標，i=0已經被title所使用
		int index = 1;
		for (IStory story : stories) {
			try {
				((WritableSheet) sheet).addCell(new Label(0, index, String
						.valueOf(story.getIssueID())));
				// tag is a list, so we translate it to a string
				String result = Join(story.getTag(), ",");
				((WritableSheet) sheet).addCell(new Label(1, index, result));
				((WritableSheet) sheet).addCell(new Label(2, index, story
						.getSummary()));
				((WritableSheet) sheet).addCell(new Label(3, index, story
						.getReleaseID()));
				((WritableSheet) sheet).addCell(new Label(4, index, story
						.getSprintID()));
				((WritableSheet) sheet).addCell(new Label(5, index, story
						.getValue()));
				((WritableSheet) sheet).addCell(new Label(6, index, story
						.getImportance()));
				((WritableSheet) sheet).addCell(new Label(7, index, story
						.getEstimated()));
				((WritableSheet) sheet).addCell(new Label(8, index, story
						.getStatus()));
				((WritableSheet) sheet).addCell(new Label(9, index, story
						.getNotes()));
				((WritableSheet) sheet).addCell(new Label(10, index, story
						.getHowToDemo()));
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
			index++;
		}
	}

	private String Join(List<TagObject> tags, String delimiter) {
		if (tags.isEmpty())
			return new String("");

		StringBuilder sb = new StringBuilder();

		for (TagObject x : tags)
			sb.append(x.getName() + delimiter);

		sb.delete(sb.length() - delimiter.length(), sb.length());

		return sb.toString();
	}

	private void setTitle() {
		// 第一排給title使用
		try {
			((WritableSheet) sheet).addCell(new Label(0, 0, "ID"));
			((WritableSheet) sheet).addCell(new Label(1, 0, "Tag"));
			((WritableSheet) sheet).addCell(new Label(2, 0, FORMTYPE_NAME));
			((WritableSheet) sheet).addCell(new Label(3, 0, "ReleaseID"));
			((WritableSheet) sheet).addCell(new Label(4, 0, "SprintID"));
			((WritableSheet) sheet).addCell(new Label(5, 0, FORMTYPE_VALUE));
			((WritableSheet) sheet)
					.addCell(new Label(6, 0, FORMTYPE_IMPORTANCE));
			((WritableSheet) sheet)
					.addCell(new Label(7, 0, FORMTYPE_ESTIMATION));
			((WritableSheet) sheet).addCell(new Label(8, 0, "Status"));
			((WritableSheet) sheet).addCell(new Label(9, 0, FORMTYPE_NOTES));
			((WritableSheet) sheet)
					.addCell(new Label(10, 0, FORMTYPE_HOWTODEMO));
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	// 是否找到story的五個欄位，分別為Name, importance, estimation, how_to_demo, notes.
	private boolean checkTitle() {
		if (sheet.findCell(FORMTYPE_NAME) == null)
			return Boolean.FALSE;
		else if (sheet.findCell(FORMTYPE_VALUE) == null)
			return Boolean.FALSE;
		else if (sheet.findCell(FORMTYPE_IMPORTANCE) == null)
			return Boolean.FALSE;
		else if (sheet.findCell(FORMTYPE_ESTIMATION) == null)
			return Boolean.FALSE;
		else if (sheet.findCell(FORMTYPE_HOWTODEMO) == null)
			return Boolean.FALSE;
		else if (sheet.findCell(FORMTYPE_NOTES) == null)
			return Boolean.FALSE;
		else
			return Boolean.TRUE;
	}

	// 判斷xls中的story是否符合格式需求，若否則將stories清空且記錄errorLog。
	private void checkStories() {
		stories = new ArrayList<IIssue>();
		// 一行一行加入至stories
		for (int i = 1; i < rows; i++) {
			Cell[] cells = sheet.getRow(i);
			if (!isBothNull(cells)) {
				IIssue issue = new Issue();
				for (int j = 0; j < columns; j++) {
					Cell titleCell = sheet.getCell(j, 0);
					String title = titleCell.getContents();
					if (title.compareToIgnoreCase(FORMTYPE_NAME) == 0) {
						String summary = cells[j].getContents();
						boolean result = getResult(summary,
								FormCheckUtil.LENGTH128);
						if (!isNull(summary) && result)
							issue.setSummary(summary);
						else {
							stories = null;
							return;
						}
					} else if (title.compareToIgnoreCase(FORMTYPE_VALUE) == 0) {
						String value = cells[j].getContents();
						if (!isNull(value)) {
							boolean result = getResult(value,
									FormCheckUtil.INTEGER);
							if (result) {
								Element history = new Element(
										ScrumEnum.HISTORY_TAG);
								history.setAttribute(ScrumEnum.ID_HISTORY_ATTR,
										DateUtil.format(new Date(),
												DateUtil._16DIGIT_DATE_TIME_2));
								Element valueElem = new Element(ScrumEnum.VALUE);
								int temp = (int) Float.parseFloat(value);
								valueElem.setText(Integer.toString(temp));
								history.addContent(valueElem);
								issue.addTagValue(history);
							} else {
								stories = null;
								return;
							}
						}
					} else if (title.compareToIgnoreCase(FORMTYPE_IMPORTANCE) == 0) {
						String importance = cells[j].getContents();
						if (!isNull(importance)) {
							boolean result = getResult(importance,
									FormCheckUtil.INTEGER);
							if (result) {
								Element history = new Element(
										ScrumEnum.HISTORY_TAG);
								history.setAttribute(ScrumEnum.ID_HISTORY_ATTR,
										DateUtil.format(new Date(),
												DateUtil._16DIGIT_DATE_TIME_2));
								Element importanceElem = new Element(
										ScrumEnum.IMPORTANCE);
								int temp = (int) Float.parseFloat(importance);
								importanceElem.setText(Integer.toString(temp));
								history.addContent(importanceElem);
								issue.addTagValue(history);
							} else {
								stories = null;
								return;
							}
						}
					} else if (title.compareToIgnoreCase(FORMTYPE_ESTIMATION) == 0) {
						String estimation = cells[j].getContents();
						if (!isNull(estimation)) {
							boolean result = getResult(estimation,
									FormCheckUtil.DIGITAL);
							if (result) {
								Element history = new Element(
										ScrumEnum.HISTORY_TAG);
								history.setAttribute(ScrumEnum.ID_HISTORY_ATTR,
										DateUtil.format(new Date(),
												DateUtil._16DIGIT_DATE_TIME_2));
								Element storyPoint = new Element(
										ScrumEnum.ESTIMATION);
								storyPoint.setText(estimation);
								history.addContent(storyPoint);
								issue.addTagValue(history);
							} else {
								stories = null;
								return;
							}
						}
					} else if (title.compareToIgnoreCase(FORMTYPE_HOWTODEMO) == 0) {
						String howToDemo = cells[j].getContents();
						if (!isNull(howToDemo)) {
							Element history = new Element(ScrumEnum.HISTORY_TAG);
							history.setAttribute(ScrumEnum.ID_HISTORY_ATTR,
									DateUtil.format(new Date(),
											DateUtil._16DIGIT_DATE_TIME_2));
							Element howToDemoElem = new Element(
									ScrumEnum.HOWTODEMO);
							howToDemoElem.setText(howToDemo.replaceAll("'",
									"''"));
							history.addContent(howToDemoElem);
							issue.addTagValue(history);
						}
					}

					else if (title.compareToIgnoreCase(FORMTYPE_NOTES) == 0) {
						String notes = cells[j].getContents();
						if (notes != null) {
							Element history = new Element(ScrumEnum.HISTORY_TAG);
							history.setAttribute(ScrumEnum.ID_HISTORY_ATTR,
									DateUtil.format(new Date(),
											DateUtil._16DIGIT_DATE_TIME_2));
							Element notesElem = new Element(ScrumEnum.NOTES);
							notesElem.setText(notes.replaceAll("'", "''"));
							history.addContent(notesElem);
							issue.addTagValue(history);
						}
					}
				}
				stories.add(issue);
			}
		}
	}

	public List<IIssue> getStories() {
		return this.stories;
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
	private Boolean isBothNull(Cell[] cells) {
		int mount = 0;
		for (int i = 0; i < cells.length; i++) {
			String contents = cells[i].getContents();
			if (isNull(contents)) {
				mount++;
			}
		}
		if (mount == cells.length)
			return Boolean.TRUE;
		else
			return Boolean.FALSE;
	}

}
