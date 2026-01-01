# Java 21 Upgrade Instructions

## Overview
These instructions guide an AI coding agent to upgrade a Java application from Java 17 to Java 21 using Amazon Corretto JDK.

## Environment Assumptions
- Platform: macOS (Macbook)
- Default shell: zsh
- curl is already installed
- JDK Provider: Amazon Corretto

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

### 3.1 Update Maven/Gradle Configuration

**For Maven projects**, update `pom.xml`:

```xml
<properties>
    <java.version>21</java.version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
</properties>
```

**For Gradle projects**, update `build.gradle`:

```gradle
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
```

### 3.2 Set JAVA_HOME Environment Variable

Ensure JAVA_HOME points to the correct Java 21 installation:

```zsh
export JAVA_HOME=$(sdk home java 21.0.9-amzn)
echo $JAVA_HOME
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
