package com.apachefop.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class PdfToJpgConverter {

    public static void convertPdfToJpg(byte[] pdfBytes) throws IOException {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int numPages = document.getNumberOfPages();

            // Create a directory for storing images if it doesn't exist
            File directory = new File("src/main/resources/images");
            if (!directory.exists()) {
                directory.mkdir();
            }

            // Convert each page of the PDF to a JPG image and save it
            for (int i = 0; i < numPages; i++) {
                BufferedImage image = pdfRenderer.renderImageWithDPI(i, 300);
                File outputFile = new File(directory, "export_" + i + ".jpeg");
                ImageIO.write(image, "jpeg", outputFile);
            }

        }
    }
}
