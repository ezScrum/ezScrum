package ntut.csie.ezScrum.pic.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.StoryObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class MakePDFService {
	private static Log log = LogFactory.getLog(MakePDFService.class);

	public File getFile(String ttfPath, ArrayList<StoryObject> issues)
			throws Exception {
		File temp = File.createTempFile("ezScrum",
				Long.toString(System.nanoTime()));
		String path = temp.getAbsolutePath();

		Document document1 = new Document(PageSize.A4);
		try {

			BaseFont bfChinese = BaseFont.createFont(ttfPath,
					BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

			PdfWriter.getInstance(document1, new FileOutputStream(path));

			document1.open();

			for (StoryObject issue : issues) {
				PdfPTable outerTable = new PdfPTable(1);
				outerTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
				outerTable.getDefaultCell().setBorderWidth(1f);
				PdfPTable table = new PdfPTable(2);
				float[] widths = { 420f, 100f };
				table.setWidths(widths);

				PdfPCell cell;
				PdfPTable innerTable;
				Paragraph paragraph;

				// Title
				cell = new PdfPCell();
				cell.setBorder(PdfPCell.TOP | PdfPCell.LEFT | PdfPCell.RIGHT);
				cell.setBorderWidth(1f);
				cell.setColspan(2);
				Paragraph titleParagraph = new Paragraph(
						"Sprint Backlog Item #" + String.valueOf(issue.getId()),
						new Font(bfChinese, 12, Font.NORMAL));
				cell.setPaddingLeft(6f);
				cell.addElement(titleParagraph);

				table.addCell(cell);

				// Tip
				cell = new PdfPCell();
				cell.setBorder(PdfPCell.LEFT);
				cell.setBorderWidth(1f);
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setBorder(PdfPCell.RIGHT);
				cell.setBorderWidth(1f);
				paragraph = new Paragraph("Importance", new Font(bfChinese, 12,
						Font.NORMAL));
				paragraph.setAlignment(Paragraph.ALIGN_CENTER);
				cell.addElement(paragraph);
				table.addCell(cell);

				// Summary and importance
				cell = new PdfPCell();
				cell.setBorder(PdfPCell.LEFT);
				cell.setBorderWidth(1f);
				cell.setPaddingLeft(6f);
				cell.addElement(new Paragraph(issue.getName(), new Font(
						bfChinese, 14, Font.NORMAL)));
				table.addCell(cell);

				innerTable = new PdfPTable(1);
				innerTable.setTotalWidth(80f);

				cell = new PdfPCell();
				cell.setBorderWidth(1f);
				cell.setMinimumHeight(60f);
				cell.setPaddingTop(-10f);
				cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
				paragraph = new Paragraph(
						String.valueOf(issue.getImportance()), new Font(
								bfChinese, 36, Font.NORMAL));
				paragraph.setAlignment(Paragraph.ALIGN_CENTER);

				cell.addElement(paragraph);
				innerTable.addCell(cell);

				cell = new PdfPCell(innerTable);
				cell.setBorder(PdfPCell.RIGHT);
				cell.setBorderWidth(1f);
				cell.setPadding(5f);

				table.addCell(cell);

				// Tip
				cell = new PdfPCell();
				cell.setBorder(PdfPCell.LEFT);
				cell.setBorderWidth(1f);
				cell.setPaddingLeft(6f);
				paragraph = new Paragraph("Notes", new Font(bfChinese, 12,
						Font.NORMAL));
				cell.addElement(paragraph);
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setBorder(PdfPCell.RIGHT);
				cell.setBorderWidth(1f);
				paragraph = new Paragraph("Estimate", new Font(bfChinese, 12,
						Font.NORMAL));
				paragraph.setAlignment(Paragraph.ALIGN_CENTER);
				cell.addElement(paragraph);
				table.addCell(cell);

				// Notes and estimated
				innerTable = new PdfPTable(1);
				innerTable.setTotalWidth(80f);

				cell = new PdfPCell();
				cell.setBorderWidth(1f);
				cell.setMinimumHeight(60f);
				cell.setPaddingBottom(10f);
				cell.addElement(new Paragraph(issue.getNotes(), new Font(
						bfChinese, 14, Font.NORMAL)));

				innerTable.addCell(cell);

				cell = new PdfPCell();
				cell.setBorder(PdfPCell.NO_BORDER);
				innerTable.addCell(cell);

				cell = new PdfPCell(innerTable);
				cell.setBorder(PdfPCell.LEFT);
				cell.setBorderWidth(1f);
				cell.setPadding(5f);

				table.addCell(cell);

				innerTable = new PdfPTable(1);
				innerTable.setTotalWidth(80f);
				innerTable.setExtendLastRow(false);

				cell = new PdfPCell();
				cell.setBorderWidth(1f);
				cell.setMinimumHeight(60f);
				cell.setUseAscender(false);
				cell.setPaddingTop(-10f);
				paragraph = new Paragraph(String.valueOf(issue.getEstimate()),
						new Font(bfChinese, 36, Font.NORMAL));
				paragraph.setAlignment(Paragraph.ALIGN_CENTER);

				cell.addElement(paragraph);
				innerTable.addCell(cell);

				cell = new PdfPCell();
				cell.setBorder(PdfPCell.NO_BORDER);
				innerTable.addCell(cell);

				cell = new PdfPCell(innerTable);
				cell.setBorder(PdfPCell.RIGHT);
				cell.setBorderWidth(1f);
				cell.setPadding(5f);

				table.addCell(cell);

				// Tip
				cell = new PdfPCell();
				cell.setBorder(PdfPCell.RIGHT | PdfPCell.LEFT);
				cell.setColspan(2);
				cell.setBorderWidth(1f);
				cell.setPaddingLeft(6f);
				paragraph = new Paragraph("How to demo", new Font(bfChinese,
						12, Font.NORMAL));
				cell.addElement(paragraph);
				table.addCell(cell);

				// How to demo
				innerTable = new PdfPTable(1);

				cell = new PdfPCell();
				cell.setBorderWidth(1f);
				cell.setMinimumHeight(60f);
				cell.setPaddingBottom(10f);
				cell.addElement(new Paragraph(issue.getHowToDemo(), new Font(
						bfChinese, 14, Font.NORMAL)));

				innerTable.addCell(cell);

				cell = new PdfPCell(innerTable);
				cell.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT);
				cell.setBorderWidth(1f);
				cell.setPadding(5f);

				table.getDefaultCell().setBorder(
						PdfPCell.LEFT | PdfPCell.BOTTOM);
				table.getDefaultCell().setBorderWidth(1f);
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setBorder(PdfPCell.RIGHT | PdfPCell.BOTTOM);
				cell.setBorderWidth(1f);
				table.addCell(cell);

				outerTable.addCell(table);
				document1.add(outerTable);
				document1.add(new Paragraph("\n"));
			}

		} catch (DocumentException de) {
			log.debug(de.toString());
		} catch (IOException ioe) {
			log.debug(ioe.toString());
		}

		document1.close();
		File file = new File(path);

		return file;

	}

}
