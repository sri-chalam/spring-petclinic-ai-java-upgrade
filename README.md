 
# spring-petclinic-ai-java-upgrade

## Purpose

This repository is a **work in progress** focused on exploring the use of AI coding agents for Java version upgrades.

The source code in this repository is a snapshot taken from the [Spring PetClinic](https://github.com/spring-projects/spring-petclinic) project, which currently uses Java 17. The primary objective of this exploration is to leverage AI coding agents to upgrade the codebase to Java 21 and Java 25.

The outcome of this exploration will be a set of AI-generated instructions and best practices for automated Java version upgrades.

## Why Java Upgrades Are Not Simple

At first glance, upgrading a Java project from version 17 to 21 might seem straightforward. One could simply prompt an LLM: "upgrade this project from Java 17 to Java 21" and expect it to handle everything. While such a prompt might accomplish a few basic changes, the reality is far more nuanced.

### The Hidden Complexity

A comprehensive Java upgrade involves addressing numerous organizational, technical, and environmental considerations:

**Java Distribution and Provider Selection**
- Which Java distribution should be used? (Oracle JDK, OpenJDK, Amazon Corretto, Azul Zulu, Eclipse Temurin, GraalVM, etc.)
- What are the organization's standards and compliance requirements?
- Are there licensing considerations or vendor support requirements?

**Installation and Environment Management**
- What installation method should be employed? (SDKMAN, jenv, manual installation, package managers)
- Does the organization follow specific conventions for Java installation paths?
- Are there trusted certificates that need to be imported post-installation?
- How should multiple Java versions be managed on development machines?

**Build Tool Variations**
- Should upgrade instructions cover Maven, Gradle, or both?
- For Gradle projects, are build files written in Groovy or Kotlin DSL?

**Upgrade Approach Selection**
- Manual dependency updates vs. automated migration tools (OpenRewrite, etc.)
- In-place upgrade vs. incremental migration strategy
- How to handle breaking changes and deprecated APIs?
- Testing strategy to ensure compatibility

**Additional Considerations**
- CI/CD pipeline updates (GitHub Actions, Jenkins, GitLab CI, AWS CodeBuild, etc.)
- Docker and container base image updates
- IDE and tooling configuration updates
- Documentation and team training requirements

### The Scale of Comprehensive Instructions

To properly address all these variations and provide concrete examples for each scenario, a complete upgrade guide would easily span hundreds or even thousands of lines. Each combination of choices (distribution × installation method × build tool × upgrade approach) represents a unique path that requires specific instructions and examples.


