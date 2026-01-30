package com.designpatterns.visitorpattern.visitor;

import com.designpatterns.visitorpattern.element.ImageDocument;
import com.designpatterns.visitorpattern.element.PDFDocument;
import com.designpatterns.visitorpattern.element.TextDocument;

public interface DocumentVisitor {
    void visit(TextDocument document);
    void visit(ImageDocument document);
    void visit(PDFDocument document);
}
