package visitorpattern.element;

import visitorpattern.visitor.DocumentVisitor;

public interface Document {
    void accept(DocumentVisitor visitor);
}
