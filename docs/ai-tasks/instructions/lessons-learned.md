# Lessons Learned


### Click "Keep" and "Allow" Buttons Promptly

As the AI assistant executes instructions and logs information to the log markdown file, it periodically asks for confirmation to keep changes. Click the "Keep" button when prompted. Similarly, the AI chat may wait for permission before making certain changes—click "Allow" to let it proceed. If you don't respond, the upgrade will pause indefinitely.

### Open Rewrite activeRecipes in build.gradle - Not a best practice 

The instructions added activeRecipe list to the build.gradle.
In the documentation of Open Rewrite came across the info that adding activeRecipes to the build.gradle is not a good practice.
Because the Java upgrade is one time job, there is no need to commit that change.
Also, usually developers may want to execute a particular recipe such as static code analysis more frequently.

When discussed that with AI agent, it gave solution. It is important to question and chat the approach or instructions given.

### The build fix loop instruction errors

The initial instructions generated in build/fix loop have quite some mistakes.

If there are errors that cann't be resolved by AI agent, the build fix loop could go into infinite loop.

There were no instructions on how LLM can identify the reason for compilation error and how it can resolve it.


### Writing instructions or script to identify current Java version is complex 
It may sound simple to find the version of Java from a Gradle configuration.
However, there are dozens of ways in which Java version can be set.

#### It is very complex to write code to write script to identify the current Java version and skip the upgrade if the version is Java 17

There are Dozens of ways how a Java applications can set Java version.
Some of them are documented in test-cases.md

Open Rewrite recipe upgradeJavato21

### Mix of zsh and bash scripts gave problem
The instruction file used both bash and zsh scripts.
The SDKMAN scripts are in bash and the others in zsh.

When the first script in bash has to be executed, got the following error:

"The terminal seems to be in an unusual state. Let me try to check the java version directly."

The execution stuck there.

Changed all script to be in bash.

### The Java installation asked a question in Terminal

Do you want java 21.0.9-amzn to be set as default? (Y/n)

Change the instruction so that it does not ask for confirmation.

### Gradlew version is 8.5 - It still went ahead to upgrade
Change the instruction to clearly instruct when to install and when not.

### Incorrect version syntax
The Coding agent incorrectly gave the syntax like the below.
It did not work. 
    rewrite(platform("org.openrewrite.recipe:rewrite-recipe-bom:latest.release"))

Then the agent went in a loop to get a version. It got 2.24.0, which does not exist.

rewrite(platform("org.openrewrite.recipe:rewrite-recipe-bom:+"))

Debated between the above syntax and hard coding. Finally chose the version.
rewrite(platform("org.openrewrite.recipe:rewrite-recipe-bom:3.22.0"))

### Open Rewrite plugin version mismatch with bom version

Got an error 
org.openrewriteRecipe org.openrewrite.config.Environment.activateRecipes(java.lang.Iterable)

The Internet search indicates that there is a mismatch between plugin and bom versions.

In plugin version was "latest.version".
Changed it. Hard coded it to the latest version. 7.24.0.

### Removed OpenRewrite Recipes
The following recipes are removed and not available anymore.
SwitchExpressions
PatternMatchingInstanceof

The list of available receipes can be searched using:

./gradlew rewriteDiscover

### Some steps are skipped - Example import certificates
The JDK is installed but the certificates are not imported into JDK.

If the certificates are not imported, the openrewrite plugin cannot be executed.

### When there was a build error - Instruction file is able to loop through build errors

It went through build/fix loop. 
Tried to fix the problem in multiple ways.
It was able to identify that Lombok version has some issues and upgraded the version to fix the problem.

When an unknown OpenRewrite receipe is used, it tried multiple ways. It gave an error message.

### The upgrade identified bug in Docker Cert import path

Java changed a cacerts path.
The Dockerfile has old Java path.
The upgrade fixed the path issue as a bonus to Java upgrade.


### Unambiguous instructions - comes again and again

The prompt "verify the upgrade is successful" is ambiguous for LLM".

It can be interprted in multiple ways.

The instructions have to mention clearly, what is the criteria for successful completion (e.g. builds fine, all test cases pass).

