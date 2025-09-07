# Visitor Design Pattern
Add new operations to a group of related objects without changing their classes.

## What is the Visitor Pattern?
The Visitor pattern lets you keep the object structure (your data types) stable while freely adding new behaviors (operations) later. Each object "accepts" a visitor, and the visitor knows how to perform the operation for that specific object type.

- Elements: the existing objects in your structure (e.g., TextDocument, PDFDocument, ImageDocument)
- Visitor: a separate object that carries a new operation (e.g., compress, extract metadata, security scan)
- Double Dispatch: `element.accept(visitor)` calls the most specific method like `visitor.visitPdf(...)`, so the right logic runs for each element type.

## Why use it?
- Add new operations without modifying element classes
- Keep element code clean and focused on core data/behavior
- Centralize cross-cutting operations in dedicated visitors

## Simple Analogy: Airport Security Check
Think of passengers (elements) going through an airport security checkpoint (visitor).
- Passengers come in different types: adult, child, crew (Text, PDF, Image)
- The security process (Visitor) applies slightly different checks based on who the passenger is
- The checkpoint doesn’t change the passenger; it just performs an operation and moves on
- If the airport adds a new procedure (e.g., health check), they introduce a new station (new Visitor) without changing the passengers themselves

## How this repo demonstrates it
In `visitorpattern/`:
- Elements: `TextDocument`, `PDFDocument`, `ImageDocument` implement `Document` with an `accept(DocumentVisitor visitor)` method
- Visitors: `CompressionVisitor`, `MetadataExtractorVisitor`, `SecurityScanVisitor` implement different operations
- Client: `DocumentProcessingSystem` builds a list of documents and runs multiple visitors over the same set, each adding behavior without altering the document classes

## When to Use
- You have a stable set of element types but expect to add new operations frequently
- You want to run multiple, unrelated operations over the same object structure
- You prefer to keep operation-specific logic outside element classes

Tip: If you expect to frequently add new element types (not just new operations), the Visitor pattern may require more updates, since each visitor needs a method for the new type.
