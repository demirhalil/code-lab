package visitorpattern.visitor;

import visitorpattern.element.ImageDocument;
import visitorpattern.element.PDFDocument;
import visitorpattern.element.TextDocument;

public class MetadataExtractorVisitor implements DocumentVisitor{
    @Override
    public void visit(final TextDocument document) {
        System.out.println("Extracting metadata from text document");
    }

    @Override
    public void visit(final ImageDocument document) {
        System.out.println("Extracting metadata from image document");
    }

    @Override
    public void visit(final PDFDocument document) {
        System.out.println("Extracting metadata from PDF document");
    }
}