### Use multiple models to review the instructions, create the instructions

Some models suggestions are better than others in some cases.

For example ChatGPT model's suggestion on instruction flow ASCII diagram is very good.

### Many mistakes in generating the instructions file
We need to review the instruction file very thoroughly.

Heading and sub heading are both given h3 - ###

The generated instruction does not clearly convey the needs of a team.
Sometimes LLM gives instructions in non-optimal way.

When a step is added between 6 and 7, when LLM was asked to renumber, it misses in some places.

LLM generated a section "Next Steps".
Developer need to discuss with LLM for better names and tell the purpose of the section. Choose a better name like "Post-Upgrade Manual Steps". 

### Some instructions refer to other sections
The other step name changes over time, when new steps are added or removed. Models are not very accurate to fix the references and sometimes.

### Mismatch in OpenRewrite Gradle Plugin and Dependency bom version

While creating the instruction file using the LLM, the model generated the following instructions.

Two different versions are used in the code snippet and "note". When the instructions were used to upgrade Java version, the old version 6.30.3 was used. 
The version 6.30.3 was old does not seem to be compatble with the new version used for rewrite recipe bom. It seems the version 6.30.3 was used because it was mentioned in the bolded note.

```groovy
plugins {
  // ... existing plugins ...
  id("org.openrewrite.rewrite") version "7.24.0"
}
```

**Note:** Version 6.30.3 or later is recommended for Java 21 migration. If an older version is present, update it to the latest version.


Changed the above block to the below:
```groovy
plugins {
  // ... existing plugins ...
  id("org.openrewrite.rewrite") version "7.24.0"
}
```

**Note:** Use the latest version of the OpenRewrite Gradle plugin. Version 7.25.0 has known compatibility issues with Java 21 migration recipes.



### Open Rewrite and Instruction Benefits
Agent tries in multiple ways to solve a problem.
If it encounters a problem with OpenRewrite, it tries to upagrade without Open rewrite. 
It takes long time to upgrade. One problem is attempted to be solved at a time.


### Many times not the fault of LLM
It is the lack of clear instructions.
Humans may understand. LLMs clear instructions


### Give Clear Instructions on the commonly needed Version Upgrades - Lombok, MapStruct

Lombok, mapstruct are commonly used libraries.

Lombok needs upgrade for each Java upgrade. Rather than AI agent figures out to identify which version to use, added instructions to use the latest lombok and mapstruct versions.

The latest versions of these libraries are backward compatible, has less vulnerabilities.

### The step number confusion
When we ask AI to generate a new step, it creates a new step. It also creates the sub section under the new step. 
Sometimes AI agent creates a new step say Step 2. under it it creates sub sctions as Step 1, Ste 2. It confuses.
We need to gude AI Agent to use differene naming conventions to the sub sections.

### When instruction was given to use the latest Splotless Gradle plugin - LLM got old version

The instructions have to be given multiple iterations to get the latest version of Gradle.

We should not believe LLM completely.

### Replaced google-java-format plugin with Spotless
In case if the google-java-format plugin is present replace it with Spotless.
This is organization specific. Some companies use Google Java format which is deprecated.

It comments out. Does not remove it.

### Script Identifies Whether Java is Installed always returns true
The LLM generated script to find whether the Java is installed or not has bugs. It always returns true.

We need to test every script generated by LLM.

### Bugs in LLM Generated Scripts
The script to get the latest version of a library dependency has bug.

### LLM Confused on Spotless Gradle Plugin
LLM got confused on how to handle Spotless plugin. When Java version is upgraded, the existing Spotless version did not work. The model chose to use a different formating style to fix the problem rather than upgrading the version of Spotless.

Explicit instructions were added to upgrade the Splotless version rather than changing the formating style, which changes the format of multipe files.


## Removed sections from the main article for brevity
### The Scale of Comprehensive Instructions

To properly address all these variations and provide concrete examples for each scenario, a complete upgrade guide would easily span hundreds or even thousands of lines. Each combination of choices (distribution × installation method × build tool × upgrade approach) represents a unique path that requires specific instructions and examples.
