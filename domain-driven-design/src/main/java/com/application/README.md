# Application Layer

The Application Layer coordinates the activity of the application. It doesn't contain business rules or state, but only coordinates tasks and delegates work to the domain objects in the next layer down.

## Components:
- **service**: Application Services that implement use cases.
- **dto**: Data Transfer Objects used to transfer data between the interface and application layers.
- **mapper**: Components responsible for converting between Domain Models and DTOs.
