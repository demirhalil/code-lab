# Code Lab

A small playground repository for experimenting with software architecture, patterns, and engineering practices. The goal is to try ideas quickly, compare approaches, and document learnings—not to produce production-ready code.

## Modules
- architecture-playground — exploratory code for architecture styles and boundaries (e.g., hexagonal, modularization, layering).
- dev-patterns — experiments with design patterns and implementation techniques (e.g., strategy, adapter, decorators), plus testing and refactoring practices.

## Getting Started
Prerequisites:
- JDK 21
- Git
- Gradle 8.14

Build the project (uses the Gradle wrapper):
```bash
./gradlew build
```

Run tests:
```bash
./gradlew test
```

List available tasks:
```bash
./gradlew tasks
```

## Project Structure
- settings.gradle — declares modules
- build.gradle and module-level build.gradle — Gradle configuration
- gradle/ — Gradle wrapper and version catalogs


