# Java 21 Upgrade Instructions

## Overview
These instructions guide an AI coding agent to upgrade a Java application from Java 17 to Java 21 using Amazon Corretto JDK.

## Environment Assumptions
- Platform: macOS (Macbook)
- Default shell: zsh
- curl is already installed
- JDK Provider: Amazon Corretto

## Important: Build Tool Scope

**ONLY upgrade Gradle-based projects.** This upgrade applies exclusively to:
- Gradle wrapper files (`gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.properties`, `gradle/wrapper/gradle-wrapper.jar`)
- `build.gradle` or `build.gradle.kts`
- `gradle.properties`
- `settings.gradle` or `settings.gradle.kts`

**DO NOT modify Maven-based projects.** Specifically, do not modify:
- Maven wrapper files (`mvnw`, `mvnw.cmd`, `.mvn/`)
- `pom.xml`
- Maven-specific configuration files

If the project uses Maven instead of Gradle, skip the project configuration updates in Step 3.

**IMPORTANT: DO NOT upgrade Spring Boot version during the Java upgrade.** Spring Boot will be upgraded in a separate task using a separate instruction file. This instruction file is focused solely on upgrading Java from version 17 to version 21.

## Guidelines for AI Agent Execution

When executing these instructions, the AI agent should:

1. **Provide Clear Feedback**: After each step, provide clear feedback about what action was taken (e.g., "Installed", "Skipped - already present", "Updated", "Verified")

2. **Echo Status Messages**: Include echo statements in scripts to inform about the current state and progress

3. **Handle Errors Gracefully**: Check for errors and provide meaningful error messages when operations fail

4. **Verify Before Proceeding**: Verify that each step completed successfully before moving to the next step

5. **Avoid Redundant Operations**: Check if a component is already installed/configured before attempting to install/configure it again

6. **Use Dynamic Values**: Prefer dynamically detecting versions and paths over hardcoded values to ensure the instructions work at the time of execution

---

## Step 1: Check and Install SDKMAN

SDKMAN (Software Development Kit Manager) is required to manage Java versions.

### 1.1 Check if SDKMAN is Already Installed

Check if the SDKMAN initialization script exists:

```bash
[ -f ~/.sdkman/bin/sdkman-init.sh ] && echo "SDKMAN is installed" || echo "SDKMAN is not installed"
```

### 1.2 Install SDKMAN (if not present)

If the file `~/.sdkman/bin/sdkman-init.sh` does not exist, install SDKMAN:

```bash
curl -s "https://get.sdkman.io" | bash
echo "SDKMAN installation complete"
```

This command downloads and executes the SDKMAN installation script, then confirms completion.

### 1.3 Initialize SDKMAN in Current Shell

After confirming SDKMAN is installed (either already present or newly installed), initialize it in the current shell session:

```bash
source ~/.sdkman/bin/sdkman-init.sh
```

**Important:** This command adds the `sdk` function to your shell environment, which is the core command for managing Java installations.

### 1.4 Verify SDKMAN Installation

Confirm SDKMAN is working correctly:

```bash
sdk version
```

This should display the SDKMAN version information.

---

## Step 2: Install Amazon Corretto Java 21

### 2.1 Check if Amazon Corretto Java 21 is Already Installed

First, check if Amazon Corretto Java 21 is already installed locally:

```zsh
if sdk list java | grep -v "local only" | grep -q "21\..*amzn"; then
    echo "Amazon Corretto Java 21 is already installed"
    JAVA21_ALREADY_INSTALLED=true
else
    echo "Amazon Corretto Java 21 is not installed"
    JAVA21_ALREADY_INSTALLED=false
fi
```

This checks if any Amazon Corretto Java 21 version is installed locally by filtering out the "local only" header and searching for installed versions.

### 2.2 Find and Install Latest Amazon Corretto Java 21 (if not present)

If Amazon Corretto Java 21 is not installed, find and install the latest version:

