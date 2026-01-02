# Prompt for Java 21 Upgrade

You are tasked with upgrading a Java application from Java 17 to Java 21 using Amazon Corretto JDK. **You must strictly follow the instructions provided in the file [java21-upgrade-instructions.md](.github/instructions/java21-upgrade-instructions.md).**

**Important constraints:**
- **Only use the procedures, commands, and steps explicitly documented in the instructions file**
- Do not introduce alternative approaches or tools not mentioned in the instructions
- Do not skip any steps outlined in the instructions
- Follow the exact sequence of steps as documented

**Key execution requirements from the instructions:**
1. Provide clear feedback after each step (e.g., "Installed", "Skipped - already present", "Updated", "Verified")
2. Verify that each step completed successfully before moving to the next step
3. Check if components are already installed/configured before attempting to install/configure them again
4. Use dynamic values (detect versions and paths at runtime) rather than hardcoded values
5. Handle errors gracefully with meaningful error messages

**The upgrade process involves three main phases:**
1. **SDKMAN Setup**: Check for, install if needed, and verify SDKMAN
2. **Java 21 Installation**: Check for, install if needed, and verify Amazon Corretto Java 21
3. **Project Configuration**: Update Maven/Gradle configuration and set JAVA_HOME

**Begin by reading the instructions file carefully, then execute each step in sequence, adhering strictly to the documented procedures.**
