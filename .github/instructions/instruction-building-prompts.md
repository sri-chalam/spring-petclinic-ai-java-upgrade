<!-- This file has prompts used to generate instructions file.-->
# Sample prompts used to generate instructions.

Prompts are iterative. If we know what needs to be generated (the goal), we can keep modifying the prompts, to reach the goals.

Claude Sonnet is used to write the instructions.

First time, Claude asks,
Allo write to java21-upgrade-instructions.md?

Choose the option: Yes, and don't ask again.

## Assumptions
Assumptions in this instructions are:
1. The developers uses Macbook.
2. The default shell is zsh
3. curl is already installed
4. The organization uses Java jdk provided by Amazon. 

## Prompt to install and initialize sdkman tool.
Initial prompt to install sdkman software and the purpose of the instruction file, assumptions.

I want to write AI coding agent instructions to upgrade the application from java 17 to Java 21.

Help me to write instructions.

The first thing in the instructions to do is whether a tool "sdkman" is installed or not.

Whether the sdkman is installed or not can be identified by checking if there is a file ~/.sdkman/bin/sdkman-init.sh

If the file ~/.sdkman/bin/sdkman-init.sh is present, execute it.

If the file is not present, install sdkman tool.

Download the sdkman software from https://get.sdkman.io using curl.

If the file is present or newly downloaded, execute the following command. The following command adds a script function "sdk" to the PATH environment variable. The "sdk" command is the core, of installating Java version.

execute "source ~/.sdkman/bin/sdkmain-init.sh"

Search Internet, if you need clarifications.

Add instructions so far.



**Important Note**: The above instructions do not have a statement to say, use zsh. It is ambiguous to LLM. So it assumed to use bash. 

Then chat with Claude on whether zsh can be used.

### Iterative question
The generated instructions use bash, is it preferable to use zsh?

Do not make changes, I want to discuss the pros and cons.

Claude mentioned, SDKMAN is bash native. For installation of sdkman, we can use bash. Rather than getting compatibility issues.

The macbook uses zsh as the default shell, but let the sdkman be installed in bash as recommended.

### Iterative prompt - fix the problem that instructions assume that JDK 21 is not present in the machine
The instructions generated under "Step 2: Install Amazon Corretto Java 21" assumes that JDK must be installed.

The instructions does not take care of the condition, the SDKMAN already exists and JDK version also already exists.

Change the instructions such that check whether Amazon JDK version 21 already installed, if it is already installed skip the process of installing.

If the JDK version is not already present, modify instructions to find the latest version of amazon corretto jdk at the time time when the instruction file is executed and install the latest version.

### Iterative prompt - Use zsh instead of bash
Use zsh for any script written from step 2 onwards.
Change the scripts from bash to zsh.

### Iterative prompt - fix the issue found in the script claude created

Under section 2.1, the following script is present.

if sdk list java | grep -q "21\..*amzn.*>>>"; then
    echo "Amazon Corretto Java 21 is already installed and in use"
    JAVA21_ALREADY_INSTALLED=true
elif sdk list java | grep -q "21\..*amzn"; then
    echo "Amazon Corretto Java 21 is installed but not currently in use"
    JAVA21_ALREADY_INSTALLED=true

The script need not distinguish between whether a Java version is currently being used or not. We are interested only in whether it is installed locally or not.

The above if elif conditions can be merged into one.

Also, we are not interested in the available JDK versions. We want only the versions that are installed currently. To know the currently installed versions, use the following command:

if sdk list java | grep -v "local only" | grep "21\..*amzn"; then

### Iterative prompt - Print clear feedback on actions taken

While generating the instructions, I want to provide general guidelines to Claude for 
Providing clear feedback about what action was taken in each step.

Add a few instructions to LLM to give clear feedback on actions being taken.

### Iterative prompt - Upgrade only Gradle not Maven
Add an instruction to indicate that upgrade only Gradle, which includes Gradle wrapper, build.gradle, gradle.properties. The goals of the instruction file is to upgrade Java.
Do not upgrade maven, which includes, maven wrapper, pom.xml.

### Iterative prompt - gradle wrapper upgrade if required
Add instruction to upgrade Gradle Wapper if needed.

Check If the Gradle version present in gradle/wrapper/gradle-wrapper.properties, is compatible with Java version 21.

If the current Gradle Wrapper version  is not compatible with Java 21, upgrade it to a compatible version.

### Iterative prompt - Remove incorectly generated instructions for Gradle configuraiton
Remove the section "3.1 Upgrade Gradle Configuration"
Because the Gradle configuration will be performed by open-rewrite step that will be added in susequent steps.

