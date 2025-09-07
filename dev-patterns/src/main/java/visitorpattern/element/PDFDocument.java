package visitorpattern.element;

import visitorpattern.visitor.DocumentVisitor;

public class PDFDocument implements Document{

    private int pageCount;
    private boolean isEncrypted;

    public PDFDocument(int pageCount, boolean isEncrypted) {
        this.pageCount = pageCount;
        this.isEncrypted = isEncrypted;
    }

    @Override
    public void accept(final DocumentVisitor visitor) {
        visitor.visit(this);
    }

    public int getPageCount() {
        return pageCount;
    }
    public boolean isEncrypted() {
        return isEncrypted;
    }
}
