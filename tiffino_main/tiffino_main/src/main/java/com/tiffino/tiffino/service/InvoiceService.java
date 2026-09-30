package com.tiffino.tiffino.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.tiffino.tiffino.entity.Order;
import com.tiffino.tiffino.entity.OrderItem;
import com.tiffino.tiffino.repository.OrderRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private OrderRepo orderRepo;

    /**
     * Generates PDF invoice for the order.
     * Only the user who owns the order can generate it.
     */
    public void generateInvoice(Long orderId, String userEmail, HttpServletResponse response) throws IOException {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        // ================= SECURITY CHECK =================
        if (!order.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "You are not allowed to view this invoice.");
            return;
        }

        if (!order.getStatus().equalsIgnoreCase("DELIVERED")) {
            throw new RuntimeException("Invoice can only be generated for delivered orders.");
        }

        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // ================= HEADER =================
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22);
            Paragraph title = new Paragraph("Tiffino Order Invoice", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("\nOrder ID: " + order.getOrderId()));
            document.add(new Paragraph("Order Date: " +
                    order.getOrderTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));

            document.add(new Paragraph("Customer: " + order.getUser().getUserName()));
            document.add(new Paragraph("Email: " + order.getUser().getEmail()));
            document.add(new Paragraph("-------------------------------------------------------------\n"));

            // ================= ORDER ITEMS TABLE =================
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            addTableHeader(table, "Meal Name");
            addTableHeader(table, "Meal Image");
            addTableHeader(table, "Price");
            addTableHeader(table, "Quantity");
            addTableHeader(table, "Total");

            double grandTotal = 0;
            List<OrderItem> items = order.getItems();

            for (OrderItem item : items) {
                double total = item.getMealPrice() * item.getQuantity();
                grandTotal += total;

                table.addCell(item.getMealName());

                try {
                    Image img = Image.getInstance(item.getMealPhoto());
                    img.scaleToFit(60, 60);
                    PdfPCell imgCell = new PdfPCell(img, true);
                    imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(imgCell);
                } catch (Exception e) {
                    table.addCell("No Image");
                }

                table.addCell("₹" + item.getMealPrice());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell("₹" + String.format("%.2f", total));
            }
            // ✅ Add this right after the loop
            double allergyCost = 0;
            if (order.getAllergies() != null && !order.getAllergies().isEmpty()) {
                allergyCost = order.getAllergies().size() * 10.0;
                document.add(new Paragraph("Allergy Add-ons (" + order.getAllergies().size() + "): ₹" + String.format("%.2f", allergyCost)));
                grandTotal += allergyCost;
            }

            document.add(table);

            // ================= FOOTER =================
            Paragraph grandTotalPara = new Paragraph("Grand Total: ₹" + String.format("%.2f", grandTotal),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            grandTotalPara.setAlignment(Element.ALIGN_RIGHT);
            document.add(grandTotalPara);

            document.add(new Paragraph("\nDelivery Address: " + order.getAddress() + ", " + order.getCity() +
                    ", " + order.getState() + " - " + order.getPinCode()));
            document.add(new Paragraph("\nThank you for ordering with Tiffino!"));

        } catch (DocumentException e) {
            throw new IOException("Error generating PDF", e);
        } finally {
            document.close();
        }
    }

    private void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setBorderWidth(1);
        header.setPhrase(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
    }
}
