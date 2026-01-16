# Domain Layer

The Domain Layer is the heart of the business logic. It contains the business state and rules.

## Components:
- **model**: Entities, Value Objects, and Aggregates that represent the business concepts.
- **service**: Domain Services that contain business logic that doesn't naturally fit into an Entity or Value Object.
- **repository**: Interfaces for accessing aggregates (Persistence ignorance).
