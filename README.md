 
# spring-petclinic-ai-java-upgrade

<!-- TODO Add Table of Contents at the end. -->


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

## AI-Driven Upgrades - Limitations and Expectations

When using AI-driven instruction files for Java upgrades, it's important to understand the nature and limitations of this approach:

### Non-Deterministic Execution

Unlike executing a traditional script, executing an AI instruction file is not deterministic. When the same instructions are executed multiple times, the output could potentially be somewhat different. Each execution may involve slightly different decisions, even when following the same instruction set.

### Potential for AI Hallucination and Mistakes

This approach is similar to using an AI agent to reply to emails or perform other automated tasks. Sometimes, the AI agent could experience hallucination or make mistakes. 

### Instruction Coverage Limitations

The Git repository in which the instructions are executed may have unique scenarios that the instructions do not provide guidance for. Every codebase is different, and the instruction set cannot anticipate every possible edge case, custom configuration. Users should be prepared to:
- Review the AI's changes carefully
- Handle scenarios not covered by the instructions
- Adapt or supplement the instructions for their specific use case


## Prerequisites and Constraints for Using the Upgrade Instructions

Before using the Java 17 to Java 21 upgrade instruction file, ensure your environment meets the following prerequisites and constraints:

### Environment Requirements

1. **Operating System**: macOS 
2. **Current Java Version**: Must be Java 17 
3. **Build Tool**: Gradle with Groovy DSL (`build.gradle`) - Maven and Gradle Kotlin DSL require adaptation
4. **Required Tools**: `curl` and Git

### Scope Limitations

1. **Java Upgrade Only**: The instructions focus **exclusively on upgrading Java from version 17 to version 21**. Spring Boot version upgrades are explicitly excluded and should be handled separately.

2. **Amazon Corretto JDK**: The instructions use **Amazon Corretto** as the Java distribution. If your organization requires a different distribution (Oracle JDK, Eclipse Temurin, Azul Zulu, etc.), the installation steps will need modification.

3. **SDKMAN for Java Management**: The instructions use **SDKMAN** to manage Java installations. Organizations using alternative tools (jenv, manual installations, package managers) will need to adapt the installation approach.

### Compatibility Note

If your environment does not match these prerequisites, you should:
- Review the instruction file and adapt it to your specific environment
- Consult with your organization's development standards and tooling requirements
- Consider creating a customized version of the instruction file for your use case

---

## Organization Trusted Certificates

If your organization uses custom trusted certificates (e.g., for internal Certificate Authorities or corporate proxies), these certificates need to be copied to certain directory before executing the Java upgrade instructions.

### Certificate Preparation Requirements

1. **Certificate Directory**: All organization trusted certificates must be placed in the directory:
   ```
   ~/trusted-certs/
   ```

2. **Supported Certificate Formats**: Only certificates with the following file extensions will be imported:
   - `.pem` files
   - `.cer` files
   - `.crt` files

   Files with other extensions will be ignored during the import process.

3. **Certificate Import Process**:
   - The upgrade instructions will automatically detect and import all valid certificates from `~/trusted-certs/`
   - Each certificate will be imported into the Java cacerts truststore with a unique alias
   - The import only occurs when Java 21 is freshly installed (not if it's already present)

5. **Optional Step**: If your organization does not use custom trusted certificates, this step can be skipped. The upgrade instructions will check for certificates and proceed accordingly.

### Example Certificate Preparation

```bash
# Create the trusted certificates directory
mkdir -p ~/trusted-certs/

# Copy your organization's certificates to this directory
# Example:
cp /path/to/your/org-root-ca.pem ~/trusted-certs/
cp /path/to/your/org-intermediate-ca.cer ~/trusted-certs/
cp /path/to/your/org-ssl-inspection.crt ~/trusted-certs/
```

Once the certificates are in place, proceed with the Java upgrade instructions as documented below.

---

## How to Execute the Java 17 to Java 21 Upgrade Instructions

This repository provides automated AI-driven instructions to upgrade Java applications from version 17 to version 21.

### Overview

The upgrade process consists of two instruction files that should be copied into any repository that needs to be upgraded from Java 17 to Java 21:

1. **Instruction File** - Contains the detailed step-by-step procedures for the upgrade
2. **LLM Prompt File** - Contains the prompt that directs an AI agent to execute the instructions

Once these files are in place, the upgrade can be executed by referencing the prompt file in an AI coding agent, which will systematically work through all the necessary steps.

### Step 1: Download the Instruction File

Download the instruction file from the following URL:
```
https://github.com/sri-chalam/spring-petclinic-ai-java-upgrade/blob/main/.github/instructions/java-17-to-21-upgrade-instructions.md
```

Save this file in your Git repository that needs to be upgraded from Java 17 to Java 21. The suggested location is:
```
<Git repo root>/.github/instructions/
```

### Step 2: Download the LLM Prompt

Download the LLM prompt file that executes the upgrade from the following URL:
```
https://github.com/sri-chalam/spring-petclinic-ai-java-upgrade/blob/main/.github/instructions/java-17-to-21-upgrade-llm-prompt.md
```

Save this file in the same location as the instruction file:
```
<Git repo root>/.github/instructions/
```

### Step 3: Execute the Upgrade with an LLM

In your LLM chat interface (such as Claude, ChatGPT, or any AI coding agent), reference the prompt file location to initiate the upgrade process:

```
Follow the instructions in @.github/instructions/java-17-to-21-upgrade-llm-prompt.md
```

The AI agent will read the prompt file, which in turn references the detailed instructions file, and execute each step of the upgrade process systematically.

### Step 4: Validate the Java 21 Upgrade

After the upgrade instructions have been executed, it's important to validate that the upgrade was successful. The instruction file includes Gradle build execution with unit tests at the end. However, additional validation steps are recommended to ensure comprehensive testing and proper IDE configuration.

#### 4.1 Execute End-to-End Tests

Run your application's end-to-end test suite to verify that the system works correctly as a whole:

Ensure that all critical user workflows and integrations are functioning correctly with Java 21.

#### 4.2 Execute Integration Tests (if available)

```bash
# Example for Gradle
./gradlew integrationTest
```

#### 4.3 Execute Manual Tests

Perform manual testing to check the application works as expected.

1. Start the application locally or in Development environment
2. Test critical user journeys
3. Verify database connectivity and operations
4. Check logging and monitoring functionality
5. Validate any Java version-specific features or optimizations
6. Verify communication to external services (AWS services or microservices)

#### 4.4 Configure IDE Settings

For IDEs such as IntelliJ IDEA, update the project settings to use the new Java 21 JDK:

**IntelliJ IDEA Configuration:**

1. **Update Gradle JDK Settings:**
   - Navigate to: `Settings/Preferences` → `Build, Execution, Deployment` → `Build Tools` → `Gradle`
   - Set "Gradle JVM" to Java 21 (e.g., `21.0.x-amzn` or your installed JDK)

2. **Update Project Structure:**
   - Navigate to: `File` → `Project Structure` → `Project`
   - Set "SDK" to Java 21
   - Set "Language level" to "21 - Record patterns, pattern matching for switch"

3. **Update Module Settings:**
   - Navigate to: `File` → `Project Structure` → `Modules`
   - For each module, verify the "Language level" is set appropriately

After making these changes, rebuild the project in your IDE to ensure everything compiles correctly with the new Java version.

