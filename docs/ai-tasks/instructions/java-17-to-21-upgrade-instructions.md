# Java 21 Upgrade Instructions

## Overview
These instructions guide an AI coding agent to upgrade a Java application from Java 17 to Java 21 using Amazon Corretto JDK.

## Environment Assumptions
- Platform: macOS (Macbook)
- Default shell: zsh
- curl is already installed
- JDK Provider: Amazon Corretto
- Build tool: Gradle with Groovy DSL (build.gradle) or Kotlin DSL (build.gradle.kts)
  - **Note:** Instructions are specific to Gradle. For Maven-based projects, this file requires adaptation.

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

If the project uses Maven instead of Gradle, skip all Gradle-specific steps (Steps 5 and 6).

**IMPORTANT: DO NOT upgrade Spring Boot version during the Java upgrade.** Spring Boot will be upgraded in a separate task using a separate instruction file. This instruction file is focused solely on upgrading Java from version 17 to version 21.

## Guidelines for AI Agent Execution

When executing these instructions, the AI agent should:

1. **Provide Clear Feedback**: After each step, provide clear feedback about what action was taken (e.g., "Installed", "Skipped - already present", "Updated", "Verified")

2. **Echo Status Messages**: Include echo statements in scripts to inform about the current state and progress

3. **Handle Errors Gracefully**: Check for errors and provide meaningful error messages when operations fail

4. **Verify Before Proceeding**: Verify that each step completed successfully before moving to the next step

5. **Avoid Redundant Operations**: Check if a component is already installed/configured before attempting to install/configure it again

6. **Use Dynamic Values**: Prefer dynamically detecting versions and paths over hardcoded values to ensure the instructions work at the time of execution

7. **Document All Changes**: Maintain an upgrade log file at `/docs/ai-tasks/logs/java-21-upgrade-log.md` to track all fixes applied and unresolved errors (see Section 6.7.1 for detailed logging requirements)

---

## Upgrade Steps

### Step 1: Verify Current Java Version (Prerequisite Check)

**IMPORTANT: These instructions are designed to upgrade Java 17 applications to Java 21 only.** Before proceeding, verify that the application is currently using Java 17.

### 1.1 Check Java Version in Project Configuration

<!--
Important Note: Java version can be configured in Gradle using many different patterns. Variables may be resolved through functions, project properties (e.g., "project.javaVersion"), or external property references. The script below handles common patterns but may not cover all edge cases.

If the script cannot detect the Java version due to edge cases, there are two options:
1. Manually set a hard-coded Java version in the Gradle configuration files
2. Skip the Java version check step entirely

Skipping the version check is safe because the OpenRewrite plugin will handle the upgrade intelligently:
- If the Java version is already 21, the Rewrite recipe will not make any changes
- If the Java version cannot be identified, the remaining upgrade instructions will still execute successfully
-->

Check the Java version specified in the project's Gradle configuration files:

```zsh
echo "Checking current Java version in project configuration..."
echo ""

# Function to resolve variable value from gradle.properties or build files
resolve_variable() {
    local var_name=$1
    local value=""

    # First check gradle.properties
    if [ -f "gradle.properties" ]; then
        value=$(grep -E "^${var_name}\s*=" gradle.properties | sed -E "s/^${var_name}\s*=\s*//" | tr -d ' "' | head -1)
    fi

    # If not found in gradle.properties, check build.gradle files
    if [ -z "$value" ] && [ -n "$BUILD_FILES" ]; then
        while IFS= read -r build_file; do
            value=$(grep -E "^\s*${var_name}\s*=" "$build_file" | sed -E "s/.*=\s*//" | tr -d ' "' | head -1)
            if [ -n "$value" ]; then
                break
            fi
        done <<< "$BUILD_FILES"
    fi

    echo "$value"
}

# Find all build.gradle and build.gradle.kts files
BUILD_FILES=$(find . -type f \( -name "build.gradle" -o -name "build.gradle.kts" \) 2>/dev/null)

JAVA_VERSION_BUILD_GRADLE=""

if [ -n "$BUILD_FILES" ]; then
    echo "Found build files:"
    echo "$BUILD_FILES"
    echo ""

    # Check each build file for Java version
    while IFS= read -r build_file; do
        echo "Checking $build_file..."

        # Check for sourceCompatibility/targetCompatibility with direct value
        VERSION_COMPAT=$(grep -E "sourceCompatibility|targetCompatibility" "$build_file" | grep -o "1[0-9]\+\|[0-9]\+" | head -1)

        # Check if sourceCompatibility/targetCompatibility references a variable
        if [ -z "$VERSION_COMPAT" ]; then
            VAR_REF=$(grep -E "sourceCompatibility|targetCompatibility" "$build_file" | sed -E 's/.*=\s*//;s/["\047]//g' | tr -d ' ' | head -1)
            if [ -n "$VAR_REF" ] && [[ ! "$VAR_REF" =~ ^[0-9]+$ ]]; then
                # It's a variable reference, try to resolve it
                RESOLVED=$(resolve_variable "$VAR_REF")
                if [[ "$RESOLVED" =~ ^[0-9]+$ ]]; then
                    VERSION_COMPAT="$RESOLVED"
                    echo "Resolved variable $VAR_REF to: $VERSION_COMPAT"
                fi
            fi
        fi

        # Check for JavaVersion.VERSION_XX enum syntax
        VERSION_ENUM=$(grep -E "JavaVersion\.VERSION_" "$build_file" | grep -oE "VERSION_[0-9]+" | grep -o "[0-9]\+" | head -1)

        # Check for Java toolchain syntax: languageVersion = JavaLanguageVersion.of(17)
        VERSION_TOOLCHAIN=$(grep -E "languageVersion\s*=\s*JavaLanguageVersion\.of\(" "$build_file" | grep -o "[0-9]\+" | head -1)

        # Check if toolchain uses a variable reference
        if [ -z "$VERSION_TOOLCHAIN" ]; then
            VAR_REF=$(grep -E "languageVersion\s*=\s*JavaLanguageVersion\.of\(" "$build_file" | sed -E 's/.*JavaLanguageVersion\.of\(//;s/\).*//;s/["\047]//g' | tr -d ' ' | head -1)
            if [ -n "$VAR_REF" ] && [[ ! "$VAR_REF" =~ ^[0-9]+$ ]] && [[ ! "$VAR_REF" =~ JavaVersion\.VERSION ]]; then
                # It's a variable reference, try to resolve it
                RESOLVED=$(resolve_variable "$VAR_REF")
                if [[ "$RESOLVED" =~ ^[0-9]+$ ]]; then
                    VERSION_TOOLCHAIN="$RESOLVED"
                    echo "Resolved variable $VAR_REF to: $VERSION_TOOLCHAIN"
                fi
            fi
        fi

        # Check for Java toolchain with enum syntax: languageVersion = JavaLanguageVersion.of(JavaVersion.VERSION_17.majorVersion)
        VERSION_TOOLCHAIN_ENUM=$(grep -E "languageVersion\s*=\s*JavaLanguageVersion\.of\(JavaVersion\.VERSION_" "$build_file" | grep -oE "VERSION_[0-9]+" | grep -o "[0-9]\+" | head -1)

        # Use whichever version was found (prefer toolchain > toolchain enum > enum > compat)
        FILE_VERSION=${VERSION_TOOLCHAIN:-${VERSION_TOOLCHAIN_ENUM:-${VERSION_ENUM:-$VERSION_COMPAT}}}

        if [ -n "$FILE_VERSION" ]; then
            echo "Found Java version in $build_file: $FILE_VERSION"

            # Store the first version found if we don't have one yet
            if [ -z "$JAVA_VERSION_BUILD_GRADLE" ]; then
                JAVA_VERSION_BUILD_GRADLE="$FILE_VERSION"
            fi
        fi
    done <<< "$BUILD_FILES"
fi

# Check gradle.properties for Java version
if [ -f "gradle.properties" ]; then
    echo ""
    echo "Checking gradle.properties..."
    JAVA_VERSION_GRADLE_PROPS=$(grep -E "javaVersion|java\.version|sourceCompatibility|targetCompatibility" gradle.properties | grep -o "1[0-9]\+\|[0-9]\+" | head -1)

    if [ -n "$JAVA_VERSION_GRADLE_PROPS" ]; then
        echo "Found Java version in gradle.properties: $JAVA_VERSION_GRADLE_PROPS"
    fi
fi

# Determine the current Java version
CURRENT_JAVA_VERSION=${JAVA_VERSION_BUILD_GRADLE:-$JAVA_VERSION_GRADLE_PROPS}

echo ""
echo "Current Java version detected: ${CURRENT_JAVA_VERSION:-Not found}"
echo ""
```

