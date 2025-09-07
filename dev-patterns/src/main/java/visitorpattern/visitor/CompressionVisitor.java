package visitorpattern.visitor;

import visitorpattern.element.ImageDocument;
import visitorpattern.element.PDFDocument;
import visitorpattern.element.TextDocument;

public class CompressionVisitor implements DocumentVisitor{
    @Override
    public void visit(final TextDocument document) {
        System.out.println("Compressing text document" + document.getContent().substring(0, 10) + "...");
        System.out.println("Text document compressed successfully");
    }

    @Override
    public void visit(final ImageDocument document) {
        System.out.println("Compressing image document" + document.getFileName());
        if (document.getFormat().equals("PNG")) {
            System.out.println("Converting PNG to JPEG image document compressed successfully");
        }
        System.out.println("Image document compressed successfully");
    }

    @Override
    public void visit(final PDFDocument document) {
        System.out.println("Compressing PDF document with" + document.getPageCount() + " pages");
        if (document.isEncrypted()) {
            System.out.println("Cannot compress encrypted PDF document");
        } else {
            System.out.println("Compressing PDF document pages...");
        }
    }
}
