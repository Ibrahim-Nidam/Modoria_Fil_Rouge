package com.modoria.order.application.service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.modoria.order.domain.model.Order;
import com.modoria.order.domain.model.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Slf4j
@Service
public class PdfInvoiceService {

    public byte[] generateInvoice(Order order) {
        log.info("Generating PDF invoice for order ID: {}", order.getId());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Document document = new Document()) {

            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Modoria - Order Invoice\n\n", titleFont);
            title.setAlignment(1); // Center alignment
            document.add(title);

            Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            document.add(new Paragraph("Order ID: " + order.getId(), regularFont));
            document.add(new Paragraph("Date: " + order.getCreatedAt(), regularFont));
            document.add(new Paragraph("Customer: " + order.getUser().getEmail(), regularFont));
            document.add(new Paragraph("Status: " + order.getStatus(), regularFont));
            document.add(new Paragraph("\n--- Items ---\n\n", regularFont));

            for (OrderItem item : order.getItems()) {
                String itemText = String.format("- %s (x%d) : $%.2f",
                        item.getProduct().getName(), item.getQuantity(), item.getPrice());
                document.add(new Paragraph(itemText, regularFont));
            }

            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            document.add(new Paragraph("\nTotal Amount: $" + order.getTotalAmount(), boldFont));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Failed to generate PDF invoice for order {}", order.getId(), e);
            throw new RuntimeException("Error generating invoice PDF", e);
        }
    }
}