### 1.2 Validate Java 17 Requirement

Verify that the detected Java version is 17:

```zsh
if [ -z "$CURRENT_JAVA_VERSION" ]; then
    echo "=========================================="
    echo "WARNING: Java Version Not Detected"
    echo "=========================================="
    echo ""
    echo "The Java version could not be identified in the Gradle configuration."
    echo "This may be due to:"
    echo "  - Complex variable resolution (functions, project properties)"
    echo "  - Non-standard Gradle configuration patterns"
    echo "  - Custom build script logic"
    echo ""
    echo "CONTINUING: The OpenRewrite plugin will handle the upgrade intelligently."
    echo "  - If Java is already at version 21, no changes will be made"
    echo "  - If Java is at version 17, it will be upgraded to version 21"
    echo ""
    echo "Proceeding with remaining upgrade steps..."
    echo ""
elif [ "$CURRENT_JAVA_VERSION" != "17" ]; then
    echo "=========================================="
    echo "WARNING: Java Version Mismatch"
    echo "=========================================="
    echo ""
    echo "These instructions are designed to upgrade Java 17 applications to Java 21."
    echo "Current Java version detected: $CURRENT_JAVA_VERSION"
    echo ""
    if [ "$CURRENT_JAVA_VERSION" = "21" ]; then
        echo "NOTE: Java 21 is already configured. The upgrade may not be necessary."
        echo "However, the OpenRewrite plugin will verify and apply any missing updates."
        echo ""
        echo "Proceeding with remaining steps..."
        echo ""
    else
        echo "CAUTION: The detected Java version is neither 17 nor 21."
        echo "  - If this version is incorrect, the upgrade will still proceed"
        echo "  - The OpenRewrite plugin will upgrade to Java 21 regardless"
        echo ""
        echo "Proceeding with remaining upgrade steps..."
        echo ""
    fi
else
    echo "✓ Java 17 detected - proceeding with upgrade to Java 21"
    echo ""
fi
```

This script:
- Searches all `build.gradle` and `build.gradle.kts` files for Java version patterns
- Handles multiple declaration styles: sourceCompatibility, targetCompatibility, JavaVersion enums, and toolchain configurations
- Resolves variable references from `gradle.properties` and build files
- Searches `gradle.properties` for Java version properties
- Extracts the Java version number
- Validates the detected version and provides appropriate warnings

**The script will CONTINUE execution in all cases:**
- If Java 17 is detected: Proceeds with the upgrade as expected
- If Java 21 is detected: Continues (OpenRewrite will skip unnecessary changes)
- If the version cannot be identified: Continues with a warning (OpenRewrite will handle the upgrade intelligently)
- If a different version is detected: Continues with a caution message (OpenRewrite will upgrade to Java 21)

---

### Step 2: Check and Install SDKMAN

SDKMAN (Software Development Kit Manager) is used to install and manage Java versions.

### 2.1 Check if SDKMAN is Already Installed

Check if the SDKMAN initialization script exists:

```bash
[ -f ~/.sdkman/bin/sdkman-init.sh ] && echo "SDKMAN is installed" || echo "SDKMAN is not installed"
```

### 2.2 Install SDKMAN (if not present)

If the file `~/.sdkman/bin/sdkman-init.sh` does not exist, install SDKMAN:

