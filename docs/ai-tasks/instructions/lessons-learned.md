# Lessons Learned


## The generated instructions have to be reviewed thoroughly

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
