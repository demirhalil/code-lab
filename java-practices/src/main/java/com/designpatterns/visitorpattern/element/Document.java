package com.designpatterns.visitorpattern.element;

import com.designpatterns.visitorpattern.visitor.DocumentVisitor;

public interface Document {
    void accept(DocumentVisitor visitor);
}