```bash
curl -s "https://get.sdkman.io" | bash
echo "SDKMAN installation complete"
```

This command downloads and executes the SDKMAN installation script, then confirms completion.

### 2.3 Initialize SDKMAN in Current Shell

After confirming SDKMAN is installed (either already present or newly installed), initialize it in the current shell session:

```bash
source ~/.sdkman/bin/sdkman-init.sh
```

**Important:** This command adds the `sdk` function to your shell environment, which is the core command for managing Java installations.

### 2.4 Verify SDKMAN Installation

Confirm SDKMAN is working correctly:

```bash
sdk version
```

This should display the SDKMAN version information.

---

### Step 3: Install Amazon Corretto Java 21

### 3.1 Check if Amazon Corretto Java 21 is Already Installed

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

### 3.2 Prepare Organization Trusted Certificates (if applicable)

**Before installing Java**, if your organization uses custom trusted certificates (e.g., for internal Certificate Authorities, SSL inspection, or corporate proxies), prepare them for import.

First, create the directory for trusted certificates:

```bash
mkdir -p ~/trusted-certs
```

Then, prompt the developer to prepare certificates:

```zsh
echo ""
echo "=========================================="
echo "Organization Trusted Certificates Setup"
echo "=========================================="
echo ""
echo "If your organization uses custom trusted certificates, please copy them to:"
echo "  ~/trusted-certs/"
echo ""
echo "Supported certificate formats: .pem, .cer, .crt"
echo "Certificates with other extensions will be ignored during import."
echo ""
echo "If you don't have organization-specific certificates, you can skip this step."
echo ""
read "cert_response?Have you copied all organization trusted certificates to ~/trusted-certs? (y/n): "
echo ""

if [[ "$cert_response" =~ ^[Yy]$ ]]; then
    echo "✓ Proceeding with certificate preparation acknowledged"
else
    echo "ℹ No certificates prepared - will check directory anyway"
fi

echo "Certificate preparation step complete"
echo ""
```

**Note:** The import process in section 2.4 will automatically detect and import any certificates present in `~/trusted-certs`, regardless of the response to this prompt.

### 3.3 Find and Install Latest Amazon Corretto Java 21 (if not present)

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

### 3.4 Import Organization Trusted Certificates into Java 21 (if applicable)

**Only if Amazon Corretto Java 21 was freshly installed in section 3.3**, import any organization trusted certificates into the Java keystore:

```zsh
# Only proceed if Java 21 was installed (not if installation was skipped)
if [ "$JAVA21_ALREADY_INSTALLED" = false ]; then
    # Get the installed Java 21 version and set JAVA_HOME
    JAVA21_VERSION=$(sdk list java | grep "21\..*amzn" | head -1 | awk '{print $NF}')

    if [ -n "$JAVA21_VERSION" ]; then
        export JAVA_HOME=$(sdk home java "$JAVA21_VERSION")
        echo "JAVA_HOME set to: $JAVA_HOME"

        # Check if trusted certs directory exists and contains certificates
        CERT_DIR="$HOME/trusted-certs"

        if [ -d "$CERT_DIR" ]; then
            # Count certificates with supported extensions
            CERT_COUNT=$(find "$CERT_DIR" -type f \( -name "*.pem" -o -name "*.cer" -o -name "*.crt" \) 2>/dev/null | wc -l | tr -d ' ')

            if [ "$CERT_COUNT" -gt 0 ]; then
                echo "Found $CERT_COUNT certificate(s) to import from ~/trusted-certs"

                # Get current date for alias suffix (YYYYMMDD format)
                CURRENT_DATE=$(date +%Y%m%d)

                # Import each certificate
                find "$CERT_DIR" -type f \( -name "*.pem" -o -name "*.cer" -o -name "*.crt" \) | while read -r cert_file; do
                    # Extract certificate filename without extension
                    cert_basename=$(basename "$cert_file")
                    cert_name="${cert_basename%.*}"

                    # Create alias with cert name and current date
                    alias_name="${cert_name}-${CURRENT_DATE}"

                    echo "Importing certificate: $cert_basename as alias: $alias_name"

                    # Import certificate into Java cacerts truststore
                    "$JAVA_HOME/bin/keytool" -import -alias "$alias_name" \
                        -cacerts -trustcacerts \
                        -file "$cert_file" \
                        -storepass changeit \
                        -noprompt

                    if [ $? -eq 0 ]; then
                        echo "✓ Successfully imported: $cert_basename"
                    else
                        echo "✗ Failed to import: $cert_basename"
                    fi
                done

                echo "Certificate import process complete"

                # List imported certificates for verification
                echo ""
                echo "Verifying imported certificates in Java truststore:"
                "$JAVA_HOME/bin/keytool" -list -cacerts -storepass changeit | grep -i "$CURRENT_DATE"

            else
                echo "No certificates found in ~/trusted-certs (looking for .pem, .cer, or .crt files)"
                echo "Skipping certificate import"
            fi
        else
            echo "Directory ~/trusted-certs does not exist"
            echo "Skipping certificate import"
        fi
    else
        echo "Error: Could not determine Java 21 version for certificate import"
    fi
else
    echo "Skipping certificate import - Java 21 was already installed (certificates may have been imported previously)"
fi
```

This script:
- Only attempts to import certificates if Java 21 was freshly installed (not if it was already present)
- Checks if the `~/trusted-certs` directory exists
- Only processes files with `.pem`, `.cer`, or `.crt` extensions
- Creates a unique alias for each certificate using the certificate name and current date
- Imports each certificate into the Java cacerts truststore with the default password `changeit`
- Uses `-noprompt` to avoid interactive confirmation
- Displays import success/failure for each certificate
- Lists the imported certificates for verification using the current date in the alias

**Important Notes:**
- If Java 21 was already installed before running these instructions, certificate import is skipped (assuming certificates were imported during the initial installation)
- The default cacerts password is `changeit` (this is the standard Java default)
- If you need to re-import certificates into an existing Java installation, you can manually run the import commands or temporarily set `JAVA21_ALREADY_INSTALLED=false`

