package ntut.csie.ezScrum.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueTag;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTVerticalJc;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.Style;
import org.docx4j.wml.Styles;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblGrid;
import org.docx4j.wml.TblGridCol;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.Tr;
import org.docx4j.wml.PPrBase.Spacing;

public class DocxMaker {
	private static Log log = LogFactory.getLog(DocxMaker.class);
	private ObjectFactory factory;
	private WordprocessingMLPackage wordMLPackage;
	final String[] title = {"ID", "Name", "Est.", "Handler", "Partners", "Notes"};
	final int FONT_SIZE = 12 * 2;	// 轉成word需要*2才是正確的大小

	public DocxMaker() {}
	
	public File getReleasePlanDocx(IReleasePlanDesc releases, List<ISprintPlanDesc> sprints, HashMap<String, List<IIssue>> storyMap, LinkedHashMap<Long, IIssue[]> taskMap,
	        HashMap<String, Float> tatolStoryPoints) {
		MainDocumentPart mainDoc;
		
		try {
			// Create the package
			wordMLPackage = WordprocessingMLPackage.createPackage();
			mainDoc = wordMLPackage.getMainDocumentPart();
			factory = Context.getWmlObjectFactory();
			alterStyleSheet();	// change the style of this docx

			/**
			 * the title of docx
			 */
			mainDoc.addStyledParagraphOfText("Title", "Release Plan #" + releases.getID() + "： " + releases.getName());
			mainDoc.addStyledParagraphOfText("Subtitle", "Start Date： " + releases.getStartDate());
			mainDoc.addStyledParagraphOfText("Subtitle", "End Date： " + releases.getEndDate());
			mainDoc.addStyledParagraphOfText("Subtitle", "Description： " + releases.getDescription());

			ISprintPlanDesc sprint;
			int sprintSize = sprints.size();
			for (int i = 0; i < sprintSize; i++) {
				sprint = sprints.get(i);
				addSprintInfo(mainDoc, sprints.get(i), tatolStoryPoints);
				addStoryInfo(mainDoc, storyMap.get(sprint.getID()), taskMap);
				if (i != sprintSize) addPageBreak(mainDoc);	// add new page
			}

			wordMLPackage.save(new File(releases.getName() + "_ReleasePlan.docx"));	// Save it
			return new File(releases.getName() + "_ReleasePlan.docx");
		} catch (Docx4JException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void addSprintInfo(MainDocumentPart mainDoc, ISprintPlanDesc sprint, HashMap<String, Float> tatolStoryPoints) {
		mainDoc.addStyledParagraphOfText("Heading1", "Sprint #" + sprint.getID() + "： " + sprint.getGoal());
		mainDoc.addStyledParagraphOfText("Subtitle", "Start Date： " + sprint.getStartDate());
		mainDoc.addStyledParagraphOfText("Subtitle", "End Date： " + sprint.getEndDate());
		mainDoc.addStyledParagraphOfText("Subtitle", "Total Story Points： " + tatolStoryPoints.get(sprint.getID()));
	}

	private void addStoryInfo(MainDocumentPart mainDoc, List<IIssue> storyList, LinkedHashMap<Long, IIssue[]> taskMap) throws JAXBException {
		IIssue story = null;
		Tbl table = null;
		int storySize = storyList.size();
		for (int i = 0; i < storySize; i++) {
			try {
				story = storyList.get(i);
				String tblXML = IOUtils.toString(new FileReader("StoryCard.xml"));
				table = (Tbl) XmlUtils.unmarshalString(tblXML);
				Tr row = null;
				// add ID
				row = (Tr) table.getContent().get(0);
				JAXBElement element = (JAXBElement) row.getContent().get(0);
				Tc tc = (Tc) element.getValue();
				tc.getContent().set(0, mainDoc.createParagraphOfText("Sprint Backlog Item #" + story.getIssueID()));
				
				// add Name
				row = (Tr) table.getContent().get(1);
				element = (JAXBElement) row.getContent().get(0);
				tc = (Tc) element.getValue();
				tc.getContent().set(0, mainDoc.createParagraphOfText(story.getSummary()));
				
				// add Importance
				element = (JAXBElement) row.getContent().get(1);
				tc = (Tc) element.getValue();
				element = (JAXBElement) tc.getContent().get(1);
				Tbl impTable = (Tbl) element.getValue();
				Tr tr = (Tr) impTable.getContent().get(0);
				element = (JAXBElement) tr.getContent().get(0);
				tc = (Tc) element.getValue();
				setCenterProperty(tc);
				tc.getContent().set(0, mainDoc.createStyledParagraphOfText("Bold_Number", story.getImportance()));
				
				// 空白格  row = (Tr) table.getContent().get(2);
				
				// add Notes
				row = (Tr) table.getContent().get(3);
				element = (JAXBElement) row.getContent().get(0);
				tc = (Tc) element.getValue();
				element = (JAXBElement) tc.getContent().get(1);
				Tbl noteTable = (Tbl) element.getValue();
				tr = (Tr) noteTable.getContent().get(0);
				element = (JAXBElement) tr.getContent().get(0);
				tc = (Tc) element.getValue();
				tc.getContent().set(0, mainDoc.createParagraphOfText(story.getNotes()));
				
				// add Estimate
				element = (JAXBElement) row.getContent().get(1);
				tc = (Tc) element.getValue();
				element = (JAXBElement) tc.getContent().get(1);
				Tbl estTable = (Tbl) element.getValue();
				tr = (Tr) estTable.getContent().get(0);
				element = (JAXBElement) tr.getContent().get(0);
				tc = (Tc) element.getValue();
				setCenterProperty(tc);
				tc.getContent().set(0, mainDoc.createStyledParagraphOfText("Bold_Number", story.getEstimated()));
				
				// 空白格  row = (Tr) table.getContent().get(4);
				// 空白格  row = (Tr) table.getContent().get(5);
				
				// add Tags
				row = (Tr) table.getContent().get(6);
				element = (JAXBElement) row.getContent().get(0);
				tc = (Tc) element.getValue();
				element = (JAXBElement) tc.getContent().get(1);
				Tbl tagTable = (Tbl) element.getValue();
				tr = (Tr) tagTable.getContent().get(0);
				element = (JAXBElement) tr.getContent().get(0);
				tc = (Tc) element.getValue();
				String tags = "";
				for (IIssueTag tag : story.getTag()) {
					if (!tags.isEmpty()) tags = tags + ",";
						tags = tags + tag.getTagName();
				}
				tc.getContent().set(0, mainDoc.createParagraphOfText(tags));
				
				// 空白格  row = (Tr) table.getContent().get(7);
				
				// add How to Demo
				row = (Tr) table.getContent().get(8);
				element = (JAXBElement) row.getContent().get(0);
				tc = (Tc) element.getValue();
				element = (JAXBElement) tc.getContent().get(1);
				Tbl howToDemoTable = (Tbl) element.getValue();
				tr = (Tr) howToDemoTable.getContent().get(0);
				element = (JAXBElement) tr.getContent().get(0);
				tc = (Tc) element.getValue();
				tc.getContent().set(0, mainDoc.createParagraphOfText(story.getHowToDemo()));

				// 空白格  row = (Tr) table.getContent().get(9);
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
	            e.printStackTrace();
            } catch (IOException e) {
	            e.printStackTrace();
            }
			mainDoc.addObject(table);
			mainDoc.addParagraphOfText("Task List: ");
			mainDoc.addObject(addTaskInfo(mainDoc, taskMap.get(story.getIssueID())));
			mainDoc.addParagraphOfText("");
			mainDoc.addParagraphOfText(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
			mainDoc.addParagraphOfText("");
		}
	}

	private void setCenterProperty(Tc tc) {
		TcPr tcPr = tc.getTcPr();
		if (tcPr == null) tcPr = factory.createTcPr();
		CTVerticalJc value = new CTVerticalJc();
		value = new CTVerticalJc();
		value.setVal(STVerticalJc.CENTER);
		tcPr.setVAlign(value);
		tc.setTcPr(tcPr);
    }

	private Tbl addTaskInfo(MainDocumentPart mainDoc, IIssue[] taskList) throws JAXBException {
		if (taskList == null) return null;
		int writableWidthTwips = wordMLPackage.getDocumentModel().getSections().get(0).getPageDimensions().getWritableWidthTwips();
		int rows = taskList.length;
		int cols = title.length;
		int cellWidthTwips = new Double(Math.floor((writableWidthTwips / cols))).intValue();
		Tbl table = factory.createTbl();
		String strTblPr = "<w:tblPr xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"><w:tblStyle w:val=\"TableGrid\"/><w:tblW w:w=\"0\" w:type=\"auto\"/><w:tblLook w:val=\"04A0\"/></w:tblPr>";
		TblPr tblPr = (TblPr) XmlUtils.unmarshalString(strTblPr);
		table.setTblPr(tblPr);

		 TblGrid tblGrid = factory.createTblGrid();
		 table.setTblGrid(tblGrid);
		 for (int i = 0; i < cols; i++) {
		 TblGridCol gridCol = factory.createTblGridCol();
			 gridCol.setW(BigInteger.valueOf(cellWidthTwips));
			 tblGrid.getGridCol().add(gridCol);
		 }

		for (int row = 0; row < rows; row++) {
			Tr tr = factory.createTr();
			table.getContent().add(tr);
			if (row == 0) {	// first row for title
				for (int col = 0; col < cols; col++) {
					Tc tc = factory.createTc();
					tc.getContent().add(mainDoc.createStyledParagraphOfText("Table_Cell_Center", title[col]));
					TcPr tcPr = factory.createTcPr();
					TblWidth cellWidth = factory.createTblWidth();
					tcPr.setTcW(cellWidth);
					cellWidth.setType(TblWidth.TYPE_AUTO);
					cellWidth.setW(BigInteger.valueOf(cellWidthTwips));
					CTVerticalJc value = new CTVerticalJc();
					value = new CTVerticalJc();
					value.setVal(STVerticalJc.CENTER);
					tcPr.setVAlign(value);
					tc.setTcPr(tcPr);
					tr.getContent().add(tc);
				}
				tr = factory.createTr();
				table.getContent().add(tr);
			}

			for (int col = 0; col < cols; col++) {
				Tc tc = factory.createTc();
				if (col == 0) {	// task Id;
					setCenterProperty(tc);
					tc.getContent().add(mainDoc.createStyledParagraphOfText("Table_Cell_Center", String.valueOf(taskList[row].getIssueID())));
				} else if (col == 1) {	// task Name
					tc.getContent().add(mainDoc.createParagraphOfText(taskList[row].getSummary()));
				} else if (col == 2) {	// task Est.;
					setCenterProperty(tc);
					tc.getContent().add(mainDoc.createStyledParagraphOfText("Table_Cell_Center", taskList[row].getEstimated()));
				} else if (col == 3) {	// task Handler
					tc.getContent().add(mainDoc.createParagraphOfText(taskList[row].getAssignto()));
				} else if (col == 4) {	// task Partners
					tc.getContent().add(mainDoc.createParagraphOfText(taskList[row].getPartners()));
				} else if (col == 5) {	// task Notes
					tc.getContent().add(mainDoc.createParagraphOfText(taskList[row].getNotes()));
				}
				tr.getContent().add(tc);
				TcPr tcPr = factory.createTcPr();
				tc.setTcPr(tcPr);
				TblWidth cellWidth = factory.createTblWidth();
				tcPr.setTcW(cellWidth);
				cellWidth.setType(TblWidth.TYPE_AUTO);
				cellWidth.setW(BigInteger.valueOf(cellWidthTwips));
			}
		}
		return table;
	}

	/**
	 * Adds a page break to the document.
	 */
	private void addPageBreak(MainDocumentPart mainDoc) {
		Br breakObj = new Br();
		breakObj.setType(STBrType.PAGE);

		P paragraph = factory.createP();
		paragraph.getContent().add(breakObj);
		mainDoc.getJaxbElement().getBody().getContent().add(paragraph);
	}

	/**
	 * The document style setting
	 */
    public void alterStyleSheet() {
        StyleDefinitionsPart styleDefinitionsPart = wordMLPackage.getMainDocumentPart().getStyleDefinitionsPart();
        Styles styles = styleDefinitionsPart.getJaxbElement();
        List<Style> stylesList = styles.getStyle();
        Style s = new Style();
        s.setStyleId("Bold");
		stylesList.add(s);
		s = new Style();
        s.setStyleId("Bold_Number");
		stylesList.add(s);
		s = new Style();
		s.setStyleId("Table_Cell_Center");
		stylesList.add(s);
		
    	RPr rpr;
        for (Style style : stylesList) {
        	rpr = style.getRPr();
        	if (rpr == null) rpr = new RPr();
            if (style.getStyleId().equals("Bold")) {
            	setBoldStyle(rpr, true);
            } else if (style.getStyleId().equals("Bold_Number")) {
                setFontSize(rpr, 56);
            	setBoldStyle(rpr, true);
            	PPr ppr = style.getPPr();
            	if (ppr == null) ppr = factory.createPPr();
            	setTextCenter(ppr);
            	setTextAfterSpace(ppr);
            	style.setPPr(ppr);
	        } else if (style.getStyleId().equals("Table_Cell_Center")) {
	        	PPr ppr = style.getPPr();
	        	if (ppr == null) ppr = factory.createPPr();
	        	setTextCenter(ppr);
	        	setTextAfterSpace(ppr);
	        	style.setPPr(ppr);
	        }
            setFontStyle(rpr);
            style.setRPr(rpr);
        }
    }
    
    /**
     * 設定該段落下面的空白
     * @param ppr the paragraph property  
     */
    private void setTextAfterSpace(PPr ppr) {
    	Spacing spacing = Context.getWmlObjectFactory().createPPrBaseSpacing();
		spacing.setAfterAutospacing(true);
    	ppr.setSpacing(spacing);
    }

    /**
     * set text in center
     * @param ppr the paragraph property  
     */
	private void setTextCenter(PPr ppr) {
    	Jc jc = new Jc();
    	jc.setVal(JcEnumeration.CENTER);
    	ppr.setJc(jc);
    }

    /**
     * 設定字型
     * @param ppr the run property  
     */
    private static void setFontStyle(RPr runProperties) {
        RFonts runFont = new RFonts();
        runFont.setAscii("Times New Roman");	// for English and Number
        runFont.setEastAsia("標楷體");			// for Chinese
        runProperties.setRFonts(runFont);
    }
    
    /**
     * 設定字的大小
     * @param ppr the run property  
     */
    private static void setFontSize(RPr runProperties, int fontSize) {
        HpsMeasure size = new HpsMeasure();
        size.setVal(BigInteger.valueOf(fontSize));
        runProperties.setSz(size);
        runProperties.setSzCs(size);
    }
    
    /**
     * 設定粗體
     * @param ppr the run property  
     */
	private static void setBoldStyle(RPr runProperties, boolean bool) {
		BooleanDefaultTrue b = new BooleanDefaultTrue();
		b.setVal(bool);
		runProperties.setB(b);
	}
}
