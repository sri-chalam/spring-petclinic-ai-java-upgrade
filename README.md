# Spring Petclinic Project - Upgrade with AI Instructions

This repository demonstrates how to use an AI coding assistant to upgrade a popular Java project from Java 17 to Java 21.

## Purpose

This repository serves as a test bed for validating the AI-assisted Java upgrade instructions described in the accompanying article. The source code is a snapshot taken from the [Spring PetClinic](https://github.com/spring-projects/spring-petclinic) project, which currently uses Java 17. The primary objective is to leverage an AI coding assistant to upgrade the codebase to newer Java versions.

## Repository Structure

1. The **main** branch contains the original codebase before the upgrade (Java 17).
2. The **java-21-upgrade** branch contains the codebase after the upgrade (Java 21).
3. The [upgrade logs](https://github.com/sri-chalam/spring-petclinic-ai-java-upgrade/blob/java-21-upgrade/docs/ai-tasks/logs/java-17-to-21-upgrade-log.md) document which libraries were upgraded, the status of each step, what files were updated, what decisions were made, and other details of the upgrade process.

## How to Upgrade This Project Using AI Instructions

### Prerequisites
- Visual Studio Code (VS Code) installed
- An AI coding assistant extension installed and configured in VS Code (e.g., [GitHub Copilot](https://marketplace.visualstudio.com/items?itemName=GitHub.copilot) or [Claude Code](https://marketplace.visualstudio.com/items?itemName=anthropic.claude-code))
- (Optional) **Organization Trusted Certificates**: If your organization uses custom certificates (e.g., internal CAs, TLS-inspecting proxies), prepare them before the upgrade:
  - Copy certificates to `~/trusted-certs/`
  - Supported formats: `.pem`, `.cer`, `.crt`
  - If the AI installs Java 21, it will automatically import these certificates into the keystore. If Java 21 is already installed, certificates are assumed to be pre-configured.
  - *Note: Required for OpenRewrite recipes to avoid PKIX SSL errors when downloading Gradle artifacts.*

### Steps
1. Clone this Git repository and open it in Visual Studio Code (VS Code).
2. Open the AI chat window of your choice (e.g., Github Copilot, Claude Code).
3. Download the AI instructions file from:
   [Java 17 to 21 Upgrade Instructions](https://github.com/sri-chalam/ai-tech-notes/blob/main/articles/ai-assisted-java-upgrade/java-17-to-21/instructions/java-17-to-21-upgrade-instructions.md)
4. Create the directory `docs/ai-tasks/instructions/` and save the downloaded file as `java-17-to-21-upgrade-instructions.md` in that directory.
5. Open the downloaded file in VS Code.
6. Execute the following prompt in the AI chat window:
```
Upgrade the application from Java 17 to Java 21 using the instructions in docs/ai-tasks/instructions/java-17-to-21-upgrade-instructions.md.
If you cannot access this file, stop and ask me to provide its contents.
Do not infer or invent upgrade steps.
```

**After executing the prompt:**
7. Monitor the messages in the chat window as the AI processes the instructions.
8. Observe the commands being executed in the terminal.
9. Review the logs generated in `/docs/ai-tasks/logs/java-17-to-21-upgrade-log.md`.

## Related Resources

- **Article**: [AI-Assisted Java Upgrade](https://github.com/sri-chalam/ai-tech-notes/blob/main/articles/ai-assisted-java-upgrade/ai-assisted-java-upgrade.md) - A comprehensive guide on using AI to upgrade Java projects.
- **AI Instructions File**: [Java 17 to 21 Upgrade Instructions](https://github.com/sri-chalam/ai-tech-notes/blob/main/articles/ai-assisted-java-upgrade/java-17-to-21/instructions/java-17-to-21-upgrade-instructions.md) - Step-by-step instructions for an AI assistant to perform the upgrade.
- **Original Project README**: [README-ORIGINAL.md](./README-ORIGINAL.md) - The original Spring PetClinic documentation.