### 3.5 Set Java 21 as Default (Optional)

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

### 3.6 Verify Java 21 Installation

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

### Step 4: Update Project Configuration

### 4.1 Set JAVA_HOME Environment Variable

Ensure JAVA_HOME points to the correct Java 21 installation:

```zsh
export JAVA_HOME=$(sdk home java 21.0.9-amzn)
echo $JAVA_HOME
```

---

### Step 5: Upgrade Gradle Wrapper (If Needed)

### 5.1 Check if Gradle Wrapper Exists

First, verify that the project uses Gradle wrapper:

```bash
if [ ! -f "gradlew" ] || [ ! -f "gradle/wrapper/gradle-wrapper.properties" ]; then
    echo "=========================================="
    echo "Gradle wrapper not found"
    echo "=========================================="
    echo ""
    echo "This project does not use Gradle wrapper."
    echo "Skipping Gradle wrapper upgrade (Step 4)."
    echo ""
    GRADLE_WRAPPER_EXISTS=false
else
    echo "✓ Gradle wrapper found - proceeding with version check"
    echo ""
    GRADLE_WRAPPER_EXISTS=true
fi
```

**If Gradle wrapper is not found, skip the remaining subsections of Step 5 and proceed to Step 6.**

### 5.2 Check Current Gradle Wrapper Version

**Only proceed if `GRADLE_WRAPPER_EXISTS=true`.**

Check the Gradle version specified in `gradle/wrapper/gradle-wrapper.properties`:

```zsh
grep "distributionUrl" gradle/wrapper/gradle-wrapper.properties
```

This will display the current Gradle version being used by the wrapper.

### 5.3 Verify Java 21 Compatibility

**Only proceed if `GRADLE_WRAPPER_EXISTS=true`.**

Gradle versions have specific Java compatibility requirements:
- **Gradle 8.5+**: Full support for Java 21

To check your current Gradle version:

```zsh
./gradlew --version
```

### 5.4 Upgrade Gradle Wrapper (If Necessary)

**Only proceed if `GRADLE_WRAPPER_EXISTS=true`.**

If the current Gradle version is below 8.5, upgrade to Gradle 8.11 (recommended for Java 21):

```zsh
./gradlew wrapper --gradle-version=8.11
```

This command will update the Gradle wrapper files to use version 8.11.

### 5.5 Verify Gradle Wrapper Upgrade

**Only proceed if `GRADLE_WRAPPER_EXISTS=true`.**

After upgrading, verify the new Gradle version:

```zsh
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

### Step 6: Use OpenRewrite to Migrate Java Code

OpenRewrite is an automated refactoring tool that can help migrate Java code from Java 17 to Java 21.

### 6.1 Check if OpenRewrite Plugin is Present

First, check if the OpenRewrite plugin is already configured in [build.gradle](build.gradle):

```zsh
grep -q "org.openrewrite.rewrite" build.gradle && echo "OpenRewrite plugin found" || echo "OpenRewrite plugin not found"
```

### 6.2 Add OpenRewrite Plugin (if not present or upgrade needed)

If the OpenRewrite plugin is not present, or if a newer version is required for Java 17 to 21 migration, add or update it in the `plugins` section of [build.gradle](build.gradle):

```groovy
plugins {
  // ... existing plugins ...
  id("org.openrewrite.rewrite") version "latest.release"
}
```

**Note:** Version 6.30.3 or later is recommended for Java 21 migration. If an older version is present, update it to the latest version.

### 6.3 Add Rewrite Dependencies (if not present)

Check if the OpenRewrite dependencies are already present in your build file ([build.gradle](build.gradle) or [build.gradle.kts](build.gradle.kts)). If they are not present, add them to the `dependencies` section:

```groovy
dependencies {
    // ... existing dependencies ...

    // Import the BOM to manage versions automatically
    rewrite(platform("org.openrewrite.recipe:rewrite-recipe-bom:latest.release"))

    // Add the specific migration artifact without a version
    rewrite("org.openrewrite.recipe:rewrite-migrate-java")
}
```

The `rewrite-migrate-java` recipe provides automated refactoring rules for Java version migrations.

**Notes:**
- If these dependencies already exist in the build file, skip this step and proceed to the next section.
- For multi-module projects, these dependencies are typically added to the root `build.gradle` or `build.gradle.kts` file. However, if subprojects have their own build files in subdirectories, you may need to add these dependencies to the appropriate subproject build files as well.
- In some project structures, the root-level `build.gradle` or `build.gradle.kts` file may not contain plugins or dependencies. Instead, these are defined in submodule build files (e.g., in subdirectories like `app/build.gradle` or `service/build.gradle.kts`). In such cases, add the OpenRewrite plugin and dependencies to the build file(s) that already contain plugins and dependencies.

### 6.4 Run Rewrite Migration

Execute the OpenRewrite migration to automatically refactor code for Java 21 compatibility. Pass the recipes directly as command line parameters:

```zsh
./gradlew rewriteRun \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava21,\
org.openrewrite.java.migrate.PatternMatchingInstanceof,\
org.openrewrite.java.migrate.SwitchExpressions,\
org.openrewrite.java.migrate.SwitchPatternMatching
```

**Recipe Descriptions:**
- `UpgradeToJava21` - Core Java 21 upgrade recipe that updates deprecated APIs and patterns
- `PatternMatchingInstanceof` - Refactors instanceof checks to use pattern matching (Java 16+ feature)
- `SwitchExpressions` - Converts traditional switch statements to modern switch expressions
- `SwitchPatternMatching` - Applies pattern matching in switch statements (Java 21 feature)

**Why pass recipes via command line?**
- Upgrade recipes are one-time operations and shouldn't be permanently added to [build.gradle](build.gradle)
- Prevents accidental re-execution of upgrade recipes in future builds
- Keeps the build configuration clean and focused on ongoing build tasks

This command will:
- Analyze the codebase for Java 17 to Java 21 migration opportunities
- Apply automated refactoring rules for the specified recipes
- Update deprecated APIs and patterns
- Use a few simple and safe Java 21 features
- Modify source files

### 6.5 Review Changes

After running the migration, review the changes made by OpenRewrite:

```bash
git diff
```

OpenRewrite makes changes directly to source files, so use git to review what was modified. Common changes include:
- Updated API usage for Java 21
- Removed deprecated method calls
- Updated language constructs to use Java 21 features
- Fixed compatibility issues

### 6.6 Verify Migration Success

After reviewing the changes, compile the project to ensure the migration was successful:

```zsh
./gradlew clean build
```

If there are any compilation errors, address them before proceeding.

### 6.7 Build/Fix Loop for Compilation Errors

After running the OpenRewrite migration and reviewing changes, iteratively fix any remaining compilation errors:

1. Execute the build command (skip tests for now):
   ```zsh
   ./gradlew clean build -x test
   ```
2. If there are compilation errors:
  - Analyze each error message carefully
  - Fix the errors using the **Error Resolution Methodology** (see section 6.7.2 below)
  - Common issues to address:
    - Deprecated APIs not handled by OpenRewrite
    - Changed method signatures in Java 21
    - New restrictions or requirements in Java 21
    - Third-party library compatibility issues
3. Re-run the build after making fixes
4. **Repeat steps 2-3 with the following exit conditions:**
   - **Success condition:** Build succeeds without compilation errors
   - **Maximum iterations:** Up to 5 build/fix cycles
   - **Stalled progress detection:** If the same error persists for 3 consecutive iterations, stop and document the issue
   - **Failure exit:** If unable to resolve after maximum iterations, document all remaining errors and request human intervention
5. Once compilation succeeds, run the test suite:
   ```zsh
   ./gradlew test
   ```
6. If tests fail:
  - Analyze test failure messages carefully
  - Fix test failures using the **Error Resolution Methodology** (see section 6.7.2 below)
  - Common test issues:
    - Behavior changes in Java 21 APIs
    - Timing or ordering differences
    - Mock/stub compatibility with new APIs
    - Test assertions that rely on implementation details that changed in Java 21
7. **Repeat steps 5-6 with the following exit conditions:**
   - **Success condition:** All tests pass
   - **Maximum iterations:** Up to 5 test/fix cycles
   - **Stalled progress detection:** If the same test failure persists for 3 consecutive iterations, stop and document the issue
   - **Failure exit:** If unable to resolve after maximum iterations, document all remaining test failures and request human intervention

**Important:** If either loop exits due to reaching maximum iterations or stalled progress:
- Create a detailed report listing all unresolved errors/failures
- Document any attempted fixes that didn't work
- Include relevant error messages and stack traces
- Stop execution and notify the user for manual review and intervention

#### 6.7.1 Upgrade Log Documentation

**All fixes, changes, and unresolved errors must be documented** in the upgrade log file located at:

```
/docs/ai-tasks/logs/java-21-upgrade-log.md
```

**Before starting the build/fix loop**, create the directory structure and initialize the log file if it doesn't exist:

```zsh
mkdir -p docs/ai-tasks/logs
```

**Log File Structure:**

The upgrade log file should contain the following sections:

```markdown
# Java 21 Upgrade Log

