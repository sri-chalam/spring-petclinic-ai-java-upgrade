 
# spring-petclinic-ai-java-upgrade

## Purpose

This repository is a **work in progress** focused on exploring the use of AI coding agents for Java version upgrades.

The source code in this repository is a snapshot taken from the [Spring PetClinic](https://github.com/spring-projects/spring-petclinic) project, which currently uses Java 17. The primary objective of this exploration is to leverage AI coding agents to upgrade the codebase to Java 21 and Java 25.

The outcome of this exploration will be a set of AI-generated instructions and best practices for automated Java version upgrades.

## 1.0. Table of Contents
<!-- TODO Add Table of Contents at the end. -->


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

## Why Custom Instructions Instead of Automated Tools?

The AI assistants such as GitHub Copilot app modernization and Amazon Q Developer offer features to upgrade Java, Spring Boot version. However, having a custom instruction file to upgrade have multiple advantages.

### Benefits of Custom Instructions File

**1. LLM-Agnostic Flexibility**
- Works with any LLM (Claude, ChatGPT, Gemini, etc.), not locked to a specific vendor
- Lower cost - no additional subscriptions beyond your chosen LLM

**2. Addresses What Automated Tools Don't Handle**
- GitHub Copilot App Modernization and Amazon Q Developer use OpenRewrite underneath for code transformations
- However, they don't address environment-specific requirements:
  - JDK distribution selection (Amazon Corretto vs others)
  - Installation methods (SDKMAN vs package managers)
  - Certificate management (importing trusted certificates)

**3. Includes the Same Build/Fix Loop as Copilot**
- Custom instructions can direct the LLM to perform iterative build/fix cycles
- Same capability as GitHub Copilot's automated loop, but transparent and customizable

**3. OpenRewrite Has Known Limitations**
- Not exhaustive - recipes handle subsets of migration patterns to avoid unintended side effects
- Missing recipes for many third-party library migrations
- Even GitHub Copilot uses an AI "build/fix loop" to handle what OpenRewrite can't automate

**4. Misc.**
- Serves as documentation
- Can embed organization-specific requirements and coding standards
- What ever the tool used, some manual intervention is required for edge cases and unsupported libraries

### CVE Vulnerability Scanning - A Separate Concern

While GitHub Copilot App Modernization includes CVE vulnerability scanning as part of the upgrade workflow, security scanning should be treated as a separate, independent concern.

**Rationale:**
- CVE vulnerabilities should be checked and fixed **frequently** (weekly or before each release), not just during major upgrades
- Security scanning is applicable to **all projects**, regardless of whether they're being upgraded
- Keeping CVE scanning separate allows it to be **reused independently** across different workflows

For this reason, comprehensive CVE scanning and remediation instructions are provided in a separate instruction file (see References section below).

### What About Third-Party Library Migrations?

Both OpenRewrite and GitHub Copilot App Modernization have the **same limitation** for third-party library migrations:

**The Core Issue:**
- OpenRewrite can only migrate libraries that have **predefined recipes**
- GitHub Copilot App Modernization uses OpenRewrite underneath, so it inherits this limitation
- For libraries without recipes (e.g., Ehcache2→Ehcache3, proprietary frameworks), **neither tool can automatically migrate them**

**What Copilot Adds:**
- After OpenRewrite runs, Copilot's AI can fix **compilation errors** and **build issues** through its iterative build/fix loop
- It can **scan for CVE vulnerabilities** and update dependency versions
- It can **learn from your manual migrations** and apply patterns to other codebases

**Bottom Line:**
- For standard Java API upgrades: Both tools work well
- For third-party library migrations without recipes: **Manual migration is still required**, whether you use OpenRewrite, Copilot, or custom instructions
- Custom instructions can explicitly guide the LLM on how to migrate specific libraries, providing the same (or better) control as Copilot's custom pattern feature


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

### Gradle Wrapper Version Compatibility

For Java 21 compatibility, your Gradle version must meet these requirements:
- **Gradle 8.5+**: Full support for Java 21 (recommended)

The upgrade instructions will automatically upgrade Gradle to version 8.11 if your current version is below 8.5.

**Note:** The instructions will only upgrade Gradle if the current wrapper version is below 8.5. If your project already uses Gradle 8.5 or higher (including 9.x), the Gradle wrapper will not be modified. If your project does not contain a Gradle wrapper, one will not be installed.

### Compatibility Note

If your environment does not match these prerequisites, you should:
- Review the instruction file and adapt it to your specific environment
- Consult with your organization's development standards and tooling requirements
- Consider creating a customized version of the instruction file for your use case

---

## What the Upgrade Instructions Will NOT Do

It's important to understand the scope boundaries of the upgrade instructions. The following modifications will **NOT** be performed:

### 1. Maven Project Modifications
**The instructions will NOT modify Maven-based projects.** The upgrade is designed exclusively for Gradle projects with Groovy DSL. If your project uses Maven, you must adapt the instructions for Maven (modify `pom.xml` instead of `build.gradle`)

### 2. Spring Boot Upgrade
**The instructions will NOT upgrade Spring Boot version.** The focus is exclusively on upgrading Java from version 17 to version 21. To reduce complexity, Spring Boot upgrades are not part of this instruction file. Additionally, keeping Java upgrade and Spring Boot upgrade in separate instruction files allows them to be used independently.

---

## Updated CI/CD and Build Files

The upgrade instructions automatically update Java version references in the following files:

- **GitHub Actions**: `.github/workflows/*.yml`
- **AWS CodeBuild**: `buildspec*.yml`
- **Docker**: `Dockerfile`, `Dockerfile.*`
- **Gradle Build Files**: `build.gradle`, `gradle.properties`, `gradle/wrapper/gradle-wrapper.properties`

**Updates applied to build.gradle:**
- sourceCompatibility and targetCompatibility (`'17'` → `'21'`)
- JavaVersion enum references (`JavaVersion.VERSION_17` → `JavaVersion.VERSION_21`)
- OpenRewrite plugin addition/update (version 6.30.3+)
- OpenRewrite dependencies and recipe configuration
- Gradle wrapper upgrade to 8.11 (if current version < 8.5)

---

## How the Upgrade Instructions Work

The upgrade process is automated through a series of steps that handle both environment setup and code migration:

### Core Components

1. **OpenRewrite Plugin**: The code migration is performed by the OpenRewrite plugin, the same underlying tool used by AI-powered upgrade assistants like GitHub Copilot app modernization and Amazon Q Developer

2. **SDKMAN Installation**: Installs SDKMAN if not already present at `~/.sdkman/` for Java version management

3. **Java 21 Installation**: Installs Amazon Corretto JDK 21 via SDKMAN if not already present in `~/.sdkman/candidates/java/`
   - If JDK 21 exists in other locations, the instructions still install it via SDKMAN for consistency
   - Installation is skipped only if Amazon Corretto 21 is already installed through SDKMAN

4. **Trusted Certificates Import**: Automatically imports organization certificates from `~/trusted-certs/` into the Java 21 truststore during fresh installations

5. **Project Configuration Updates**: Updates Gradle wrapper, build files, Dockerfiles, CI/CD workflows, and other configuration files to use Java 21

6. **Code Migration**: Executes OpenRewrite recipes to automatically refactor code for Java 21 compatibility

7. **Java 17 Prerequisite Check**: Validates the project uses Java 17 before proceeding (see Prerequisites section)

8. **Gradle Compatibility Check**: Upgrades Gradle wrapper to 8.11 if needed (see Prerequisites for details).

9. **CI/CD Pipeline Updates**: Updates CI/CD configuration files (see "Updated CI/CD and Build Files" section for details)

10. **Iterative Build/Fix Loop**: After OpenRewrite migration, performs an automated build/fix cycle to resolve any remaining compilation errors and test failures that OpenRewrite couldn't handle automatically. This loop:
   - Executes `./gradlew clean build` to compile code and run tests
   - Analyzes compilation errors and identifies root causes
   - Fixes common issues
   - Re-runs the build after each fix

---

## Organization Trusted Certificates

If your organization uses custom trusted certificates (e.g., for internal Certificate Authorities or corporate proxies), these certificates need to be copied to certain directory before executing the Java upgrade instructions.

### Certificate Preparation Requirements

1. **Certificate Directory**: All organization trusted certificates need to be copied to `~/trusted-certs/`

2. **Supported Certificate Formats**: Only certificates with `.pem`, `.cer`, or `.crt` file extensions will be imported. Files with other extensions will be ignored during the import process.

3. **Certificate Import Process**:
   - The upgrade instructions will automatically detect and import all valid certificates from `~/trusted-certs/`
   - Each certificate will be imported into the Java cacerts truststore with a unique alias
   - The import only occurs when Java 21 is freshly installed (not if it's already present)

4. **Optional Step**: If your organization does not use custom trusted certificates, this step can be skipped. The upgrade continues normally if no certificates are found.

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

---

## Post-Upgrade Validation and Configuration

### Step 1: Validate the Java 21 Upgrade

After the upgrade instructions have been executed, it's important to validate that the upgrade was successful. The instruction file includes Gradle build execution with unit tests at the end. However, additional validation steps are recommended to ensure comprehensive testing and proper IDE configuration.

#### 1.1 Execute End-to-End Tests

Run your application's end-to-end test suite to verify that the system works correctly as a whole:

Ensure that all critical user workflows and integrations are functioning correctly with Java 21.

#### 1.2 Execute Integration Tests (if available)

```zsh
# Example for Gradle
./gradlew integrationTest
```

#### 1.3 Execute Manual Tests

Perform manual testing to check the application works as expected.

1. Start the application locally or in Development environment
2. Test critical user journeys
3. Verify database connectivity and operations
4. Check logging and monitoring functionality
5. Validate any Java version-specific features or optimizations
6. Verify communication to external services (AWS services or microservices)

### Step 2: Configure IDE Settings

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

### Step 3: Upgrading to Java 25

**For Java 21 to Java 25 Upgrades:**

To upgrade an application from Java 21 to Java 25, use a separate instruction file specifically designed for this upgrade path.

**For Java 17 to Java 25 Upgrades:**

If you need to upgrade an application from Java 17 to Java 25, use a two-step process:

1. **Step 1**: Upgrade from Java 17 to Java 21
2. **Step 2**: Upgrade from Java 21 to Java 25

For each of these upgrade paths, there are two separate instruction files provided in this repository:
- Java 17 to Java 21 upgrade instructions
- Java 21 to Java 25 upgrade instructions

**Why Separate Instruction Files?**

Using individual instruction files for each upgrade path (17→21, 21→25) is more modular and reduces complexity. If a single instruction file attempted to handle multiple upgrade paths (17→21 or 21→25 or 17→25), the instructions would become complex and could confuse or cause hallucination in LLMs. Separate, focused instruction files ensure clearer execution and more predictable results.

---

## References

- [GitHub Awesome Copilot - Instructions](https://github.com/github/awesome-copilot/tree/main/instructions) - A community-contributed collection of instruction files for GitHub Copilot and AI coding agents

