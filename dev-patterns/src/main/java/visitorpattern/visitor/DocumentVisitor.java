package visitorpattern.visitor;

import visitorpattern.element.ImageDocument;
import visitorpattern.element.PDFDocument;
import visitorpattern.element.TextDocument;

public interface DocumentVisitor {
    void visit(TextDocument document);
    void visit(ImageDocument document);
    void visit(PDFDocument document);
}