**Date**: YYYY-MM-DD
**Upgrade**: Java 17 to Java 21
**Status**: In Progress | Completed | Blocked

---

## Summary

Brief overview of the upgrade process and overall status.

---

## Fixes Applied

### Fix #1: [Brief description]
- **File**: path/to/file.java:line_number
- **Error Type**: Compilation Error | Deprecated API | etc.
- **Root Cause**: Description of the issue
- **Solution Applied**: Description of the fix
- **Source**: URL or reference to documentation/Stack Overflow
- **Date**: YYYY-MM-DD

### Fix #2: [Brief description]
...

---

## Unresolved Errors

### Error #1: [Brief description]
- **File**: path/to/file.java:line_number
- **Error Message**: Complete error message
- **Error Category**: API Deprecation | Package Migration | etc.
- **Attempted Solutions**:
  1. OpenRewrite recipe attempted: recipe-name (Result: Failed)
  2. Internet search queries: "query text"
  3. Manual fix attempted: Description (Result: Failed because...)
- **References**: Links to documentation, Stack Overflow, etc.
- **Recommended Next Steps**: Suggestions for manual resolution
- **Date**: YYYY-MM-DD

---

## Build/Test Summary

- **Total Build Iterations**: X
- **Total Fixes Applied**: X
- **Unresolved Errors**: X
- **Tests Passed**: X / Y
- **Final Status**: Success | Needs Manual Intervention
```

**Documentation Requirements:**

1. **For each successful fix** (Step 4 of section 5.8.2 Error Resolution Methodology):
   - Document in the "Fixes Applied" section
   - Include file path, error type, root cause, solution, and source
   - Add immediately after applying the fix

2. **For each unresolved error** (Step 5 of section 5.8.2 Error Resolution Methodology):
   - Document in the "Unresolved Errors" section
   - Include complete error details and all attempted solutions
   - Add when the error cannot be resolved

3. **Update the Summary section**:
   - Update status as the process progresses
   - Provide high-level overview of changes

4. **Update Build/Test Summary**:
   - Keep track of iteration counts
   - Update success/failure metrics
   - Update final status when complete

#### 6.7.2 Error Resolution Methodology

When compilation errors or test failures occur, follow this systematic approach to resolve them:

##### Step 1: Extract and Categorize the Error

1. **Capture the complete error message** including:
   - File path and line number
   - Error type (compilation error, deprecation warning, etc.)
   - Full error description and stack trace
   - Any suggested fixes from the compiler

2. **Categorize the error type:**
   - **API Deprecation/Removal**: Method or class no longer available in Java 21
   - **API Signature Change**: Method parameters or return types changed
   - **Package Migration**: Classes moved to different packages (e.g., javax.* → jakarta.*)
   - **Access Modifier Restriction**: New access restrictions in Java 21
   - **Third-party Library Incompatibility**: External dependencies not compatible with Java 21
   - **Behavioral Change**: API behavior changed in Java 21
   - **Other**: Uncategorized errors

##### Step 2: Search for OpenRewrite Recipes

**Before making manual code changes**, search for existing OpenRewrite recipes that can automatically fix the error:

1. **Search the OpenRewrite recipe catalog:**
   ```bash
   # List all available recipes in the rewrite-migrate-java dependency
   ./gradlew rewriteDiscover
   ```

2. **Search for recipes by keyword** related to the error:
   - Visit the OpenRewrite recipe catalog: https://docs.openrewrite.org/recipes
   - Search for recipes related to the error (e.g., "deprecated", "remove", "migrate", specific API names)
   - Look specifically in the "Java version migration" section: https://docs.openrewrite.org/recipes/java/migrate

3. **Common OpenRewrite recipes for Java 21 migration:**
   - `org.openrewrite.java.migrate.RemovedModifierRestrictions` - Fix removed modifier restrictions
   - `org.openrewrite.java.migrate.DeprecatedAPIs` - Replace deprecated APIs
   - `org.openrewrite.java.migrate.RemovedJavaSecurityManagerAPIs` - Remove SecurityManager usage
   - `org.openrewrite.java.migrate.RemovedThreadMethods` - Update removed Thread methods
   - `org.openrewrite.java.migrate.jakarta.*` - Migrate javax to jakarta packages
   - Search for more recipes at: https://docs.openrewrite.org/recipes/java/migrate/upgradetojava21

4. **If a relevant recipe is found:**
   - Run ONLY the new recipe to fix the specific error (do not re-run the recipes that were already executed in section 5.4):
     ```bash
     ./gradlew rewriteRun \
       -Drewrite.activeRecipes=org.openrewrite.java.migrate.<NewRecipeForTheError>
     ```
     Replace `<NewRecipeForTheError>` with the actual recipe name that addresses the error.

   **Why run only the new recipe?**
   - The initial migration recipes (`UpgradeToJava21`, `PatternMatchingInstanceof`, `SwitchExpressions`, `SwitchPatternMatching`) have already been executed in section 5.4
   - Re-running them is unnecessary and wasteful - they are idempotent but will re-scan the entire codebase
   - Running only the new recipe is faster and makes it easier to see what changed in git diff
   - OpenRewrite recipes are designed to be independent and can be run individually

   - Re-run the build to verify the fix:
     ```bash
     ./gradlew clean build
     ```
   - If the error is resolved, continue to the next error (if any)
   - If the error persists, proceed to Step 3

##### Step 3: Search the Internet for Solutions

**If no OpenRewrite recipe is found or the recipe didn't resolve the error**, search the Internet for solutions:

1. **Construct an effective search query** using:
   - The exact error message (in quotes)
   - "Java 21" or "Java 17 to 21 migration"
   - The affected API, class, or method name
   - Example: `"java.lang.SecurityManager deprecated" Java 21 migration`

2. **Search authoritative sources in this order:**
   - **Official Java documentation:**
     - Java 21 Release Notes: https://www.oracle.com/java/technologies/javase/21-relnotes.html
     - Java 21 API Documentation: https://docs.oracle.com/en/java/javase/21/docs/api/
     - JEP (JDK Enhancement Proposals) related to Java 21
   - **Stack Overflow** with filters:
     - Search with tags: [java-21], [java], [migration]
     - Look for answers with high votes and recent dates (2023+)
   - **GitHub Issues and Discussions:**
     - Search in repositories of affected third-party libraries
     - Look for migration guides and compatibility issues
   - **Technical blogs and migration guides:**
     - Baeldung, DZone, InfoQ articles on Java 21 migration
     - Spring Blog (if using Spring Framework)

3. **Evaluate search results:**
   - Prioritize official documentation and well-established sources
   - Look for solutions that explain the root cause, not just workarounds
   - Verify the solution applies to your specific Java version (17 → 21)
   - Check if the solution has been tested and validated by others

4. **Document the source** of the solution for future reference in the upgrade log file (see Documentation section below)

##### Step 4: Apply Automated Code Fixes

**If Internet research provides a solution**, automatically apply the fix and document the changes in the upgrade log file:

1. **Common fix patterns for Java 21 migration:**

   **A. Deprecated API Replacement:**
   ```java
   // Before (Java 17)
   Integer.parseInt("123", 10);

   // After (Java 21) - if the API was removed or changed
   // Use the recommended replacement from documentation
   ```

   **B. Package Migration (javax → jakarta):**
   ```java
   // Before
   import javax.servlet.http.HttpServlet;

   // After
   import jakarta.servlet.http.HttpServlet;
   ```

   **C. SecurityManager Removal:**
   ```java
   // Before
   SecurityManager sm = System.getSecurityManager();

   // After
   // Remove SecurityManager usage or use alternative security mechanisms
   ```

   **D. Thread Method Changes:**
   ```java
   // Before
   Thread.stop();  // Removed in Java 21

   // After
   // Use interrupt() and proper thread coordination
   thread.interrupt();
   ```

   **E. Third-party Library Updates:**
   - Check if a newer version of the library supports Java 21
   - Update the dependency version in [build.gradle](build.gradle):
     ```groovy
     dependencies {
       implementation 'group:artifact:new-version'  // Updated version
     }
     ```

2. **Make targeted, minimal changes:**
   - Only modify the code necessary to fix the error
   - Avoid refactoring or restructuring beyond what's needed
   - Preserve existing functionality and behavior

3. **Test the fix:**
   - Re-run the build after each fix
   - Verify the specific error is resolved
   - Ensure no new errors are introduced

##### Step 5: Document Unresolvable Errors

**If the error cannot be resolved** after trying all strategies:

1. **Create a detailed error report** in the upgrade log file including:
   - Complete error message and stack trace
   - File path and line number
   - Error category (from Step 1)
   - OpenRewrite recipes attempted (if any)
   - Internet search queries used
   - Solutions attempted and why they didn't work
   - Links to relevant documentation or Stack Overflow posts
   - Recommended next steps or alternatives

2. **Add the error to the unresolved errors section** in the upgrade log file

3. **Continue to the next error** (if within iteration limits) or exit the loop

##### Step 6: Track Error Resolution Progress

**To implement stalled progress detection:**

1. **Maintain a record** of errors encountered in each iteration:
   - Error signature (file path + line number + error message)
   - Iteration number when the error was first seen
   - Resolution attempts made

2. **Compare errors across iterations:**
   - If the exact same error appears in 3 consecutive iterations, it's considered "stalled"
   - Stop attempting to fix that error and add it to the unresolvable list

3. **Count successful fixes:**
   - If at least one error is fixed in an iteration, continue to the next iteration
   - If no progress is made (same number of errors or same errors), increment the stall counter

##### Example Workflow

**Example: Fixing a deprecated API error**

1. **Error encountered:**
   ```
   error: cannot find symbol
   symbol:   method getSubject()
   location: class javax.security.auth.Subject
   ```

2. **Categorize:** API Deprecation/Removal

3. **Search OpenRewrite recipes:**
   - Find recipe: `org.openrewrite.java.migrate.RemovedJavaSecurityManagerAPIs`
   - Add recipe to build.gradle
   - Run `./gradlew rewriteRun`
   - Result: Error persists (recipe doesn't cover this specific case)

4. **Search Internet:**
   - Query: `"Subject.getSubject() deprecated" Java 21 migration`
   - Find: Official Java 21 release notes indicating `Subject.getSubject()` was removed
   - Solution: Use `Subject.current()` instead

5. **Apply fix:**
   ```java
   // Before
   Subject subject = Subject.getSubject(AccessController.getContext());

   // After
   Subject subject = Subject.current();
   ```

6. **Verify:**
   - Run `./gradlew clean build`
   - Error resolved ✓

---

### Step 7: Update Dockerfile (If Present)

If the repository contains a Dockerfile with a Java 17 base image, it needs to be updated to use Java 21.

### 7.1 Check for Dockerfile

Search for Dockerfile(s) in the repository:

```bash
find . -name "Dockerfile*" -type f
```

### 7.2 Identify Java Version in Dockerfile

For each Dockerfile found, check if it references Java 17:

```bash
grep -i "java.*17\|17.*java\|VARIANT=17\|JAVA_VERSION=17" <path-to-dockerfile>
```

### 7.3 Update Dockerfile to Java 21

If Java 17 is found in the Dockerfile, update it to Java 21. Common patterns to look for and update:

#### Pattern 1: VARIANT argument
```dockerfile
# Before
ARG VARIANT=17-bullseye

