package com.designpatterns.visitorpattern.element;

import com.designpatterns.visitorpattern.visitor.DocumentVisitor;

public class TextDocument implements Document{
    private String content;
    private int wordCount;

    public TextDocument(String content, int wordCount) {
        this.content = content;
        this.wordCount = wordCount;
    }

    @Override
    public void accept(final DocumentVisitor visitor) {
        visitor.visit(this);
    }

    public String getContent() {
        return content;
    }
    public int getWordCount() {
        return wordCount;
    }
}
