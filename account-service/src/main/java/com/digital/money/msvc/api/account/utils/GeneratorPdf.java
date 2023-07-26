package com.digital.money.msvc.api.account.utils;

import com.digital.money.msvc.api.account.model.dto.TransactionGetDto;
import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;

import java.awt.*;
import java.io.IOException;

@Setter
public class GeneratorPdf {

    // List to hold all Students
    private TransactionGetDto transactionSuccessful;

    public void generate(HttpServletResponse response) throws DocumentException, IOException {

        // Creating the Object of Document
        Document document = new Document(new RectangleReadOnly(600.0F, 320.0F));
        // Getting instance of PdfWriter
        PdfWriter.getInstance(document, response.getOutputStream());
        // Opening the created document to modify it
        document.open();

        // Creating font
        // Setting font style and size
        Font fontTiltle = FontFactory.getFont(FontFactory.TIMES_BOLD);
        fontTiltle.setSize(20);
        fontTiltle.setColor(Color.BLUE);

        // Creating paragraph
        Paragraph paragraph = new Paragraph("Successful Transfer", fontTiltle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        // Adding the created paragraph in document
        document.add(paragraph);

        // Creating a table of 3 columns
        PdfPTable table = new PdfPTable(2);
        table.setComplete(true);
        table.setWidthPercentage(100f);
        table.setWidths(new int[]{2, 3});
        table.setSpacingBefore(5);
        table.setSpacingAfter(5);

        // Create Table Cells for table header
        PdfPCell cell = new PdfPCell();
        cell.setPadding(4f);
        cell.setPaddingLeft(2f);
        cell.setIndent(4f);

        // Creating font
        // Setting font style and size
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        font.setColor(CMYKColor.BLACK);
        Font fontNoBold = FontFactory.getFont(FontFactory.COURIER_BOLD, 14);
        fontNoBold.setColor(CMYKColor.BLACK);
        Font fontBlue = FontFactory.getFont(FontFactory.COURIER_BOLD, 14);
        fontBlue.setColor(CMYKColor.BLUE);
        Font fontRed = FontFactory.getFont(FontFactory.COURIER_BOLD, 14);
        fontRed.setColor(CMYKColor.RED);

        // Adding headings in the created table cell/ header
        // Adding Cell to table
        cell.setPhrase(new Phrase("ID", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase(String.valueOf(transactionSuccessful.getTransactionId()), fontNoBold));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Amount", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("$".concat(String.valueOf(transactionSuccessful.getAmount())), fontRed));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Date", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase(String.valueOf(transactionSuccessful.getRealizationDate()), fontNoBold));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Description", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase(transactionSuccessful.getDescription(), fontNoBold));
        table.addCell(cell);
        cell.setPhrase(new Phrase("From", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase(transactionSuccessful.getFromCvu(), fontNoBold));
        table.addCell(cell);
        cell.setPhrase(new Phrase("To", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase(transactionSuccessful.getToCvu(), fontNoBold));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Type", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase(transactionSuccessful.getType().name(), fontBlue));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Account", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase(transactionSuccessful.getAccount().getAlias(), fontNoBold));
        table.addCell(cell);
        cell.setPhrase(new Phrase("$".concat(String.valueOf(transactionSuccessful.getAccount().getAvailableBalance())), fontBlue));
        table.addCell(cell);

        document.add(table);
        document.close();
    }
}