# After
ARG VARIANT=21-bullseye
```

#### Pattern 2: JAVA_VERSION argument
```dockerfile
# Before
ARG JAVA_VERSION=17.0.7-ms

# After
ARG JAVA_VERSION=21.0.9-amzn
```

**Note:** Update the specific Java version to the latest available Java 21 version. For Amazon Corretto, use a version like `21.0.9-amzn` or later.

#### Pattern 3: Base image with explicit Java version
```dockerfile
# Before
FROM [<registry>/]amazoncorretto:17-alpine

# After
FROM [<registry>/]amazoncorretto:21-alpine
```

**Note:** The `[<registry>/]` prefix is optional and represents container registry paths like `ghcr.io/`, `docker.io/`, etc. If present, preserve the registry prefix and only update the Java version number from 17 to 21.

#### Pattern 4: SDKMAN installation in Dockerfile
```dockerfile
# Before
RUN bash -lc '. /usr/local/sdkman/bin/sdkman-init.sh && sdk install java 17.0.7-ms && sdk use java 17.0.7-ms'

# After
RUN bash -lc '. /usr/local/sdkman/bin/sdkman-init.sh && sdk install java 21.0.9-amzn && sdk use java 21.0.9-amzn'
```

### 7.4 Verify Dockerfile Changes

After updating the Dockerfile, verify the changes:

```bash
grep -i "java.*21\|21.*java\|VARIANT=21\|JAVA_VERSION=21" <path-to-dockerfile>
```

This should confirm that all Java 17 references have been updated to Java 21.

### 7.5 Test Docker Build (Optional)

If Docker is available, test building the image to ensure the Dockerfile changes are valid:

```bash
docker build -f <path-to-dockerfile> -t test-java21-upgrade .
```

This verifies that the Dockerfile syntax is correct and the Java 21 base image is accessible.

---

### Step 8: Update GitHub Actions Workflow Files (If Present)

GitHub Actions workflow files may specify Java versions for CI/CD builds. If any workflow files reference Java 17, they need to be updated to Java 21.

### 8.1 Check for GitHub Actions Workflow Files

Search for workflow files in the repository:

```bash
find .github/workflows -name "*.yml" -o -name "*.yaml" 2>/dev/null
```

### 8.2 Identify Java 17 References in Workflow Files

Check all workflow files for Java 17 references:

```bash
grep -r -i "java.*17\|17.*java\|java-version.*17" .github/workflows/ 2>/dev/null
```

### 8.3 Update Workflow Files to Java 21

If Java 17 is found in workflow files, update it to Java 21. Common patterns to look for and update:

#### Pattern 1: Matrix strategy with Java version
```yaml
# Before
strategy:
  matrix:
    java: [ '17' ]

