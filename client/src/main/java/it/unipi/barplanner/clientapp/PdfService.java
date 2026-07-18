package it.unipi.barplanner.clientapp;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

public class PdfService {

    private Color coloreIntestazione;
    private Color coloreSfondoRiga;
    private Color coloreTestoHeader;
    private String titoloMenu;
    private String emoji;

    public void creaPdfMenu(List<MenuItem> items, File dest, String turnoCorrente) throws Exception {
        if ("GIORNO".equalsIgnoreCase(turnoCorrente)) {
            this.coloreIntestazione = new Color(101, 67, 33);  // Marrone scuro
            this.coloreTestoHeader = Color.ORANGE;             // arancione/oro
            this.coloreSfondoRiga = new Color(250, 240, 230);  // Beige lino
            this.titoloMenu = "Breakfast & Brunch";
            this.emoji = "☕";
        } else {
            this.coloreIntestazione = new Color(25, 25, 112);  // Midnight Blue
            this.coloreTestoHeader = Color.WHITE;              // Scritta bianca
            this.coloreSfondoRiga = new Color(230, 230, 250);  // Lavanda chiaro
            this.titoloMenu = "Cocktail & Drinks";
            this.emoji = "🍸";
        }
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(dest));
        document.open();
        
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);
        PdfPCell cellHeader = new PdfPCell(new Phrase(emoji + " " + titoloMenu, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, coloreTestoHeader)));
        cellHeader.setBackgroundColor(coloreIntestazione);
        cellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellHeader.setPadding(20);
        cellHeader.setBorder(Rectangle.NO_BORDER);
        headerTable.addCell(cellHeader);
        document.add(headerTable);
        
        String dataStampa = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Paragraph info = new Paragraph("Menu made on: " + dataStampa);
        info.setAlignment(Element.ALIGN_CENTER);
        info.setSpacingBefore(10);
        info.setSpacingAfter(20);
        document.add(info);
        
        PdfPTable table = new PdfPTable(new float[] {3, 3, 1.5f});
        table.setWidthPercentage(100);
        addHeaderCell(table, "Item");
        addHeaderCell(table, "Ingredients");
        addHeaderCell(table, "Price");
        
        boolean alternate = false;
        for (MenuItem item : items) {
            Color bg = alternate ? coloreSfondoRiga : Color.WHITE;
            
            addBodyCell(table, item.getNome(), bg, Element.ALIGN_LEFT);
            addBodyCell(table, formatIngredienti(item.getIngredienti()), bg, Element.ALIGN_LEFT);
            addBodyCell(table, String.format("%.2f €", item.getPrezzo()), bg, Element.ALIGN_RIGHT);
            
            alternate = !alternate;
        }
        
        document.add(table);
        document.close();
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, coloreTestoHeader)));
        cell.setBackgroundColor(coloreIntestazione);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }
    
    private void addBodyCell(PdfPTable table, String text, Color bgColor, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : ""));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(align);
        cell.setPadding(6);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBorderWidthBottom(0.5f);
        cell.setBorderColorBottom(Color.LIGHT_GRAY);
        table.addCell(cell);
    }
    
    private String formatIngredienti(Set<String> ingredienti) {
        if (ingredienti == null || ingredienti.isEmpty()) return "-";
        return String.join(", ", ingredienti);
    }
}
