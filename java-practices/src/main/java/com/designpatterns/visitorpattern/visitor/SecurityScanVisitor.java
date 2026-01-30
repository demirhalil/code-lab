package com.designpatterns.visitorpattern.visitor;

import com.designpatterns.visitorpattern.element.ImageDocument;
import com.designpatterns.visitorpattern.element.PDFDocument;
import com.designpatterns.visitorpattern.element.TextDocument;

public class SecurityScanVisitor implements DocumentVisitor{
    @Override
    public void visit(final TextDocument document) {
        System.out.println("Scanning text document for viruses...");
    }

    @Override
    public void visit(final ImageDocument document) {
        System.out.println("Scanning image document for viruses...");
    }

    @Override
    public void visit(final PDFDocument document) {
        System.out.println("Scanning PDF document for viruses...");
    }
}