# After
strategy:
  matrix:
    java: [ '21' ]
```

#### Pattern 2: Setup Java action with java-version
```yaml
# Before
- name: Set up JDK 17
  uses: actions/setup-java@v3
  with:
    java-version: '17'
    distribution: 'corretto'

# After
- name: Set up JDK 21
  uses: actions/setup-java@v3
  with:
    java-version: '21'
    distribution: 'corretto'
```

#### Pattern 3: Multiple Java versions in matrix (keeping Java 17 for compatibility testing)
```yaml
# If testing against multiple Java versions and you want to keep Java 17 for compatibility:
strategy:
  matrix:
    java: [ '17', '21' ]

# If upgrading completely to Java 21 only:
strategy:
  matrix:
    java: [ '21' ]
```

**Note:** Ensure the `distribution` field is set to `'corretto'` to use Amazon Corretto JDK, consistent with the local development environment.

### 8.4 Verify Workflow File Changes

After updating the workflow files, verify the changes:

```bash
grep -r -i "java.*21\|21.*java\|java-version.*21" .github/workflows/ 2>/dev/null
```

This should confirm that Java version references have been updated to 21.

### 8.5 Validate Workflow Syntax (Optional)

If the GitHub CLI (`gh`) is installed, you can validate the workflow syntax:

```bash
gh workflow list
```

Or commit the changes and check the Actions tab on GitHub to ensure workflows run successfully with Java 21.

---

### Step 9: Update AWS CodeBuild buildspec.yml Files (If Present)

AWS CodeBuild buildspec.yml files may specify Java runtime versions. If any buildspec files reference Java 17, they need to be updated to Java 21.

### 9.1 Check for AWS CodeBuild buildspec Files

Search for buildspec files in the repository:

```bash
find . -name "buildspec*.yml" -o -name "buildspec*.yaml" 2>/dev/null
```

### 9.2 Identify Java 17 References in buildspec Files

Check all buildspec files for Java 17 references:

```bash
grep -r -i "java.*17\|corretto17\|runtime.*17" buildspec*.yml buildspec*.yaml 2>/dev/null
```

### 9.3 Update buildspec Files to Java 21

If Java 17 is found in buildspec files, update it to Java 21. Common patterns to look for and update:

#### Pattern 1: Runtime version in install phase
```yaml
# Before
phases:
  install:
    runtime-versions:
      java: corretto17