```zsh
if [ "$JAVA21_ALREADY_INSTALLED" = false ]; then
    # Find the latest Amazon Corretto Java 21 version
    LATEST_JAVA21=$(sdk list java | grep "21\..*amzn" | grep -v ">>>" | head -1 | awk '{print $NF}')

    if [ -z "$LATEST_JAVA21" ]; then
        echo "Error: No Amazon Corretto Java 21 versions found"
        exit 1
    fi

    echo "Installing Amazon Corretto Java 21: $LATEST_JAVA21"
    sdk install java "$LATEST_JAVA21"
    echo "Amazon Corretto Java 21 installation complete"
else
    echo "Skipping installation - Amazon Corretto Java 21 is already present"
fi
```

This script:
- Dynamically finds the latest Amazon Corretto Java 21 version available at the time of execution
- Installs it only if not already present
- Provides clear feedback about what action was taken

### 2.3 Set Java 21 as Default (Optional)

To make Java 21 the default version for all new shell sessions:

```zsh
# Get the installed Java 21 version (either newly installed or already present)
JAVA21_VERSION=$(sdk list java | grep "21\..*amzn" | head -1 | awk '{print $NF}')

if [ -n "$JAVA21_VERSION" ]; then
    sdk default java "$JAVA21_VERSION"
    echo "Set $JAVA21_VERSION as default Java version"
else
    echo "Error: Could not find Amazon Corretto Java 21 version"
fi
```

This dynamically identifies the installed Java 21 version and sets it as default.

### 2.4 Verify Java 21 Installation

Confirm Java 21 is active:

```zsh
java -version
```

Expected output should show:
```
openjdk version "21.0.x" 2024-xx-xx LTS
OpenJDK Runtime Environment Corretto-21.0.x.x.x
OpenJDK 64-Bit Server VM Corretto-21.0.x.x.x
```

---

## Step 3: Update Project Configuration

### 3.1 Set JAVA_HOME Environment Variable

Ensure JAVA_HOME points to the correct Java 21 installation:

```zsh
export JAVA_HOME=$(sdk home java 21.0.9-amzn)
echo $JAVA_HOME
```

---

## Step 4: Upgrade Gradle Wrapper (If Needed)

### 4.1 Check Current Gradle Wrapper Version

Check the Gradle version specified in `gradle/wrapper/gradle-wrapper.properties`:

```bash
grep "distributionUrl" gradle/wrapper/gradle-wrapper.properties
```

This will display the current Gradle version being used by the wrapper.

### 4.2 Verify Java 21 Compatibility

Gradle versions have specific Java compatibility requirements:
- **Gradle 8.5+**: Full support for Java 21
- **Gradle 7.6+**: Basic Java 21 compatibility
- **Gradle < 7.6**: Not compatible with Java 21

To check your current Gradle version:

```bash
./gradlew --version
```

### 4.3 Upgrade Gradle Wrapper (If Necessary)

If the current Gradle version is below 8.5, upgrade to Gradle 8.11 (recommended for Java 21):

```bash
./gradlew wrapper --gradle-version=8.11
```

This command will update the Gradle wrapper files to use version 8.11.

### 4.4 Verify Gradle Wrapper Upgrade

After upgrading, verify the new Gradle version:

```bash
./gradlew --version
```

Expected output should show Gradle 8.11 and Java 21:
```
------------------------------------------------------------
Gradle 8.11
------------------------------------------------------------

Build time:   2024-xx-xx xx:xx:xx UTC
Revision:     <revision-hash>

Kotlin:       1.9.x
Groovy:       3.0.x
Ant:          Apache Ant(TM) version 1.10.x compiled on <date>
JVM:          21.0.x (Amazon.com Inc. 21.0.x+xx-LTS)
OS:           Mac OS X 14.x.x aarch64
```

---

## Next Steps

After completing the above steps, proceed with:
1. Updating dependencies to Java 21 compatible versions
2. Running tests to identify compatibility issues
3. Updating deprecated APIs
4. Building and deploying the application

---

## References

- [SDKMAN Installation Guide](https://sdkman.io/install/)
- [Amazon Corretto via SDKMAN](https://sdkman.io/jdks/amzn/)
- [Amazon Corretto 21 Documentation](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/)
