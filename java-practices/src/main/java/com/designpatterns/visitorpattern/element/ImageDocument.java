package com.designpatterns.visitorpattern.element;

import com.designpatterns.visitorpattern.visitor.DocumentVisitor;

public class ImageDocument implements Document{
    private String fileName;
    private String format;
    private long sizeInBytes;

    public ImageDocument(String fileName, String format, long sizeInBytes) {
        this.fileName = fileName;
        this.format = format;
        this.sizeInBytes = sizeInBytes;
    }

    @Override
    public void accept(final DocumentVisitor visitor) {
        visitor.visit(this);
    }

    public String getFileName() {
        return fileName;
    }
    public String getFormat() {
        return format;
    }
    public long getSizeInBytes() {
        return sizeInBytes;
    }
}
