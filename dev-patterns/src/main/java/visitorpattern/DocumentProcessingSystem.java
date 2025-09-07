package visitorpattern;

import visitorpattern.element.Document;
import visitorpattern.element.ImageDocument;
import visitorpattern.element.PDFDocument;
import visitorpattern.element.TextDocument;
import visitorpattern.visitor.CompressionVisitor;
import visitorpattern.visitor.DocumentVisitor;
import visitorpattern.visitor.MetadataExtractorVisitor;
import visitorpattern.visitor.SecurityScanVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocumentProcessingSystem {
    public static void main(String[] args) {
        List<Document> documents = Arrays.asList(
                new TextDocument("Lorem ipsum dolor sit amet, consectetur adipiscing elit...", 1500),
                new ImageDocument("vacation-photo.png", "PNG", 2048576),
                new PDFDocument(25, false),
                new PDFDocument(10, true)
        );

        List<DocumentVisitor> visitors = Arrays.asList(
                new SecurityScanVisitor(),
                new MetadataExtractorVisitor(),
                new CompressionVisitor()
        );

        for (DocumentVisitor visitor : visitors) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("--- Processing documents with " + visitor.getClass().getSimpleName() + " ---");
            System.out.println("=".repeat(50));

            for (Document document : documents) {
                System.out.println("\n--- Processing " + document.getClass().getSimpleName() + " ---");
                document.accept(visitor);
            }

        }

    }
}