### Iterative prompt - Do not upgrade Spring Boot version during the Java upgrade
Under the section "Important: Build Tool Scope",
Add an important note as
DO NOT upgrade Spring Boot version during the Java upgrade. Spring Boot will be upgraded in a separate task and using a separate instruction file.

### Iterative prompt - Working with LLM to remove confusing instructions

LLM generated the following instruction:

```bash
# Upgrade to Gradle 8.5 or later (recommended for Java 21)
./gradlew wrapper --gradle-version=8.5

# Or upgrade to the latest stable version
# Check https://gradle.org/releases/ for the latest version
./gradlew wrapper --gradle-version=8.11
```

The highligted statements are a bit confusing.

We need to use only version either 8.5 ot 8.11.

It seems to me that 8.11 version is better. Please fix the instructions. Keep only 8.11 version.

### Iterative prompt - Add instrutions to use Open Rewrite.

Add step 5. Add instructions to add OpenRewrite Gradle plugin to upgrade this application from Java 17 to 21.

The details of instructions to add are below:

Add openrewrite rewrite plugin only if it is not already present in build.gradle.
If it is already present, if the newer plugin is required for Java 17 to 21 upgrade, change the existing plugin.

Add rewrite migrate java recipe to build.gradle to upgrade  Java.

Add Rewrite dependency to build.gradle.

Use run migrate command to upgrade Java version.

### Iterative prompt - use amazoncorretto jdk instead of eclipse-temurin

In Pattern 3, the eclipse-temurin is referenced.
As per the initial instructions, I would like to use Amazon corretto jdk.

Change lines 391, 394 to use amazoncorretto jdk.

### Iterative prompt - optional registry name

Under pattern 3, in line 391 and 394, after "FROM" keyword, there can be prefix such as ghcr.io/<some-pattern>/

How to represent that in line 391 and 394?

### Iterative prompt - Update Java version in Github Workflow yml files
The Github Actions workflow files are present under .github/workflows/ as *.yml files. These yml files may have Java version set optionally.
If any of these files set Java version as 17, change it to 21.

Add instrunction to the above.

### Iterative prompt - Update Java version ins buildspec.yml file
Some Git Repos use AWS CodeBuild and use buildspec.yml files.
The buildspec.yml file may contain Java version.
If any of these files set Java version as 17, change it to 21.

Add instrunction to the above.

### Iterative prompt - Execute Gradle build with test cases at the end
Add instructions at the end, Gradle build with test cases have to be executed.

### Iterative prompt - Next steps - end to end tests
Under the section ## Next steps, update the point 1, in line number 717, to execute end to end tests and any other tests that are not executed as part of the Gradle Build.

### Iterative prompt - Import Trusted Certs

Currently, the section "2.2 Find and Install Latest Amazon Corretto Java 21 (if not present)" starting in line 112, installs the latest version of Amazon corretto Java.

Update the instructions to do the following:
1. Ask the developer to copy any trusted certs of the orgnization to be ito be copied into the directory ~/trusted-certs/. 
2. Immediately, after installing Java, if there are any certs in this directory, write instructions to import them into the JDK installed.
3. If there are no certs present in the directory, there is no need to import.
4. If the Java version is not installed, then also there is no need to import certs.
5. The certs must have extensions .pem, .cer or .crt
6. The certs with any other extension, even though present under ~/trusted-certs, they should not be imported.
7. use the following command to import the certs:

```$JAVA_HOME/bin/keytool -import -alias <take the cert name without extenstion>-<append-current-date> -cacerts -trustedcerts -file <each-trusted-cert-file-name> -storepass changeit -noprompt
```
8. Execute a command to list the trusted certs that are imported into trusted store. This is to verify whether the certs are imported correctly.

All these are very lengthy, it is better to split section 2.2 into two steps, one to install Java and the other to import certs, do it.

#### Iterative prompt - add interactive prompt

Add an interative prompt in section 2.2. that asks y/n.
Irrespective of the answer, check the concents of the directory has certs, import those certs.

#### Iterative prompt - correction in logic of script - import certs only if JDK is installed

Section 2.4 starting in line number 180, imports the certs, even if the JDK is already is installed.

Change the scripts such that import the certs only if the JDK is installed as part of executing these instructions.

### Iterative Prompt - Skip the instructions, if the application is already in Java 17

Somewhere In the beginning of the instruction file, add instruction to skip all the instructions, if the current application does not have Java 17.

In order to find whether the current Java version, check build.gradle or gradle.properties.

### Iterative Prompt - adding additional Rewrite Recipes - Use couple of minor new features

In the list of active Recipes, in addition to the UpgradeToJava21, add the following recipes:
"org.openrewrite.java.migrate.PatternMatchingInstanceof"
"org.openrewrite.java.migrate.SwitchExpressions"
"org.openrewrite.java.migrate.SwitchPatternMatching"