# After
phases:
  install:
    runtime-versions:
      java: corretto21
```

#### Pattern 2: Explicit Java version specification
```yaml
# Before
phases:
  install:
    runtime-versions:
      java: 17

# After
phases:
  install:
    runtime-versions:
      java: 21
```

#### Pattern 3: Environment variables for Java version
```yaml
# Before
env:
  variables:
    JAVA_VERSION: "17"
    JAVA_HOME: "/usr/lib/jvm/java-17-amazon-corretto"

# After
env:
  variables:
    JAVA_VERSION: "21"
    JAVA_HOME: "/usr/lib/jvm/java-21-amazon-corretto"
```

#### Pattern 4: CodeBuild image with Java version
```yaml
# Before
version: 0.2
# Using standard image with Java 17
# If comments reference Java 17, update them

# After
version: 0.2
# Using standard image with Java 21
# Update any comments referencing Java version
```

**Note:** Ensure you're using Amazon Corretto (`corretto21`) to maintain consistency with the local development environment.

### 9.4 Update CodeBuild Project Configuration

If the CodeBuild project uses a specific image version, you may also need to update the project configuration in AWS Console or via Infrastructure as Code (IaC):

```yaml
# Terraform example
resource "aws_codebuild_project" "example" {
  environment {
    image = "aws/codebuild/amazonlinux2-x86_64-standard:5.0"  # Supports Java 21
  }
}
```

**Note:** AWS CodeBuild standard images version 5.0 and later include support for Amazon Corretto 21.

### 9.5 Verify buildspec File Changes

After updating the buildspec files, verify the changes:

```bash
grep -r -i "java.*21\|corretto21\|runtime.*21" buildspec*.yml buildspec*.yaml 2>/dev/null
```

This should confirm that Java version references have been updated to 21.

### 9.6 Test CodeBuild Execution (Optional)

If you have access to AWS CodeBuild, trigger a build to ensure the buildspec changes work correctly with Java 21:

```bash
aws codebuild start-build --project-name <your-project-name>
```

Monitor the build logs to verify that Java 21 is being used during the build process.

---

### Step 10: Execute Gradle Build with Test Cases

After completing all the configuration updates and migrations, it's essential to verify that the application builds successfully and all tests pass with Java 21.

### 10.1 Clean Build with Tests

Execute a clean build with all test cases to ensure the Java 21 upgrade is successful:

```bash
./gradlew clean build
```

This command will:
- Clean the build directory to remove any cached artifacts from previous builds
- Compile the source code with Java 21
- Compile the test code
- Run all unit tests
- Run all integration tests (if configured)
- Generate build artifacts

### 10.2 Verify Build Success

After the build completes, verify that:
1. The build completed successfully without errors
2. All tests passed
3. No deprecation warnings related to Java version compatibility

### 10.3 Run Tests Separately (Optional)

If you want to run tests separately without building artifacts:

```bash
./gradlew test
```

For integration tests (if configured separately):

```bash
./gradlew integrationTest
```

### 10.4 Review Test Results

Check the test results in the console output. For detailed test reports, review:

```bash
# Open test report in browser (macOS)
open build/reports/tests/test/index.html
```

### 10.5 Troubleshoot Test Failures

If any tests fail:
1. Review the test output for specific error messages
2. Check if failures are related to Java 21 compatibility
3. Update deprecated APIs or incompatible code patterns
4. Re-run the tests after fixes

### 10.6 Verify JAR/WAR Artifacts

Ensure that the build artifacts are created with Java 21:

```bash
# Check the build output directory
ls -lh build/libs/

# Verify the Java version in the JAR manifest
unzip -p build/libs/*.jar META-INF/MANIFEST.MF | grep -i "build-jdk"
```

---

## Next Steps

After completing the above steps, proceed with:
1. Running end-to-end tests and any other tests (e.g., performance tests, smoke tests, manual tests) that are not executed as part of the Gradle build to identify compatibility issues
2. Manually updating any remaining deprecated APIs not handled by OpenRewrite
3. Building and deploying the application

---

## References

- [SDKMAN Installation Guide](https://sdkman.io/install/)
- [Amazon Corretto via SDKMAN](https://sdkman.io/jdks/amzn/)
- [Amazon Corretto 21 Documentation](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/)
